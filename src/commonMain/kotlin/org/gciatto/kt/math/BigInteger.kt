package org.gciatto.kt.math

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.js.JsStatic
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.random.Random

/**
 * An immutable arbitrary-precision integer, with the operations of Java's `java.math.BigInteger`.
 *
 * Instances are created through the factory methods of the companion object ([of], [probablePrime]) or the
 * constants ([ZERO], [ONE], [TWO], [TEN], [NEGATIVE_ONE]). On the JVM they are backed by `java.math.BigInteger`,
 * on JS by native BigInts, elsewhere by a Kotlin port of the JDK implementation.
 *
 * Arithmetic and comparisons are available as Kotlin operators (`+`, `-`, `*`, `/`, `%`, unary `-` and `+`, `!`,
 * `<`, `..`, `[]`), most binary operations also accept [Int] and [Long] operands, and bitwise operations
 * use two's complement semantics, as if the value were sign-extended indefinitely.
 */
@JsExport
@Suppress("EXPOSED_NOT_EXPORTED_SUPER_INTERFACE_WARNING")
interface BigInteger : Comparable<BigInteger> {
    /**
     * The absolute value of this [BigInteger].
     */
    @JsName("absoluteValue")
    val absoluteValue: BigInteger

    /**
     * The signum function of this [BigInteger]: -1, 0 or 1 as this value is negative, zero or positive.
     */
    @JsName("signum")
    val signum: Int

    /**
     * The number of bits in the minimal two's complement representation of this [BigInteger], *excluding* a sign
     * bit. For positive values, this is the number of bits in the ordinary binary representation; in general it is
     * `ceil(log2(if (this < 0) -this else this + 1))`. It is 0 for [ZERO] and [NEGATIVE_ONE].
     */
    @JsName("bitLength")
    val bitLength: Int

    /**
     * The number of bits in the two's complement representation of this [BigInteger] that differ from its sign bit
     * (i.e. the number of ones for non-negative values, the number of zeros for negative ones). Useful when
     * implementing bit-vector style sets atop [BigInteger]s.
     */
    @JsName("bitCount")
    val bitCount: Int

    /**
     * The range of [BigInteger]s from this value to [endInclusive], both included: the `..` operator.
     * It is empty if [endInclusive] is less than this value.
     */
    @JsName("rangeTo")
    operator fun rangeTo(endInclusive: BigInteger): BigIntegerRange = BigIntegerRange(this, endInclusive)

    /** As [rangeTo], with an [Int] upper bound. */
    @JsName("rangeToInt")
    operator fun rangeTo(endInclusive: Int): BigIntegerRange = BigIntegerRange(this, of(endInclusive))

    /** As [rangeTo], with a [Long] upper bound. */
    @JsName("rangeToLong")
    operator fun rangeTo(endInclusive: Long): BigIntegerRange = BigIntegerRange(this, of(endInclusive))

    /**
     * Returns the first integer greater than this [BigInteger] that is probably prime. The probability that the
     * returned number is composite does not exceed `2^-100`. This method never skips over a prime when searching:
     * if it returns `p`, there is no prime `q` such that `this < q < p`.
     *
     * @return the first integer greater than this value that is probably prime.
     * @throws ArithmeticException if this value is negative or too large.
     */
    @JsName("nextProbablePrime")
    fun nextProbablePrime(): BigInteger

    /**
     * Returns `this + other`: the `+` operator.
     *
     * @param other the value to be added to this [BigInteger].
     * @return `this + other`
     */
    @JsName("plus")
    operator fun plus(other: BigInteger): BigInteger

    /** As [plus], with an [Int] operand. */
    @JsName("plusInt")
    operator fun plus(other: Int): BigInteger = plus(of(other))

    /** As [plus], with a [Long] operand. */
    @JsName("plusLong")
    operator fun plus(other: Long): BigInteger = plus(of(other))

    /**
     * Returns `this - other`: the binary `-` operator.
     *
     * @param other the value to be subtracted from this [BigInteger].
     * @return `this - other`
     */
    @JsName("minus")
    operator fun minus(other: BigInteger): BigInteger

    /** As [minus], with an [Int] operand. */
    @JsName("minusInt")
    operator fun minus(other: Int): BigInteger = minus(of(other))

