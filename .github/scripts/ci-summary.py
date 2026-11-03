"""Condenses each CI job's outcome into a digest, and every digest into one run summary (as in tuProlog/2p-kt).

Each job runs `collect` (from the repository root, after Gradle) to boil its results down to a small JSON digest,
uploaded as an artifact: test results, coverage and Gradle scan links (`build`, `matrix`), style findings (`lint`),
documentation warnings (`docs`), or what got released and where (`release`). Then it runs `report` over every digest
uploaded so far, plus the run's live job list, and `publish`es the result to the run's "CI summary" check run, so that
the summary appears with the first finished job and fills in as the others finish. The final `summary` job renders
the complete version into the run summary page, emits annotations, and completes the check run.

    python3 ci-summary.py collect --out digest.json --kind build|matrix|lint|docs|release [--dim k=v ...]
        [--outcome success] [--log FILE] [--version X]
    python3 ci-summary.py report --digests DIR [--jobs jobs.json] [--baseline FILE] --summary OUT [--title-out FILE]
        [--annotate]
    python3 ci-summary.py publish --check-run ID --summary FILE --title FILE [--conclusion success|failure]
"""

import argparse
import json
import os
import re
import subprocess
import sys
import urllib.request
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

MAX_FAILURES = 100  # per digest, so that a catastrophic run keeps its artifact (and the summary) small
MAX_LINES = 30  # style findings or documentation warnings listed per digest
MAX_ANNOTATIONS = 10  # GitHub shows at most 10 error annotations per step anyway
MAX_CHECK_RUN_TEXT = 65000  # check run outputs are limited to 65535 characters
COVERAGE_DROP_ALERT = 0.1  # percentage points
TEST_RESULTS = Path("build/test-results")
KOVER_REPORT = Path("build/reports/kover/report.xml")
SCANS_FILE = Path("build/gradle-scans.txt")
LOCAL_REPOSITORY = Path("build/project-local-repository")
NPM_PACKAGE_JSON = Path("build/packages/js/package.json")
DOC_WARNING = re.compile(r"^(WARNING\b|w: |warning: )")
DOC_ERROR = re.compile(r"^(ERROR\b|e: )")
ANSI_ESCAPE = re.compile(r"\x1b\[[0-9;]*m")
FINDING = re.compile(r"^\S+:\d+(:\d+)?:? ")  # path:line[:column]: message (ktlint, detekt)
MAVEN_CENTRAL = "https://repo1.maven.org/maven2"
NPM_REGISTRY = "https://registry.npmjs.org"


# ---------------------------------------------------------------------------------------------------- collect


def collect_tests():
    """Test counts per Gradle test task (jvmTest, jsNodeTest, linuxX64Test, ...), from the JUnit XML reports."""
    suites = defaultdict(lambda: {"tests": 0, "failures": 0, "skipped": 0})
    failed = []
    for report in sorted(TEST_RESULTS.glob("*/**/*.xml")):
        task = report.relative_to(TEST_RESULTS).parts[0]
        try:
            root = ET.parse(report).getroot()
        except ET.ParseError:
            continue
        for case in root.iter("testcase"):
            suite = suites[task]
            suite["tests"] += 1
            problem = case.find("failure")
            if problem is None:
                problem = case.find("error")
            if problem is not None:
                suite["failures"] += 1
                if len(failed) < MAX_FAILURES:
                    message = (problem.get("message") or problem.text or "").strip().splitlines()
                    failed.append(
                        {
                            "task": task,
                            "class": case.get("classname", ""),
                            "name": case.get("name", ""),
                            "message": message[0][:300] if message else "",
                        }
                    )
            elif case.find("skipped") is not None:
                suite["skipped"] += 1
    return [{"task": t, **counts} for t, counts in sorted(suites.items())], failed


