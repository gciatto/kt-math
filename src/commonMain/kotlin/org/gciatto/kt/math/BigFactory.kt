package org.gciatto.kt.math

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.js.JsStatic
import kotlin.jvm.JvmStatic
import kotlin.random.Random

/**
 * Creates [BigInteger]s, [BigDecimal]s and [BigRational]s, from numbers or by parsing strings.
 *
 * It gathers in one place the factory methods of the companion objects of [BigInteger] (`of`, `probablePrime`),
 * [BigDecimal] (`of`, `valueOf`) and [BigRational] (`valueOf`), plus parsing variants returning `null` instead of
 * throwing. Code creating numbers through a [BigFactory] can be handed another one, e.g. in tests.
 *
 * The `context` arguments default to [MathContext.UNLIMITED], which doesn't round.
 *
 * [BigFactory.default] creates the most efficient implementation for the platform at hand: `java.math` on the JVM,
 * native `BigInt`s on JS, the Kotlin implementation (with C kernels) on Kotlin/Native.
 */
@JsExport
@Suppress("TooManyFunctions")
interface BigFactory {
    // BigInteger

    /** The [BigInteger] whose value is [value]. */
    @JsName("bigIntegerOfInt")
    fun bigIntegerOf(value: Int): BigInteger

    /** The [BigInteger] whose value is [value]. */
    @JsName("bigIntegerOfLong")
    fun bigIntegerOf(value: Long): BigInteger

    /**
     * Parses a [BigInteger] from its representation in [radix] (an optional sign, then digits).
     *
     * @throws NumberFormatException if [value] isn't a valid representation.
     */
    @JsName("parseBigInteger")
    fun parseBigInteger(
        value: String,
        radix: Int = DEFAULT_RADIX,
    ): BigInteger

    /** As [parseBigInteger], but returns `null` if [value] isn't a valid representation. */
    @JsName("parseBigIntegerOrNull")
    fun parseBigIntegerOrNull(
        value: String,
        radix: Int = DEFAULT_RADIX,
    ): BigInteger? =
        try {
            parseBigInteger(value, radix)
        } catch (_: NumberFormatException) {
            null
        }

    /** The [BigInteger] whose two's complement representation, big-endian, is [value]. */
    @JsName("bigIntegerOfIntArray")
    fun bigIntegerOf(value: IntArray): BigInteger

    /**
     * The [BigInteger] whose two's complement representation, big-endian, is [bytes] (as [BigInteger.toByteArray]
     * returns it).
     *
     * @throws NumberFormatException if [bytes] is empty.
     */
    @JsName("bigIntegerOfBytes")
    fun bigIntegerOf(bytes: ByteArray): BigInteger

    /**
     * The [BigInteger] whose sign is [signum] (-1, 0 or 1) and whose magnitude is the big-endian unsigned number of
     * [length] bytes of [magnitude] from [offset].
     *
     * @throws NumberFormatException if [signum] is invalid, or 0 with a non-zero magnitude.
     */
    @JsName("bigIntegerOfSignMagnitude")
    fun bigIntegerOf(
        signum: Int,
        magnitude: ByteArray,
        offset: Int = 0,
        length: Int = magnitude.size,
    ): BigInteger

    /**
     * A random positive [BigInteger] of [bitLength] bits which is probably prime (`2^-100` chance of not being), drawn
     * from [random].
     */
    @JsExport.Ignore // Random isn't exportable
    fun probablePrime(
        bitLength: Int,
        random: Random,
    ): BigInteger

    /** As `probablePrime(bitLength, random)`, drawing from Kotlin's default [Random]. */
    @JsName("probablePrime")
    fun probablePrime(bitLength: Int): BigInteger = probablePrime(bitLength, Random)

    // BigDecimal

