/*
 * Ported from big-math 2.3.2 (https://github.com/eobermuhlner/big-math), ch.obermuhlner.math.big.internal.
 *
 * MIT License
 *
 * Copyright (c) 2017 Eric Obermühlner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.gciatto.kt.math.big.internal

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.MathContext

/**
 * Calculates exp using the Maclaurin series.
 *
 * See [Wikipedia: Taylor Series](https://de.wikipedia.org/wiki/Taylorreihe).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object ExpCalculator : SeriesCalculator() {
    private var n = 0
    private var oneOverFactorialOfN = BigRational.ONE

    override fun getCurrentFactor(): BigRational = oneOverFactorialOfN

    override fun calculateNextFactor() {
        n++
        oneOverFactorialOfN = oneOverFactorialOfN.div(n)
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerNIterator(x, mathContext)
}

/**
 * Calculates sine using the Maclaurin series.
 *
 * See [Wikipedia: Taylor Series](https://de.wikipedia.org/wiki/Taylorreihe).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object SinCalculator : SeriesCalculator(true) {
    private var n = 0
    private var negative = false
    private var factorial2nPlus1 = BigRational.ONE

    override fun getCurrentFactor(): BigRational {
        var factor = factorial2nPlus1.reciprocal()
        if (negative) {
            factor = -factor
        }
        return factor
    }

    override fun calculateNextFactor() {
        n++
        factorial2nPlus1 = factorial2nPlus1.times(2 * n)
        factorial2nPlus1 = factorial2nPlus1.times(2 * n + 1)
        negative = !negative
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerTwoNPlusOneIterator(x, mathContext)
}

/**
 * Calculates cosine using the Maclaurin series.
 *
 * See [Wikipedia: Taylor Series](https://de.wikipedia.org/wiki/Taylorreihe).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object CosCalculator : SeriesCalculator(true) {
    private var n = 0
    private var negative = false
    private var factorial2n = BigRational.ONE

    override fun getCurrentFactor(): BigRational {
        var factor = factorial2n.reciprocal()
        if (negative) {
            factor = -factor
        }
        return factor
    }

    override fun calculateNextFactor() {
        n++
        factorial2n = factorial2n.times(2 * n - 1).times(2 * n)
        negative = !negative
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerTwoNIterator(x, mathContext)
}

/**
 * Calculates sinus hyperbolicus using the Taylor series.
 *
 * See [Wikipedia: Taylor Series](https://en.wikipedia.org/wiki/Taylor_series).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object SinhCalculator : SeriesCalculator(true) {
    private var n = 0
    private var factorial2nPlus1 = BigRational.ONE

    override fun getCurrentFactor(): BigRational = factorial2nPlus1.reciprocal()

    override fun calculateNextFactor() {
        n++
        factorial2nPlus1 = factorial2nPlus1.times(2 * n)
        factorial2nPlus1 = factorial2nPlus1.times(2 * n + 1)
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerTwoNPlusOneIterator(x, mathContext)
}

/**
 * Calculates cosinus hyperbolicus using the Taylor series.
 *
 * See [Wikipedia: Taylor Series](https://en.wikipedia.org/wiki/Taylor_series).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object CoshCalculator : SeriesCalculator(true) {
    private var n = 0
    private var factorial2n = BigRational.ONE

    override fun getCurrentFactor(): BigRational = factorial2n.reciprocal()

    override fun calculateNextFactor() {
        n++
        factorial2n = factorial2n.times(2 * n - 1).times(2 * n)
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerTwoNIterator(x, mathContext)
}

/**
 * Calculates arc sinus using the Maclaurin series.
 *
 * See [Wikipedia: Taylor Series](https://de.wikipedia.org/wiki/Taylorreihe).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object AsinCalculator : SeriesCalculator() {
    private var n = 0
    private var factorial2n = BigRational.ONE
    private var factorialN = BigRational.ONE
    private var fourPowerN = BigRational.ONE

    override fun getCurrentFactor(): BigRational =
        factorial2n.div(fourPowerN.times(factorialN).times(factorialN).times(2 * n + 1))

    override fun calculateNextFactor() {
        n++
        factorial2n = factorial2n.times(2 * n - 1).times(2 * n)
        factorialN = factorialN.times(n)
        fourPowerN = fourPowerN.times(FOUR)
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerTwoNPlusOneIterator(x, mathContext)

    private const val FOUR = 4
}

/**
 * Calculates arc hyperbolic tangens (inverse hyperbolic tangens) using the Taylor series.
 *
 * See [Wikipedia: Taylor Series](https://en.wikipedia.org/wiki/Taylor_series).
 *
 * No argument checking or optimizations are done. This implementation is **not** intended to be called directly.
 */
internal object AtanhCalculator : SeriesCalculator(true) {
    private var n = 0

    override fun getCurrentFactor(): BigRational = BigRational.valueOf(1, 2 * n + 1)

    override fun calculateNextFactor() {
        n++
    }

    override fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator = PowerTwoNPlusOneIterator(x, mathContext)
}
