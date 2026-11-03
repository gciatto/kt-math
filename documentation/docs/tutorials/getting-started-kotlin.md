# Getting started in Kotlin

This tutorial computes with the three number types of Kt-Math from Kotlin code. The same code runs on every target
of a multiplatform project: JVM, JavaScript and Kotlin/Native.

## Add the dependency

In the `build.gradle.kts` of a Kotlin Multiplatform project (see [the how-to guide](../how-to/add-kt-math-as-a-dependency.md)
for other set-ups):

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.gciatto:kt-math:VERSION")
            }
        }
    }
}
```

Everything lives in the `org.gciatto.kt.math` package.

## Integers

`BigInteger`s are created from strings or Kotlin numbers, and support Kotlin's arithmetic and comparison operators:

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/TestKotlinUsage.kt:big-integers"
```

## Decimals

`BigDecimal`s are exact decimal numbers with a *scale* (the number of digits after the decimal point): `1.10` and
`1.1` are equal in value, but not in scale. Divisions whose result has no exact decimal representation need a
`MathContext` (a precision and a `RoundingMode`), or a scale and a rounding mode:

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/TestKotlinUsage.kt:big-decimals"
```

## Rationals

`BigRational`s are fractions of arbitrary-precision numbers: sums, products and quotients are exact, and a decimal
approximation is computed only on request:

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/TestKotlinUsage.kt:big-rationals"
```

## Factories and conversions

`BigFactory` gathers every way of creating numbers, including parsing functions returning `null` on invalid input;
`BigFactory.default` uses the fastest implementation of the platform. Every number type converts to the others:

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/TestKotlinUsage.kt:factory"
```

## Next steps

- [Parse and convert numbers](../how-to/parse-and-convert-numbers.md)
- [Control precision and rounding](../how-to/control-precision-and-rounding.md)
- [Compute logarithms, roots, trigonometric functions](../how-to/use-big-math.md) with `BigDecimalMath`