def collect_coverage():
    """Line coverage of the JVM tests, in total and per package."""
    if not KOVER_REPORT.exists():
        return None
    packages = defaultdict(lambda: [0, 0])
    total = [0, 0]
    for package in ET.parse(KOVER_REPORT).getroot().iter("package"):
        for source in package.iter("sourcefile"):
            counter = source.find("counter[@type='LINE']")
            if counter is None:
                continue
            covered, missed = int(counter.get("covered")), int(counter.get("missed"))
            name = package.get("name").replace("/", ".")
            packages[name][0] += covered
            packages[name][1] += missed
            total[0] += covered
            total[1] += missed
    return {"total": total, "packages": dict(packages)}


def collect_style():
    """ktlint and detekt findings, from their plain-text reports (one finding per line)."""
    findings = {"ktlint": [], "detekt": []}
    for tool, pattern in (("ktlint", "build/reports/ktlint/**/*.txt"), ("detekt", "build/reports/detekt/*.txt")):
        for report in sorted(Path(".").glob(pattern)):
            lines = ANSI_ESCAPE.sub("", report.read_text()).splitlines()
            findings[tool] += [line.strip().removeprefix(os.getcwd() + "/") for line in lines if FINDING.match(line.strip())]
    return {tool: {"count": len(lines), "lines": lines[:MAX_LINES]} for tool, lines in findings.items()}


def collect_log(path):
    """Warnings and errors of a build log (MkDocs `WARNING`/`ERROR`, compiler `w:`/`e:`)."""
    lines = Path(path).read_text(errors="replace").splitlines() if path and Path(path).exists() else []
    warnings = [line.strip() for line in lines if DOC_WARNING.match(line.strip())]
    errors = [line.strip() for line in lines if DOC_ERROR.match(line.strip())]
    return {
        "warnings": {"count": len(warnings), "lines": warnings[:MAX_LINES]},
        "errors": {"count": len(errors), "lines": errors[:MAX_LINES]},
    }


def exists(url):
    try:
        with urllib.request.urlopen(urllib.request.Request(url, method="HEAD"), timeout=20) as response:
            return response.status == 200
    except Exception:  # noqa: BLE001 -- unreachable or missing are the same thing here
        return False


def on_github_packages(name, version):
    """Whether the npm package `name` (unscoped) has `version` on GitHub Packages, under the repository owner."""
    owner = os.environ.get("GITHUB_REPOSITORY_OWNER", "")
    versions = subprocess.run(
        ["gh", "api", f"users/{owner}/packages/npm/{name}/versions", "--paginate", "-q", ".[].name"],
        capture_output=True,
        text=True,
    )
    return versions.returncode == 0 and version in versions.stdout.split()


def collect_release(version):
    """What this release produced, and whether each item can actually be found where it was published."""
    if not version:
        return None
    poms = {}  # group/artifact/version/artifact-version.pom -> artifact
    for pom in LOCAL_REPOSITORY.glob(f"**/{version}/*.pom"):
        poms[pom.relative_to(LOCAL_REPOSITORY).as_posix()] = pom.parent.parent.name
    maven = [{"name": name, "found": exists(f"{MAVEN_CENTRAL}/{path}")} for path, name in sorted(poms.items())]
    npm, github_npm = [], []
    if NPM_PACKAGE_JSON.exists():
        name = json.loads(NPM_PACKAGE_JSON.read_text())["name"]
        npm = [{"name": name, "found": exists(f"{NPM_REGISTRY}/{name.replace('/', '%2f')}/{version}")}]
        unscoped = name.split("/")[-1]
        owner = os.environ.get("GITHUB_REPOSITORY_OWNER", "")
        github_npm = [{"name": f"@{owner}/{unscoped}", "found": on_github_packages(unscoped, version)}]
    assets = subprocess.run(
        ["gh", "release", "view", version, "--json", "assets", "-q", "[.assets[].name]"],
        capture_output=True,
        text=True,
    )
    github = json.loads(assets.stdout) if assets.returncode == 0 and assets.stdout.strip() else []
    return {"version": version, "maven": maven, "npm": npm, "github_npm": github_npm, "github": github}


