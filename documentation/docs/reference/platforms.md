# Platforms

## Targets

| Platform | Targets |
|---|---|
| JVM | Java 8 or later |
| JavaScript | Node.js and browsers (IR compiler) |
| Kotlin/Native, Apple | `macosArm64`, `iosArm64`, `iosX64`, `iosSimulatorArm64`, `watchosArm32`, `watchosArm64`, `watchosDeviceArm64`, `watchosSimulatorArm64`, `tvosArm64`, `tvosSimulatorArm64` |
| Kotlin/Native, Linux | `linuxX64`, `linuxArm64` |
| Kotlin/Native, Windows | `mingwX64` |
| Kotlin/Native, Android NDK | `androidNativeArm32`, `androidNativeArm64`, `androidNativeX86`, `androidNativeX64` |

Every Kotlin/Native target is supported, except the ones Kotlin deprecated (`macosX64`, `watchosX64`, `tvosX64`).

## Implementations

`BigInteger.of`, `BigDecimal.of` and `BigFactory.default` create the fastest implementation of each platform:

| Platform | `BigInteger` | `BigDecimal` |
|---|---|---|
| JVM | `java.math.BigInteger` (adapter) | `java.math.BigDecimal` (adapter) |
| JavaScript | native `BigInt` | Kotlin port of `java.math.BigDecimal`, on `BigInt`-backed `BigInteger`s |
| Kotlin/Native | Kotlin port of `java.math.BigInteger`; C kernels for multiplication, squaring and Montgomery multiplication (`modPow`) on 64-bit targets | Kotlin port of `java.math.BigDecimal` |

`BigRational` is the same Kotlin class everywhere, on top of the platform's `BigDecimal`s and `BigInteger`s.

## Languages

| Language | How |
|---|---|
| Kotlin | any target, `org.gciatto.kt.math` package |
| Java | the JVM artifact; factories are static methods, Kotlin extensions are in the `Conversions` class |
| JavaScript | the `kt-math` npm package; overloads have distinct names (see the `@JsName` annotations in the API reference) |
| C | a Kotlin/Native shared library and header, prebuilt for Linux, macOS and Windows in the GitHub releases: see [Getting started in C](../tutorials/getting-started-c.md) and [Call Kt-Math from C](../how-to/call-kt-math-from-c.md) |
