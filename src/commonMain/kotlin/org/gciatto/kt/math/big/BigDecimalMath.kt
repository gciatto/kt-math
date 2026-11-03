/*
 * Ported from big-math 2.3.2 (https://github.com/eobermuhlner/big-math), ch.obermuhlner.math.big.BigDecimalMath.
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
@file:Suppress("TooManyFunctions", "MagicNumber", "NAME_SHADOWING")
@file:JvmName("BigDecimalMath")
@file:JsExport

package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigDecimal.Companion.ONE
import org.gciatto.kt.math.BigDecimal.Companion.TEN
import org.gciatto.kt.math.BigDecimal.Companion.ZERO
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode
import org.gciatto.kt.math.big.internal.AsinCalculator
import org.gciatto.kt.math.big.internal.CosCalculator
import org.gciatto.kt.math.big.internal.CoshCalculator
import org.gciatto.kt.math.big.internal.ExpCalculator
import org.gciatto.kt.math.big.internal.SinCalculator
import org.gciatto.kt.math.big.internal.SinhCalculator
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

// Advanced functions operating on BigDecimals: a multiplatform port of big-math's BigDecimalMath, with the same
// functions, algorithms and results. Its static methods are extension functions on BigDecimal here (for Java, still
// static methods of the BigDecimalMath class), except for those without a BigDecimal argument and atan2.

private val TWO = BigDecimal.of(2)
private val THREE = BigDecimal.of(3)
private val MINUS_ONE = BigDecimal.of(-1)
private val ONE_HALF = BigDecimal.valueOf(0.5)
private val ONE_HUNDRED_EIGHTY = BigDecimal.of(180)

private val DOUBLE_MAX_VALUE = BigDecimal.valueOf(Double.MAX_VALUE)
private val INT_MIN_VALUE = BigDecimal.of(Int.MIN_VALUE)
private val INT_MAX_VALUE = BigDecimal.of(Int.MAX_VALUE)
private val LONG_MIN_VALUE = BigDecimal.of(Long.MIN_VALUE)
private val LONG_MAX_VALUE = BigDecimal.of(Long.MAX_VALUE)

private var log2Cache: BigDecimal? = null
private val log2CacheLock = CacheLock()

private var log3Cache: BigDecimal? = null
private val log3CacheLock = CacheLock()

private var log10Cache: BigDecimal? = null
private val log10CacheLock = CacheLock()

private var piCache: BigDecimal? = null
private val piCacheLock = CacheLock()

private var eCache: BigDecimal? = null
private val eCacheLock = CacheLock()

private val ROUGHLY_TWO_PI = BigDecimal.of("3.141592653589793").times(TWO)

private const val EXPECTED_INITIAL_PRECISION = 15

private val factorialCache: Array<BigDecimal> =
    run {
        var result = ONE
        Array(100) { i ->
            if (i > 0) result = result.times(BigDecimal.of(i))
            result
        }
    }

private val spougeFactorialConstantsCache = mutableMapOf<Int, List<BigDecimal>>()
private val spougeFactorialConstantsCacheLock = CacheLock()

/**
 * Creates a [BigDecimal] from the specified `String` representation, rounded with [mathContext] (by default, not
 * rounded at all).
 *
 * This method is equivalent to the String constructor of [BigDecimal] but has been optimized for large strings
 * (several thousand digits).
 *
 * @throws NumberFormatException if `string` is not a valid representation of a [BigDecimal]
 */
@JvmOverloads
fun toBigDecimal(
    string: String,
    mathContext: MathContext = MathContext.UNLIMITED,
): BigDecimal {
    val len = string.length
    if (len < 600) {
        return BigDecimal.of(string, mathContext)
    }

    val splitLength = len / (if (len >= 10000) 8 else 5)
    return toBigDecimal(string, mathContext, splitLength)
}

@JsName("toBigDecimalSplit")
@Suppress("CyclomaticComplexMethod", "LongMethod", "NestedBlockDepth", "ThrowsCount")
internal fun toBigDecimal(
    string: String,
    mathContext: MathContext,
    splitLength: Int,
): BigDecimal {
    val len = string.length

    if (len < splitLength) {
        return BigDecimal.of(string, mathContext)
    }

    val chars = string.toCharArray()

    var numberHasSign = false
    var negative = false
    var numberIndex = 0
    var dotIndex = -1
    var expIndex = -1
    var expHasSign = false
    var scale = 0

    for (i in 0 until len) {
        when (chars[i]) {
            '+' ->
                if (expIndex >= 0) {
                    if (expHasSign) {
                        throw NumberFormatException("Multiple signs in exponent")
                    }
                    expHasSign = true
                } else {
                    if (numberHasSign) {
                        throw NumberFormatException("Multiple signs in number")
                    }
                    numberHasSign = true
                    numberIndex = i + 1
                }
            '-' ->
                if (expIndex >= 0) {
                    if (expHasSign) {
                        throw NumberFormatException("Multiple signs in exponent")
                    }
                    expHasSign = true
                } else {
                    if (numberHasSign) {
                        throw NumberFormatException("Multiple signs in number")
                    }
                    numberHasSign = true
                    negative = true
                    numberIndex = i + 1
                }
            'e', 'E' -> {
                if (expIndex >= 0) {
                    throw NumberFormatException("Multiple exponent markers")
                }
                expIndex = i
            }
            '.' -> {
                if (dotIndex >= 0) {
                    throw NumberFormatException("Multiple decimal points")
                }
                dotIndex = i
            }
            else ->
                if (dotIndex >= 0 && expIndex == -1) {
                    scale++
                }
        }
    }

    val numberEndIndex: Int
    var exp = 0
    if (expIndex >= 0) {
        numberEndIndex = expIndex
        val expString = string.substring(expIndex + 1, len)
        exp = expString.toInt()
        scale = adjustScale(scale, exp.toLong())
    } else {
        numberEndIndex = len
    }

    var result: BigDecimal
    if (dotIndex >= 0) {
        val leftLength = dotIndex - numberIndex
        val bigDecimalLeft = toBigDecimalRecursive(string, numberIndex, leftLength, exp, splitLength)
        val rightLength = numberEndIndex - dotIndex - 1
        val bigDecimalRight =
            toBigDecimalRecursive(string, dotIndex + 1, rightLength, exp - rightLength, splitLength)
        result = bigDecimalLeft.plus(bigDecimalRight)
    } else {
        result = toBigDecimalRecursive(string, numberIndex, numberEndIndex - numberIndex, exp, splitLength)
    }

    if (scale != 0) {
        result = result.setScale(scale)
    }

    if (negative) {
        result = -result
    }

    if (mathContext.precision != 0) {
        result = result.rounded(mathContext)
    }

    return result
}

