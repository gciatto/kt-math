/*
 * Ported from big-math 2.3.2 (https://github.com/eobermuhlner/big-math), ch.obermuhlner.math.big.BigRational.
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
@file:Suppress("TooManyFunctions")

package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode
import kotlin.math.absoluteValue
import kotlin.math.ln
import kotlin.math.max

/**
 * A rational number represented as a quotient of two values.
 *
 * Basic calculations with rational numbers (+ - * /) have no loss of precision. This allows to use [BigRational] as
 * a replacement for [BigDecimal] if absolute accuracy is desired.
 *
 * The values are internally stored as [BigDecimal] (for performance optimizations) but represented as [BigInteger]
 * (for mathematical correctness) when accessed with [numeratorBigInteger] and [denominatorBigInteger].
 *
 * The following basic calculations have no loss of precision: [plus], [minus], [times], [div], [pow].
 *
 * The following calculations are special cases of the ones listed above and have no loss of precision:
 * [unaryMinus], [reciprocal], [increment], [decrement].
 *
 * Any [BigRational] value can be converted into an arbitrary [precision][withPrecision] (number of significant
 * digits) or [scale][withScale] (number of digits after the decimal point).
 *
 * Unlike the Java original, it doesn't extend `Number` (kt-math's numbers are interfaces): it has the same `toInt`,
 * `toLong`, `toFloat` and `toDouble` conversions, though.
 */