def collect(args):
    tests = args.kind in ("build", "matrix")
    suites, failed = collect_tests() if tests else ([], [])
    digest = {
        "kind": args.kind,
        "dims": dict(d.split("=", 1) for d in args.dim),
        "outcome": args.outcome,
        "suites": suites,
        "failed": failed,
        "coverage": collect_coverage() if args.kind == "build" else None,
        "style": collect_style() if args.kind == "lint" else None,
        "log": collect_log(args.log) if args.kind == "docs" else None,
        "release": collect_release(args.version) if args.kind == "release" else None,
        "scans": SCANS_FILE.read_text().split() if SCANS_FILE.exists() else [],
    }
    Path(args.out).write_text(json.dumps(digest))


# ----------------------------------------------------------------------------------------------------- report


JOB_ICONS = {"success": "✅", "failure": "❌", "cancelled": "🚫", "skipped": "➖", "timed_out": "⌛"}


def job_icon(job):
    if job is None:
        return "➖"
    if job["status"] != "completed":
        return "⏳"
    return JOB_ICONS.get(job["conclusion"], "⚠️")


def pct(covered, missed):
    total = covered + missed
    return 100.0 * covered / total if total else 0.0


def totals(suites):
    return {k: sum(s[k] for s in suites) for k in ("tests", "failures", "skipped")}


def status_icon(digest):
    if digest["outcome"] == "skipped":
        return "⏭️"  # inputs unchanged since the last green run: tests were not re-run
    failures = totals(digest["suites"])["failures"]
    if failures:
        return f"❌ {failures}"
    if digest["outcome"] not in ("success", ""):
        return "⚠️"  # the job failed without any failing test: compilation, usage tests, setup, infrastructure
    return "✅"


def is_bad(digest):
    return status_icon(digest).startswith(("❌", "⚠️"))


def escape(text):
    return text.replace("|", "\\|")


def failure_rows(failed):
    rows = ["| Task | Test | Message |", "|---|---|---|"]
    for f in failed:
        test = escape(f"{f['class'].rsplit('.', 1)[-1]} › {f['name']}")
        rows.append(f"| `{f['task']}` | {test} | {escape(f['message'])} |")
    return rows


def pending(title, job):
    if job is not None and job["status"] != "completed":
        return [f"## {title}", "", "⏳ Still running.", ""]
    if job is not None and job["conclusion"] in ("skipped", "cancelled"):
        return [f"## {title}", "", f"{job_icon(job)} Not run ({job['conclusion']}).", ""]
    return [f"## {title}", "", "No digest: the job didn't get as far as writing one.", ""]


def section_tests(title, build, build_job):
    if build is None:
        return pending(title, build_job)
    suites, failed = build["suites"], build["failed"]
    if not suites:
        return [f"## {title}", "", "No results (the job didn't get as far as running the tests).", ""]
    t = totals(suites)
    icon = "❌" if t["failures"] else "✅"
    out = [f"## {title}", "", f"{icon} **{t['tests']}** tests, **{t['failures']}** failed, {t['skipped']} skipped", ""]
    if failed:
        out += failure_rows(failed)
        if t["failures"] > len(failed):
            out.append(f"| … | only the first {MAX_FAILURES} failures are listed | |")
        out.append("")
    out += ["<details><summary>Per task</summary>", "", "| Task | Tests | Failed | Skipped |", "|---|--:|--:|--:|"]
    for s in suites:
        out.append(f"| `{s['task']}` | {s['tests']} | {s['failures']} | {s['skipped']} |")
    return out + ["", "</details>", ""]


def matrix_column(dims):
    runtime = dims.get("runtime")
    if runtime == "js":
        return f"Node {dims.get('version')}"
    if runtime == "native":
        return "Native"
    return f"JDK {dims.get('version')} {dims.get('dist', '')}".strip()


