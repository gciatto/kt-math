# Control precision and rounding

`BigInteger` and `BigRational` operations are exact. `BigDecimal` operations are exact too, unless they are given a
`MathContext` (a precision, i.e. a number of significant digits, and a `RoundingMode`) or a scale and a
`RoundingMode`.

## When exactness is impossible

A division whose quotient has no finite decimal representation throws `ArithmeticException`, unless told how to round:

```kotlin
val a = BigDecimal.ONE
val b = BigDecimal.of(3)
a / b                                           // throws ArithmeticException
a.div(b, MathContext(5))                        // 0.33333: 5 significant digits, HALF_UP
a.div(b, MathContext(5, RoundingMode.CEILING))  // 0.33334
a.div(b, 2, RoundingMode.HALF_EVEN)             // 0.33: scale 2
```

Operations that are usually inexact (`sqrt`, and big-math's functions) always take a `MathContext`.

## Precision or scale?

- A `MathContext(precision)` bounds the number of **significant digits**: `MathContext(3)` rounds `12345` to `1.23E+4`
  and `0.012345` to `0.0123`. `MathContext.DECIMAL32`, `DECIMAL64` and `DECIMAL128` match the IEEE 754 decimal
  formats; `MathContext.UNLIMITED` (precision 0) means exact arithmetic.
- A **scale** is the number of digits after the decimal point: `setScale(2, RoundingMode.HALF_UP)` turns `1.005` into
  `1.01` (and `1` into `1.00`).

## Rounding modes

| Mode | Rounds towards | `2.5` | `-2.5` | `1.1` |
|---|---|---|---|---|
| `UP` | away from zero | `3` | `-3` | `2` |
| `DOWN` | zero | `2` | `-2` | `1` |
| `CEILING` | positive infinity | `3` | `-2` | `2` |
| `FLOOR` | negative infinity | `2` | `-3` | `1` |
| `HALF_UP` | the nearest neighbour, or away from zero when halfway | `3` | `-3` | `1` |
| `HALF_DOWN` | the nearest neighbour, or zero when halfway | `2` | `-2` | `1` |
| `HALF_EVEN` | the nearest neighbour, or the even one when halfway | `2` | `-2` | `1` |
| `UNNECESSARY` | nowhere: throws `ArithmeticException` if rounding is needed | throws | throws | throws |

## Equality and scale

`BigDecimal.equals` compares values *and* scales (`1.0 != 1.00`), as in Java; compare values with `compareTo`
(`compare` from JavaScript), or normalise with `stripTrailingZeros()`.
