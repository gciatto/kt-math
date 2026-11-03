/*
 * Ported from big-math 2.3.2 (https://github.com/eobermuhlner/big-math), ch.obermuhlner.math.big.BigComplex and
 * ch.obermuhlner.math.big.BigComplexMath (whose static methods are members here).
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

package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A complex number, with a real part [re] and an imaginary part [im], both [BigDecimal]s.
 *
 * Addition, subtraction and multiplication are exact unless given a [MathContext]; division and the functions
 * (`exp`, `log`, `sin`, `pow`, `sqrt`, ...: big-math's `BigComplexMath`, members here) need one.
 *
 * Two [BigComplex]es are [equal][equals] if their parts have the same values, regardless of their scales (unlike
 * [BigDecimal]s): [strictEquals] compares the scales too.
 *
 * @property re the real part.
 * @property im the imaginary part.
 */
@JsExport
class BigComplex private constructor(
    @JvmField val re: BigDecimal,
    @JvmField val im: BigDecimal,
) {
    /** Returns this plus [value], exactly. */
    @JsName("plus")
    operator fun plus(value: BigComplex): BigComplex =
        valueOf(
            re.plus(value.re),
            im.plus(value.im),
        )

    /** Returns this plus [value], rounded with [mathContext]. */
    @JsName("plusWithContext")
    fun plus(
        value: BigComplex,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.plus(value.re, mathContext),
            im.plus(value.im, mathContext),
        )

    /** Returns this plus the real [value], rounded with [mathContext]. */
    @JsName("plusBigDecimalWithContext")
    fun plus(
        value: BigDecimal,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.plus(value, mathContext),
            im,
        )

    /** Returns this plus the real [value], exactly. */
    @JsName("plusBigDecimal")
    operator fun plus(value: BigDecimal): BigComplex =
        valueOf(
            re.plus(value),
            im,
        )

    /** Returns this plus the real [value] (as by [BigDecimal.valueOf]), exactly. */
    @JsName("plusDouble")
    operator fun plus(value: Double): BigComplex = plus(BigDecimal.valueOf(value))

    /** Returns this minus [value], exactly. */
    @JsName("minus")
    operator fun minus(value: BigComplex): BigComplex =
        valueOf(
            re.minus(value.re),
            im.minus(value.im),
        )

    /** Returns this minus [value], rounded with [mathContext]. */
    @JsName("minusWithContext")
    fun minus(
        value: BigComplex,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.minus(value.re, mathContext),
            im.minus(value.im, mathContext),
        )

    /** Returns this minus the real [value], rounded with [mathContext]. */
    @JsName("minusBigDecimalWithContext")
    fun minus(
        value: BigDecimal,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.minus(value, mathContext),
            im,
        )

    /** Returns this minus the real [value], exactly. */
    @JsName("minusBigDecimal")
    operator fun minus(value: BigDecimal): BigComplex =
        valueOf(
            re.minus(value),
            im,
        )

    /** Returns this minus the real [value] (as by [BigDecimal.valueOf]), exactly. */
    @JsName("minusDouble")
    operator fun minus(value: Double): BigComplex = minus(BigDecimal.valueOf(value))

    /** Returns this times [value], exactly. */
    @JsName("times")
    operator fun times(value: BigComplex): BigComplex =
        valueOf(
            re.times(value.re).minus(im.times(value.im)),
            re.times(value.im).plus(im.times(value.re)),
        )

    /** Returns this times [value], rounded with [mathContext]. */
    @JsName("timesWithContext")
    fun times(
        value: BigComplex,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.times(value.re, mathContext).minus(im.times(value.im, mathContext), mathContext),
            re.times(value.im, mathContext).plus(im.times(value.re, mathContext), mathContext),
        )

    /** Returns this times the real [value], rounded with [mathContext]. */
    @JsName("timesBigDecimalWithContext")
    fun times(
        value: BigDecimal,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.times(value, mathContext),
            im.times(value, mathContext),
        )

    /** Returns this times the real [value], exactly. */
    @JsName("timesBigDecimal")
    operator fun times(value: BigDecimal): BigComplex =
        valueOf(
            re.times(value),
            im.times(value),
        )

    /** Returns this times the real [value] (as by [BigDecimal.valueOf]), exactly. */
    @JsName("timesDouble")
    operator fun times(value: Double): BigComplex = times(BigDecimal.valueOf(value))

    /** Returns this divided by [value], rounded with [mathContext]. */
    @JsName("div")
    fun div(
        value: BigComplex,
        mathContext: MathContext,
    ): BigComplex = times(value.reciprocal(mathContext), mathContext)

    /** Returns this divided by the real [value], rounded with [mathContext]. */
    @JsName("divBigDecimal")
    fun div(
        value: BigDecimal,
        mathContext: MathContext,
    ): BigComplex =
        valueOf(
            re.divide(value, mathContext),
            im.divide(value, mathContext),
        )

    /** Returns this divided by the real [value] (as by [BigDecimal.valueOf]), rounded with [mathContext]. */
    @JsName("divDouble")
    fun div(
        value: Double,
        mathContext: MathContext,
    ): BigComplex = div(BigDecimal.valueOf(value), mathContext)

    /** Returns the reciprocal of this, 1/this, rounded with [mathContext]. */
    fun reciprocal(mathContext: MathContext): BigComplex {
        val scale = absSquare(mathContext)
        return valueOf(
            re.divide(scale, mathContext),
            (-im).divide(scale, mathContext),
        )
    }

    /** Returns the conjugate of this: the same real part, the opposite imaginary part. */
    fun conjugate(): BigComplex = valueOf(re, -im)

    /** Returns the opposite of this, -this. */
    operator fun unaryMinus(): BigComplex = valueOf(-re, -im)

    /** Returns the absolute value (magnitude) of this, rounded with [mathContext]. */
    fun abs(mathContext: MathContext): BigDecimal = sqrt(absSquare(mathContext), mathContext)

    /** Returns the angle (argument) of this, in radians, rounded with [mathContext]. */
    fun angle(mathContext: MathContext): BigDecimal = atan2(im, re, mathContext)

    /** Returns the square of the absolute value of this, rounded with [mathContext]. */
    fun absSquare(mathContext: MathContext): BigDecimal =
        re.times(re, mathContext).plus(im.times(im, mathContext), mathContext)

    /** Returns whether this is a real number, i.e. its imaginary part is 0. */
    fun isReal(): Boolean = im.signum == 0

    /** Returns the real part of this, as a [BigComplex] (see [re] for it as a [BigDecimal]). */
    @JsName("realPart")
    fun re(): BigComplex = valueOf(re, BigDecimal.ZERO)

    /** Returns the imaginary part of this, as a real [BigComplex] (see [im] for it as a [BigDecimal]). */
    @JsName("imaginaryPart")
    fun im(): BigComplex = valueOf(BigDecimal.ZERO, im)

    /** Returns this with both parts rounded with [mathContext]. */
    fun round(mathContext: MathContext): BigComplex = valueOf(re.rounded(mathContext), im.rounded(mathContext))

    /**
     * Calculates the factorial of this, with Spouge's approximation for non-integer values (see
     * [Wikipedia: Spouge's approximation](https://en.wikipedia.org/wiki/Spouge%27s_approximation)).
     *
     * @throws ArithmeticException if this is a negative integer value (-1, -2, -3, ...)
     */
    fun factorial(mathContext: MathContext): BigComplex {
        val x = this
        if (x.isReal() && x.re.isIntValue()) {
            return valueOf(factorial(x.re.toIntExact()).rounded(mathContext))
        }

        // https://en.wikipedia.org/wiki/Spouge%27s_approximation
        val mc = MathContext(mathContext.precision * 2, mathContext.roundingMode)

        val a = mathContext.precision * 13 / 10
        val constants = getSpougeFactorialConstants(a)

        val bigA = BigDecimal.of(a)

        var negative = false
        var factor = valueOf(constants[0])
        for (k in 1 until a) {
            val bigK = BigDecimal.of(k)
            factor = factor.plus(valueOf(constants[k]).div(x.plus(bigK), mc), mc)
            negative = !negative
        }

        var result = x.plus(bigA, mc).pow(x.plus(BigDecimal.valueOf(0.5), mc), mc)
        result = result.times((-x).minus(bigA, mc).exp(mc), mc)
        result = result.times(factor, mc)

        return result.round(mathContext)
    }

    /** Calculates the gamma function of this, `(this - 1)!` (see [factorial]). */
    fun gamma(mathContext: MathContext): BigComplex = minus(ONE).factorial(mathContext)

    /** Calculates the natural exponent of this, *e<sup>this</sup>*. */
    fun exp(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        val expRe = x.re.exp(mc)
        return valueOf(
            expRe.times(x.im.cos(mc), mc).rounded(mathContext),
            expRe.times(x.im.sin(mc), mc),
        ).round(mathContext)
    }

    /** Calculates the sine (sinus) of this. */
    fun sin(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return valueOf(
            x.re
                .sin(mc)
                .times(x.im.cosh(mc), mc)
                .rounded(mathContext),
            x.re
                .cos(mc)
                .times(x.im.sinh(mc), mc)
                .rounded(mathContext),
        )
    }

    /** Calculates the cosine (cosinus) of this. */
    fun cos(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return valueOf(
            x.re
                .cos(mc)
                .times(x.im.cosh(mc), mc)
                .rounded(mathContext),
            (-x.re.sin(mc).times(x.im.sinh(mc), mc)).rounded(mathContext),
        )
    }

    //
    // http://scipp.ucsc.edu/~haber/archives/physics116A10/arc_10.pdf

    /** Calculates the tangens of this. */
    fun tan(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return x.sin(mc).div(x.cos(mc), mc).round(mathContext)
    }

    /** Calculates the arc tangens (inverted tangens) of this. */
    fun atan(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return I
            .minus(x, mc)
            .div(I.plus(x, mc), mc)
            .log(mc)
            .div(I, mc)
            .div(TWO, mc)
            .round(mathContext)
    }

    /** Calculates the inverse cotangens (arc cotangens) of this. */
    fun acot(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return x
            .plus(I, mc)
            .div(x.minus(I, mc), mc)
            .log(mc)
            .div(I, mc)
            .div(TWO, mc)
            .round(mathContext)
    }

    /** Calculates the arc sine (inverted sine) of this. */
    fun asin(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return (-I)
            .times(
                I.times(x, mc).plus(ONE.minus(x.times(x, mc), mc).sqrt(mc), mc).log(mc),
                mc,
            ).round(mathContext)
    }

    /** Calculates the arc cosine (inverted cosine) of this. */
    fun acos(mathContext: MathContext): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return (-I)
            .times(
                x.plus(x.times(x, mc).minus(ONE, mc).sqrt(mc), mc).log(mc),
                mc,
            ).round(mathContext)
    }

    /** Calculates the square root of this (the principal one, with a non-negative real part). */
    fun sqrt(mathContext: MathContext): BigComplex {
        val x = this
        // https://math.stackexchange.com/questions/44406/how-do-i-get-the-square-root-of-a-complex-number
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        val magnitude = x.abs(mc)

        val a = x.plus(magnitude, mc)
        return a.div(a.abs(mc), mc).times(sqrt(magnitude, mc), mc).round(mathContext)
    }

    /** Calculates the natural logarithm of this (the principal one, with an imaginary part in (-π, π]). */
    fun log(mathContext: MathContext): BigComplex {
        val x = this
        // https://en.wikipedia.org/wiki/Complex_logarithm
        val mc1 = MathContext(mathContext.precision + 20, mathContext.roundingMode)
        val mc2 = MathContext(mathContext.precision + 5, mathContext.roundingMode)

        return valueOf(
            x.abs(mc1).log(mc1).rounded(mathContext),
            x.angle(mc2),
        ).round(mathContext)
    }

    /** Calculates this to the power of the integer [y], by repeated squaring. */
    @JsName("powLong")
    fun pow(
        y: Long,
        mathContext: MathContext,
    ): BigComplex {
        var x = this
        var y = y
        val mc = MathContext(mathContext.precision + 10, mathContext.roundingMode)

        if (y < 0) {
            return ONE.div(x.pow(-y, mc), mc).round(mathContext)
        }

        var result = ONE
        while (y > 0) {
            if ((y and 1L) == 1L) {
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

        return result.round(mathContext)
    }

    /** Calculates this to the power of the real [y]. */
    @JsName("powBigDecimal")
    fun pow(
        y: BigDecimal,
        mathContext: MathContext,
    ): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        val angleTimesN = x.angle(mc).times(y, mc)
        return valueOf(
            angleTimesN.cos(mc),
            angleTimesN.sin(mc),
        ).times(x.abs(mc).pow(y, mc), mc).round(mathContext)
    }

    /** Calculates this to the power of [y]. */
    @JsName("pow")
    fun pow(
        y: BigComplex,
        mathContext: MathContext,
    ): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return y.times(x.log(mc), mc).exp(mc).round(mathContext)
    }

    /** Calculates the real [n]'th root of this. */
    @JsName("rootBigDecimal")
    fun root(
        n: BigDecimal,
        mathContext: MathContext,
    ): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return x.pow(BigDecimal.ONE.divide(n, mc), mc).round(mathContext)
    }

    /** Calculates the [n]'th root of this. */
    @JsName("root")
    fun root(
        n: BigComplex,
        mathContext: MathContext,
    ): BigComplex {
        val x = this
        val mc = MathContext(mathContext.precision + 4, mathContext.roundingMode)

        return x.pow(ONE.div(n, mc), mc).round(mathContext)
    }

    // TODO add root() for the k'th root - https://math.stackexchange.com/questions/322481/principal-nth-root-of-a-complex-number

    override fun hashCode(): Int = 31 * re.stripTrailingZeros().hashCode() + im.stripTrailingZeros().hashCode()

    /**
     * Returns whether [other] is a [BigComplex] with the same values as this: unlike [BigDecimal.equals], `1.0` and
     * `1.00` are equal (see [strictEquals]).
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BigComplex) return false
        return re.compareTo(other.re) == 0 && im.compareTo(other.im) == 0
    }

    /** Returns whether [other] is a [BigComplex] whose parts are [equal][BigDecimal.equals] to this's, scales too. */
    fun strictEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BigComplex) return false
        return re == other.re && im == other.im
    }

    /** Returns this as `"(re + im i)"`, or `"(re - |im| i)"` if the imaginary part is negative. */
    override fun toString(): String =
        if (im.signum >= 0) {
            "($re + $im i)"
        } else {
            "($re - ${-im} i)"
        }

    companion object {
        private val TWO = BigDecimal.of(2)

        /** The value 0 as [BigComplex]. */
        @JvmField
        val ZERO = BigComplex(BigDecimal.ZERO, BigDecimal.ZERO)

        /** The value 1 as [BigComplex]. */
        @JvmField
        val ONE = BigComplex(BigDecimal.ONE, BigDecimal.ZERO)

        /** The imaginary unit *i* as [BigComplex]. */
        @JvmField
        val I = BigComplex(BigDecimal.ZERO, BigDecimal.ONE)

        /** Returns the [BigComplex] with the [real] and [imaginary] parts (by default, a real number). */
        @JvmStatic
        @JvmOverloads
        @JsName("valueOf")
        fun valueOf(
            real: BigDecimal,
            imaginary: BigDecimal = BigDecimal.ZERO,
        ): BigComplex {
            if (real.signum == 0) {
                if (imaginary.signum == 0) {
                    return ZERO
                }
                if (imaginary.compareTo(BigDecimal.ONE) == 0) {
                    return I
                }
            }
            if (imaginary.signum == 0 && real.compareTo(BigDecimal.ONE) == 0) {
                return ONE
            }

            return BigComplex(real, imaginary)
        }

        /** Returns the real [BigComplex] with the [real] part, converted as by [BigDecimal.valueOf]. */
        @JvmStatic
        @JsName("valueOfDouble")
        fun valueOf(real: Double): BigComplex = valueOf(BigDecimal.valueOf(real), BigDecimal.ZERO)

        /** Returns the [BigComplex] with the [real] and [imaginary] parts, converted as by [BigDecimal.valueOf]. */
        @JvmStatic
        @JsName("valueOfDoubles")
        fun valueOf(
            real: Double,
            imaginary: Double,
        ): BigComplex = valueOf(BigDecimal.valueOf(real), BigDecimal.valueOf(imaginary))

        /** Returns the [BigComplex] with polar coordinates [radius] and [angle] (in radians). */
        @JvmStatic
        @JsName("valueOfPolar")
        fun valueOfPolar(
            radius: BigDecimal,
            angle: BigDecimal,
            mathContext: MathContext,
        ): BigComplex {
            if (radius.signum == 0) {
                return ZERO
            }

            return valueOf(
                radius.times(angle.cos(mathContext), mathContext),
                radius.times(angle.sin(mathContext), mathContext),
            )
        }

        /** As the other [valueOfPolar], converting [radius] and [angle] as by [BigDecimal.valueOf]. */
        @JvmStatic
        @JsName("valueOfPolarDouble")
        fun valueOfPolar(
            radius: Double,
            angle: Double,
            mathContext: MathContext,
        ): BigComplex = valueOfPolar(BigDecimal.valueOf(radius), BigDecimal.valueOf(angle), mathContext)
    }
}
