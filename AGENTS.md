# kt-math — agent notes

Single-module Kotlin Multiplatform (JVM + JS) library: a port of Java's `BigInteger`/`BigDecimal`. Build logic
comes from the `io.github.gciatto.kt-mpp.*` plugins (see `build.gradle.kts`), configured via `gradle.properties`.

## Code layout

- `src/commonMain`: the actual implementation (`CommonBigInteger`, `CommonBigDecimal`, `MutableBigInteger`, ...),
  ported almost line-by-line from the JDK. Keep that structure (and JDK-style names, e.g. `` `val` ``
  parameters) when touching ported code: it is what makes diffing against the JDK sources possible.
- `src/jvmMain`: adapters delegating to `java.math` (`JavaBigIntegerAdapter`, `JavaBigDecimalAdapter`) and the
  `actual` factories (`JvmUtils.kt`); `src/jsMain`: the JS `actual` factories (`JsUtils.kt`) and the
  `@JsExport`ed entry points (`Factory.kt`).
- `BigInteger`/`BigDecimal` are `@JsExport`ed interfaces: any change to them is a change of the public JS API too.

## Gradle tasks: known task names, don't rediscover them

Don't run `./gradlew tasks --all`; these names (from `.github/workflows/`, the authoritative CI source) are stable:

- **Style**: `ktlintFormat` (auto-fixes), `ktlintCheck` (verifies only), `detektAll`. Use `detektAll`, **not**
  `detekt`: in a multiplatform project the plain `detekt` task has no sources (`NO-SOURCE`) and silently checks
  nothing, while `check` (CI's build job) runs every per-source-set detekt task. CI runs `ktlintFormat` first,
  then `ktlintCheck detektAll`, and commits formatting fixes: mirror that order locally.
- **Compile without running tests**: `jvmMainClasses`/`jvmTestClasses`, `jsMainClasses`/`jsTestClasses`.
- **Test**: `jvmTest`, `jsTest`; `check` runs style + tests + everything else wired into it.
- **Docs**: `dokkaGenerateHtml`. **Publishing dry-run**: `publishAllPublicationsToProjectLocalRepository
  zipMavenCentralPortalPublication releaseMavenCentralPortalPublication --dry-run`.
- Default to `--parallel --continue` (as CI does): one invocation collects all failures.

## Keeping Gradle output out of your context

- Use `--console=plain` when capturing output non-interactively, redirect to a file and grep for the outcome:
  `./gradlew check --console=plain --continue > /tmp/gradle.log 2>&1; grep -B2 -A20 "FAILED\|^e: " /tmp/gradle.log`.
- Read lint findings from the reports instead of the console:
  `build/reports/ktlint/ktlint<SourceSet>SourceSetCheck/ktlint<SourceSet>SourceSetCheck.txt` and
  `build/reports/detekt/{main,test}.txt`. The detekt reports are shared by all platform source sets, so they only
  hold the **last** one that ran: run a single task (e.g. `detektJsMain`) to read its findings.
- `-q` hides compiler errors on failure: prefer `--console=plain` + grep when a task may fail.
- Dependency resolution may hit Maven Central rate limits (HTTP 429): the Google mirror of Maven Central is
  declared before it in both `settings.gradle.kts` and `build.gradle.kts` for that reason. Don't remove it, and
  don't add `google()` to the project repositories: it answers 403 for non-Android artifacts, which stops
  Gradle from trying the next repository.

## Compiler warnings are errors

`allWarningsAsErrors=true`: every Kotlin warning breaks the build, so Kotlin upgrades often surface new ones.
Fix them in code rather than suppressing them; suppress only when the fix would change the public API, and say
why. Things already learned:

- In the ported code, `==` between arrays was Java reference equality: replace it with `===`, not
  `contentEquals`, to preserve semantics.
- Redundant conversions (`x.toLong()` on a `Long`) are flagged: just drop them.
- KT-83009 (`@JsExport`ed interface exposing non-exported supertype `Comparable`) is suppressed on
  `BigInteger`/`BigDecimal` via `@Suppress("EXPOSED_NOT_EXPORTED_SUPER_INTERFACE_WARNING")`: fixing it would mean
  dropping `Comparable`, a breaking change. For deprecation-style diagnostics (the ones saying "will become an
  error"), the suppressible name carries a `_WARNING` suffix.
- JVM compiler flags live in `gradle.properties` (`ktCompilerArgsJvm`, `ktCompilerArgsJs`, `ktCompilerArgs`),
  `;`-separated.

## detekt

Config: `.detekt.yml` (deltas over the defaults).

- **JDK compatibility**: detekt's embedded compiler can crash with a bare version-number error on JDKs newer
  than it supports (seen with JDK 25/26). Run it on JDK 21–22 then:
  `./gradlew detektAll -Dorg.gradle.java.home=<path-to-a-JDK-21-or-22>`.
- **`MagicNumber` exemptions**: `const val` at file/class scope is exempt; a plain `val` at top level or in a
  class body is not; the same `val` inside a `private companion object` is.
- **`UnreachableCode` false positive**: two or more sequential `?: return` / `if (...) return` guards get flagged.
  Compute the nullable values first, then use one combined `if (a == null || b == null) return`, keeping as
  separate early returns any guard protecting an expression that may *throw* rather than return null.
- **`TooManyFunctions`**: overridden functions are ignored (the adapters implement the large public
  interfaces); the `actual` factory files suppress it file-wide, since their size is dictated by `Utils.kt`'s
  `expect` declarations.

## Publishing

- `org.danilopianini:publish-on-central` is force-pinned to 9.2.4 in `build.gradle.kts` (and frozen in
  `renovate.json`): newer versions crash the Maven Central release (DanySK/publish-on-central#1804). Same pin as
  tuProlog/2p-kt; lift both together.
- Releases are done by semantic-release in CI (`release.config.js`), from conventional commits: write commit
  messages accordingly (`fix:`, `feat:`, `chore:`, ...), since they determine the next version.
