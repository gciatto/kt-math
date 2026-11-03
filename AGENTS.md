# kt-math — agent notes

Kotlin Multiplatform (JVM, JS, native) library: a port of Java's `BigInteger`/`BigDecimal` (the root project), plus
the `big-math` module (a port of big-math's `BigDecimalMath`, see below). Build logic comes from the
`io.github.gciatto.kt-mpp.*` plugins (see `build.gradle.kts`), configured via `gradle.properties`.

## The big-math module

- `big-math/` (artifact and npm package `kt-math-big-math`, package `org.gciatto.kt.math.big`): `BigDecimalMath` and
  the series calculators of big-math 2.3.2 (MIT, Eric Obermühlner; the license header stays in every ported file), on
  kt-math's multiplatform API. Ported line by line, with the original names: keep it diffable. big-math's
  `BigRational` lives in the core module (`org.gciatto.kt.math.BigRational`, ported the same way), so that
  `BigFactory` and the conversions can build it; `TestAgainstBigMath` still compares it with big-math's.
- Mapping notes: `BigDecimal.valueOf(Double)` (core) is `java.math.BigDecimal.valueOf(double)` (Java's
  `Double.toString` scale) on every platform; kt-math's `div(_, mc)`/`round(mc)` are nullable, hence `divide`/`rounded`; big-math's
  `synchronized` caches use `CacheLock` (`synchronized` on the JVM, a non-reentrant spin lock elsewhere: never fill a
  cache while holding its own lock, never take the locks in a cycle).
- Tests: `TestAgainstBigMath` (jvmTest) compares every function with big-math itself (`libs.big.math`), results and
  exception types; both libraries keep caches, filled in the same order since each case runs on big-math first. Avoid
  inputs that make both compute huge powers (e.g. `root(x, 1E-10)`): they run for minutes. `TestKnownValues`
  (commonTest) runs on every platform.
- Build: the root `build.gradle.kts` configures the native targets, the `nonJvm` source-set group and the `detektAll`
  extension for every multiplatform project (`allprojects`); `big-math/build.gradle.kts` applies the kt-mpp plugins
  without versions (already on the classpath), copies group/version from the root (kt-mpp's `versions` plugin can't
  be applied to subprojects), and prefixes its Maven artifact ids with `kt-math-`. Root task names (`check`,
  `detektAll`, `publishAllPublicationsToProjectLocalRepository`, ...) run in both projects.

## Code layout

- `src/commonMain`: the actual implementation (`CommonBigInteger`, `CommonBigDecimal`, `MutableBigInteger`, ...),
  ported almost line-by-line from the JDK. Keep that structure (and JDK-style names, e.g. `` `val` ``
  parameters) when touching ported code: it is what makes diffing against the JDK sources possible.
- `src/jvmMain`: adapters delegating to `java.math` (`JavaBigIntegerAdapter`, `JavaBigDecimalAdapter`) and the
  `actual` factories (`JvmUtils.kt`); `src/nonJvmMain` (shared by JS and every native target, a custom group of
  the default hierarchy template in `build.gradle.kts`): the `actual` factories using the Kotlin implementation
  (`NonJvmUtils.kt`); `src/jsMain` and `src/nativeMain`: the few platform specifics (logging, casts, `lock`), plus
  the `@JsExport`ed JS entry points (`Factory.kt`).
- Targets: JVM, JS, and every non-deprecated Kotlin/Native target (listed in `build.gradle.kts`).
- `BigInteger` implementations: `java.math` on the JVM (`JavaBigIntegerAdapter`), native JS BigInts on JS
  (`JsBigInteger`, `src/jsMain`), `CommonBigInteger` on native targets. `BigDecimal`: `java.math` on the JVM, the
  Kotlin `CommonBigDecimal` elsewhere, which works with *any* `BigInteger` through its interface (created with
  `BigInteger.of`, so native BigInts on JS): it must not use `CommonBigInteger` internals (the JDK code it's ported
  from relies on `BigInteger` package-private methods and `MutableBigInteger`; their interface-based equivalents
  are at the top of `CommonBigDecimal.kt`). On the JVM it's only used by tests and the Java 8 `sqrt` fallback, on top
  of `java.math`-backed `BigInteger`s: don't mix in `CommonBigInteger`s there.
- Creating numbers: the companions (`BigInteger.of`, `BigDecimal.of`/`valueOf`, `BigRational.valueOf`), gathered (plus
  `parse*OrNull` variants) by the `BigFactory` interface, whose `BigFactory.default` delegates to them; conversions
  among the three are members (`toBigDecimal`, `toBigRational`, `toBigInteger`), and `Conversions.kt` has
  `toBigInteger`/`toBigDecimal`/`toBigRational` extensions on Kotlin numbers and strings. A new factory method goes in
  the companion, `BigFactory`, `DefaultBigFactory` and `TestBigFactory`.
- `BigInteger`/`BigDecimal` are `@JsExport`ed interfaces: any change to them is a change of the public JS API too.

## Gradle tasks: known task names, don't rediscover them

Don't run `./gradlew tasks --all`; these names (from `.github/workflows/`, the authoritative CI source) are stable:

- **Style**: `ktlintFormat` (auto-fixes), `ktlintCheck` (verifies only), `detektAll`. Use `detektAll`, **not**
  `detekt`: in a multiplatform project the plain `detekt` task has no sources (`NO-SOURCE`) and silently checks
  nothing, while `check` (CI's build job) runs every per-source-set detekt task. CI runs `ktlintFormat` first,
  then `ktlintCheck detektAll`, and commits formatting fixes: mirror that order locally.
- **Compile without running tests**: `jvmMainClasses`/`jvmTestClasses`, `jsMainClasses`/`jsTestClasses`.
- **Test**: `jvmTest`, `jsTest`; `check` runs style + tests + everything else wired into it.
- **Docs**: `dokkaGenerateHtml` (per module); the website is `:documentation:assembleSite` (MkDocs Material pages
  from `documentation/docs/` plus the aggregated Dokka API under `/api/`; needs `pip install mkdocs-material`), built
  by `.github/workflows/docs.yml` on every run and published on GitHub Pages from `master`. Its code examples are
  quoted from the usage tests (`--8<-- [start:x]` markers in `TestKotlinUsage`, `TestJavaUsage`, `usage.test.cjs`,
  `usage.c`, big-math's `TestKnownValues`): keep the markers when editing those tests. `mkdocs build --strict` fails on
  broken links and missing snippets. **Publishing dry-run**: `publishAllPublicationsToProjectLocalRepository
  zipMavenCentralPortalPublication releaseMavenCentralPortalPublication --dry-run`.
- Default to `--parallel --continue` (as CI does): one invocation collects all failures.

## Testing

- On the JVM, `BigInteger.of`/`BigDecimal.of` delegate to `java.math` (`JavaBigIntegerAdapter`/`JavaBigDecimalAdapter`),
  so the common implementation only runs in JS tests and in `src/jvmTest/.../TestAgainstJavaMath.kt`. That test runs
  every operation through the shared interfaces on both `Common*` and the adapters, and compares results and
  exception types; failures are reported as one example per operation. Extend it (inputs or operations) rather than
  writing hand-picked expectations. The file holds a `sealed` base class (helpers, shared inputs) and one subclass per
  type, to stay under detekt's `LargeClass`/`LongMethod` limits.
- Expectations come from the JDK running the tests, and CI runs 17, 21 and 25: the port follows JDK 17 (the
  `sqrt` algorithm, exponent limits in string parsing), so cases where JDK versions disagree among themselves are skipped
  or compared by value (e.g. `movePointLeft(0)` on negative scales, the scale of exact `sqrt` results; JDK 26+
  computes `sqrt` via `rootn` and parses exponents into a `long` scale). Run `jvmTest` with
  `-Dorg.gradle.java.home=<newer JDK>` too when touching those operations.
- On JS and native targets, the same kind of comparison runs from a file: `generateDifferentialCases` (a `JavaExec` of
  `src/jvmTest/.../GenerateDifferentialCases.kt`) runs the named operations of `src/commonTest/.../DifferentialOps.kt`
  on the JVM (i.e. on `java.math`) and writes cases and outcomes to `build/differential/cases.tsv`; `jsNodeTest`
  and the native test tasks depend on it and get the path via `KT_MATH_DIFFERENTIAL_CASES`, and `TestDifferentialCases`
  (`src/nonJvmTest`) replays every case on the Kotlin implementation. To cover a new operation there, add it to
  `DifferentialOps` and its inputs to the generator. Outcomes are rendered identically on every platform: operations
  return `Double`/`Float`/`Char` as bits/codes.
- JS pitfalls behind the JS-only failures found so far: `Long` is a BigInt (`-Xes-long-as-bigint`), so `Long` division
  by zero throws a native `RangeError` (not an `ArithmeticException`; the JDK code sometimes relies on it), and
  `Int.toLong()` of a non-integer number throws too; `Int` division by zero silently yields 0; on a `dynamic`, `is Int`
  is true for every JS number. Catch `Throwable`, not `Exception`, to see native errors. Kotlin has no BigInt type:
  `JsBigInteger` stores a `dynamic`, whose arithmetic operators compile to JS ones, and uses `js()` for the rest; `js()`
  supports neither BigInt literals (`0n`: use `BigInt(0)`) nor `**`.
- Usage tests: the API as other languages see it, part of `check`: `src/jvmTest/java` (JUnit 4, compiled with the
  Kotlin JVM tests: needs `@JvmStatic`/`@JvmField`/`@JvmOverloads` where Java would otherwise see `Companion` or miss
  default arguments), `src/jsUsageTest` (`jsUsageTest`: Node's test runner on the npm package from
  `assembleJsPackage`; `Comparable` isn't exported to JS, hence the `compare` members, `compareTo` in JS; it first
  runs `tsUsageTest`, which type-checks `usage.ts` against the package's `kt-math.d.ts` with Kotlin's own `tsc`), and
  `src/nativeUsageTest/c` (`nativeUsageTest`: the host's `cc` against the Kotlin/Native shared library and header of
  linuxX64 or macosArm64; overloads get trailing underscores in C, so adding an overload may rename others there).
  Without Xcode, cross-link `linkKtmathDebugSharedLinuxX64` and build the C file in a container (e.g. `gcc` image).
- Coverage: `./gradlew jvmTest koverXmlReport` (or `koverHtmlReport`), reports under `build/reports/kover/`. JVM-only:
  JS coverage is not measured.
- Bug patterns found in the port, worth grepping for when something diverges from the JDK: Java expressions with
  side effects inside indices (`a[--i] = b[i]` evaluates `--i` first), post- vs pre-increment results, `System.arraycopy`
  replaced by loops (must handle overlapping ranges: use `copyInto`), `!!` on fields the JDK allows to be null,
  Java `assert`s ported as `require` (always on in Kotlin), algorithm thresholds tested on the wrong operand (results
  stay right, but zero/edge cases take another path), and copy-paste typos (`isInfinite() || isInfinite()`).
- `BigInteger.rem`/`%` is the remainder (sign of the dividend) on every platform; Java's `mod` is the internal
  `CommonBigInteger.mod`.

## Native targets

- Native tests run only on matching hosts: `linuxX64Test` on Linux x64, `mingwX64Test` on Windows, `macosArm64Test` and
  the `*SimulatorArm64Test` tasks on macOS, where linking needs a full Xcode (not just the command line tools). CI's
  `test-extensively` runs them all; locally, without Xcode, exclude the Apple test and `linkDebugTest*` tasks from
  `check` with `-x`.
- Kotlin/Native doesn't run on Linux ARM64 hosts at all. On a Mac without Xcode, a Linux test binary can still be
  cross-linked (`./gradlew linkDebugTestLinuxArm64`) and run in a container, passing the differential cases:
  `docker run --rm -e KT_MATH_DIFFERENTIAL_CASES=/c.tsv -v $PWD/build/differential/cases.tsv:/c.tsv -v
  $PWD/build/bin/linuxArm64/debugTest:/t ubuntu /t/test.kexe` (with colima, mount paths under the home directory).
- Apple klibs are cross-compiled on Linux (Kotlin's default), so releases keep running on Ubuntu; CI's build job
  runs `assemble`, which builds every klib and shared-metadata jar as publishing does (without signing). Run it
  locally too when touching source sets: the metadata compilations aren't part of `check`.
- kt-mpp's `detektAll` only covers JVM and JS: `build.gradle.kts` adds `detektLinuxX64Main`/`Test`, which cover
  `commonMain`, `nonJvmMain` and `nativeMain`.

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

## Java 8 compatibility

The JVM target is Java 8 (`jvm = "1.8"` in the version catalog, `-Xjdk-release=1.8` in `ktCompilerArgsJvm`), so the
compiler rejects JDK 9+ APIs in main *and* test code. Where `java.math` only has a JDK 9+ method (`sqrt`,
`sqrtAndRemainder`, `BigInteger(signum, bytes, off, len)`), the JVM adapters use the Kotlin implementation, and
tests reach the JDK method by reflection (`jdk9(...)` in `TestAgainstJavaMath`). Run the JVM tests on Java 8 with
`./gradlew jvmTest -PtestJvmVersion=8` (Gradle itself still needs JDK 17+; the JDK 8 comes from the foojay resolver,
as Azul Zulu, which exists for every OS/architecture); CI does it in `test-extensively`.

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
