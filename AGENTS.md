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
- On JS, the same kind of comparison runs from a file: `generateDifferentialCases` (a `JavaExec` of
  `src/jvmTest/.../GenerateDifferentialCases.kt`) runs the named operations of `src/commonTest/.../DifferentialOps.kt`
  on the JVM (i.e. on `java.math`) and writes cases and outcomes to `build/differential/cases.tsv`; `jsNodeTest`
  depends on it, gets the path via `KT_MATH_DIFFERENTIAL_CASES`, and `TestDifferentialCases` replays every case on the
  common implementation. To cover a new operation on JS, add it to `DifferentialOps` and its inputs to the generator.
  Outcomes are rendered identically on both platforms: operations return `Double`/`Float`/`Char` as bits/codes.
- JS pitfalls behind the JS-only failures found so far: `Long` is a BigInt (`-Xes-long-as-bigint`), so `Long` division
  by zero throws a native `RangeError` (not an `ArithmeticException`; the JDK code sometimes relies on it), and
  `Int.toLong()` of a non-integer number throws too; `Int` division by zero silently yields 0; on a `dynamic`, `is Int`
  is true for every JS number. Catch `Throwable`, not `Exception`, to see native errors.
- Coverage: `./gradlew jvmTest koverXmlReport` (or `koverHtmlReport`), reports under `build/reports/kover/`. JVM-only:
  JS coverage is not measured.
- Bug patterns found in the port, worth grepping for when something diverges from the JDK: Java expressions with
  side effects inside indices (`a[--i] = b[i]` evaluates `--i` first), post- vs pre-increment results, `System.arraycopy`
  replaced by loops (must handle overlapping ranges: use `copyInto`), `!!` on fields the JDK allows to be null,
  Java `assert`s ported as `require` (always on in Kotlin), algorithm thresholds tested on the wrong operand (results
  stay right, but zero/edge cases take another path), and copy-paste typos (`isInfinite() || isInfinite()`).
- `BigInteger.rem`/`%` is the remainder (sign of the dividend) on every platform; Java's `mod` is the internal
  `CommonBigInteger.mod`.

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
