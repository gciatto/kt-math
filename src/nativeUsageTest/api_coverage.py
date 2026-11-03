"""Checks that usage.c covers the whole C API: every function of the kt-math part of the header Kotlin/Native
generates (libktmath_api.h) must be called in usage.c by its full path, as KT_MATH.<path>(...), e.g.
KT_MATH.BigInteger.plus(...) or KT_MATH.BigDecimal.Companion.of_(...).

Usage: api_coverage.py <libktmath_api.h> <usage.c>: prints the uncovered functions, fails if any."""
import re
import sys

UNCALLABLE_MARK = "<uncallable>"
HELPERS = {"_type", "_instance"}  # lookup helpers of every class, not API
# Kotlin types C code can't create a value of: functions taking them can't be called from C
UNCALLABLE = ("IntArray", "kotlin_random_Random", "kotlin_ranges_ClosedRange", "kotlin_ranges_OpenEndRange",
              "kotlin_Array ")


def api(header):
    """The paths of the functions under kotlin.root.org.gciatto.kt.math, e.g. BigInteger.Companion.of."""
    lines = open(header).read().split("/* User functions. */", 1)[1].split("\n")
    stack = [[]]  # the paths of the functions in each open struct, relative to it
    for line in lines:
        if re.match(r"\s*struct \{", line):
            stack.append([])
        elif (m := re.match(r"\s*\} (\w+);", line)) and len(stack) > 1:
            inner = stack.pop()
            stack[-1] += [[m.group(1)] + path for path in inner]
        elif m := re.match(r"[^(]*\(\*(\w+)\)\((.*)\);", line):
            if any(t in m.group(2) for t in UNCALLABLE):
                stack[-1].append([m.group(1), UNCALLABLE_MARK])
            else:
                stack[-1].append([m.group(1)])
    prefix = ["kotlin", "root", "org", "gciatto", "kt", "math"]
    functions = [path[len(prefix):] for path in stack[0] if path[: len(prefix)] == prefix and path[-1] not in HELPERS]
    callable_ = [".".join(path) for path in functions if path[-1] != UNCALLABLE_MARK]
    uncallable = [".".join(path[:-1]) for path in functions if path[-1] == UNCALLABLE_MARK]
    return callable_, uncallable


def main(header, source):
    code = open(source).read()
    functions, uncallable = api(header)
    for f in uncallable:
        print(f"  skipped: KT_MATH.{f} (a parameter of a type C can't create)")
    missing = [f for f in functions if not re.search(r"KT_MATH\." + re.escape(f) + r"\(", code)]
    print(f"{len(functions) - len(missing)}/{len(functions)} functions of the C API called in {source}")
    for f in missing:
        print(f"  not called: KT_MATH.{f}")
    return 1 if missing else 0


if __name__ == "__main__":
    sys.exit(main(*sys.argv[1:]))