private fun adjustScale(
    scale: Int,
    exp: Long,
): Int {
    val adjustedScale = scale - exp
    if (adjustedScale > Int.MAX_VALUE || adjustedScale < Int.MIN_VALUE) {
        throw NumberFormatException(
            "Scale out of range: $adjustedScale while adjusting scale $scale to exponent $exp",
        )
    }
    return adjustedScale.toInt()
}

private fun toBigDecimalRecursive(
    string: String,
    offset: Int,
    length: Int,
    scale: Int,
    splitLength: Int,
): BigDecimal {
    if (length > splitLength) {
        val mid = length / 2
        val bigDecimalLeft = toBigDecimalRecursive(string, offset, mid, scale + length - mid, splitLength)
        val bigDecimalRight = toBigDecimalRecursive(string, offset + mid, length - mid, scale, splitLength)
        return bigDecimalLeft.plus(bigDecimalRight)
    }
    if (length == 0) {
        return ZERO
    }
    return BigDecimal.of(string.substring(offset, offset + length)).movePointRight(scale)
}

/** Returns whether the specified [BigDecimal] value can be represented as `int`. */
fun BigDecimal.isIntValue(): Boolean = isIntegral() && this >= INT_MIN_VALUE && this <= INT_MAX_VALUE

/** Returns whether the specified [BigDecimal] value can be represented as `long`. */
fun BigDecimal.isLongValue(): Boolean = isIntegral() && this >= LONG_MIN_VALUE && this <= LONG_MAX_VALUE

// big-math's isIntValue and isLongValue caught the exceptions of intValueExact and longValueExact: these are the same
// checks, without exceptions
private fun BigDecimal.isIntegral(): Boolean = signum == 0 || scale <= 0 || stripTrailingZeros().scale <= 0

/**
 * Returns whether the specified [BigDecimal] value can be represented as `double` (without being infinite;
 * precision may be lost).
 */
fun BigDecimal.isDoubleValue(): Boolean {
    val value = this
    if (value > DOUBLE_MAX_VALUE) {
        return false
    }
    if (value < -DOUBLE_MAX_VALUE) {
        return false
    }

    return true
}

/**
 * Returns the mantissa of the specified [BigDecimal] written as *mantissa * 10<sup>exponent</sup>*: always
 * between 1 (inclusive) and 10 (exclusive) in absolute value, unless the value is 0.
 */
fun BigDecimal.mantissa(): BigDecimal {
    val value = this
    val exponent = value.exponent()
    if (exponent == 0) {
        return value
    }

    return value.movePointLeft(exponent)
}

/** Returns the exponent of the specified [BigDecimal] written as *mantissa * 10<sup>exponent</sup>*. */
fun BigDecimal.exponent(): Int = precision - scale - 1

/** Returns the number of significant digits of the specified [BigDecimal] (trailing zeros excluded). */
fun BigDecimal.significantDigits(): Int {
    val value = this
    val stripped = value.stripTrailingZeros()
    return if (stripped.scale >= 0) {
        stripped.precision
    } else {
        stripped.precision - stripped.scale
    }
}

/** Returns the integral part of the specified [BigDecimal] (left of the decimal point). */
fun BigDecimal.integralPart(): BigDecimal = setScale(0, RoundingMode.DOWN)

/** Returns the fractional part of the specified [BigDecimal] (right of the decimal point). */
fun BigDecimal.fractionalPart(): BigDecimal = minus(integralPart())

/** Rounds the specified [BigDecimal] to the precision of the specified [MathContext]. */
internal fun round(
    value: BigDecimal,
    mathContext: MathContext,
): BigDecimal = value.rounded(mathContext)

/**
 * Rounds the specified [BigDecimal] to the precision of the specified [MathContext], including trailing zeroes:
 * `roundWithTrailingZeroes(1.0, MathContext(4))` is `1.000`.
 */
fun BigDecimal.roundWithTrailingZeroes(mathContext: MathContext): BigDecimal {
    val value = this
    if (value.precision == mathContext.precision) {
        return value
    }
    if (value.signum == 0) {
        return ZERO.setScale(mathContext.precision - 1)
    }

    try {
        val stripped = value.stripTrailingZeros()
        val exponentStripped = stripped.exponent() // value.precision() - value.scale() - 1;

        val zero =
            if (exponentStripped < -1) {
                ZERO.setScale(mathContext.precision - exponentStripped)
            } else {
                ZERO.setScale(mathContext.precision + exponentStripped + 1)
            }
        return stripped.plus(zero, mathContext)
    } catch (_: ArithmeticException) {
        return value.rounded(mathContext)
    }
}