    /** The [BigDecimal] whose value is [value], rounded according to [context]. */
    @JsName("bigDecimalOfInt")
    fun bigDecimalOf(
        value: Int,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal

    /** The [BigDecimal] whose value is [value], rounded according to [context]. */
    @JsName("bigDecimalOfLong")
    fun bigDecimalOf(
        value: Long,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal

    /**
     * The [BigDecimal] whose value is exactly the binary [value] (e.g. `0.1000000000000000055511151231257827...` for
     * `0.1`), rounded according to [context]: see [bigDecimalValueOf] for the shortest decimal representation.
     *
     * @throws NumberFormatException if [value] is infinite or NaN.
     */
    @JsName("bigDecimalOfDouble")
    fun bigDecimalOf(
        value: Double,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal

    /**
     * The [BigDecimal] whose value is the shortest decimal representation of [value] (e.g. `0.1` for `0.1`), as
     * Java's `BigDecimal.valueOf(double)`.
     *
     * @throws NumberFormatException if [value] is infinite or NaN.
     */
    @JsName("bigDecimalValueOf")
    fun bigDecimalValueOf(value: Double): BigDecimal

    /** The [BigDecimal] whose value is [value], rounded according to [context]. */
    @JsName("bigDecimalOfBigInteger")
    fun bigDecimalOf(
        value: BigInteger,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal

    /** The [BigDecimal] whose value is `unscaledValue × 10^-scale`, rounded according to [context]. */
    @JsName("bigDecimalOfScaled")
    fun bigDecimalOf(
        unscaledValue: BigInteger,
        scale: Int,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal

    /** The [BigDecimal] whose value is `unscaledValue × 10^-scale`. */
    @JsName("bigDecimalOfScaledLong")
    fun bigDecimalOf(
        unscaledValue: Long,
        scale: Int,
    ): BigDecimal

    /**
     * Parses a [BigDecimal] (e.g. `"-1.23E+4"`, see [BigDecimal.of]), rounded according to [context].
     *
     * @throws NumberFormatException if [value] isn't a valid representation.
     */
    @JsName("parseBigDecimal")
    fun parseBigDecimal(
        value: String,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal

    /** As [parseBigDecimal], but returns `null` if [value] isn't a valid representation. */
    @JsName("parseBigDecimalOrNull")
    fun parseBigDecimalOrNull(
        value: String,
        context: MathContext = MathContext.UNLIMITED,
    ): BigDecimal? =
        try {
            parseBigDecimal(value, context)
        } catch (_: NumberFormatException) {
            null
        }

    // BigRational

    /** The [BigRational] whose value is [value]. */
    @JsName("bigRationalOfInt")
    fun bigRationalOf(value: Int): BigRational

    /** The [BigRational] whose value is [value]. */
    @JsName("bigRationalOfLong")
    fun bigRationalOf(value: Long): BigRational

    /** The [BigRational] whose value is [value]. */
    @JsName("bigRationalOfBigInteger")
    fun bigRationalOf(value: BigInteger): BigRational

    /** The [BigRational] whose value is exactly [value]. */
    @JsName("bigRationalOfBigDecimal")
    fun bigRationalOf(value: BigDecimal): BigRational

    /**
     * The [BigRational] whose value is the shortest decimal representation of [value] (e.g. exactly `1/10` for `0.1`).
     *
     * @throws NumberFormatException if [value] is infinite or NaN.
     */
    @JsName("bigRationalOfDouble")
    fun bigRationalOf(value: Double): BigRational

    /**
     * The [BigRational] `numerator / denominator`.
     *
     * @throws ArithmeticException if [denominator] is zero.
     */
    @JsName("bigRationalOfInts")
    fun bigRationalOf(
        numerator: Int,
        denominator: Int,
    ): BigRational

    /**
     * The [BigRational] `numerator / denominator`.
     *
     * @throws ArithmeticException if [denominator] is zero.
     */
    @JsName("bigRationalOfBigIntegers")
    fun bigRationalOf(
        numerator: BigInteger,
        denominator: BigInteger,
    ): BigRational

    /**
     * The [BigRational] `numerator / denominator`.
     *
     * @throws ArithmeticException if [denominator] is zero.
     */
    @JsName("bigRationalOfBigDecimals")
    fun bigRationalOf(
        numerator: BigDecimal,
        denominator: BigDecimal,
    ): BigRational

    /**
     * Parses a [BigRational]: a decimal number as accepted by [parseBigDecimal], or a division of them (`"1/3"`,
     * `"-1.5/2.5"`).
     *
     * @throws NumberFormatException if [value] isn't a valid representation.
     * @throws ArithmeticException if a divisor is zero.
     */
    @JsName("parseBigRational")
    fun parseBigRational(value: String): BigRational

    /** As [parseBigRational], but returns `null` if [value] isn't a valid representation, or divides by zero. */
    @JsName("parseBigRationalOrNull")
    fun parseBigRationalOrNull(value: String): BigRational? =
        try {
            parseBigRational(value)
        } catch (_: NumberFormatException) {
            null
        } catch (_: ArithmeticException) {
            null
        }

    companion object {
        /** The radix of the decimal representation, the default one of [parseBigInteger]. */
        const val DEFAULT_RADIX: Int = 10

        /** The most efficient implementation of the platform: the one of [BigInteger.of], [BigDecimal.of]. */
        @JvmStatic // BigFactory.getDefault() in Java
        @JsStatic
        @JsName("DEFAULT") // BigFactory.DEFAULT in JS: `default` is a reserved word there
        val default: BigFactory get() = DefaultBigFactory
    }
}