def column_order(column):
    return ({"JDK": 0, "Native": 1, "Node": 2}[column.split()[0]], column)


# job names of test-extensively.yml, as GitHub renders matrix jobs: `test-jvm (ubuntu-latest, 17, oracle)`
MATRIX_JOB = re.compile(r"test-(jvm-8|jvm|native|js) \(([^)]*)\)$")


def matrix_jobs(jobs):
    """Test-extensively jobs of the live job list, keyed by (os, column) like the digests."""
    found = {}
    for job in jobs:
        match = MATRIX_JOB.search(job["name"])
        if match:
            kind, values = match.group(1), [v.strip() for v in match.group(2).split(",")]
            if kind == "jvm":
                dims = {"runtime": "jvm", "os": values[0], "version": values[1], "dist": values[2]}
            elif kind == "jvm-8":
                dims = {"runtime": "jvm", "os": values[0], "version": "8", "dist": "zulu"}
            elif kind == "native":
                dims = {"runtime": "native", "os": values[0]}
            else:
                dims = {"runtime": "js", "os": values[0], "version": values[1]}
            found[(dims["os"], matrix_column(dims))] = job
    return found


RUNTIMES = {"jvm": "JVM", "js": "JS (Node)", "native": "Kotlin/Native"}


def section_matrix(matrix, jobs):
    out = ["## Test extensively", ""]
    live = matrix_jobs(jobs)
    if not matrix and not live:
        return out + ["Not run (it only starts once `lint` and `build` succeed).", ""]
    cell = {(d["dims"]["os"], matrix_column(d["dims"])): d for d in matrix}
    keys = set(cell) | set(live)
    oses = sorted({os_name for os_name, _ in keys})
    columns = sorted({column for _, column in keys}, key=column_order)
    out += ["| OS | " + " | ".join(columns) + " |", "|---|" + "---|" * len(columns)]
    for os_name in oses:
        row = []
        for column in columns:
            if (os_name, column) in cell:
                row.append(status_icon(cell[(os_name, column)]))
            else:
                row.append(job_icon(live.get((os_name, column))) if (os_name, column) in live else " ")
        out.append(f"| **{os_name}** | " + " | ".join(row) + " |")
    out += [
        "",
        "✅ passed · ❌ n failing tests · ⚠️ job failed outside tests · ⏭️ skipped, inputs unchanged since a green run · "
        "⏳ running · ➖ not run · 🚫 cancelled",
        "",
    ]

    # Projections: for each dimension value, how many of its jobs went wrong. A value whose jobs ALL fail, while
    # others pass, points at that OS / runtime / version.
    projections = {
        "OS": lambda d: d["dims"]["os"],
        "Runtime": lambda d: RUNTIMES[d["dims"]["runtime"]],
        "Version": lambda d: matrix_column(d["dims"]).rsplit(" ", 1)[0] if d["dims"]["runtime"] == "jvm" else (
            matrix_column(d["dims"]) if d["dims"]["runtime"] == "js" else None
        ),
        "JDK distribution": lambda d: d["dims"].get("dist"),
    }
    bad_jobs = [d for d in matrix if is_bad(d)]
    if not bad_jobs:
        running = sum(1 for key, job in live.items() if key not in cell and job["status"] != "completed")
        done = f"All {len(matrix)} finished jobs passed (or were skipped as unchanged since a green run)"
        return out + [done + (f"; {running} still running." if running else "."), ""]
    out += ["| Dimension | Value | Jobs with problems |", "|---|---|---|"]
    suspects = []
    for dimension, key in projections.items():
        groups = defaultdict(list)
        for d in matrix:
            if key(d):
                groups[key(d)].append(d)
        for value, group in sorted(groups.items()):
            bad = sum(is_bad(j) for j in group)
            out.append(f"| {dimension} | {value} | {'🔴' if bad == len(group) else '🟠' if bad else '🟢'} {bad}/{len(group)} |")
            if bad and all(key(j) == value for j in bad_jobs) and len(groups) > 1:
                suspects.append(f"{dimension.lower()} **{value}**")
    out.append("")
    if suspects:
        out += ["> [!WARNING]", "> Every job with problems shares " + " and ".join(suspects) + ".", ""]

    by_test = defaultdict(list)
    for d in matrix:
        for f in d["failed"]:
            by_test[(f["task"], f["class"].rsplit(".", 1)[-1], f["name"])].append(
                f"{d['dims']['os']} / {matrix_column(d['dims'])}"
            )
    if by_test:
        out += ["| Task | Test | Failed on |", "|---|---|---|"]
        for (task, cls, name), where in sorted(by_test.items()):
            out.append(f"| `{task}` | {escape(cls)} › {escape(name)} | {', '.join(sorted(where))} |")
        out.append("")
    return out