class BigRational private constructor(
    num: BigDecimal,
    denom: BigDecimal,
) : Comparable<BigRational> {
    /** The numerator of this rational number as [BigDecimal]. */
    val numerator: BigDecimal

    /** The denominator of this rational number as [BigDecimal]. Guaranteed to not be 0, and to be positive. */
    val denominator: BigDecimal

    init {
        var n = num
        var d = denom
        if (d.signum == 0) {
            throw ArithmeticException("Divide by zero")
        }
        if (d.signum < 0) {
            n = -n
            d = -d
        }
        numerator = n
        denominator = d
    }

    private constructor(value: Int) : this(BigDecimal.of(value), BigDecimal.ONE)

    /** The numerator of this rational number as [BigInteger]. */
    val numeratorBigInteger: BigInteger get() = numerator.toBigInteger()

    /** The denominator of this rational number as [BigInteger]. Guaranteed to not be 0, and to be positive. */
    val denominatorBigInteger: BigInteger get() = denominator.toBigInteger()

    /** Reduces this rational number to the smallest numerator/denominator with the same value. */
    fun reduce(): BigRational {
        var n = numerator.toBigInteger()
        var d = denominator.toBigInteger()

        val gcd = n.gcd(d)
        n /= gcd
        d /= gcd

        return valueOf(n, d)
    }

    /** Returns the integer part of this rational number: `-1.5` → `-1`. */
    fun integerPart(): BigRational = of(numerator.minus(numerator.rem(denominator)), denominator)

    /** Returns the fraction part of this rational number: `-1.5` → `-0.5`. */
    fun fractionPart(): BigRational = of(numerator.rem(denominator), denominator)

    /** Negates this rational number (inverting the sign). */
    operator fun unaryMinus(): BigRational {
        if (isZero) {
            return this
        }

        return of(-numerator, denominator)
    }

    /** Calculates the reciprocal of this rational number (1/x). */
    fun reciprocal(): BigRational = of(denominator, numerator)

    /** The absolute value of this rational number. */
    val absoluteValue: BigRational get() = if (isPositive) this else -this

    /** The signum function of this rational number: -1, 0 or 1. */
    val signum: Int get() = numerator.signum

    /** Calculates the increment of this rational number (+ 1). */
    fun increment(): BigRational = of(numerator.plus(denominator), denominator)

    /** Calculates the decrement of this rational number (- 1). */
    fun decrement(): BigRational = of(numerator.minus(denominator), denominator)

    /** Calculates the addition (+) of this rational number and the specified argument. */
    operator fun plus(value: BigRational): BigRational {
        if (denominator == value.denominator) {
            return of(numerator.plus(value.numerator), denominator)
        }

        val n = numerator.times(value.denominator).plus(value.numerator.times(denominator))
        val d = denominator.times(value.denominator)
        return of(n, d)
    }

    private fun plus(value: BigDecimal): BigRational = of(numerator.plus(value.times(denominator)), denominator)

    /** Calculates the addition (+) of this rational number and the specified argument. */
    operator fun plus(value: BigInteger): BigRational {
        if (value == BigInteger.ZERO) {
            return this
        }
        return plus(BigDecimal.of(value))
    }

    /** Calculates the addition (+) of this rational number and the specified argument. */
    operator fun plus(value: Int): BigRational {
        if (value == 0) {
            return this
        }
        return plus(BigInteger.of(value))
    }

    /** Calculates the subtraction (-) of this rational number and the specified argument. */
    operator fun minus(value: BigRational): BigRational {
        if (denominator == value.denominator) {
            return of(numerator.minus(value.numerator), denominator)
        }

        val n = numerator.times(value.denominator).minus(value.numerator.times(denominator))
        val d = denominator.times(value.denominator)
        return of(n, d)
    }

    private fun minus(value: BigDecimal): BigRational = of(numerator.minus(value.times(denominator)), denominator)

    /** Calculates the subtraction (-) of this rational number and the specified argument. */
    operator fun minus(value: BigInteger): BigRational {
        if (value == BigInteger.ZERO) {
            return this
        }
        return minus(BigDecimal.of(value))
    }

    /** Calculates the subtraction (-) of this rational number and the specified argument. */
    operator fun minus(value: Int): BigRational {
        if (value == 0) {
            return this
        }
        return minus(BigInteger.of(value))
    }

    /** Calculates the multiplication (*) of this rational number and the specified argument. */
    operator fun times(value: BigRational): BigRational {
        if (isZero || value.isZero) {
            return ZERO
        }
        if (this == ONE) {
            return value
        }
        if (value == ONE) {
            return this
        }

        val n = numerator.times(value.numerator)
        val d = denominator.times(value.denominator)
        return of(n, d)
    }

    // private, because we want to hide that we use BigDecimal internally
    private fun times(value: BigDecimal): BigRational {
        val n = numerator.times(value)
        val d = denominator
        return of(n, d)
    }

    /** Calculates the multiplication (*) of this rational number and the specified argument. */
    operator fun times(value: BigInteger): BigRational {
        if (isZero || value.signum == 0) {
            return ZERO
        }
        if (this == ONE) {
            return valueOf(value)
        }
        if (value == BigInteger.ONE) {
            return this
        }

        return times(BigDecimal.of(value))
    }

    /** Calculates the multiplication (*) of this rational number and the specified argument. */
    operator fun times(value: Int): BigRational = times(BigInteger.of(value))

    /** Calculates the division (/) of this rational number and the specified argument. */
    operator fun div(value: BigRational): BigRational {
        if (value == ONE) {
            return this
        }

        val n = numerator.times(value.denominator)
        val d = denominator.times(value.numerator)
        return of(n, d)
    }

    private fun div(value: BigDecimal): BigRational {
        val n = numerator
        val d = denominator.times(value)
        return of(n, d)
    }

    /** Calculates the division (/) of this rational number and the specified argument. */
    operator fun div(value: BigInteger): BigRational {
        if (value == BigInteger.ONE) {
            return this
        }

        return div(BigDecimal.of(value))
    }

    /** Calculates the division (/) of this rational number and the specified argument. */
    operator fun div(value: Int): BigRational = div(BigInteger.of(value))

    /** Whether this rational number is zero. */
    val isZero: Boolean get() = numerator.signum == 0

    private val isPositive: Boolean get() = numerator.signum > 0

    /** Whether this rational number is an integer number without fraction part. */
    val isInteger: Boolean get() = isIntegerInternal || reduce().isIntegerInternal

    // Whether this rational number is an integer number without having to reduce it
    private val isIntegerInternal: Boolean get() = denominator.compareTo(BigDecimal.ONE) == 0

    /** Calculates this rational number to the power (x^y) of the specified argument. */
    infix fun pow(exponent: Int): BigRational {
        if (exponent == 0) {
            return ONE
        }
        if (exponent == 1) {
            return this
        }

        val n: BigInteger
        val d: BigInteger
        if (exponent > 0) {
            n = numerator.toBigInteger().pow(exponent)
            d = denominator.toBigInteger().pow(exponent)
        } else {
            n = denominator.toBigInteger().pow(-exponent)
            d = numerator.toBigInteger().pow(-exponent)
        }
        return valueOf(n, d)
    }

    // Finds the minimum (smaller) of two rational numbers
    private fun min(value: BigRational): BigRational = if (this <= value) this else value

    // Finds the maximum (larger) of two rational numbers
    private fun max(value: BigRational): BigRational = if (this >= value) this else value

    /** Returns a rational number with approximatively `this` value and the specified precision. */
    fun withPrecision(precision: Int): BigRational = valueOf(toBigDecimal(MathContext(precision)))

    /** Returns a rational number with approximatively `this` value and the specified scale. */
    fun withScale(scale: Int): BigRational = valueOf(toBigDecimal().setScale(scale, RoundingMode.HALF_UP))

    private fun precision(): Int = countDigits(numerator.toBigInteger()) + countDigits(denominator.toBigInteger())

    /** Returns this rational number as a double value. */
    fun toDouble(): Double {
        // TODO best accuracy or maybe bigDecimalValue(MathContext.DECIMAL64).doubleValue() is enough?
        return toBigDecimal().toDouble()
    }

    /** Returns this rational number as a float value. */
    fun toFloat(): Float = toBigDecimal().toFloat()

    /** Returns this rational number as an int value (truncating the fraction part). */
    fun toInt(): Int = toBigDecimal().toInt()

    /** Returns this rational number as a long value (truncating the fraction part). */
    fun toLong(): Long = toBigDecimal().toLong()

    /**
     * Returns this rational number as a [BigDecimal]: as many digits as the numerator and denominator have together,
     * at least 34 (`MathContext.DECIMAL128`).
     */
    fun toBigDecimal(): BigDecimal {
        val precision = max(precision(), MathContext.DECIMAL128.precision)
        return toBigDecimal(MathContext(precision))
    }

    /** Returns this rational number as a [BigDecimal] with the precision specified by the [MathContext]. */
    fun toBigDecimal(mc: MathContext): BigDecimal = numerator.divide(denominator, mc)

    override fun compareTo(other: BigRational): Int {
        if (this === other) {
            return 0
        }
        return numerator.times(other.denominator).compareTo(denominator.times(other.numerator))
    }

    override fun hashCode(): Int {
        if (isZero) {
            return 0
        }
        return numerator.hashCode() + denominator.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is BigRational) {
            return false
        }

        if (numerator != other.numerator) {
            return false
        }
        return denominator == other.denominator
    }

    override fun toString(): String {
        if (isZero) {
            return "0"
        }
        if (isIntegerInternal) {
            return numerator.toString()
        }
        return toBigDecimal().toString()
    }

    /** Returns a plain string representation of this rational number without any exponent. */
    fun toPlainString(): String {
        if (isZero) {
            return "0"
        }
        if (isIntegerInternal) {
            return numerator.toPlainString()
        }
        return toBigDecimal().toPlainString()
    }

    /** Returns the string representation of this rational number in the form "numerator/denominator". */
    fun toRationalString(): String {
        if (isZero) {
            return "0"
        }
        if (isIntegerInternal) {
            return numerator.toString()
        }
        return "$numerator/$denominator"
    }

    /** Returns the string representation of this rational number as integer and fraction parts: "integer n/d". */
    fun toIntegerRationalString(): String {
        val fractionNumerator = numerator.rem(denominator)
        val integerNumerator = numerator.minus(fractionNumerator)
        val integerPart = integerNumerator.div(denominator)

        val result = StringBuilder()
        if (integerPart.signum != 0) {
            result.append(integerPart)
        }
        if (fractionNumerator.signum != 0) {
            if (result.isNotEmpty()) {
                result.append(' ')
                result.append(fractionNumerator.absoluteValue)
            } else {
                result.append(fractionNumerator)
            }
            result.append('/')
            result.append(denominator)
        }
        if (result.isEmpty()) {
            result.append('0')
        }

        return result.toString()
    }

    companion object {
        /** The value 0 as [BigRational]. */
        val ZERO = BigRational(0)

        /** The value 1 as [BigRational]. */
        val ONE = BigRational(1)

        /** The value 2 as [BigRational]. */
        val TWO = BigRational(2)

        /** The value 10 as [BigRational]. */
        @Suppress("MagicNumber")
        val TEN = BigRational(10)

        private val bernoulliCache = mutableListOf<BigRational>()

        private val bernoulliCacheLock = CacheLock()

        private fun countDigits(number: BigInteger): Int {
            val factor = ln(2.0) / ln(10.0)
            val digitCount = (factor * number.bitLength + 1).toInt()
            if (BigInteger.TEN.pow(digitCount - 1) > number) {
                return digitCount - 1
            }
            return digitCount
        }

        /** Creates a rational number of the specified int value. */
        fun valueOf(value: Int): BigRational {
            if (value == 0) {
                return ZERO
            }
            if (value == 1) {
                return ONE
            }
            return BigRational(value)
        }

        /** Creates a rational number of the specified numerator/denominator int values. */
        fun valueOf(
            numerator: Int,
            denominator: Int,
        ): BigRational = of(BigDecimal.of(numerator), BigDecimal.of(denominator))

        /** Creates a rational number of the specified integer and fraction parts: `valueOf(3, 1, 2)` is 3½. */
        fun valueOf(
            integer: Int,
            fractionNumerator: Int,
            fractionDenominator: Int,
        ): BigRational {
            if (fractionNumerator < 0 || fractionDenominator < 0) {
                throw ArithmeticException("Negative value")
            }

            val integerPart = valueOf(integer)
            val fractionPart = valueOf(fractionNumerator, fractionDenominator)
            return if (integerPart.isPositive) integerPart.plus(fractionPart) else integerPart.minus(fractionPart)
        }

        /** Creates a rational number of the specified numerator/denominator BigInteger values. */
        fun valueOf(
            numerator: BigInteger,
            denominator: BigInteger,
        ): BigRational = of(BigDecimal.of(numerator), BigDecimal.of(denominator))

        /** Creates a rational number of the specified [BigInteger] value. */
        fun valueOf(value: BigInteger): BigRational {
            if (value.compareTo(BigInteger.ZERO) == 0) {
                return ZERO
            }
            if (value.compareTo(BigInteger.ONE) == 0) {
                return ONE
            }
            return valueOf(value, BigInteger.ONE)
        }

        /**
         * Creates a rational number of the specified double value.
         *
         * @throws NumberFormatException if the double value is infinite or NaN.
         */
        fun valueOf(value: Double): BigRational {
            if (value == 0.0) {
                return ZERO
            }
            if (value == 1.0) {
                return ONE
            }
            if (value.isInfinite()) {
                throw NumberFormatException("Infinite")
            }
            if (value.isNaN()) {
                throw NumberFormatException("NaN")
            }
            return valueOf(
                org.gciatto.kt.math.big
                    .valueOf(value),
            )
        }

        /** Creates a rational number of the specified [BigDecimal] value. */
        fun valueOf(value: BigDecimal): BigRational {
            if (value.compareTo(BigDecimal.ZERO) == 0) {
                return ZERO
            }
            if (value.compareTo(BigDecimal.ONE) == 0) {
                return ONE
            }

            val scale = value.scale
            return if (scale == 0) {
                BigRational(value, BigDecimal.ONE)
            } else if (scale < 0) {
                val n = BigDecimal.of(value.unscaledValue).times(BigDecimal.ONE.movePointLeft(value.scale))
                BigRational(n, BigDecimal.ONE)
            } else {
                val n = BigDecimal.of(value.unscaledValue)
                val d = BigDecimal.ONE.movePointRight(value.scale)
                BigRational(n, d)
            }
        }

        /**
         * Creates a rational number of the specified string representation: a decimal number as accepted by
         * [BigDecimal.of], or the division of two of them (`"1/3"`, `"1.5/2.5"`).
         */
        fun valueOf(string: String): BigRational {
            // as Java's String.split, drop trailing empty strings
            // (an array, as in Java: "/" has no parts, and strings[0] throws ArrayIndexOutOfBoundsException on the JVM)
            val strings =
                string
                    .split("/")
                    .dropLastWhile { it.isEmpty() }
                    .ifEmpty { if (string.isEmpty()) listOf("") else emptyList() }
                    .toTypedArray()
            var result = valueOfSimple(strings[0])
            for (i in 1 until strings.size) {
                result = result.div(valueOfSimple(strings[i]))
            }
            return result
        }

        private fun valueOfSimple(string: String): BigRational = valueOf(BigDecimal.of(string))

        /**
         * Creates a rational number of the specified parts of a decimal number with an optional repeating fraction,
         * e.g. `valueOf(true, "3", "14", "15", "2")` is 314.1515... (3.1415... × 10²).
         */
        @Suppress("CyclomaticComplexMethod")
        fun valueOf(
            positive: Boolean,
            integerPart: String?,
            fractionPart: String?,
            fractionRepeatPart: String?,
            exponentPart: String?,
        ): BigRational {
            var result = ZERO

            if (!fractionRepeatPart.isNullOrEmpty()) {
                val lotsOfNines = BigInteger.TEN.pow(fractionRepeatPart.length).minus(BigInteger.ONE)
                result = valueOf(bigInteger(fractionRepeatPart), lotsOfNines)
            }

            if (!fractionPart.isNullOrEmpty()) {
                result = result.plus(valueOf(bigInteger(fractionPart)))
                result = result.div(BigInteger.TEN.pow(fractionPart.length))
            }

            if (!integerPart.isNullOrEmpty()) {
                result = result.plus(bigInteger(integerPart))
            }

            if (!exponentPart.isNullOrEmpty()) {
                val exponent = exponentPart.toInt()
                val powerOfTen = BigInteger.TEN.pow(exponent.absoluteValue)
                result = if (exponent >= 0) result.times(powerOfTen) else result.div(powerOfTen)
            }

            if (!positive) {
                result = -result
            }

            return result
        }

        // `new BigInteger(string)`: radix 10, without kt-math's detection of 0x/0b/0o prefixes
        private fun bigInteger(string: String): BigInteger = BigInteger.of(string, DECIMAL_RADIX)

        private const val DECIMAL_RADIX = 10

        /** Creates a rational number of the specified numerator/denominator [BigDecimal] values. */
        fun valueOf(
            numerator: BigDecimal,
            denominator: BigDecimal,
        ): BigRational = valueOf(numerator).div(valueOf(denominator))

        private fun of(
            numerator: BigDecimal,
            denominator: BigDecimal,
        ): BigRational {
            if (numerator.signum == 0 && denominator.signum != 0) {
                return ZERO
            }
            if (numerator.compareTo(BigDecimal.ONE) == 0 && denominator.compareTo(BigDecimal.ONE) == 0) {
                return ONE
            }
            return BigRational(numerator, denominator)
        }

        /** Returns the smallest of the specified rational numbers, or zero if none. */
        fun min(vararg values: BigRational): BigRational {
            if (values.isEmpty()) {
                return ZERO
            }
            var result = values[0]
            for (i in 1 until values.size) {
                result = result.min(values[i])
            }
            return result
        }

        /** Returns the largest of the specified rational numbers, or zero if none. */
        fun max(vararg values: BigRational): BigRational {
            if (values.isEmpty()) {
                return ZERO
            }
            var result = values[0]
            for (i in 1 until values.size) {
                result = result.max(values[i])
            }
            return result
        }

        /**
         * Calculates the Bernoulli number for the specified index.
         *
         * This function calculates the **first Bernoulli numbers** and therefore `bernoulli(1)` returns -0.5.
         * Note that `bernoulli(x)` for all odd x > 1 returns 0.
         *
         * See [Wikipedia: Bernoulli number](https://en.wikipedia.org/wiki/Bernoulli_number).
         *
         * @throws ArithmeticException if x < 0
         */
        fun bernoulli(n: Int): BigRational {
            if (n < 0) {
                throw ArithmeticException("Illegal bernoulli(n) for n < 0: n = $n")
            }
            if (n == 1) {
                return valueOf(-1, 2)
            } else if (n % 2 == 1) {
                return ZERO
            }

            return bernoulliCacheLock.withLock {
                val index = n / 2

                if (bernoulliCache.size <= index) {
                    for (i in bernoulliCache.size..index) {
                        val b = calculateBernoulli(i * 2)
                        bernoulliCache.add(b)
                    }
                }

                bernoulliCache[index]
            }
        }

        // The Java original sums the terms with a parallel stream: the sum is the same rational number
        private fun calculateBernoulli(n: Int): BigRational =
            (0..n)
                .map { k ->
                    var jSum = ZERO
                    var bin = ONE
                    for (j in 0..k) {
                        val jPowN = valueOf(j).pow(n)
                        if (j % 2 == 0) {
                            jSum = jSum.plus(bin.times(jPowN))
                        } else {
                            jSum = jSum.minus(bin.times(jPowN))
                        }

                        bin = bin.times(valueOf(k - j).div(valueOf(j + 1)))
                    }
                    jSum.div(valueOf(k + 1))
                }.fold(ZERO) { a, b -> a.plus(b) }
    }
}