    /** As [minus], with a [Long] operand. */
    @JsName("minusLong")
    operator fun minus(other: Long): BigInteger = minus(of(other))

    /**
     * Returns `this * other`: the `*` operator.
     * Implementations may be faster when `other` is this very value (squaring).
     *
     * @param other the value to be multiplied by this [BigInteger].
     * @return `this * other`
     */
    @JsName("times")
    operator fun times(other: BigInteger): BigInteger

    /** As [times], with an [Int] operand. */
    @JsName("timesInt")
    operator fun times(other: Int): BigInteger = times(of(other))

    /** As [times], with a [Long] operand. */
    @JsName("timesLong")
    operator fun times(other: Long): BigInteger = times(of(other))

    /**
     * Returns `this / other`, truncated toward zero (as for [Int] and [Long]): the `/` operator.
     *
     * @param other the value by which this [BigInteger] is to be divided.
     * @return `this / other`
     * @throws ArithmeticException if [other] is zero.
     */
    @JsName("div")
    operator fun div(other: BigInteger): BigInteger

    /** As [div], with an [Int] operand. */
    @JsName("divInt")
    operator fun div(other: Int): BigInteger = div(of(other))

    /** As [div], with a [Long] operand. */
    @JsName("divLong")
    operator fun div(other: Long): BigInteger = div(of(other))

    /**
     * Returns an array of two [BigInteger]s: the quotient `this / other` (as [div]) followed by the remainder
     * `this % other` (as [rem]).
     *
     * @param other the value by which this [BigInteger] is to be divided, and the remainder computed.
     * @return an array holding the quotient at index 0 and the remainder at index 1.
     * @throws ArithmeticException if [other] is zero.
     */
    @JsName("divideAndRemainder")
    fun divideAndRemainder(other: BigInteger): Array<out BigInteger>

    /** As [divideAndRemainder], with an [Int] operand. */
    @JsName("divideAndRemainderInt")
    fun divideAndRemainder(other: Int): Array<out BigInteger> = divideAndRemainder(of(other))

    /** As [divideAndRemainder], with a [Long] operand. */
    @JsName("divideAndRemainderLong")
    fun divideAndRemainder(other: Long): Array<out BigInteger> = divideAndRemainder(of(other))

    /**
     * Returns `this % other`, whose sign is the one of this value (as [rem]).
     * Exported to JS as `remainder`.
     *
     * @param other the value by which this [BigInteger] is to be divided, and the remainder computed.
     * @return `this % other`
     * @throws ArithmeticException if [other] is zero.
     */
    @JsName("remainder")
    fun remainder(other: BigInteger): BigInteger

    /** As [remainder], with an [Int] operand. */
    @JsName("remainderInt")
    fun remainder(other: Int): BigInteger = remainder(of(other))

    /** As [remainder], with a [Long] operand. */
    @JsName("remainderLong")
    fun remainder(other: Long): BigInteger = remainder(of(other))

    /**
     * Returns this [BigInteger] raised to the power of [exponent], i.e. `this^exponent`.
     * Note that [exponent] is an [Int] rather than a [BigInteger].
     *
     * @param exponent the exponent to which this [BigInteger] is to be raised.
     * @return `this^exponent`
     * @throws ArithmeticException if [exponent] is negative (the result would not be an integer).
     */
    @JsName("pow")
    infix fun pow(exponent: Int): BigInteger

    /**
     * Returns the integer square root of this [BigInteger]: the largest integer `s` such that `s * s <= this`,
     * i.e. `floor(sqrt(this))`. It is less than the real square root whenever the latter is not an integer.
     *
     * @return the integer square root of this value.
     * @throws ArithmeticException if this value is negative.
     */
    @JsName("sqrt")
    fun sqrt(): BigInteger

    /**
     * Returns an array of two [BigInteger]s: the integer square root `s` of this value (as [sqrt]) and the
     * remainder `this - s * s`.
     *
     * @return an array holding the integer square root at index 0 and the remainder at index 1.
     * @throws ArithmeticException if this value is negative.
     * @see sqrt
     */
    @JsName("sqrtAndRemainder")
    fun sqrtAndRemainder(): Array<out BigInteger>