def section_coverage(build, build_job, baseline):
    out = ["## Coverage (JVM)", ""]
    if build is None:
        return pending("Coverage (JVM)", build_job), []
    coverage = build.get("coverage")
    if not coverage:
        return out + ["No Kover report (it is only produced when every test passes).", ""], []
    base = (baseline or {}).get("coverage")
    total = pct(*coverage["total"])
    line = f"**{total:.1f}%** of lines"
    alerts = []
    if base:
        delta = total - pct(*base["total"])
        line += f" ({delta:+.2f} pp vs. base)"
        if delta < -COVERAGE_DROP_ALERT:
            alerts.append(f"Line coverage dropped by {-delta:.2f} percentage points ({pct(*base['total']):.1f}% → {total:.1f}%).")
    else:
        line += " (no baseline: the base branch has no successful run with a coverage digest yet)"
    out += [line, ""]
    if alerts:
        out += ["> [!CAUTION]", "> " + alerts[0], ""]
    out += ["<details><summary>Per package</summary>", "", "| Package | Lines | Coverage | Δ |", "|---|--:|--:|--:|"]
    base_packages = (base or {}).get("packages", {})
    for package, (covered, missed) in sorted(coverage["packages"].items()):
        delta = ""
        if package in base_packages:
            d = pct(covered, missed) - pct(*base_packages[package])
            delta = f"{'🔻' if d < -COVERAGE_DROP_ALERT else ''}{d:+.2f}"
        out.append(f"| `{package}` | {covered + missed} | {pct(covered, missed):.1f}% | {delta} |")
    return out + ["", "</details>", ""], alerts


def listing(summary, lines):
    if not lines:
        return []
    return [f"<details><summary>{summary}</summary>", "", "```", *lines, "```", "", "</details>", ""]


def section_lint(lint, job):
    if lint is None:
        return pending("Style (ktlint, detekt)", job)
    style = lint["style"]
    out = ["## Style (ktlint, detekt)", ""]
    icon = "✅" if lint["outcome"] in ("success", "") else "❌"
    out.append(f"{icon} ktlint: **{style['ktlint']['count']}** findings · detekt: **{style['detekt']['count']}** findings")
    if lint["dims"].get("autoformat") == "true":
        out.append("· formatting fixes were committed automatically")
    out.append("")
    for tool in ("ktlint", "detekt"):
        out += listing(f"{tool} findings", style[tool]["lines"])
    return out


def section_docs(docs, job):
    if docs is None:
        if job is None or job["conclusion"] == "skipped":
            return ["## Documentation", "", "➖ Not built: the documentation is only built after CI succeeds.", ""]
        return pending("Documentation", job)
    log = docs["log"]
    icon = "✅" if docs["outcome"] in ("success", "") else "❌"
    out = ["## Documentation", "", f"{icon} **{log['warnings']['count']}** warnings, **{log['errors']['count']}** errors", ""]
    out += listing("Errors", log["errors"]["lines"])
    out += listing("Warnings", log["warnings"]["lines"])
    return out


