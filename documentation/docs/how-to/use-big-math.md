# Use big-math

Package `org.gciatto.kt.math.big` is a port of [big-math](https://github.com/eobermuhlner/big-math) (by Eric
Obermühlner, MIT license): the functions `java.math` lacks, at any precision, on every platform. big-math's
`BigDecimalMath` static methods are extension functions on `BigDecimal` (`x.log(mc)`), or top-level functions when
they have no `BigDecimal` argument (`pi(mc)`, `factorial(n)`) and for `atan2(y, x, mc)`, and take a `MathContext` for
the precision of the result. Java sees them as the static methods of the `BigDecimalMath` class, as in big-math;
JavaScript, as functions taking the `BigDecimal` as their first argument.

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