    /**
     * Returns the greatest common divisor of the absolute values of this [BigInteger] and [other].
     * It is 0 if both values are 0.
     *
     * @param other the value with which the GCD is to be computed.
     * @return `gcd(|this|, |other|)`
     */
    @JsName("gcd")
    fun gcd(other: BigInteger): BigInteger

    /** As [gcd], with an [Int] operand. */
    @JsName("gcdInt")
    fun gcd(other: Int): BigInteger = gcd(of(other))

    /** As [gcd], with a [Long] operand. */
    @JsName("gcdLong")
    fun gcd(other: Long): BigInteger = gcd(of(other))

    /**
     * Returns `-this`: the unary `-` operator.
     */
    @JsName("unaryMinus")
    operator fun unaryMinus(): BigInteger

    /**
     * Returns this value unchanged: the unary `+` operator.
     */
    @JsName("unaryPlus")
    operator fun unaryPlus(): BigInteger

    /**
     * Returns `this % modulus`, the `%` operator: the same as [remainder], so the result has the sign of
     * `this`, consistently with Kotlin's `%` on [Int] and [Long].
     *
     * @param modulus the divisor.
     * @return `this % modulus`
     * @throws ArithmeticException if [modulus] is zero.
     * @see remainder
     */
    @JsName("rem")
    operator fun rem(modulus: BigInteger): BigInteger

    /** As [rem], with an [Int] operand. */
    @JsName("remInt")
    operator fun rem(modulus: Int): BigInteger = rem(of(modulus))

    /** As [rem], with a [Long] operand. */
    @JsName("remLong")
    operator fun rem(modulus: Long): BigInteger = rem(of(modulus))

    /**
     * Returns `this^exponent mod modulus`, always non-negative. Unlike [pow], this method permits negative
     * exponents, in which case the result is a power of the [modInverse].
     *
     * @param exponent the exponent.
     * @param modulus the modulus.
     * @return `this^exponent mod modulus`, between 0 (included) and [modulus] (excluded).
     * @throws ArithmeticException if [modulus] is not positive, or [exponent] is negative and this [BigInteger]
     * is not relatively prime to [modulus].
     * @see modInverse
     */
    @JsName("modPow")
    fun modPow(
        exponent: BigInteger,
        modulus: BigInteger,
    ): BigInteger

    /**
     * Returns `this^-1 mod modulus`: the multiplicative inverse of this value modulo [modulus], between 0
     * (included) and [modulus] (excluded).
     *
     * @param modulus the modulus.
     * @return `this^-1 mod modulus`
     * @throws ArithmeticException if [modulus] is not positive, or this [BigInteger] has no multiplicative inverse
     * modulo [modulus] (that is, it is not relatively prime to [modulus]).
     */
    @JsName("modInverse")
    fun modInverse(modulus: BigInteger): BigInteger

    /** As [modInverse], with an [Int] modulus. */
    @JsName("modInverseInt")
    fun modInverse(modulus: Int): BigInteger = modInverse(of(modulus))

    /** As [modInverse], with a [Long] modulus. */
    @JsName("modInverseLong")
    fun modInverse(modulus: Long): BigInteger = modInverse(of(modulus))

    /**
     * Returns `this << n`, i.e. `floor(this * 2^n)`. The shift distance [n] may be negative, in which case this
     * method performs a right shift (as [shr]).
     *
     * @param n the shift distance, in bits.
     * @return `this << n`
     * @see shr
     */
    @JsName("shl")
    infix fun shl(n: Int): BigInteger

    /**
     * Returns `this >> n`, with sign extension, i.e. `floor(this / 2^n)`. The shift distance [n] may be negative,
     * in which case this method performs a left shift (as [shl]).
     *
     * @param n the shift distance, in bits.
     * @return `this >> n`
     * @see shl
     */
    @JsName("shr")
    infix fun shr(n: Int): BigInteger

    /**
     * Returns the bitwise AND of this [BigInteger] and [other]. The result is negative if and only if both
     * operands are negative.
     *
     * @param other the value to be AND'ed with this [BigInteger].
     * @return `this and other`
     */
    @JsName("and")
    fun and(other: BigInteger): BigInteger

