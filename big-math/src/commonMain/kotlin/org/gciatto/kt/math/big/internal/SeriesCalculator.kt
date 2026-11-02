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
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.big.BigDecimalMath
import org.gciatto.kt.math.big.BigRational
import org.gciatto.kt.math.big.CacheLock
import org.gciatto.kt.math.big.divide
import org.gciatto.kt.math.big.rounded

/**
 * Utility class to calculate taylor series efficiently until the maximum error (as defined by the precision in the
 * [MathContext] is reached.
 *
 * Stores the factors of the taylor series terms so that future calculations will be faster.
 */
internal abstract class SeriesCalculator protected constructor(
    private val calculateInPairs: Boolean = false,
) {
    private val factors = mutableListOf<BigRational>()

    private val factorsLock = CacheLock()

    /**
     * Calculates the series for the specified value x and the precision defined in the [MathContext].
     */
    fun calculate(
        x: BigDecimal,
        mathContext: MathContext,
    ): BigDecimal {
        val acceptableError = BigDecimal.ONE.movePointLeft(mathContext.precision + 1)

        val powerIterator = createPowerIterator(x, mathContext)

        var sum = BigDecimal.ZERO
        var step: BigDecimal
        var i = 0
        do {
            var factor: BigRational
            var xToThePower: BigDecimal

            factor = getFactor(i)
            xToThePower = powerIterator.getCurrentPower()
            powerIterator.calculateNextPower()
            step = factor.numerator.times(xToThePower).divide(factor.denominator, mathContext)
            i++

            if (calculateInPairs) {
                factor = getFactor(i)
                xToThePower = powerIterator.getCurrentPower()
                powerIterator.calculateNextPower()
                val step2 = factor.numerator.times(xToThePower).divide(factor.denominator, mathContext)
                step = step.plus(step2)
                i++
            }

            sum = sum.plus(step)
        } while (step.absoluteValue > acceptableError)

        return sum.rounded(mathContext)
    }

    /** Creates the [PowerIterator] used for this series. */
    protected abstract fun createPowerIterator(
        x: BigDecimal,
        mathContext: MathContext,
    ): PowerIterator

    /** Returns the factor of the term with specified index. */
    protected fun getFactor(index: Int): BigRational =
        factorsLock.withLock {
            while (factors.size <= index) {
                val factor = getCurrentFactor()
                factors.add(factor)
                calculateNextFactor()
            }
            factors[index]
        }

    /** Returns the factor of the highest term already calculated. */
    protected abstract fun getCurrentFactor(): BigRational

    /** Calculates the factor of the next term. */
    protected abstract fun calculateNextFactor()
}

/** Iterator over the powers of a value x. */
internal interface PowerIterator {
    /** Returns the current power. */
    fun getCurrentPower(): BigDecimal

    /** Calculates the next power. */
    fun calculateNextPower()
}

/** [PowerIterator] to calculate x^n. */
internal class PowerNIterator(
    private val x: BigDecimal,
    private val mathContext: MathContext,
) : PowerIterator {
    private var powerOfX: BigDecimal = BigDecimal.ONE

    override fun getCurrentPower(): BigDecimal = powerOfX

    override fun calculateNextPower() {
        powerOfX = powerOfX.times(x, mathContext)
    }
}

/** [PowerIterator] to calculate x^(2*n). */
internal class PowerTwoNIterator(
    x: BigDecimal,
    private val mathContext: MathContext,
) : PowerIterator {
    private val xPowerTwo: BigDecimal = x.times(x, mathContext)

    private var powerOfX: BigDecimal = BigDecimal.ONE

    override fun getCurrentPower(): BigDecimal = powerOfX

    override fun calculateNextPower() {
        powerOfX = powerOfX.times(xPowerTwo, mathContext)
    }
}

/** [PowerIterator] to calculate x^(2*n+1). */
internal class PowerTwoNPlusOneIterator(
    x: BigDecimal,
    private val mathContext: MathContext,
) : PowerIterator {
    private val xPowerTwo: BigDecimal = x.times(x, mathContext)

    private var powerOfX: BigDecimal = x

    override fun getCurrentPower(): BigDecimal = powerOfX

    override fun calculateNextPower() {
        powerOfX = powerOfX.times(xPowerTwo, mathContext)
    }
}

/** [PowerIterator] to calculate x^(2*n-1). */
internal class PowerTwoNMinusOneIterator(
    x: BigDecimal,
    private val mathContext: MathContext,
) : PowerIterator {
    private val xPowerTwo: BigDecimal = x.times(x, mathContext)

    private var powerOfX: BigDecimal = BigDecimalMath.reciprocal(x, mathContext)

    override fun getCurrentPower(): BigDecimal = powerOfX

    override fun calculateNextPower() {
        powerOfX = powerOfX.times(xPowerTwo, mathContext)
    }
}
