# Reference

- [API reference](https://gciatto.github.io/kt-math/api/): every public class and function, generated from the
  KDoc of the sources (both modules: `kt-math` and `kt-math-big-math`).
- [Platforms](platforms.md): supported targets, and the implementation each one uses.

## Main types

| Type | What it is |
|---|---|
| `BigInteger` | arbitrary-precision integer, the API of `java.math.BigInteger` |
| `BigDecimal` | arbitrary-precision decimal (unscaled `BigInteger` and scale), the API of `java.math.BigDecimal` |
| `BigRational` | exact fraction, the API of big-math's `BigRational` |
| `MathContext`, `RoundingMode` | precision and rounding of `BigDecimal` operations, as in `java.math` |
| `BigFactory` | creation and parsing of the three number types; `BigFactory.default` is the platform's |
| `BigDecimalMath` (big-math) | logarithms, powers, roots, trigonometric and hyperbolic functions, constants |