    /** As [and], with an [Int] operand. */
    @JsName("andInt")
    fun and(other: Int): BigInteger = and(of(other))

    /** As [and], with a [Long] operand. */
    @JsName("andLong")
    fun and(other: Long): BigInteger = and(of(other))

    /**
     * Returns the bitwise OR of this [BigInteger] and [other]. The result is negative if and only if either
     * operand is negative.
     *
     * @param other the value to be OR'ed with this [BigInteger].
     * @return `this or other`
     */
    @JsName("or")
    fun or(other: BigInteger): BigInteger

    /** As [or], with an [Int] operand. */
    @JsName("orInt")
    fun or(other: Int): BigInteger = or(of(other))

    /** As [or], with a [Long] operand. */
    @JsName("orLong")
    fun or(other: Long): BigInteger = or(of(other))

    /**
     * Returns the bitwise XOR of this [BigInteger] and [other]. The result is negative if and only if exactly one
     * of the operands is negative.
     *
     * @param other the value to be XOR'ed with this [BigInteger].
     * @return `this xor other`
     */
    @JsName("xor")
    fun xor(other: BigInteger): BigInteger

    /** As [xor], with an [Int] operand. */
    @JsName("xorInt")
    fun xor(other: Int): BigInteger = xor(of(other))

    /** As [xor], with a [Long] operand. */
    @JsName("xorLong")
    fun xor(other: Long): BigInteger = xor(of(other))

    /**
     * Returns the bitwise complement of this [BigInteger], i.e. `-this - 1`: the `!` operator (`inv()` on [Int] and
     * [Long]). The result is negative if and only if this value is non-negative.
     *
     * @return the bitwise complement of this value.
     */
    @JsName("not")
    operator fun not(): BigInteger

    /**
     * Returns `this and other.not()`, a convenience for masking operations. The result is negative if and only if
     * this value is negative and [other] is non-negative.
     *
     * @param other the value to be complemented and AND'ed with this [BigInteger].
     * @return `this and !other`
     */
    @JsName("andNot")
    fun andNot(other: BigInteger): BigInteger

    /** As [andNot], with an [Int] operand. */
    @JsName("andNotInt")
    fun andNot(other: Int): BigInteger = andNot(of(other))

    /** As [andNot], with a [Long] operand. */
    @JsName("andNotLong")
    fun andNot(other: Long): BigInteger = andNot(of(other))

    /**
     * Returns `true` if and only if the bit at index [n] (0 being the least significant one) is set in the two's
     * complement representation of this value, i.e. `this and (1 shl n) != 0`.
     *
     * @param n the index of the bit to test.
     * @return `true` if and only if the designated bit is set.
     * @throws ArithmeticException if [n] is negative.
     * @see get
     */
    @JsName("testBit")
    fun testBit(n: Int): Boolean

    /**
     * The same as [testBit]: the indexing operator, `x[n]`.
     *
     * @param n the index of the bit to test.
     * @return `true` if and only if the designated bit is set.
     * @throws ArithmeticException if [n] is negative.
     */
    @JsName("get")
    operator fun get(n: Int): Boolean

    /**
     * Returns a copy of this value whose bit at index [n] is set if [b] is `true` ([setBit]), or cleared otherwise
     * ([clearBit]). This value is left unchanged, since [BigInteger]s are immutable: as an operator, `x[n] = b`
     * compiles to `x.set(n, b)` and discards the result, so call this method explicitly.
     *
     * @param n the index of the bit to set or clear.
     * @param b whether the bit is to be set.
     * @return this value with the designated bit set to [b].
     * @throws ArithmeticException if [n] is negative.
     */
    @JsName("set")
    operator fun set(
        n: Int,
        b: Boolean,
    ): BigInteger

    /**
     * Returns this value with the bit at index [n] set, i.e. `this or (1 shl n)`.
     *
     * @param n the index of the bit to set.
     * @return `this or (1 shl n)`
     * @throws ArithmeticException if [n] is negative.
     */
    @JsName("setBit")
    fun setBit(n: Int): BigInteger

    /**
     * Returns this value with the bit at index [n] cleared, i.e. `this andNot (1 shl n)`.
     *
     * @param n the index of the bit to clear.
     * @return `this andNot (1 shl n)`
     * @throws ArithmeticException if [n] is negative.
     */
    @JsName("clearBit")
    fun clearBit(n: Int): BigInteger

