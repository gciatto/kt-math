# Use big-math

`BigDecimalMath` is a port of [big-math](https://github.com/eobermuhlner/big-math) (by Eric
Obermühlner, MIT license): the functions `java.math` lacks, at any precision, on every platform. They are the members
of the `BigDecimalMath` object (package `org.gciatto.kt.math.big`), and take a `MathContext` for the precision of the
result.

It is part of Kt-Math's single artifact: see [Add Kt-Math as a dependency](add-kt-math-as-a-dependency.md).

## Constants

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/big/TestKnownValues.kt:constants"
```

## Roots, powers and logarithms

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/big/TestKnownValues.kt:rootsAndLogarithms"
```

## Trigonometric and hyperbolic functions

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/big/TestKnownValues.kt:trigonometry"
```

## Factorial, gamma, Bernoulli numbers

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/big/TestKnownValues.kt:factorialGammaBernoulli"
```

The results are compared with big-math itself in Kt-Math's JVM tests, digit by digit.