/** Calculates the reciprocal of the specified [BigDecimal]: 1/x. */
fun BigDecimal.reciprocal(mathContext: MathContext): BigDecimal = ONE.divide(this, mathContext)

/**
 * Calculates the factorial of the specified integer argument: *n!* = 1*2*3*...*n.
 *
 * @throws ArithmeticException if x < 0
 */
fun factorial(n: Int): BigDecimal {
    if (n < 0) {
        throw ArithmeticException("Illegal factorial(n) for n < 0: n = $n")
    }
    if (n < factorialCache.size) {
        return factorialCache[n]
    }

    val result = factorialCache[factorialCache.size - 1]
    return result.times(factorialRecursion(factorialCache.size, n))
}

private fun factorialLoop(
    n1: Int,
    n2: Int,
): BigDecimal {
    var n1 = n1
    val limit = Long.MAX_VALUE / n2
    var accu: Long = 1
    var result = ONE
    while (n1 <= n2) {
        if (accu <= limit) {
            accu *= n1.toLong()
        } else {
            result = result.times(BigDecimal.of(accu))
            accu = n1.toLong()
        }
        n1++
    }
    return result.times(BigDecimal.of(accu))
}

private fun factorialRecursion(
    n1: Int,
    n2: Int,
): BigDecimal {
    val threshold = if (n1 > 200) 80 else 150
    if (n2 - n1 < threshold) {
        return factorialLoop(n1, n2)
    }
    val mid = (n1 + n2) shr 1
    return factorialRecursion(mid + 1, n2).times(factorialRecursion(n1, mid))
}

/**
 * Calculates the factorial of the specified [BigDecimal], with Spouge's approximation for non-integer values
 * (see [Wikipedia: Spouge's approximation](https://en.wikipedia.org/wiki/Spouge%27s_approximation)).
 *
 * @throws ArithmeticException if x is a negative integer value (-1, -2, -3, ...)
 * @throws UnsupportedOperationException if x is a non-integer value and the [MathContext] has unlimited precision
 */
@JsName("factorialDecimal")
fun BigDecimal.factorial(mathContext: MathContext): BigDecimal {
    val x = this
    if (x.isIntValue()) {
        return round(factorial(x.toIntExact()), mathContext)
    }

    // https://en.wikipedia.org/wiki/Spouge%27s_approximation
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision shl 1, mathContext.roundingMode)

    val a = mathContext.precision * 13 / 10
    val constants = getSpougeFactorialConstants(a)

    val bigA = BigDecimal.of(a)

    var negative = false
    var factor = constants[0]
    for (k in 1 until a) {
        val bigK = BigDecimal.of(k)
        factor = factor.plus(constants[k].divide(x.plus(bigK), mc))
        negative = !negative
    }

    var result = x.plus(bigA).pow(x.plus(BigDecimal.valueOf(0.5)), mc)
    result = result.times((-x).minus(bigA).exp(mc))
    result = result.times(factor)

    return round(result, mathContext)
}

internal fun getSpougeFactorialConstants(a: Int): List<BigDecimal> =
    spougeFactorialConstantsCacheLock.withLock {
        spougeFactorialConstantsCache.getOrPut(a) {
            val constants = ArrayList<BigDecimal>(a)
            val mc = MathContext(a * 15 / 10)

            val c0 = sqrt(pi(mc).times(TWO, mc), mc)
            constants.add(c0)

            var negative = false
            for (k in 1 until a) {
                val bigK = BigDecimal.of(k)
                val deltaAK = a.toLong() - k
                var ck = BigDecimal.of(deltaAK).pow(bigK.minus(ONE_HALF), mc)
                ck = ck.times(BigDecimal.of(deltaAK).exp(mc), mc)
                ck = ck.divide(factorial(k - 1), mc)
                if (negative) {
                    ck = -ck
                }
                constants.add(ck)

                negative = !negative
            }

            constants.toList()
        }
    }

/**
 * Calculates the gamma function of the specified [BigDecimal]: Γ(x) = (x-1)!.
 *
 * @throws ArithmeticException if x-1 is a negative integer value (-1, -2, -3, ...)
 * @throws UnsupportedOperationException if x is a non-integer value and the [MathContext] has unlimited precision
 */
fun BigDecimal.gamma(mathContext: MathContext): BigDecimal = minus(ONE).factorial(mathContext)

/**
 * Calculates the Bernoulli number for the specified index (first Bernoulli numbers: `bernoulli(1)` is -0.5).
 *
 * @throws ArithmeticException if x < 0
 */
fun bernoulli(
    n: Int,
    mathContext: MathContext,
): BigDecimal {
    if (n < 0) {
        throw ArithmeticException("Illegal bernoulli(n) for n < 0: n = $n")
    }

    val b = BigRational.bernoulli(n)
    return b.toBigDecimal(mathContext)
}

/**
 * Calculates [BigDecimal] x to the power of [BigDecimal] y (x<sup>y</sup>).
 *
 * @throws ArithmeticException if the result can't be calculated (e.g. x < 0 and y non-integer)
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.pow(
    y: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x.signum == 0) {
        when (y.signum) {
            0 -> return round(ONE, mathContext)
            1 -> return round(ZERO, mathContext)
        }
    }

    // big-math's note to optimize y=0, y=1, y=10^k, y=-1, y=-10^k: they are integers, hence computed by repeated
    // squaring, in O(log |y|) multiplications. Shortcuts (e.g. round(x, mathContext) for y=1) would skip the
    // intermediate rounding to a larger precision, changing some results.
    if (y.isLongValue()) {
        return x.pow(y.toLong(), mathContext)
    }

    if (y.fractionalPart().signum == 0) {
        return powInteger(x, y, mathContext)
    }

    // x^y = exp(y*log(x))
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result = y.times(x.log(mc), mc).exp(mc)

    return round(result, mathContext)
}

/**
 * Calculates [BigDecimal] x to the power of `long` y (x<sup>y</sup>): unlike [BigDecimal.pow], y may be
 * negative or larger than `Int.MAX_VALUE`.
 *
 * @throws ArithmeticException if y is negative and the result can't be calculated (e.g. x is 0)
 */