    /**
     * Returns this value with the bit at index [n] flipped, i.e. `this xor (1 shl n)`.
     *
     * @param n the index of the bit to flip.
     * @return `this xor (1 shl n)`
     * @throws ArithmeticException if [n] is negative.
     */
    @JsName("flipBit")
    fun flipBit(n: Int): BigInteger

    /**
     * Returns `true` if this [BigInteger] is probably prime, `false` if it is definitely composite.
     * If [certainty] is not positive, `true` is returned.
     *
     * @param certainty a measure of the uncertainty that the caller is willing to tolerate: if the call returns
     * `true`, the probability that this value is prime exceeds `1 - 1/2^certainty`. The execution time of this
     * method is proportional to the value of this parameter.
     * @return `true` if this value is probably prime, `false` if it is definitely composite.
     */
    @JsName("isProbablePrime")
    fun isProbablePrime(certainty: Int): Boolean

    /**
     * Compares this [BigInteger] with [other] numerically: the comparison operators (`<`, `<=`, `>`, `>=`) call it.
     * From JS, use [compare] instead.
     *
     * @param other the value to which this [BigInteger] is to be compared.
     * @return a negative number, zero or a positive number as this value is less than, equal to, or greater than
     * [other].
     */
    override operator fun compareTo(other: BigInteger): Int

    /**
     * The same as [compareTo] (negative, zero or positive as this is less than, equal to or greater than [other]),
     * callable from JS as `compareTo`: [Comparable] isn't exported to JS (KT-83009), so [compareTo] isn't either.
     */
    @JsName("compareTo")
    fun compare(other: BigInteger): Int = compareTo(other)

    /**
     * Returns `true` if and only if [other] is a [BigInteger] numerically equal to this one.
     *
     * @param other the object to which this [BigInteger] is to be compared.
     * @return `true` if and only if [other] is a [BigInteger] whose value is equal to this one.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Returns the lesser of this [BigInteger] and [other]. If they are equal, either may be returned.
     *
     * @param other the value with which the minimum is to be computed.
     * @return the lesser of this value and [other].
     */
    @JsName("min")
    fun min(other: BigInteger): BigInteger

    /**
     * Returns the greater of this [BigInteger] and [other]. If they are equal, either may be returned.
     *
     * @param other the value with which the maximum is to be computed.
     * @return the greater of this value and [other].
     */
    @JsName("max")
    fun max(other: BigInteger): BigInteger

    /**
     * Returns the hash code of this [BigInteger], consistent with [equals]: the same as `java.math.BigInteger`'s
     * for the same value.
     */
    override fun hashCode(): Int

    /**
     * Returns the representation of this [BigInteger] in [radix]: digits from `0` to `9` and from `a` to `z`
     * (lower case), preceded by `-` if this value is negative, with no radix prefix. If [radix] is outside the
     * range from 2 to 36 (both included), it defaults to 10. The result can be parsed back with
     * `of(string, radix)`. Exported to JS as `toStringWithRadix`.
     *
     * @param radix the radix of the representation.
     * @return the representation of this value in [radix].
     * @see Int.toString
     */
    @JsName("toStringWithRadix")
    fun toString(radix: Int): String

    /**
     * Returns the decimal representation of this [BigInteger], preceded by `-` if it is negative: the same as
     * `toString(10)`. The result can be parsed back with [of].
     */
    override fun toString(): String

    /**
     * Returns the two's complement representation of this [BigInteger], in big-endian byte order (the most
     * significant byte at index 0). The array has the minimum number of bytes needed to represent this value,
     * including at least one sign bit: `bitLength / 8 + 1` (see [bitLength]).
     *
     * @return the two's complement representation of this value.
     */
    @JsName("toByteArray")
    fun toByteArray(): ByteArray

    /** This [BigInteger] as a [BigDecimal] with scale 0. */
    @JsName("toBigDecimal")
    fun toBigDecimal(): BigDecimal = BigDecimal.of(this)

