package org.gciatto.kt.math

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.js.JsStatic
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Immutable, arbitrary-precision signed decimal numbers, as Java's `java.math.BigDecimal`.
 *
 * A [BigDecimal] consists of an arbitrary precision integer [unscaledValue] and a 32-bit integer [scale]: its value
 * is `unscaledValue × 10^-scale`. If zero or positive, the scale is the number of digits to the right of the
 * decimal point; if negative, the unscaled value is multiplied by ten to the power of the negation of the scale.
 *
 * Instances are created with the factories of the companion object ([of], [valueOf]) or with [BigFactory]. On the
 * JVM they wrap a `java.math.BigDecimal`; elsewhere they are implemented in Kotlin, on top of [BigInteger].
 *
 * Arithmetic is available through operators (`+`, `-`, `*`, `/`, `%`, unary `-` and `+`): the plain operators compute
 * exact results (except [div], whose scale is chosen as described there), while the overloads taking a
 * [MathContext] round the result to its precision with its [RoundingMode]. Operations that cannot produce an exact
 * result when one is required (e.g. [div] of `1` by `3`, or rounding with [RoundingMode.UNNECESSARY]) throw an
 * [ArithmeticException].
 *
 * Comparison ([compareTo], and therefore `<`, `>`, ...) is by numerical value, while `equals` also takes the scale
 * into account, as in Java: `2.0` and `2.00` compare as equal, but are not `equals`.
 *
 * The string representation (`toString()`) uses scientific notation when the exponent is needed, as Java's
 * `BigDecimal.toString()`; see also [toPlainString] and [toEngineeringString].
 */
@JsExport
@Suppress(
    "EXPOSED_NOT_EXPORTED_SUPER_INTERFACE_WARNING",
    "ktlint:standard:backing-property-naming",
)
interface BigDecimal : Comparable<BigDecimal> {
    /**
     * The absolute value of this [BigDecimal], with the same [scale] as this.
     */
    @JsName("absoluteValue")
    val absoluteValue: BigDecimal

    /**
     * The signum function of this [BigDecimal]: -1, 0, or 1 as this is negative, zero, or positive.
     */
    @JsName("signum")
    val signum: Int

    /**
     * The *scale* of this [BigDecimal]. If zero or positive, the scale is the number of digits to the right of the
     * decimal point. If negative, the unscaled value of the number is multiplied by ten to the power of the negation
     * of the scale. For example, a scale of `-3` means the unscaled value is multiplied by 1000.
     */
    @JsName("scale")
    val scale: Int

    /**
     * The *precision* of this [BigDecimal], i.e. the number of digits in its [unscaledValue].
     *
     * The precision of a zero value is 1.
     */
    @JsName("precision")
    val precision: Int

    /**
     * The *unscaled value* of this [BigDecimal], i.e. `this × 10^scale`.
     */
    @JsName("unscaledValue")
    val unscaledValue: BigInteger

    /**
     * Returns a [BigDecimal] whose value is `this + augend` and whose scale is `max(this.scale, augend.scale)`.
     * This is the `+` operator.
     *
     * @param augend value to be added to this [BigDecimal].
     * @return `this + augend`
     */
    @JsName("plus")
    operator fun plus(augend: BigDecimal?): BigDecimal