@JsName("powLong")
fun BigDecimal.pow(
    y: Long,
    mathContext: MathContext,
): BigDecimal {
    var x = this
    var y = y
    val mc =
        if (mathContext.precision == 0) {
            mathContext
        } else {
            MathContext(mathContext.precision + 10, mathContext.roundingMode)
        }

    // y=0, y=1, y=10^k, y=-1, y=-10^k need no shortcut: see pow(BigDecimal, MathContext)
    if (y < 0) {
        val value = x.pow(-y, mc).reciprocal(mc)
        return round(value, mathContext)
    }

    var result = ONE
    while (y > 0) {
        if (y and 1L == 1L) {
            // odd exponent -> multiply result with x
            result = result.times(x, mc)
            y -= 1
        }

        if (y > 0) {
            // even exponent -> square x
            x = x.times(x, mc)
        }

        y = y shr 1
    }

    return round(result, mathContext)
}

private fun powInteger(
    x: BigDecimal,
    integerY: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    var x = x
    var integerY = integerY
    if (integerY.fractionalPart().signum != 0) {
        throw IllegalArgumentException("Not integer value: $integerY")
    }

    if (integerY.signum < 0) {
        return ONE.divide(powInteger(x, -integerY, mathContext), mathContext)
    }

    val mc = MathContext(max(mathContext.precision, -integerY.scale) + 30, mathContext.roundingMode)

    var result = ONE
    while (integerY.signum > 0) {
        var halfY = integerY.divide(TWO, mc)

        if (halfY.fractionalPart().signum != 0) {
            // odd exponent -> multiply result with x
            result = result.times(x, mc)
            integerY = integerY.minus(ONE)
            halfY = integerY.divide(TWO, mc)
        }

        if (halfY.signum > 0) {
            // even exponent -> square x
            x = x.times(x, mc)
        }

        integerY = halfY
    }

    return round(result, mathContext)
}