    /**
     * The [BigDecimal] `this × 10^-scale`: this [BigInteger] as the unscaled value of a [BigDecimal] with [scale].
     */
    @JsName("toBigDecimalWithScale")
    fun toBigDecimal(scale: Int): BigDecimal = BigDecimal.of(this, scale, 0)

    /** This [BigInteger] as a [BigRational]. */
    @JsName("toBigRational")
    fun toBigRational(): BigRational = BigRational.valueOf(this)

    /**
     * Converts this [BigInteger] to an [Int], keeping only its low-order 32 bits, as [Long.toInt] does. This may
     * lose information about the magnitude of the value, and even return a result with the opposite sign.
     *
     * @return the low-order 32 bits of this value, as an [Int].
     * @see toIntExact
     */
    @JsName("toInt")
    fun toInt(): Int

    /**
     * Converts this [BigInteger] to a [Long], keeping only its low-order 64 bits. This may lose information about
     * the magnitude of the value, and even return a result with the opposite sign.
     *
     * @return the low-order 64 bits of this value, as a [Long].
     * @see toLongExact
     */
    @JsName("toLong")
    fun toLong(): Long

    /**
     * Converts this [BigInteger] to a [Byte], keeping only its low-order 8 bits (possibly changing magnitude and
     * sign).
     *
     * @see toByteExact
     */
    @JsName("toByte")
    fun toByte(): Byte

    /**
     * Converts this [BigInteger] to a [Char] whose code is given by its low-order 16 bits: `toInt().toChar()`.
     */
    @JsExport.Ignore // a Kotlin Char is no JS type
    fun toChar(): Char

    /**
     * Converts this [BigInteger] to a [Short], keeping only its low-order 16 bits (possibly changing magnitude and
     * sign).
     *
     * @see toShortExact
     */
    @JsName("toShort")
    fun toShort(): Short

    /**
     * Converts this [BigInteger] to the nearest [Float]. Values too large in magnitude become
     * [Float.POSITIVE_INFINITY] or [Float.NEGATIVE_INFINITY]; precision may be lost even when the result is finite.
     */
    @JsName("toFloat")
    fun toFloat(): Float

    /**
     * Converts this [BigInteger] to the nearest [Double]. Values too large in magnitude become
     * [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY]; precision may be lost even when the result is
     * finite.
     */
    @JsName("toDouble")
    fun toDouble(): Double

    /**
     * Converts this [BigInteger] to a [Long], checking for lost information.
     *
     * @return this value as a [Long].
     * @throws ArithmeticException if this value is outside the range of [Long].
     * @see toLong
     */
    @JsName("toLongExact")
    fun toLongExact(): Long

    /**
     * Converts this [BigInteger] to an [Int], checking for lost information.
     *
     * @return this value as an [Int].
     * @throws ArithmeticException if this value is outside the range of [Int].
     * @see toInt
     */
    @JsName("toIntExact")
    fun toIntExact(): Int

    /**
     * Converts this [BigInteger] to a [Short], checking for lost information.
     *
     * @return this value as a [Short].
     * @throws ArithmeticException if this value is outside the range of [Short].
     * @see toShort
     */
    @JsName("toShortExact")
    fun toShortExact(): Short

    /**
     * Converts this [BigInteger] to a [Byte], checking for lost information.
     *
     * @return this value as a [Byte].
     * @throws ArithmeticException if this value is outside the range of [Byte].
     * @see toByte
     */
    @JsName("toByteExact")
    fun toByteExact(): Byte

