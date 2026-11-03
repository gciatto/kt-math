# Parse and convert numbers

## From strings and Kotlin numbers

Each type has factory functions in its companion object (`BigInteger.of`, `BigDecimal.of`, `BigRational.valueOf`),
also gathered in the [`BigFactory`](https://gciatto.github.io/kt-math/api/kt-math/org.gciatto.kt.math/-big-factory/)
interface, together with parsing functions returning `null` instead of throwing:

| Input | Throwing | Returning `null` | Extension function |
|---|---|---|---|
| `String` to `BigInteger` | `factory.parseBigInteger(s, radix)` | `factory.parseBigIntegerOrNull(s, radix)` | `s.toBigInteger(radix)` |
| `String` to `BigDecimal` | `factory.parseBigDecimal(s, mc)` | `factory.parseBigDecimalOrNull(s, mc)` | `s.toBigDecimal(mc)` |
| `String` to `BigRational` | `factory.parseBigRational(s)` | `factory.parseBigRationalOrNull(s)` | `s.toBigRational()` |
| `Int`/`Long` | `factory.bigIntegerOf(n)`, `factory.bigDecimalOf(n)`, `factory.bigRationalOf(n)` | | `n.toBigInteger()`, ... |
| `Double` | `factory.bigDecimalValueOf(d)` (shortest), `factory.bigDecimalOf(d)` (exact binary value) | | `d.toBigDecimal()` (shortest) |

where `factory` is `BigFactory.default`. `BigRational`s parse fractions (`"1/3"`, `"-1.5/2.5"`) as well as decimals.

!!! note "Doubles"
    `0.1` isn't exactly representable as a `Double`: `bigDecimalOf(0.1)` is its exact binary value,
    `0.1000000000000000055511151231257827021181583404541015625`, while `bigDecimalValueOf(0.1)` and
    `0.1.toBigDecimal()` give `0.1`, the shortest decimal that rounds to it (as Java's `BigDecimal.valueOf(double)`).

## Among the number types

| From \ To | `BigInteger` | `BigDecimal` | `BigRational` |
|---|---|---|---|
| `BigInteger` | | `toBigDecimal()`, `toBigDecimal(scale)` | `toBigRational()` |
| `BigDecimal` | `toBigInteger()` (truncates), `toBigIntegerExact()` | | `toBigRational()` (exact) |
| `BigRational` | `toBigInteger()` (truncates) | `toBigDecimal()`, `toBigDecimal(mc)` | |

```kotlin
--8<-- "src/commonTest/kotlin/org/gciatto/kt/math/TestKotlinUsage.kt:factory"
```

## To Kotlin numbers and strings

`toInt()`, `toLong()`, `toDouble()`, ... truncate or round silently, as Kotlin's conversions of primitive numbers;
the `...Exact()` variants (`toIntExact()`, `toLongExact()`, ...) throw `ArithmeticException` instead of losing
information. `toString()` gives the canonical representation (scientific notation for very large or small decimals),
`BigDecimal.toPlainString()` never uses an exponent, `BigInteger.toString(radix)` uses any radix up to 36, and
`BigRational.toRationalString()` gives `numerator/denominator`.
