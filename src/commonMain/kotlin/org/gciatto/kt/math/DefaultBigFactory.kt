package org.gciatto.kt.math

import kotlin.random.Random

/** The [BigFactory] delegating to the companion objects of [BigInteger], [BigDecimal] and [BigRational]. */
internal object DefaultBigFactory : BigFactory {
    override fun bigIntegerOf(value: Int): BigInteger = BigInteger.of(value)

    override fun bigIntegerOf(value: Long): BigInteger = BigInteger.of(value)

    override fun parseBigInteger(
        value: String,
        radix: Int,
    ): BigInteger = BigInteger.of(value, radix)

    override fun bigIntegerOf(value: IntArray): BigInteger = BigInteger.of(value)

    override fun bigIntegerOf(
        signum: Int,
        magnitude: ByteArray,
        offset: Int,
        length: Int,
    ): BigInteger = BigInteger.of(signum, magnitude, offset, length)

    override fun probablePrime(
        bitLength: Int,
        random: Random,
    ): BigInteger = BigInteger.probablePrime(bitLength, random)

    override fun bigDecimalOf(
        value: Int,
        context: MathContext?,
    ): BigDecimal = if (context == null) BigDecimal.of(value) else BigDecimal.of(value, context)

    override fun bigDecimalOf(
        value: Long,
        context: MathContext?,
    ): BigDecimal = if (context == null) BigDecimal.of(value) else BigDecimal.of(value, context)

    override fun bigDecimalOf(
        value: Double,
        context: MathContext?,
    ): BigDecimal = BigDecimal.of(value, context)

    override fun bigDecimalValueOf(value: Double): BigDecimal = BigDecimal.valueOf(value)

    override fun bigDecimalOf(
        value: BigInteger,
        context: MathContext?,
    ): BigDecimal = BigDecimal.of(value, context)

    override fun bigDecimalOf(
        unscaledValue: BigInteger,
        scale: Int,
        context: MathContext?,
    ): BigDecimal = BigDecimal.of(unscaledValue, scale, 0).let { if (context == null) it else it.round(context)!! }

    override fun bigDecimalOf(
        unscaledValue: Long,
        scale: Int,
    ): BigDecimal = BigDecimal.of(unscaledValue, scale)

    override fun parseBigDecimal(
        value: String,
        context: MathContext?,
    ): BigDecimal = BigDecimal.of(value, context)

    override fun bigRationalOf(value: Int): BigRational = BigRational.valueOf(value)

    override fun bigRationalOf(value: Long): BigRational = BigRational.valueOf(BigInteger.of(value))

    override fun bigRationalOf(value: BigInteger): BigRational = BigRational.valueOf(value)

    override fun bigRationalOf(value: BigDecimal): BigRational = BigRational.valueOf(value)

    override fun bigRationalOf(value: Double): BigRational = BigRational.valueOf(value)

    override fun bigRationalOf(
        numerator: Int,
        denominator: Int,
    ): BigRational = BigRational.valueOf(numerator, denominator)

    override fun bigRationalOf(
        numerator: BigInteger,
        denominator: BigInteger,
    ): BigRational = BigRational.valueOf(numerator, denominator)

    override fun bigRationalOf(
        numerator: BigDecimal,
        denominator: BigDecimal,
    ): BigRational = BigRational.valueOf(numerator, denominator)

    override fun parseBigRational(value: String): BigRational = BigRational.valueOf(value)
}