    /** Constants and factory methods for [BigInteger]s. */
    companion object {
        /** The [BigInteger] constant 0. */
        @JvmField
        @JsName("ZERO")
        @JsStatic
        val ZERO: BigInteger = BigIntegers.zero

        /** The [BigInteger] constant 1. */
        @JvmField
        @JsName("ONE")
        @JsStatic
        val ONE: BigInteger = BigIntegers.one

        /** The [BigInteger] constant 2. */
        @JvmField
        @JsName("TWO")
        @JsStatic
        val TWO: BigInteger = BigIntegers.two

        /** The [BigInteger] constant -1. */
        @JvmField
        @JsName("NEGATIVE_ONE")
        @JsStatic
        val NEGATIVE_ONE: BigInteger = BigIntegers.negativeOne

        /** The [BigInteger] constant 10. */
        @JvmField
        @JsName("TEN")
        @JsStatic
        val TEN: BigInteger = BigIntegers.ten

        /**
         * Returns a positive [BigInteger] that is probably prime, with the given [bitLength] (see
         * [BigInteger.bitLength]). The probability that the returned value is composite does not exceed `2^-100`.
         *
         * @param bitLength the bit length of the returned [BigInteger].
         * @param rnd the source of random bits used to select candidates to be tested for primality.
         * @return a [BigInteger] of [bitLength] bits that is probably prime.
         * @throws ArithmeticException if [bitLength] is less than 2 or too large.
         */
        @JvmStatic
        @JsExport.Ignore // Random isn't exportable: see BigFactory.probablePrime(bitLength)
        fun probablePrime(
            bitLength: Int,
            rnd: Random,
        ): BigInteger = bigProbablePrimeInteger(bitLength, rnd)

        /**
         * Returns the [BigInteger] whose value is [value]. Exported to JS as `ofLong`.
         *
         * @param value the value of the [BigInteger] to return.
         * @return a [BigInteger] with the given value.
         */
        @JsName("ofLong")
        @JvmStatic
        @JsStatic
        fun of(value: Long): BigInteger = bigIntegerOf(value)

        /**
         * Returns the [BigInteger] whose value is [value].
         *
         * @param value the value of the [BigInteger] to return.
         * @return a [BigInteger] with the given value.
         */
        @JsName("of")
        @JvmStatic
        @JsStatic
        fun of(value: Int): BigInteger = bigIntegerOf(value)

        /**
         * Parses a [BigInteger] from [value]: an optional sign (`-` or `+`) followed by digits, decimal unless a
         * `0x`, `0o` or `0b` prefix (in any case) selects radix 16, 8 or 2. Exported to JS as `parse`.
         *
         * @param value the representation to parse.
         * @return the parsed [BigInteger].
         * @throws NumberFormatException if [value] is not a valid representation.
         */
        @JsName("parse")
        @JvmStatic
        @JsStatic
        fun of(value: String): BigInteger = bigIntegerOf(value)

        /**
         * Parses a [BigInteger] from its representation in [radix]: an optional sign (`-` or `+`) followed by one
         * or more digits in [radix] (no prefix). Exported to JS as `parseWithRadix`.
         *
         * @param value the representation to parse.
         * @param radix the radix of the representation, from 2 to 36 (both included).
         * @return the parsed [BigInteger].
         * @throws NumberFormatException if [value] is not a valid representation in [radix], or [radix] is out of
         * range.
         */
        @JsName("parseWithRadix")
        @JvmStatic
        @JsStatic
        fun of(
            value: String,
            radix: Int,
        ): BigInteger = bigIntegerOf(value, radix)

        /**
         * Returns the [BigInteger] whose two's complement representation is [value]: 32-bit words in big-endian
         * order (the most significant one at index 0). Exported to JS as `ofIntArray`.
         *
         * @param value the two's complement representation, which must not be empty.
         * @return the [BigInteger] represented by [value].
         */
        @JsName("ofIntArray")
        @JvmStatic
        @JsStatic
        fun of(value: IntArray): BigInteger = bigIntegerOf(value)

        /**
         * Returns the [BigInteger] whose sign is [signum] and whose magnitude is the unsigned big-endian number
         * held by the [len] bytes of [magnitude] starting at index [off]. Exported to JS as `ofSignMagnitudeSlice`.
         *
         * @param signum the sign of the result: -1, 0 or 1.
         * @param magnitude the bytes holding the magnitude, most significant first.
         * @param off the index of the first byte of the magnitude.
         * @param len the number of bytes of the magnitude.
         * @return the [BigInteger] with the given sign and magnitude.
         * @throws NumberFormatException if [signum] is not -1, 0 or 1, or it is 0 and the magnitude is not zero.
         * @throws IndexOutOfBoundsException if [off] and [len] do not designate a range within [magnitude].
         */
        @JsName("ofSignMagnitudeSlice")
        @JvmStatic
        @JsStatic
        fun of(
            signum: Int,
            magnitude: ByteArray,
            off: Int,
            len: Int,
        ): BigInteger = bigIntegerOf(signum, magnitude, off, len)
    }
}
