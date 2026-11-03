/*
 * Ported from big-math 2.3.2 (https://github.com/eobermuhlner/big-math),
 * ch.obermuhlner.math.big.internal.CoshCalculator.
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
