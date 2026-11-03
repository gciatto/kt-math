# Kt-Math

[![Maven Central](https://img.shields.io/maven-central/v/io.github.gciatto/kt-math?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.gciatto/kt-math)
[![npm](https://img.shields.io/npm/v/kt-math?label=npm)](https://www.npmjs.com/package/kt-math)
[![CI/CD](https://github.com/gciatto/kt-math/actions/workflows/dispatcher.yml/badge.svg)](https://github.com/gciatto/kt-math/actions/workflows/dispatcher.yml)
[![Documentation](https://img.shields.io/badge/docs-gciatto.github.io%2Fkt--math-blueviolet)](https://gciatto.github.io/kt-math/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Platforms](https://img.shields.io/badge/platforms-JVM%20%7C%20JS%20%7C%20Native%20%7C%20C-informational)](https://gciatto.github.io/kt-math/reference/platforms/)
[![License](https://img.shields.io/badge/license-GPL--2.0%20with%20Classpath%20Exception-blue)](LICENSE)

**Arbitrary-precision integers, decimals and rationals for Kotlin Multiplatform**: the API of Java's
`java.math.BigInteger` and `java.math.BigDecimal`, plus `BigRational` and a library of mathematical functions, on the
JVM, JavaScript and every Kotlin/Native target, usable from Kotlin, Java, JavaScript and C.

```kotlin
import org.gciatto.kt.math.*

val x = BigInteger.of("123456789012345678901234567890")
x * 42.toBigInteger()                               // 5185185138518518513851851851380
"1.10".toBigDecimal().div(3.toBigDecimal(), MathContext(10)) // 0.3666666667
("1/3".toBigRational() + BigRational.valueOf(1, 6)).reduce() // 1/2
```

## Why Kt-Math

Kotlin's standard library has no arbitrary-precision numbers outside the JVM. Kt-Math brings `java.math`'s well-known
API to common Kotlin code, so the same code computes with big numbers everywhere, and gets the same results:

- **The `java.math` API, everywhere.** `BigInteger` and `BigDecimal` mirror their Java namesakes (arithmetic, bit
  operations, `modPow`, primality, scales, `MathContext`, the eight `RoundingMode`s, `sqrt`, ...), with Kotlin
  operators on top.
- **The fastest implementation of each platform.** On the JVM, Kt-Math delegates to `java.math` itself; on JavaScript,
  `BigInteger`s are native `BigInt`s; on Kotlin/Native, a line-by-line port of the JDK, with C kernels for the hot
  loops of multiplication and `modPow`.
- **The same results on every platform.** The Kotlin port is tested *differentially*: thousands of operations run on
  `java.math` on the JVM, and every platform must reproduce their results, and their exceptions, exactly.
- **More than `java.math`.** Exact `BigRational`s; `BigFactory`, with parsing functions returning `null` on invalid
  input; conversions among all number types; and, in `org.gciatto.kt.math.big`, logarithms, powers, roots,
  trigonometric and hyperbolic functions, gamma, π and *e* at any precision, complex numbers and decimal ranges (a
  port of [big-math](https://github.com/eobermuhlner/big-math)).
- **Four languages.** An idiomatic Kotlin API, Java-friendly static factories (Java 8+), an npm package with JS names
  for every overload, and a C API through Kotlin/Native shared libraries.

## Getting started

| | Coordinates |
|---|---|
| Gradle (Kotlin Multiplatform or JVM) | `implementation("io.github.gciatto:kt-math:VERSION")` |
| Maven | `io.github.gciatto:kt-math-jvm:VERSION` |
| npm | `npm install kt-math` |

Then follow a tutorial for [Kotlin](https://gciatto.github.io/kt-math/tutorials/getting-started-kotlin/),
[Java](https://gciatto.github.io/kt-math/tutorials/getting-started-java/) or
[JavaScript](https://gciatto.github.io/kt-math/tutorials/getting-started-js/), or see how to
[call Kt-Math from C](https://gciatto.github.io/kt-math/how-to/call-kt-math-from-c/). The
[API reference](https://gciatto.github.io/kt-math/api/) documents every class and function.

## Platforms

| Platform | Targets | `BigInteger` | `BigDecimal` |
|---|---|---|---|
| JVM | Java 8+ | `java.math.BigInteger` | `java.math.BigDecimal` |
| JavaScript | Node.js, browsers | native `BigInt` | Kotlin port, on `BigInt`s |
| Kotlin/Native | macOS, iOS, watchOS, tvOS, Linux, Windows, Android NDK: every non-deprecated target | Kotlin port, with C kernels | Kotlin port |

`BigRational`, `BigComplex` and `BigDecimalMath` are written in Kotlin on top of the platform's numbers. See the
[platforms reference](https://gciatto.github.io/kt-math/reference/platforms/) for the full list of targets.

## Performance

Average time per operation, as printed by `./gradlew benchmark` (JVM 17, Node.js 22, and a Kotlin/Native
`linuxArm64` release binary in a Linux container, all on the same Apple Silicon laptop; lower is better):

| Operation | JVM (`java.math`) | JavaScript (`BigInt`) | Kotlin/Native |
|---|---:|---:|---:|
| 2048 × 2048 bits product | 1.1 µs | 10.9 µs | 1.2 µs |
| 8192 × 8192 bits product | 16.8 µs | 70.7 µs | 29.1 µs |
| 8192 / 2048 bits division | 43.6 µs | 25.7 µs | 33.5 µs |
| 8192 bits `toString` | 125 µs | 44 µs | 160 µs |
| 2048 bits `modPow` | 3.0 ms | 11.7 ms | 4.7 ms |
| 4096 bits `modPow` | 23.8 ms | 65.5 ms | 37.4 ms |
| `fibonacci(10000)` (additions) | 2.1 ms | 0.7 ms | 3.0 ms |
| `1000!` (products by small integers) | 262 µs | 113 µs | 319 µs |
| decimal division, 100 digits | 1.2 µs | 2.7 µs | 1.8 µs |
| decimal `sqrt`, 1000 digits | 594 µs | 782 µs | 778 µs |

On Kotlin/Native, the C kernels make multiplication and `modPow` up to 10 times faster than the plain Kotlin port.
Run `./gradlew benchmark` to reproduce the numbers on your machine.

## Development

Kt-Math is built with Gradle (JDK 17+); `./gradlew check` runs the style checks and the tests the host can run, and
`./gradlew :documentation:assembleSite` builds the [website](https://gciatto.github.io/kt-math/). See
[how to contribute](https://gciatto.github.io/kt-math/how-to/contribute/) and `AGENTS.md` for the details. Releases
are automatic, from [conventional commits](https://www.conventionalcommits.org/).

## License

Kt-Math is a port of OpenJDK's `java.math`, hence distributed under the same license: the
[GNU General Public License, version 2, with the Classpath Exception](LICENSE), which allows linking it into software
under any license. The `org.gciatto.kt.math.big` package and `BigRational` port [big-math](https://github.com/eobermuhlner/big-math),
under the MIT license.
