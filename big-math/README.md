# kt-math-big-math

Kotlin multi-platform port of [big-math](https://github.com/eobermuhlner/big-math) 2.3.2's `BigDecimalMath`, on top
of Kt-Math's `BigDecimal`/`BigInteger` (big-math's `BigRational` is ported in Kt-Math itself): exp, log, pow, roots, trigonometric and hyperbolic
functions (and their inverses), factorial, gamma, Bernoulli numbers, pi, e, on every platform Kt-Math supports.

```kotlin
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.big.BigDecimalMath

val mc = MathContext(50)
BigDecimalMath.pi(mc) // 3.1415926535897932384626433832795028841971693993751
BigDecimalMath.log(BigDecimal.of(2), mc) // 0.69314718055994530941723212145817656807550013436026
```

## License

The ported code keeps big-math's license:

```
MIT License

Copyright (c) 2017 Eric Obermühlner

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