def section_release(release, job):
    if release is None:
        if job is None or job["conclusion"] == "skipped":
            return ["## Release", "", "➖ Not run: releases only happen after every check succeeds.", ""]
        return pending("Release", job)
    items = release["release"]
    if not items:
        return ["## Release", "", "No release this run (semantic-release found nothing to release).", ""]
    out = ["## Release", "", f"Version **{items['version']}**", "", "| Where | Released | Found there |", "|---|--:|--:|"]
    alerts = []
    places = (("Maven Central", items["maven"]), ("npm", items["npm"]), ("GitHub Packages (npm)", items["github_npm"]))
    for where, entries in places:
        found = sum(e["found"] for e in entries)
        out.append(f"| {where} | {len(entries)} | {'✅' if found == len(entries) else '⚠️'} {found} |")
        if found < len(entries):
            alerts.append(f"{len(entries) - found} of {len(entries)} {where} items of {items['version']} can't be found there")
    out.append(f"| GitHub release assets | {len(items['github'])} | |")
    out.append("")
    if alerts:
        out += ["> [!WARNING]", "> " + "; ".join(alerts) + " (Maven Central can lag behind by up to half an hour).", ""]
    missing = [e["name"] for _, entries in places for e in entries if not e["found"]]
    out += listing("Not found", missing[:MAX_LINES] + ([f"… and {len(missing) - MAX_LINES} more"] if len(missing) > MAX_LINES else []))
    out += listing("GitHub release assets", items["github"])
    return out


def find_job(jobs, suffix):
    return next((j for j in jobs if j["name"] == suffix or j["name"].endswith(" / " + suffix)), None)


def report(args):
    digests = [json.loads(p.read_text()) for p in sorted(Path(args.digests).glob("**/*.json"))] if Path(args.digests).exists() else []
    # one JSON object ({name, status, conclusion}) per line, as `gh api --paginate --jq '.jobs[]|...'` prints them
    jobs_file = Path(args.jobs) if args.jobs else None
    jobs = [json.loads(line) for line in jobs_file.read_text().splitlines() if line.strip()] if jobs_file and jobs_file.exists() else []
    by_kind = defaultdict(list)
    for digest in digests:
        by_kind[digest["kind"]].append(digest)
    build = next(iter(by_kind["build"]), None)
    matrix = by_kind["matrix"]
    baseline = json.loads(Path(args.baseline).read_text()) if args.baseline and Path(args.baseline).exists() else None
    build_job = find_job(jobs, "ci-cd / build")

    # the summary job itself (and the run-wide `success` gate) aren't part of the progress
    counted = [j for j in jobs if not j["name"].endswith(("summary", "success"))]
    finished = [j for j in counted if j["status"] == "completed"]
    problems = [j for j in finished if j["conclusion"] in ("failure", "timed_out", "cancelled")]
    if counted:
        state = "running" if len(finished) < len(counted) else ("failed" if problems else "passed")
        headline = f"{len(finished)}/{len(counted)} jobs finished" + (f", {len(problems)} with problems" if problems else "")
    else:
        state, headline = ("failed" if any(is_bad(d) for d in digests) else "passed"), f"{len(digests)} jobs reported"
    icon = {"running": "⏳", "failed": "❌", "passed": "✅"}[state]

    out = ["# CI summary", "", f"{icon} {headline}", ""]
    if jobs:
        overview = [("Style", "ci-cd / lint", "lint"), ("Build", "ci-cd / build", "build"), ("Docs", "docs / build", "docs"), ("Release", "ci-cd / release", "release")]
        cells = []
        for label, name, kind in overview:
            job, digest = find_job(jobs, name), next(iter(by_kind[kind]), None)
            if digest is not None and job is not None and job["status"] != "completed":
                # the job publishing this very update is still "in progress", though its digest is final
                icon_of_job = "✅" if digest["outcome"] in ("success", "") else "❌"
            else:
                icon_of_job = job_icon(job)
            cells.append(f"{icon_of_job} {label}")
        live = matrix_jobs(jobs).values()
        if live:
            done = sum(1 for j in live if j["status"] == "completed")
            cells.insert(2, f"{'⏳' if done < len(live) else '❌' if any(j['conclusion'] == 'failure' for j in live) else '✅'} Test extensively {done}/{len(live)}")
        out += [" · ".join(cells), ""]
    scans = [s for d in digests for s in d["scans"]]
    if scans:
        out += ["**Gradle scans** (published for failed builds only): " + " · ".join(f"[{i + 1}]({s})" for i, s in enumerate(scans)), ""]

    out += section_lint(next(iter(by_kind["lint"]), None), find_job(jobs, "ci-cd / lint"))
    out += section_matrix(matrix, jobs)
    out += section_tests("Tests (build job)", build, build_job)
    coverage_section, alerts = section_coverage(build, build_job, baseline)
    out += coverage_section
    out += section_docs(next(iter(by_kind["docs"]), None), find_job(jobs, "docs / build"))
    out += section_release(next(iter(by_kind["release"]), None), find_job(jobs, "ci-cd / release"))
    Path(args.summary).open("a").write("\n".join(out) + "\n")
    if args.title_out:
        Path(args.title_out).write_text(f"{icon} {headline}")

    if not args.annotate:
        return
    annotations = 0
    for d in [build, *matrix]:
        for f in (d or {}).get("failed", []):
            if annotations == MAX_ANNOTATIONS:
                break
            where = f" ({d['dims']['os']} / {matrix_column(d['dims'])})" if d["kind"] == "matrix" else ""
            print(f"::error title=Test failed{where}: {f['task']} › {f['name']}::{f['message']}")
            annotations += 1
    for alert in alerts:
        print(f"::warning title=Coverage dropped::{alert}")


