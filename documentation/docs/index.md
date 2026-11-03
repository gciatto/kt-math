# Kt-Math

**Arbitrary-precision integers, decimals and rationals for Kotlin Multiplatform**, usable from Kotlin, Java,
JavaScript and C.

Kt-Math offers the API of Java's `java.math.BigInteger` and `java.math.BigDecimal` (plus a `BigRational` type) on
every Kotlin platform, each backed by the fastest implementation available there:

| Platform | `BigInteger` | `BigDecimal`, `BigRational` |
|---|---|---|
| JVM (Java 8+) | `java.math.BigInteger` | `java.math.BigDecimal`; Kotlin `BigRational` |
| JavaScript (Node.js, browsers) | native `BigInt` | Kotlin port of `java.math.BigDecimal`, on `BigInt`s |
| Kotlin/Native (all targets) | Kotlin port of `java.math.BigInteger`, with C kernels for the hot loops | Kotlin port |

The Kotlin port follows the JDK sources line by line, and its results are checked against `java.math` on every
platform: the same operation gives the same result (and throws the same exception) everywhere.

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/TestKotlinUsage.kt:big-integers"
```

## Where to go next

- New to Kt-Math? Start from a tutorial: [Kotlin](tutorials/getting-started-kotlin.md),
  [Java](tutorials/getting-started-java.md), [JavaScript](tutorials/getting-started-js.md) or
  [C](tutorials/getting-started-c.md).
- Looking for a specific task? See the [how-to guides](how-to/index.md), e.g.
  [adding Kt-Math to a project](how-to/add-kt-math-as-a-dependency.md).
- Looking for a class or function? See the [API reference](https://gciatto.github.io/kt-math/api/).
- Wondering how it works? See the [explanation](explanation/index.md) section.