/**
 * Calculates the square root of [BigDecimal] x
 * (see [Wikipedia: Square root](http://en.wikipedia.org/wiki/Square_root)).
 *
 * @throws ArithmeticException if x < 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
internal fun sqrt(
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    checkMathContext(mathContext)
    when (x.signum) {
        0 -> return ZERO
        -1 -> throw ArithmeticException("Illegal sqrt(x) for x < 0: x = $x")
    }

    val maxPrecision = mathContext.precision + 6
    val acceptableError = ONE.movePointLeft(mathContext.precision + 1)

    var result: BigDecimal
    var adaptivePrecision: Int
    if (x.isDoubleValue()) {
        result = BigDecimal.valueOf(sqrt(x.toDouble()))
        adaptivePrecision = EXPECTED_INITIAL_PRECISION
    } else {
        result = x.times(ONE_HALF, mathContext)
        adaptivePrecision = 1
    }

    var last: BigDecimal

    if (adaptivePrecision < maxPrecision) {
        if (result.times(result).compareTo(x) == 0) {
            return round(result, mathContext) // early exit if x is a square number
        }

        do {
            last = result
            adaptivePrecision = adaptivePrecision shl 1
            if (adaptivePrecision > maxPrecision) {
                adaptivePrecision = maxPrecision
            }
            val mc = MathContext(adaptivePrecision, mathContext.roundingMode)
            result = x.divide(result, mc).plus(last).times(ONE_HALF, mc)
        } while (adaptivePrecision < maxPrecision || result.minus(last).absoluteValue > acceptableError)
    }

    return round(result, mathContext)
}

/**
 * Calculates the n'th root of [BigDecimal] x
 * (see [Wikipedia: Nth root](https://en.wikipedia.org/wiki/Nth_root)).
 *
 * @throws ArithmeticException if n <= 0 or x < 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.root(
    n: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    when (n.signum) {
        -1, 0 -> throw ArithmeticException("Illegal root(x, n) for n <= 0: n = $n")
    }

    when (x.signum) {
        0 -> return ZERO
        -1 -> throw ArithmeticException("Illegal root(x, n) for x < 0: x = $x")
    }

    if (x.isDoubleValue() && n.isDoubleValue()) {
        val initialResult = x.toDouble().pow(1.0 / n.toDouble())
        if (initialResult.isFinite()) {
            return rootUsingNewtonRaphson(x, n, BigDecimal.valueOf(initialResult), mathContext)
        }
    }

    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    return x.pow(ONE.divide(n, mc), mathContext)
}

private fun rootUsingNewtonRaphson(
    x: BigDecimal,
    n: BigDecimal,
    initialResult: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    if (n <= ONE) {
        val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
        return x.pow(ONE.divide(n, mc), mathContext)
    }

    val maxPrecision = mathContext.precision * 2
    val acceptableError = ONE.movePointLeft(mathContext.precision + 1)

    val nMinus1 = n.minus(ONE)
    var result = initialResult
    var adaptivePrecision = 12

    if (adaptivePrecision < maxPrecision) {
        var step: BigDecimal

        do {
            adaptivePrecision *= 3
            if (adaptivePrecision > maxPrecision) {
                adaptivePrecision = maxPrecision
            }
            val mc = MathContext(adaptivePrecision, mathContext.roundingMode)

            step = x.divide(result.pow(nMinus1, mc), mc).minus(result).divide(n, mc)
            result = result.plus(step)
        } while (adaptivePrecision < maxPrecision || step.absoluteValue > acceptableError)
    }

    return round(result, mathContext)
}

/**
 * Calculates the natural logarithm of [BigDecimal] x
 * (see [Wikipedia: Natural logarithm](http://en.wikipedia.org/wiki/Natural_logarithm)).
 *
 * @throws ArithmeticException if x <= 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.log(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x.signum <= 0) {
        throw ArithmeticException("Illegal log(x) for x <= 0: x = $x")
    }
    if (x.compareTo(ONE) == 0) {
        return ZERO
    }

    val result =
        when (x.compareTo(TEN)) {
            0 -> logTen(mathContext)
            1 -> logUsingExponent(x, mathContext)
            else -> logUsingTwoThree(x, mathContext)
        }

    return round(result, mathContext)
}

/**
 * Calculates the logarithm of [BigDecimal] x to the base 2.
 *
 * @throws ArithmeticException if x <= 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.log2(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

    val result = x.log(mc).divide(logTwo(mc), mc)
    return round(result, mathContext)
}

/**
 * Calculates the logarithm of [BigDecimal] x to the base 10.
 *
 * @throws ArithmeticException if x <= 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.log10(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 2, mathContext.roundingMode)

    val result = x.log(mc).divide(logTen(mc), mc)
    return round(result, mathContext)
}

private fun logUsingNewton(
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    // https://en.wikipedia.org/wiki/Natural_logarithm in chapter 'High Precision'
    // y = y + 2 * (x-exp(y)) / (x+exp(y))

    val maxPrecision = mathContext.precision + 20
    val acceptableError = ONE.movePointLeft(mathContext.precision + 1)

    var result: BigDecimal
    var adaptivePrecision: Int
    val doubleX = x.toDouble()
    if (doubleX > 0.0 && x.isDoubleValue()) {
        result = BigDecimal.valueOf(ln(doubleX))
        adaptivePrecision = EXPECTED_INITIAL_PRECISION
    } else {
        result = x.divide(TWO, mathContext)
        adaptivePrecision = 1
    }

    var step: BigDecimal

    do {
        adaptivePrecision *= 3
        if (adaptivePrecision > maxPrecision) {
            adaptivePrecision = maxPrecision
        }
        val mc = MathContext(adaptivePrecision, mathContext.roundingMode)

        val expY = result.exp(mc)
        step = TWO.times(x.minus(expY)).divide(x.plus(expY), mc)
        result = result.plus(step)
    } while (adaptivePrecision < maxPrecision || step.absoluteValue > acceptableError)

    return result
}

private fun logUsingExponent(
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    val mcDouble = MathContext(mathContext.precision shl 1, mathContext.roundingMode)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

    val exponent = x.exponent()
    val mantissa = x.mantissa()

    var result = logUsingTwoThree(mantissa, mc)
    if (exponent != 0) {
        result = result.plus(BigDecimal.of(exponent).times(logTen(mcDouble), mc))
    }
    return result
}

@Suppress("CyclomaticComplexMethod", "LongMethod")
private fun logUsingTwoThree(
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    val mcDouble = MathContext(mathContext.precision shl 1, mathContext.roundingMode)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

    var factorOfTwo = 0
    var powerOfTwo = 1
    var factorOfThree = 0
    var powerOfThree = 1

    var value = x.toDouble()
    if (value < 0.01) {
        // do nothing
    } else if (value < 0.1) { // never happens when called by logUsingExponent()
        while (value < 0.6) {
            value *= 2.0
            factorOfTwo--
            powerOfTwo = powerOfTwo shl 1
        }
    } else if (value < 0.115) { // (0.1 - 0.11111 - 0.115) -> (0.9 - 1.0 - 1.035)
        factorOfThree = -2
        powerOfThree = 9
    } else if (value < 0.14) { // (0.115 - 0.125 - 0.14) -> (0.92 - 1.0 - 1.12)
        factorOfTwo = -3
        powerOfTwo = 8
    } else if (value < 0.2) { // (0.14 - 0.16667 - 0.2) - (0.84 - 1.0 - 1.2)
        factorOfTwo = -1
        powerOfTwo = 2
        factorOfThree = -1
        powerOfThree = 3
    } else if (value < 0.3) { // (0.2 - 0.25 - 0.3) -> (0.8 - 1.0 - 1.2)
        factorOfTwo = -2
        powerOfTwo = 4
    } else if (value < 0.42) { // (0.3 - 0.33333 - 0.42) -> (0.9 - 1.0 - 1.26)
        factorOfThree = -1
        powerOfThree = 3
    } else if (value < 0.7) { // (0.42 - 0.5 - 0.7) -> (0.84 - 1.0 - 1.4)
        factorOfTwo = -1
        powerOfTwo = 2
    } else if (value < 1.4) { // (0.7 - 1.0 - 1.4) -> (0.7 - 1.0 - 1.4)
        // do nothing
    } else if (value < 2.5) { // (1.4 - 2.0 - 2.5) -> (0.7 - 1.0 - 1.25)
        factorOfTwo = 1
        powerOfTwo = 2
    } else if (value < 3.5) { // (2.5 - 3.0 - 3.5) -> (0.833333 - 1.0 - 1.166667)
        factorOfThree = 1
        powerOfThree = 3
    } else if (value < 5.0) { // (3.5 - 4.0 - 5.0) -> (0.875 - 1.0 - 1.25)
        factorOfTwo = 2
        powerOfTwo = 4
    } else if (value < 7.0) { // (5.0 - 6.0 - 7.0) -> (0.833333 - 1.0 - 1.166667)
        factorOfThree = 1
        powerOfThree = 3
        factorOfTwo = 1
        powerOfTwo = 2
    } else if (value < 8.5) { // (7.0 - 8.0 - 8.5) -> (0.875 - 1.0 - 1.0625)
        factorOfTwo = 3
        powerOfTwo = 8
    } else if (value < 10.0) { // (8.5 - 9.0 - 10.0) -> (0.94444 - 1.0 - 1.11111)
        factorOfThree = 2
        powerOfThree = 9
    } else {
        while (value > 1.4) { // never happens when called by logUsingExponent()
            value /= 2.0
            factorOfTwo++
            powerOfTwo = powerOfTwo shl 1
        }
    }

    var correctedX = x
    var result = ZERO

    if (factorOfTwo > 0) {
        correctedX = correctedX.divide(BigDecimal.of(powerOfTwo), mc)
        result = result.plus(logTwo(mcDouble).times(BigDecimal.of(factorOfTwo), mc))
    } else if (factorOfTwo < 0) {
        correctedX = correctedX.times(BigDecimal.of(powerOfTwo), mc)
        result = result.minus(logTwo(mcDouble).times(BigDecimal.of(-factorOfTwo), mc))
    }

    if (factorOfThree > 0) {
        correctedX = correctedX.divide(BigDecimal.of(powerOfThree), mc)
        result = result.plus(logThree(mcDouble).times(BigDecimal.of(factorOfThree), mc))
    } else if (factorOfThree < 0) {
        correctedX = correctedX.times(BigDecimal.of(powerOfThree), mc)
        result = result.minus(logThree(mcDouble).times(BigDecimal.of(-factorOfThree), mc))
    }

    // identity checks, as in Java: nothing was factored out
    if (x === correctedX && result === ZERO) {
        return logUsingNewton(x, mathContext)
    }

    result = result.plus(logUsingNewton(correctedX, mc), mc)

    return result
}

/**
 * Returns the number pi, calculated with the Chudnovsky algorithm
 * (see [Wikipedia: Pi](https://en.wikipedia.org/wiki/Pi)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun pi(mathContext: MathContext): BigDecimal {
    checkMathContext(mathContext)
    // as in Java: a freshly computed value is returned as is, a cached one is rounded
    var cached: BigDecimal? = null
    val computed =
        piCacheLock.withLock {
            val current = piCache
            if (current != null && mathContext.precision <= current.precision) {
                cached = current
                null
            } else {
                piChudnovski(mathContext).also { piCache = it }
            }
        }
    return computed ?: round(cached!!, mathContext)
}

private fun piChudnovski(mathContext: MathContext): BigDecimal {
    val mc = MathContext(mathContext.precision + 10, mathContext.roundingMode)

    val value24 = BigDecimal.of(24)
    val value640320 = BigDecimal.of(640320)
    val value13591409 = BigDecimal.of(13591409)
    val value545140134 = BigDecimal.of(545140134)
    val valueDivisor = value640320.pow(3).divide(value24, mc)

    var sumA = ONE
    var sumB = ZERO

    var a = ONE
    var dividendTerm1: Long = 5 // -(6*k - 5)
    var dividendTerm2: Long = -1 // 2*k - 1
    var dividendTerm3: Long = -1 // 6*k - 1
    var kPower3: BigDecimal

    val iterationCount = (mc.precision + 13) / 14
    for (k in 1..iterationCount.toLong()) {
        val valueK = BigDecimal.of(k)
        dividendTerm1 += -6
        dividendTerm2 += 2
        dividendTerm3 += 6
        val dividend =
            BigDecimal
                .of(dividendTerm1)
                .times(BigDecimal.of(dividendTerm2))
                .times(BigDecimal.of(dividendTerm3))
        kPower3 = valueK.pow(3)
        val divisor = kPower3.times(valueDivisor, mc)
        a = a.times(dividend).divide(divisor, mc)
        val b = valueK.times(a, mc)

        sumA = sumA.plus(a)
        sumB = sumB.plus(b)
    }

    val value426880 = BigDecimal.of(426880)
    val value10005 = BigDecimal.of(10005)
    val factor = value426880.times(sqrt(value10005, mc))
    val pi = factor.divide(value13591409.times(sumA, mc).plus(value545140134.times(sumB, mc)), mc)

    return round(pi, mathContext)
}

/**
 * Returns the number e (see [Wikipedia: E (mathematical_constant)](https://en.wikipedia.org/wiki/E_(mathematical_constant))).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun e(mathContext: MathContext): BigDecimal {
    checkMathContext(mathContext)
    // as in Java: a freshly computed value is returned as is, a cached one is rounded
    var cached: BigDecimal? = null
    val computed =
        eCacheLock.withLock {
            val current = eCache
            if (current != null && mathContext.precision <= current.precision) {
                cached = current
                null
            } else {
                ONE.exp(mathContext).also { eCache = it }
            }
        }
    return computed ?: round(cached!!, mathContext)
}

private fun logTen(mathContext: MathContext): BigDecimal {
    // as in Java: a freshly computed value is returned as is, a cached one is rounded
    var cached: BigDecimal? = null
    val computed =
        log10CacheLock.withLock {
            val current = log10Cache
            if (current != null && mathContext.precision <= current.precision) {
                cached = current
                null
            } else {
                logUsingNewton(TEN, mathContext).also { log10Cache = it }
            }
        }
    return computed ?: round(cached!!, mathContext)
}

private fun logTwo(mathContext: MathContext): BigDecimal {
    // as in Java: a freshly computed value is returned as is, a cached one is rounded
    var cached: BigDecimal? = null
    val computed =
        log2CacheLock.withLock {
            val current = log2Cache
            if (current != null && mathContext.precision <= current.precision) {
                cached = current
                null
            } else {
                logUsingNewton(TWO, mathContext).also { log2Cache = it }
            }
        }
    return computed ?: round(cached!!, mathContext)
}

private fun logThree(mathContext: MathContext): BigDecimal {
    // as in Java: a freshly computed value is returned as is, a cached one is rounded
    var cached: BigDecimal? = null
    val computed =
        log3CacheLock.withLock {
            val current = log3Cache
            if (current != null && mathContext.precision <= current.precision) {
                cached = current
                null
            } else {
                logUsingNewton(THREE, mathContext).also { log3Cache = it }
            }
        }
    return computed ?: round(cached!!, mathContext)
}

/**
 * Calculates the natural exponent of [BigDecimal] x: e<sup>x</sup>
 * (see [Wikipedia: Exponent](https://en.wikipedia.org/wiki/Exponent)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.exp(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x.signum == 0) {
        return ONE
    }

    return expIntegralFractional(x, mathContext)
}

private fun expIntegralFractional(
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    val integralPart = x.integralPart()

    if (integralPart.signum == 0) {
        return expTaylor(x, mathContext)
    }

    val fractionalPart = x.minus(integralPart)

    val mc = MathContext(mathContext.precision + 10, mathContext.roundingMode)

    val z = ONE.plus(fractionalPart.divide(integralPart, mc))
    val t = expTaylor(z, mc)

    val result = t.pow(integralPart.toIntExact().toLong(), mc)

    return round(result, mathContext)
}

private fun expTaylor(
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    var x = x
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)

    x = x.divide(BigDecimal.of(256), mc)

    var result = ExpCalculator.calculate(x, mc)
    result = result.pow(256L, mc)
    return round(result, mathContext)
}

/**
 * Calculates the sine (sinus) of [BigDecimal] x
 * (see [Wikipedia: Sine](http://en.wikipedia.org/wiki/Sine)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.sin(mathContext: MathContext): BigDecimal {
    var x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)

    if (x.absoluteValue > ROUGHLY_TWO_PI) {
        val mc2 = MathContext(mc.precision + 4, mathContext.roundingMode)
        val twoPi = TWO.times(pi(mc2))
        x = x.rem(twoPi, mc2)
    }

    val result = SinCalculator.calculate(x, mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc sine (inverted sine) of [BigDecimal] x
 * (see [Wikipedia: Arcsine](http://en.wikipedia.org/wiki/Arcsine)).
 *
 * @throws ArithmeticException if x > 1 or x < -1
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.asin(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x > ONE) {
        throw ArithmeticException("Illegal asin(x) for x > 1: x = $x")
    }
    if (x < MINUS_ONE) {
        throw ArithmeticException("Illegal asin(x) for x < -1: x = $x")
    }

    if (x.signum == -1) {
        return -(-x).asin(mathContext)
    }

    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)

    if (x >= BigDecimal.valueOf(0.707107)) {
        val xTransformed = sqrt(ONE.minus(x.times(x)), mc)
        return xTransformed.acos(mathContext)
    }

    val result = AsinCalculator.calculate(x, mc)
    return round(result, mathContext)
}

/**
 * Calculates the cosine (cosinus) of [BigDecimal] x
 * (see [Wikipedia: Cosine](http://en.wikipedia.org/wiki/Cosine)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.cos(mathContext: MathContext): BigDecimal {
    var x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)

    if (x.absoluteValue > ROUGHLY_TWO_PI) {
        val mc2 = MathContext(mc.precision + 4, mathContext.roundingMode)
        val twoPi = TWO.times(pi(mc2), mc2)
        x = x.rem(twoPi, mc2)
    }

    val result = CosCalculator.calculate(x, mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc cosine (inverted cosine) of [BigDecimal] x
 * (see [Wikipedia: Arccosine](http://en.wikipedia.org/wiki/Arccosine)).
 *
 * @throws ArithmeticException if x > 1 or x < -1
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.acos(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x > ONE) {
        throw ArithmeticException("Illegal acos(x) for x > 1: x = $x")
    }
    if (x < MINUS_ONE) {
        throw ArithmeticException("Illegal acos(x) for x < -1: x = $x")
    }

    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)

    val result = pi(mc).divide(TWO, mc).minus(x.asin(mc))
    return round(result, mathContext)
}

/**
 * Calculates the tangens of [BigDecimal] x
 * (see [Wikipedia: Tangens](http://en.wikipedia.org/wiki/Tangens)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.tan(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x.signum == 0) {
        return ZERO
    }

    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)
    val result = x.sin(mc).divide(x.cos(mc), mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc tangens (inverted tangens) of [BigDecimal] x
 * (see [Wikipedia: Arctangens](http://en.wikipedia.org/wiki/Arctangens)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.atan(mathContext: MathContext): BigDecimal {
    var x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)

    x = x.divide(sqrt(ONE.plus(x.times(x, mc)), mc), mc)

    val result = x.asin(mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc tangens (inverted tangens) of [BigDecimal] y / x in the range -pi to pi
 * (see [Wikipedia: Atan2](http://en.wikipedia.org/wiki/Atan2)).
 *
 * @throws ArithmeticException if x = 0 and y = 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun atan2(
    y: BigDecimal,
    x: BigDecimal,
    mathContext: MathContext,
): BigDecimal {
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 3, mathContext.roundingMode)

    return if (x.signum > 0) { // x > 0
        y.divide(x, mc).atan(mathContext)
    } else if (x.signum < 0) {
        if (y.signum > 0) { // x < 0 && y > 0
            y.divide(x, mc).atan(mc).plus(pi(mc), mathContext)
        } else if (y.signum < 0) { // x < 0 && y < 0
            y.divide(x, mc).atan(mc).minus(pi(mc), mathContext)
        } else { // x < 0 && y = 0
            pi(mathContext)
        }
    } else {
        if (y.signum > 0) { // x == 0 && y > 0
            pi(mc).divide(TWO, mathContext)
        } else if (y.signum < 0) { // x == 0 && y < 0
            -pi(mc).divide(TWO, mathContext)
        } else {
            throw ArithmeticException("Illegal atan2(y, x) for x = 0; y = 0")
        }
    }
}

/**
 * Calculates the cotangens of [BigDecimal] x
 * (see [Wikipedia: Cotangens](http://en.wikipedia.org/wiki/Cotangens)).
 *
 * @throws ArithmeticException if x = 0
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.cot(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    if (x.signum == 0) {
        throw ArithmeticException("Illegal cot(x) for x = 0")
    }

    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)
    val result = x.cos(mc).divide(x.sin(mc), mc)
    return round(result, mathContext)
}

/**
 * Calculates the inverse cotangens (arc cotangens) of [BigDecimal] x
 * (see [Wikipedia: Arccotangens](http://en.wikipedia.org/wiki/Arccotangens)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.acot(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)
    val result = pi(mc).divide(TWO, mc).minus(x.atan(mc))
    return round(result, mathContext)
}

/**
 * Calculates the hyperbolic sine of [BigDecimal] x
 * (see [Wikipedia: Hyperbolic function](https://en.wikipedia.org/wiki/Hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.sinh(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)
    val result = SinhCalculator.calculate(x, mc)
    return round(result, mathContext)
}

/**
 * Calculates the hyperbolic cosine of [BigDecimal] x
 * (see [Wikipedia: Hyperbolic function](https://en.wikipedia.org/wiki/Hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.cosh(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)
    val result = CoshCalculator.calculate(x, mc)
    return round(result, mathContext)
}

/**
 * Calculates the hyperbolic tangens of [BigDecimal] x
 * (see [Wikipedia: Hyperbolic function](https://en.wikipedia.org/wiki/Hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.tanh(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result = x.sinh(mc).divide(x.cosh(mc), mc)
    return round(result, mathContext)
}

/**
 * Calculates the hyperbolic cotangens of [BigDecimal] x
 * (see [Wikipedia: Hyperbolic function](https://en.wikipedia.org/wiki/Hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.coth(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result = x.cosh(mc).divide(x.sinh(mc), mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc hyperbolic sine (inverse hyperbolic sine) of [BigDecimal] x
 * (see [Wikipedia: Inverse hyperbolic function](https://en.wikipedia.org/wiki/Inverse_hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.asinh(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 10, mathContext.roundingMode)
    val result = x.plus(sqrt(x.times(x, mc).plus(ONE, mc), mc)).log(mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc hyperbolic cosine (inverse hyperbolic cosine) of [BigDecimal] x
 * (see [Wikipedia: Inverse hyperbolic function](https://en.wikipedia.org/wiki/Inverse_hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.acosh(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result = x.plus(sqrt(x.times(x).minus(ONE), mc)).log(mc)
    return round(result, mathContext)
}

/**
 * Calculates the arc hyperbolic tangens (inverse hyperbolic tangens) of [BigDecimal] x
 * (see [Wikipedia: Inverse hyperbolic function](https://en.wikipedia.org/wiki/Inverse_hyperbolic_function)).
 *
 * @throws ArithmeticException if x >= 1 or x <= -1
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.atanh(mathContext: MathContext): BigDecimal {
    val x = this
    if (x >= ONE) {
        throw ArithmeticException("Illegal atanh(x) for x >= 1: x = $x")
    }
    if (x <= MINUS_ONE) {
        throw ArithmeticException("Illegal atanh(x) for x <= -1: x = $x")
    }

    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result =
        ONE
            .plus(x)
            .divide(ONE.minus(x), mc)
            .log(mc)
            .times(ONE_HALF)
    return round(result, mathContext)
}

/**
 * Calculates the arc hyperbolic cotangens (inverse hyperbolic cotangens) of [BigDecimal] x
 * (see [Wikipedia: Inverse hyperbolic function](https://en.wikipedia.org/wiki/Inverse_hyperbolic_function)).
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.acoth(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result =
        x
            .plus(ONE)
            .divide(x.minus(ONE), mc)
            .log(mc)
            .times(ONE_HALF)
    return round(result, mathContext)
}

/**
 * Converts an angle measured in radians to an approximately equivalent angle measured in degrees.
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.toDegrees(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result = x.times(ONE_HUNDRED_EIGHTY.divide(pi(mc), mc), mc)
    return round(result, mathContext)
}

/**
 * Converts an angle measured in degrees to an approximately equivalent angle measured in radians.
 *
 * @throws UnsupportedOperationException if the [MathContext] has unlimited precision
 */
fun BigDecimal.toRadians(mathContext: MathContext): BigDecimal {
    val x = this
    checkMathContext(mathContext)
    val mc = MathContext(mathContext.precision + 6, mathContext.roundingMode)
    val result = x.times(pi(mc).divide(ONE_HUNDRED_EIGHTY, mc), mc)
    return round(result, mathContext)
}

private fun checkMathContext(mathContext: MathContext) {
    if (mathContext.precision == 0) {
        throw UnsupportedOperationException("Unlimited MathContext not supported")
    }
}