# ---------------------------------------------------------------------------------------------------- publish


def publish(args):
    """Updates the "CI summary" check run with the rendered summary (completing it if a conclusion is given)."""
    text = Path(args.summary).read_text()
    if len(text) > MAX_CHECK_RUN_TEXT:
        text = text[:MAX_CHECK_RUN_TEXT] + "\n\n… truncated: see the run summary page for the rest."
    payload = {"output": {"title": Path(args.title).read_text().strip()[:200], "summary": text}}
    if args.conclusion:
        payload.update(status="completed", conclusion=args.conclusion)
    repository = os.environ["GITHUB_REPOSITORY"]
    subprocess.run(
        ["gh", "api", "--method", "PATCH", f"repos/{repository}/check-runs/{args.check_run}", "--input", "-"],
        input=json.dumps(payload),
        text=True,
        check=True,
        stdout=subprocess.DEVNULL,
    )


def main():
    parser = argparse.ArgumentParser()
    commands = parser.add_subparsers(dest="command", required=True)
    c = commands.add_parser("collect")
    c.add_argument("--out", required=True)
    c.add_argument("--kind", choices=("build", "matrix", "lint", "docs", "release"), required=True)
    c.add_argument("--dim", action="append", default=[])
    c.add_argument("--outcome", default="")
    c.add_argument("--log")
    c.add_argument("--version", default="")
    r = commands.add_parser("report")
    r.add_argument("--digests", required=True)
    r.add_argument("--jobs")
    r.add_argument("--baseline")
    r.add_argument("--summary", required=True)
    r.add_argument("--title-out")
    r.add_argument("--annotate", action="store_true")
    p = commands.add_parser("publish")
    p.add_argument("--check-run", required=True)
    p.add_argument("--summary", required=True)
    p.add_argument("--title", required=True)
    p.add_argument("--conclusion", choices=("success", "failure", "neutral", "cancelled"))
    args = parser.parse_args()
    {"collect": collect, "report": report, "publish": publish}[args.command](args)


if __name__ == "__main__":
    sys.exit(main())