    /**
     * As [plus], rounding according to [mc].
     *
     * If either number is zero and the precision setting is nonzero, then the other number, rounded if necessary, is
     * used as the result.
     *
     * @param augend value to be added to this [BigDecimal].
     * @param mc the context to use.
     * @return `this + augend`, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY].
     */
    @JsName("plusWithContext")
    fun plus(
        augend: BigDecimal?,
        mc: MathContext,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is `this - subtrahend` and whose scale is
     * `max(this.scale, subtrahend.scale)`. This is the `-` operator.
     *
     * @param subtrahend value to be subtracted from this [BigDecimal].
     * @return `this - subtrahend`
     */
    @JsName("minus")
    operator fun minus(subtrahend: BigDecimal): BigDecimal

    /**
     * As [minus], rounding according to [mc].
     *
     * If [subtrahend] is zero, then this, rounded if necessary, is used as the result. If this is zero, then the
     * result is `subtrahend.unaryMinus(mc)`.
     *
     * @param subtrahend value to be subtracted from this [BigDecimal].
     * @param mc the context to use.
     * @return `this - subtrahend`, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY].
     */
    @JsName("minusWithContext")
    fun minus(
        subtrahend: BigDecimal,
        mc: MathContext,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is `this × multiplicand` and whose scale is
     * `this.scale + multiplicand.scale`. This is the `*` operator.
     *
     * @param multiplicand value to be multiplied by this [BigDecimal].
     * @return `this * multiplicand`
     */
    @JsName("times")
    operator fun times(multiplicand: BigDecimal?): BigDecimal

    /**
     * As [times], rounding according to [mc].
     *
     * @param multiplicand value to be multiplied by this [BigDecimal].
     * @param mc the context to use.
     * @return `this * multiplicand`, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY].
     */
    @JsName("timesWithContext")
    fun times(
        multiplicand: BigDecimal,
        mc: MathContext,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is `this / divisor` and whose preferred scale is
     * `this.scale - divisor.scale`. This is the `/` operator.
     *
     * If the exact quotient cannot be represented (because it has a non-terminating decimal expansion, e.g. `1 / 3`),
     * an [ArithmeticException] is thrown: use the overloads taking a [MathContext], a scale or a [RoundingMode] then.
     * Otherwise, the result is the exact quotient, with the preferred scale if it can represent it, else with the
     * smallest scale that can.
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @return `this / divisor`
     * @throws ArithmeticException if [divisor] is zero, or the exact quotient does not have a terminating decimal
     * expansion.
     */
    @JsName("div")
    operator fun div(divisor: BigDecimal): BigDecimal

    /**
     * As [div], rounding according to [mc].
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @param mc the context to use.
     * @return `this / divisor`, rounded as necessary.
     * @throws ArithmeticException if [divisor] is zero, if the result is inexact but the rounding mode is
     * [RoundingMode.UNNECESSARY], or if `mc.precision == 0` and the quotient has a non-terminating decimal expansion.
     */
    @JsName("divWithContext")
    fun div(
        divisor: BigDecimal,
        mc: MathContext,
    ): BigDecimal?

    /**
     * Returns a [BigDecimal] whose value is `this / divisor` and whose scale is [scale]. If rounding must be
     * performed to generate a result with the specified scale, [roundingMode] is applied.
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @param scale scale of the [BigDecimal] quotient to be returned.
     * @param roundingMode rounding mode to apply.
     * @return `this / divisor`
     * @throws ArithmeticException if [divisor] is zero, or `roundingMode == RoundingMode.UNNECESSARY` and the
     * specified scale is insufficient to represent the result of the division exactly.
     */
    @JsName("divWithScale")
    fun div(
        divisor: BigDecimal,
        scale: Int,
        roundingMode: RoundingMode,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is `this / divisor` and whose scale is `this.scale`. If rounding must be
     * performed to generate a result with that scale, [roundingMode] is applied.
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @param roundingMode rounding mode to apply.
     * @return `this / divisor`
     * @throws ArithmeticException if [divisor] is zero, or `roundingMode == RoundingMode.UNNECESSARY` and
     * `this.scale` is insufficient to represent the result of the division exactly.
     */
    @JsName("divWithRounding")
    fun div(
        divisor: BigDecimal,
        roundingMode: RoundingMode,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is the integer part of the quotient `this / divisor`, rounded towards zero.
     * The preferred scale of the result is `this.scale - divisor.scale`.
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @return the integer part of `this / divisor`.
     * @throws ArithmeticException if [divisor] is zero.
     */
    @JsName("divideToIntegralValue")
    fun divideToIntegralValue(divisor: BigDecimal): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is the integer part of `this / divisor`. Since the integer part of the exact
     * quotient does not depend on the rounding mode, the rounding mode of [mc] does not affect the result. The
     * preferred scale of the result is `this.scale - divisor.scale`.
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @param mc the context to use.
     * @return the integer part of `this / divisor`.
     * @throws ArithmeticException if [divisor] is zero, or `mc.precision > 0` and the result requires a precision
     * of more than `mc.precision` digits.
     */
    @JsName("divideToIntegralValueWithContext")
    fun divideToIntegralValue(
        divisor: BigDecimal,
        mc: MathContext,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is the remainder `this % divisor`. This is the `%` operator.
     *
     * The remainder is given by `this - this.divideToIntegralValue(divisor) * divisor`. Note that this is *not* the
     * modulo operation: the result has the sign of this, and can be negative.
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @return `this % divisor`.
     * @throws ArithmeticException if [divisor] is zero.
     * @see divideToIntegralValue
     */
    @JsName("rem")
    operator fun rem(divisor: BigDecimal): BigDecimal

    /**
     * As [rem], with [mc] affecting the implicit division used to compute the remainder. The remainder computation
     * itself is by definition exact: therefore, the remainder may contain more than `mc.precision` digits.
     *
     * The remainder is given by `this - this.divideToIntegralValue(divisor, mc) * divisor`. Note that this is not the
     * modulo operation (the result can be negative).
     *
     * @param divisor value by which this [BigDecimal] is to be divided.
     * @param mc the context to use.
     * @return `this % divisor`, rounded as necessary.
     * @throws ArithmeticException if [divisor] is zero, if the result is inexact but the rounding mode is
     * [RoundingMode.UNNECESSARY], or if `mc.precision > 0` and the result of `this.divideToIntegralValue(divisor)`
     * would require a precision of more than `mc.precision` digits.
     * @see divideToIntegralValue
     */
    @JsName("remWithContext")
    fun rem(
        divisor: BigDecimal,
        mc: MathContext,
    ): BigDecimal

    /**
     * Returns the results of [divideToIntegralValue] and of [rem] on the two operands, as a pair.
     *
     * If both the integer quotient and the remainder are needed, this method is faster than calling
     * [divideToIntegralValue] and [rem] separately, because the division is carried out only once.
     *
     * @param divisor value by which this [BigDecimal] is to be divided, and the remainder computed.
     * @return a pair whose first element is the quotient (the result of [divideToIntegralValue]) and whose second
     * element is the remainder.
     * @throws ArithmeticException if [divisor] is zero.
     * @see divideToIntegralValue
     * @see rem
     */
    @JsName("divideAndRemainder")
    fun divideAndRemainder(divisor: BigDecimal): Pair<BigDecimal, BigDecimal>

    /**
     * As [divideAndRemainder], with the division computed according to [mc], as in the [divideToIntegralValue] and
     * [rem] overloads taking a [MathContext].
     *
     * @param divisor value by which this [BigDecimal] is to be divided, and the remainder computed.
     * @param mc the context to use.
     * @return a pair whose first element is the quotient (the result of [divideToIntegralValue]) and whose second
     * element is the remainder.
     * @throws ArithmeticException if [divisor] is zero, if the result is inexact but the rounding mode is
     * [RoundingMode.UNNECESSARY], or if `mc.precision > 0` and the result of `this.divideToIntegralValue(divisor)`
     * would require a precision of more than `mc.precision` digits.
     * @see divideToIntegralValue
     * @see rem
     */
    @JsName("divideAndRemainderWithContext")
    fun divideAndRemainder(
        divisor: BigDecimal,
        mc: MathContext,
    ): Pair<BigDecimal, BigDecimal>

    /**
     * Returns an approximation of the square root of this, rounded according to [mc].
     *
     * The preferred scale of the result is `this.scale / 2`. The value of the result is always within one ulp of the
     * exact decimal value for the precision in question. If the rounding mode is [RoundingMode.HALF_UP],
     * [RoundingMode.HALF_DOWN], or [RoundingMode.HALF_EVEN], the result is within one half an ulp of the exact
     * decimal value.
     *
     * The square root of a number numerically equal to [ZERO] is numerically equal to [ZERO], with a preferred scale
     * according to the general rule above. In particular, `ZERO.sqrt(mc) == ZERO` with any [MathContext].
     *
     * @param mc the context to use: by default, `MathContext()` (9 digits, [RoundingMode.HALF_UP]).
     * @return the square root of this.
     * @throws ArithmeticException if this is less than zero, if an exact result is requested (`mc.precision == 0`)
     * and there is no finite decimal expansion of the exact result, or if
     * `mc.roundingMode == RoundingMode.UNNECESSARY` and the exact result cannot fit in `mc.precision` digits.
     * @see BigInteger.sqrt
     */
    @JsName("sqrt")
    fun sqrt(mc: MathContext = MathContext()): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is `this^n`, computed exactly, to unlimited precision. The scale of the
     * result is `this.scale * n`.
     *
     * [n] must be in the range 0 through 999999999, inclusive. `ZERO.pow(0)` returns [ONE].
     *
     * @param n power to raise this [BigDecimal] to.
     * @return `this^n`
     * @throws ArithmeticException if [n] is out of range.
     */
    @JsName("pow")
    infix fun pow(n: Int): BigDecimal

    /**
     * Returns a [BigDecimal] whose value is `this^n`, rounded according to [mc]. The implementation uses the core
     * algorithm defined in ANSI standard X3.274-1996. In general, the result is within two ulps of the exact
     * numerical value for the chosen precision.
     *
     * The X3.274-1996 algorithm is:
     * - an [ArithmeticException] is thrown if
     *   - `abs(n) > 999999999`,
     *   - `mc.precision == 0` and `n < 0`, or
     *   - `mc.precision > 0` and [n] has more than `mc.precision` decimal digits;
     * - if [n] is zero, [ONE] is returned even if this is zero; otherwise,
     * - if [n] is positive, the result is calculated via the repeated squaring technique into a single accumulator.
     *   The individual multiplications with the accumulator use the same settings as [mc], except for a precision
     *   increased to `mc.precision + elength + 1`, where `elength` is the number of decimal digits in [n];
     * - if [n] is negative, the result is calculated as if [n] were positive; this value is then divided into one
     *   using the working precision specified above;
     * - the final value from either the positive or negative case is then rounded to the destination precision.
     *
     * @param n power to raise this [BigDecimal] to.
     * @param mc the context to use.
     * @return `this^n`, using the ANSI standard X3.274-1996 algorithm.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY], or
     * [n] is out of range.
     */
    @JsName("powWithContext")
    fun pow(
        n: Int,
        mc: MathContext,
    ): BigDecimal?

    /**
     * As [absoluteValue], rounding according to [mc].
     *
     * @param mc the context to use.
     * @return the absolute value of this, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY].
     */
    @JsName("absoluteValueWithContext")
    fun absoluteValue(mc: MathContext): BigDecimal?

    /**
     * Returns a [BigDecimal] whose value is `-this` and whose scale is `this.scale`. This is the unary `-` operator.
     *
     * @return `-this`.
     */
    @JsName("unaryMinus")
    operator fun unaryMinus(): BigDecimal

    /**
     * As [unaryMinus], rounding according to [mc].
     *
     * @param mc the context to use.
     * @return `-this`, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY].
     */
    @JsName("unaryMinusWithContext")
    fun unaryMinus(mc: MathContext): BigDecimal?

    /**
     * Returns this [BigDecimal] (`+this`), included for symmetry with [unaryMinus]. This is the unary `+` operator.
     *
     * @return `this`.
     * @see unaryMinus
     */
    @JsName("unaryPlus")
    operator fun unaryPlus(): BigDecimal

    /**
     * Returns `+this`, rounded according to [mc]. The effect of this method is identical to that of [round].
     *
     * @param mc the context to use.
     * @return this, rounded as necessary. A zero result will have a scale of 0.
     * @throws ArithmeticException if the result is inexact but the rounding mode is [RoundingMode.UNNECESSARY].
     * @see round
     */
    @JsName("unaryPlusWithContext")
    fun unaryPlus(mc: MathContext): BigDecimal?

    /**
     * Returns this [BigDecimal] rounded according to [mc]. If the precision setting is 0, no rounding takes place.
     * The effect of this method is identical to that of [unaryPlus] with a [MathContext].
     *
     * @param mc the context to use.
     * @return this, rounded according to [mc].
     * @throws ArithmeticException if the rounding mode is [RoundingMode.UNNECESSARY] and the operation would require
     * rounding.
     * @see unaryPlus
     */
    @JsName("round")
    fun round(mc: MathContext): BigDecimal?

    /**
     * Returns a [BigDecimal] whose scale is [newScale], and whose unscaled value is determined by multiplying or
     * dividing this [BigDecimal]'s unscaled value by the appropriate power of ten to maintain its overall value. If
     * the scale is reduced by the operation, the unscaled value must be divided (rather than multiplied), and the
     * value may be changed; in this case, [roundingMode] is applied to the division.
     *
     * Since [BigDecimal]s are immutable, this method does *not* modify this object, despite its name: it returns a
     * [BigDecimal] with the requested scale, which may or may not be newly allocated.
     *
     * @param newScale scale of the [BigDecimal] to be returned.
     * @param roundingMode the rounding mode to apply.
     * @return a [BigDecimal] whose scale is [newScale], numerically equal to this up to rounding.
     * @throws ArithmeticException if `roundingMode == RoundingMode.UNNECESSARY` and the specified scaling operation
     * would require rounding.
     * @see RoundingMode
     */
    @JsName("setScaleRounding")
    fun setScale(
        newScale: Int,
        roundingMode: RoundingMode,
    ): BigDecimal

    /**
     * As the [setScale] overload taking a [RoundingMode], with the rounding mode given by its legacy integer code,
     * i.e. its [RoundingMode.value] (`0` for [RoundingMode.UP] through `7` for [RoundingMode.UNNECESSARY]).
     *
     * @param newScale scale of the [BigDecimal] to be returned.
     * @param roundingMode the integer code of the rounding mode to apply.
     * @return a [BigDecimal] whose scale is [newScale], numerically equal to this up to rounding.
     * @throws ArithmeticException if [roundingMode] is the code of [RoundingMode.UNNECESSARY] and the specified
     * scaling operation would require rounding.
     * @throws IllegalArgumentException if [roundingMode] does not represent a valid rounding mode.
     */
    @Deprecated(
        "The method setScale(Int, RoundingMode) should be used in preference to this legacy method.",
    )
    @JsName("setScaleInt")
    fun setScale(
        newScale: Int,
        roundingMode: Int,
    ): BigDecimal

    /**
     * Returns a [BigDecimal] whose scale is [newScale] and whose value is numerically equal to this one's. Throws an
     * [ArithmeticException] if this is not possible.
     *
     * This call is typically used to increase the scale, in which case it is guaranteed that there exists a
     * [BigDecimal] of the specified scale and the correct value. It can also be used to reduce the scale if the
     * caller knows that the [BigDecimal] has sufficiently many zeros at the end of its fractional part (i.e., factors
     * of ten in its integer value) to allow for the rescaling without changing its value.
     *
     * This method returns the same result as the two-argument overloads of [setScale], but saves the caller the
     * trouble of specifying a rounding mode in cases where it is irrelevant. Like them, it does not modify this object.
     *
     * @param newScale scale of the [BigDecimal] to be returned.
     * @return a [BigDecimal] whose scale is [newScale] and whose value is equal to this one's.
     * @throws ArithmeticException if the specified scaling operation would require rounding.
     */
    @JsName("setScale")
    fun setScale(newScale: Int): BigDecimal

    /**
     * Returns a [BigDecimal] equivalent to this one, with the decimal point moved [n] places to the left. If [n] is
     * non-negative, the call merely adds [n] to the scale. If [n] is negative, the call is equivalent to
     * `movePointRight(-n)`. The result has value `this × 10^-n` and scale `max(this.scale + n, 0)`.
     *
     * @param n number of places to move the decimal point to the left.
     * @return a [BigDecimal] equivalent to this one, with the decimal point moved [n] places to the left.
     * @throws ArithmeticException if the scale overflows.
     */
    @JsName("movePointLeft")
    fun movePointLeft(n: Int): BigDecimal

    /**
     * Returns a [BigDecimal] equivalent to this one, with the decimal point moved [n] places to the right. If [n] is
     * non-negative, the call merely subtracts [n] from the scale. If [n] is negative, the call is equivalent to
     * `movePointLeft(-n)`. The result has value `this × 10^n` and scale `max(this.scale - n, 0)`.
     *
     * @param n number of places to move the decimal point to the right.
     * @return a [BigDecimal] equivalent to this one, with the decimal point moved [n] places to the right.
     * @throws ArithmeticException if the scale overflows.
     */
    @JsName("movePointRight")
    fun movePointRight(n: Int): BigDecimal

    /**
     * Returns a [BigDecimal] whose numerical value is equal to `this × 10^n`. The scale of the result is
     * `this.scale - n`.
     *
     * @param n the exponent of the power of ten to scale by.
     * @return a [BigDecimal] whose numerical value is equal to `this × 10^n`.
     * @throws ArithmeticException if the scale would be outside the range of a 32-bit integer.
     */
    @JsName("scaleByPowerOfTen")
    fun scaleByPowerOfTen(n: Int): BigDecimal

    /**
     * Returns a [BigDecimal] numerically equal to this one, but with any trailing zeros removed from the
     * representation. For example, stripping the trailing zeros from `600.0`, whose ([unscaledValue], [scale]) is
     * `(6000, 1)`, yields `6E2`, whose ([unscaledValue], [scale]) is `(6, -2)`. If this [BigDecimal] is numerically
     * equal to zero, [ZERO] is returned.
     *
     * @return a numerically equal [BigDecimal] with any trailing zeros removed.
     */
    @JsName("stripTrailingZeros")
    fun stripTrailingZeros(): BigDecimal

    /**
     * Compares this [BigDecimal] with [other] by numerical value: two [BigDecimal]s that are equal in value but have
     * a different scale (like `2.0` and `2.00`) are considered equal by this method, unlike `equals`. This backs the
     * comparison operators `<`, `<=`, `>` and `>=`.
     *
     * @param other [BigDecimal] to which this [BigDecimal] is to be compared.
     * @return -1, 0, or 1 as this [BigDecimal] is numerically less than, equal to, or greater than [other].
     */
    override fun compareTo(other: BigDecimal): Int

    /**
     * The same as [compareTo] (negative, zero or positive as this is less than, equal to or greater than [other]),
     * callable from JS as `compareTo`: [Comparable] isn't exported to JS (KT-83009), so [compareTo] isn't either.
     */
    @JsName("compareTo")
    fun compare(other: BigDecimal): Int = compareTo(other)

    /**
     * Returns the minimum of this [BigDecimal] and `val`.
     *
     * @param val value with which the minimum is to be computed.
     * @return the [BigDecimal] whose value is the lesser of this [BigDecimal] and `val`. If they are equal, as
     * defined by [compareTo], this is returned.
     * @see compareTo
     */
    @JsName("min")
    fun min(`val`: BigDecimal): BigDecimal

    /**
     * Returns the maximum of this [BigDecimal] and `val`.
     *
     * @param val value with which the maximum is to be computed.
     * @return the [BigDecimal] whose value is the greater of this [BigDecimal] and `val`. If they are equal, as
     * defined by [compareTo], this is returned.
     * @see compareTo
     */
    @JsName("max")
    fun max(`val`: BigDecimal): BigDecimal

    /**
     * Returns a string representation of this [BigDecimal], using engineering notation if an exponent is needed.
     *
     * The result is as the one of `toString()`, except that, if exponential notation is used, the power of ten is
     * adjusted to be a multiple of three (engineering notation), such that the integer part of nonzero values is in
     * the range 1 through 999. If exponential notation is used for zero values, a decimal point and one or two
     * fractional zero digits are used, so that the scale of the zero value is preserved.
     *
     * Unlike the output of `toString()`, the output of this method is *not* guaranteed to give back the same
     * ([unscaledValue], [scale]) pair when parsed back with [of]: it is only guaranteed to give back a numerically
     * equal value.
     *
     * @return the string representation of this [BigDecimal], using engineering notation if an exponent is needed.
     */
    @JsName("toEngineeringString")
    fun toEngineeringString(): String

    /**
     * Returns a string representation of this [BigDecimal] without an exponent field. For values with a positive
     * scale, the number of digits to the right of the decimal point is used to indicate the scale. For values with a
     * zero or negative scale, the resulting string is generated as if the value were converted to a numerically equal
     * value with zero scale, and as if all the trailing zeros of the zero scale value were present in the result.
     *
     * The entire string is prefixed by a minus sign `-` if the unscaled value is less than zero. No sign character is
     * prefixed if the unscaled value is zero or positive.
     *
     * If the result of this method is parsed back with [of], only the numerical value of this [BigDecimal] is
     * necessarily recovered: the representation of the new [BigDecimal] may have a different scale. In particular, if
     * this [BigDecimal] has a negative scale, the parsed string has a scale of zero.
     *
     * @return a string representation of this [BigDecimal] without an exponent field.
     * @see toEngineeringString
     */
    @JsName("toPlainString")
    fun toPlainString(): String

    /**
     * Converts this [BigDecimal] to a [BigInteger], discarding any fractional part (i.e. truncating towards zero).
     * Note that this conversion can lose information about the precision of this [BigDecimal].
     *
     * To have an exception thrown if the conversion is inexact (in other words, if a nonzero fractional part is
     * discarded), use [toBigIntegerExact].
     *
     * @return this [BigDecimal] converted to a [BigInteger].
     */
    @JsName("toBigInteger")
    fun toBigInteger(): BigInteger

    /** This [BigDecimal] as a [BigRational], exactly: `1.25` is `5/4` (`125/100`, not reduced). */
    @JsName("toBigRational")
    fun toBigRational(): BigRational = BigRational.valueOf(this)

    /**
     * Converts this [BigDecimal] to a [BigInteger], checking for lost information.
     *
     * @return this [BigDecimal] converted to a [BigInteger].
     * @throws ArithmeticException if this has a nonzero fractional part.
     */
    @JsName("toBigIntegerExact")
    fun toBigIntegerExact(): BigInteger

    /**
     * Converts this [BigDecimal] to a [Long], discarding any fractional part, and returning only the low-order 64
     * bits if the resulting integer is too big to fit in a [Long] (as Java's narrowing conversion from `double` to
     * `long`). Note that this conversion can lose information about the overall magnitude and precision of this
     * [BigDecimal], as well as return a result with the opposite sign.
     *
     * @return this [BigDecimal] converted to a [Long].
     * @see toLongExact
     */
    @JsName("toLong")
    fun toLong(): Long

    /**
     * Converts this [BigDecimal] to a [Long], checking for lost information.
     *
     * @return this [BigDecimal] converted to a [Long].
     * @throws ArithmeticException if this has a nonzero fractional part, or does not fit in a [Long].
     */
    @JsName("toLongExact")
    fun toLongExact(): Long

    /**
     * Converts this [BigDecimal] to an [Int], discarding any fractional part, and returning only the low-order 32
     * bits if the resulting integer is too big to fit in an [Int]. Note that this conversion can lose information
     * about the overall magnitude and precision of this [BigDecimal], as well as return a result with the opposite
     * sign.
     *
     * @return this [BigDecimal] converted to an [Int].
     * @see toIntExact
     */
    @JsName("toInt")
    fun toInt(): Int

    /**
     * Converts this [BigDecimal] to a [Byte]: the low-order 8 bits of [toInt]. This conversion can lose information
     * about the overall magnitude and precision of this [BigDecimal], as well as return a result with the opposite
     * sign.
     *
     * @return this [BigDecimal] converted to a [Byte].
     * @see toByteExact
     */
    @JsName("toByte")
    fun toByte(): Byte

    /**
     * Converts this [BigDecimal] to a [Char], whose code is the low-order 16 bits of [toInt].
     *
     * @return this [BigDecimal] converted to a [Char].
     */
    @JsExport.Ignore // a Kotlin Char is no JS type
    fun toChar(): Char

    /**
     * Converts this [BigDecimal] to a [Short]: the low-order 16 bits of [toInt]. This conversion can lose
     * information about the overall magnitude and precision of this [BigDecimal], as well as return a result with the
     * opposite sign.
     *
     * @return this [BigDecimal] converted to a [Short].
     * @see toShortExact
     */
    @JsName("toShort")
    fun toShort(): Short

    /**
     * Converts this [BigDecimal] to an [Int], checking for lost information.
     *
     * @return this [BigDecimal] converted to an [Int].
     * @throws ArithmeticException if this has a nonzero fractional part, or does not fit in an [Int].
     */
    @JsName("toIntExact")
    fun toIntExact(): Int

    /**
     * Converts this [BigDecimal] to a [Short], checking for lost information.
     *
     * @return this [BigDecimal] converted to a [Short].
     * @throws ArithmeticException if this has a nonzero fractional part, or does not fit in a [Short].
     */
    @JsName("toShortExact")
    fun toShortExact(): Short

    /**
     * Converts this [BigDecimal] to a [Byte], checking for lost information.
     *
     * @return this [BigDecimal] converted to a [Byte].
     * @throws ArithmeticException if this has a nonzero fractional part, or does not fit in a [Byte].
     */
    @JsName("toByteExact")
    fun toByteExact(): Byte

    /**
     * Converts this [BigDecimal] to a [Float]. If this [BigDecimal] has too great a magnitude to be represented as a
     * [Float], it is converted to [Float.NEGATIVE_INFINITY] or [Float.POSITIVE_INFINITY] as appropriate. Note that,
     * even when the result is finite, this conversion can lose information about the precision of this [BigDecimal].
     *
     * @return this [BigDecimal] converted to a [Float].
     */
    @JsName("toFloat")
    fun toFloat(): Float

    /**
     * Converts this [BigDecimal] to a [Double]. If this [BigDecimal] has too great a magnitude to be represented as a
     * [Double], it is converted to [Double.NEGATIVE_INFINITY] or [Double.POSITIVE_INFINITY] as appropriate. Note
     * that, even when the result is finite, this conversion can lose information about the precision of this
     * [BigDecimal].
     *
     * @return this [BigDecimal] converted to a [Double].
     */
    @JsName("toDouble")
    fun toDouble(): Double

    /**
     * Returns the size of an ulp (a unit in the last place) of this [BigDecimal]. An ulp of a nonzero [BigDecimal]
     * is the positive distance between this value and the [BigDecimal] next larger in magnitude with the same number
     * of digits. An ulp of a zero value is numerically equal to 1 with the scale of this. The result is stored with
     * the same scale as this, so the result for zero and nonzero values is the one whose ([unscaledValue], [scale])
     * is `(1, this.scale)`.
     *
     * @return the size of an ulp of this.
     */
    @JsName("ulp")
    fun ulp(): BigDecimal

    companion object {
        /**
         * The value 0, with a scale of 0.
         */
        @JvmField
        @JsName("ZERO")
        @JsStatic
        val ZERO: BigDecimal = BigDecimals.zero

        /**
         * The value 1, with a scale of 0.
         */
        @JvmField
        @JsName("ONE")
        @JsStatic
        val ONE: BigDecimal = BigDecimals.one

        /**
         * The value 2, with a scale of 0.
         */
        @JvmField
        @JsName("TWO")
        @JsStatic
        val TWO: BigDecimal = BigDecimals.two

        /**
         * The value 10, with a scale of 0.
         */
        @JvmField
        @JsName("TEN")
        @JsStatic
        val TEN: BigDecimal = BigDecimals.ten

        /**
         * The value 0.1, with a scale of 1.
         */
        @JvmField
        @JsName("ONE_TENTH")
        @JsStatic
        val ONE_TENTH: BigDecimal = BigDecimals.oneTenth

        /**
         * The value 0.5, with a scale of 1.
         */
        @JvmField
        @JsName("ONE_HALF")
        @JsStatic
        val ONE_HALF: BigDecimal = BigDecimals.oneHalf

        /**
         * The constant π (pi), truncated to 255 fractional digits (scale 255).
         */
        @JvmField
        @JsName("PI")
        @JsStatic
        val PI: BigDecimal = BigDecimals.pi

        /**
         * The constant *e* (Euler's number), truncated to 255 fractional digits (scale 255).
         */
        @JvmField
        @JsName("E")
        @JsStatic
        val E: BigDecimal = BigDecimals.e

        /**
         * Translates a [Long] unscaled value and an [Int] scale into a [BigDecimal]. Frequently used values may be
         * shared rather than allocated anew.
         *
         * @param unscaledVal unscaled value of the [BigDecimal].
         * @param scale scale of the [BigDecimal].
         * @return a [BigDecimal] whose value is `unscaledVal × 10^-scale`.
         */
        @JvmStatic
        @JsName("ofScaledLong")
        @JsStatic
        fun of(
            unscaledVal: Long,
            scale: Int,
        ): BigDecimal = bigDecimalOf(unscaledVal, scale)

        /**
         * As the [of] overload taking an unscaled [Long] value and a scale, also given the precision of the result,
         * i.e. the number of decimal digits of [unscaledVal] (or `0`, if unknown).
         *
         * [prec] must be accurate: on the JVM, the value is rounded to [prec] digits with
         * [RoundingMode.UNNECESSARY] (thus throwing an [ArithmeticException] if [unscaledVal] has more digits),
         * elsewhere it is stored as the [precision] of the result without being checked.
         *
         * @param unscaledVal unscaled value of the [BigDecimal].
         * @param scale scale of the [BigDecimal].
         * @param prec the number of decimal digits of [unscaledVal], or `0` if unknown.
         * @return a [BigDecimal] whose value is `unscaledVal × 10^-scale`.
         */
        internal fun of(
            unscaledVal: Long,
            scale: Int,
            prec: Int,
        ): BigDecimal = bigDecimalOf(unscaledVal, scale, prec)

        /**
         * Translates an [Int] value into a [BigDecimal] with a scale of zero.
         *
         * @param val value of the [BigDecimal].
         * @return a [BigDecimal] whose value is `val`.
         */
        @JvmStatic
        @JsName("of")
        @JsStatic
        fun of(`val`: Int): BigDecimal = bigDecimalOf(`val`)

        /**
         * Translates a [Long] value into a [BigDecimal] with a scale of zero.
         *
         * @param val value of the [BigDecimal].
         * @return a [BigDecimal] whose value is `val`.
         */
        @JvmStatic
        @JsName("ofLong")
        @JsStatic
        fun of(`val`: Long): BigDecimal = bigDecimalOf(`val`)

        /**
         * Translates a [BigInteger] unscaled value and an [Int] scale into a [BigDecimal], also given the precision
         * of the result, i.e. the number of decimal digits of [intVal] (or `0`, if unknown).
         *
         * [prec] must be accurate: on the JVM, the value is rounded to [prec] digits with
         * [RoundingMode.UNNECESSARY] (thus throwing an [ArithmeticException] if [intVal] has more digits), elsewhere
         * it is stored as the [precision] of the result without being checked.
         *
         * @param intVal unscaled value of the [BigDecimal].
         * @param scale scale of the [BigDecimal].
         * @param prec the number of decimal digits of [intVal], or `0` if unknown.
         * @return a [BigDecimal] whose value is `intVal × 10^-scale`.
         */
        internal fun of(
            intVal: BigInteger,
            scale: Int,
            prec: Int,
        ): BigDecimal = bigDecimalOf(intVal, scale, prec)

        /**
         * Translates a [Double] into a [BigDecimal] which is the exact decimal representation of its binary
         * floating-point value, rounded according to [ctx] if any. The scale of the result (before rounding) is the
         * smallest non-negative value such that `10^scale × val` is an integer.
         *
         * The result can be somewhat unpredictable: since `0.1` cannot be represented exactly as a [Double],
         * `of(0.1)` is `0.1000000000000000055511151231257827021181583404541015625`. Use [valueOf] to get `0.1`
         * instead, or the [of] overload parsing a [String].
         *
         * @param val [Double] to convert to a [BigDecimal].
         * @param ctx the context to use for rounding, or `null` not to round.
         * @return a [BigDecimal] whose value is equal to `val`, rounded according to [ctx] if any.
         * @throws NumberFormatException if `val` is infinite or NaN.
         * @throws ArithmeticException if the result is inexact but the rounding mode of [ctx] is
         * [RoundingMode.UNNECESSARY].
         * @see valueOf
         */
        @JvmStatic
        @JsName("ofDouble")
        @JvmOverloads
        @JsStatic
        fun of(
            `val`: Double,
            ctx: MathContext? = null,
        ): BigDecimal = bigDecimalOf(`val`, ctx)

        /**
         * Translates a `double` into a [BigDecimal] using its shortest decimal representation, as Java's
         * `BigDecimal.valueOf(double)` (i.e. `new BigDecimal(Double.toString(value))`): `valueOf(0.1)` is `0.1`, while
         * [of] gives the exact value of the binary `double` (`0.1000000000000000055511151231257827...`). The scale
         * is the one of Java's `Double.toString` on every platform: plain notation with at least one fractional digit
         * in `[10^-3, 10^7)`, else scientific notation with at least one fractional digit.
         *
         * @param value `double` to convert to a [BigDecimal].
         * @return a [BigDecimal] whose value is equal to or approximately equal to the value of [value].
         * @throws NumberFormatException if [value] is infinite or NaN.
         */
        @JvmStatic
        @JsName("valueOfDouble")
        @JsStatic
        fun valueOf(value: Double): BigDecimal {
            if (value == 0.0) return ZERO.setScale(1)
            if (value.isNaN() || value.isInfinite()) throw NumberFormatException("Infinite or NaN")
            val stripped = of(value.toString()).stripTrailingZeros()
            val exponent = stripped.precision - stripped.scale - 1
            val magnitude = kotlin.math.abs(value)
            val scale =
                if (magnitude >= PLAIN_MIN && magnitude < PLAIN_MAX) {
                    maxOf(1, stripped.scale)
                } else {
                    maxOf(2, stripped.precision) - 1 - exponent
                }
            return stripped.setScale(scale)
        }

        /**
         * As the [of] overload taking a [Double], on `val` converted to [Double] (exactly): the result is the exact
         * value of the binary [Float], e.g. `0.100000001490116119384765625` for `0.1f`.
         *
         * @param val [Float] to convert to a [BigDecimal].
         * @param ctx the context to use for rounding, or `null` not to round.
         * @return a [BigDecimal] whose value is equal to `val`, rounded according to [ctx] if any.
         * @throws NumberFormatException if `val` is infinite or NaN.
         * @throws ArithmeticException if the result is inexact but the rounding mode of [ctx] is
         * [RoundingMode.UNNECESSARY].
         */
        @JvmStatic
        @JsName("ofFloat")
        @JvmOverloads
        @JsStatic
        fun of(
            `val`: Float,
            ctx: MathContext? = null,
        ): BigDecimal = bigDecimalOf(`val`, ctx)

        /**
         * Parses the string representation of a [BigDecimal], rounding it according to [ctx] if any, as Java's
         * `new BigDecimal(String)`.
         *
         * The string consists of an optional sign (`+` or `-`), followed by a sequence of zero or more decimal digits
         * (the integer part), optionally followed by a fraction (a `.` followed by zero or more decimal digits),
         * optionally followed by an exponent (`e` or `E`, followed by an optionally signed integer). The integer part
         * and the fraction must not both be empty, and the string must not contain any extraneous characters (such as
         * whitespace). Examples: `0`, `-1.23`, `1.23E+3`, `12.3e-10`.
         *
         * The unscaled value of the result is the integer obtained by concatenating the digits of the integer part and
         * of the fraction; its scale is the number of digits in the fraction, minus the exponent. Thus, the strings
         * returned by `toString()`, [toPlainString] and [toEngineeringString] can be parsed back.
         *
         * @param val the string representation of a [BigDecimal].
         * @param ctx the context to use for rounding, or `null` not to round.
         * @return the parsed [BigDecimal], rounded according to [ctx] if any.
         * @throws NumberFormatException if `val` is not a valid representation of a [BigDecimal], or the scale would
         * overflow.
         * @throws ArithmeticException if the result is inexact but the rounding mode of [ctx] is
         * [RoundingMode.UNNECESSARY].
         */
        @JvmStatic
        @JsName("parse")
        @JvmOverloads
        @JsStatic
        fun of(
            `val`: String,
            ctx: MathContext? = null,
        ): BigDecimal = bigDecimalOf(`val`, ctx)

        /**
         * Translates a [BigInteger] into a [BigDecimal] with a scale of zero, rounded according to [ctx] if any.
         *
         * @param val [BigInteger] to convert to a [BigDecimal].
         * @param ctx the context to use for rounding, or `null` not to round.
         * @return a [BigDecimal] whose value is `val`, rounded according to [ctx] if any.
         * @throws ArithmeticException if the result is inexact but the rounding mode of [ctx] is
         * [RoundingMode.UNNECESSARY].
         */
        @JvmStatic
        @JsName("ofBigInteger")
        @JvmOverloads
        @JsStatic
        fun of(
            `val`: BigInteger,
            ctx: MathContext? = null,
        ): BigDecimal = bigDecimalOf(`val`, ctx)

        /**
         * As the [of] overload taking an [Int], rounding according to [ctx].
         *
         * @param val value of the [BigDecimal].
         * @param ctx the context to use.
         * @return a [BigDecimal] whose value is `val`, rounded according to [ctx].
         * @throws ArithmeticException if the result is inexact but the rounding mode of [ctx] is
         * [RoundingMode.UNNECESSARY].
         */
        @JvmStatic
        @JsName("ofWithContext")
        @JsStatic
        fun of(
            `val`: Int,
            ctx: MathContext,
        ): BigDecimal = bigDecimalOf(`val`, ctx)

        /**
         * As the [of] overload taking a [Long], rounding according to [ctx].
         *
         * @param val value of the [BigDecimal].
         * @param ctx the context to use.
         * @return a [BigDecimal] whose value is `val`, rounded according to [ctx].
         * @throws ArithmeticException if the result is inexact but the rounding mode of [ctx] is
         * [RoundingMode.UNNECESSARY].
         */
        @JvmStatic
        @JsName("ofLongWithContext")
        @JsStatic
        fun of(
            `val`: Long,
            ctx: MathContext,
        ): BigDecimal = bigDecimalOf(`val`, ctx)
    }
}

// Java's Double.toString uses plain notation in [PLAIN_MIN, PLAIN_MAX), see BigDecimal.valueOf
private const val PLAIN_MIN = 1e-3
private const val PLAIN_MAX = 1e7
