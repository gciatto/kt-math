// one actual per expect factory declared in commonMain's Utils.kt: on every non-JVM platform, the Kotlin implementation
@file:Suppress("TooManyFunctions")

package org.gciatto.kt.math

internal actual fun bigDecimalOf(
    unscaledVal: Long,
    scale: Int,
): BigDecimal = CommonBigDecimal.of(unscaledVal, scale)

internal actual fun bigDecimalOf(
    unscaledVal: Long,
    scale: Int,
    prec: Int,
): BigDecimal = CommonBigDecimal.of(unscaledVal, scale, prec)

internal actual fun bigDecimalOf(value: Int): BigDecimal = CommonBigDecimal.of(value)

internal actual fun bigDecimalOf(value: Long): BigDecimal = CommonBigDecimal.of(value)

internal actual fun bigDecimalOf(
    intVal: BigInteger,
    scale: Int,
    prec: Int,
): BigDecimal = CommonBigDecimal.of(intVal.toCommon(), scale, prec)

private val exceptionalDoubles = setOf(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

internal actual fun bigDecimalOf(
    value: Double,
    ctx: MathContext?,
): BigDecimal =
    when {
        value in exceptionalDoubles -> throw NumberFormatException("Infinite or NaN")
        ctx == null -> CommonBigDecimal(value)
        else -> CommonBigDecimal.of(value, ctx)
    }

internal actual fun bigDecimalOf(
    value: Float,
    ctx: MathContext?,
): BigDecimal = bigDecimalOf(value.toDouble(), ctx)

internal actual fun bigDecimalOf(
    value: String,
    ctx: MathContext?,
): BigDecimal =
    if (ctx == null) {
        CommonBigDecimal(value)
    } else {
        CommonBigDecimal.of(value, ctx)
    }

internal actual fun bigDecimalOf(
    value: BigInteger,
    ctx: MathContext?,
): BigDecimal {
    val value: CommonBigInteger = value.toCommon()
    return if (ctx == null) {
        CommonBigDecimal(value)
    } else {
        CommonBigDecimal.of(value, ctx)
    }
}

internal actual fun bigDecimalOf(
    value: Int,
    ctx: MathContext,
): BigDecimal = CommonBigDecimal.of(value, ctx)

internal actual fun bigDecimalOf(
    value: Long,
    ctx: MathContext,
): BigDecimal = CommonBigDecimal.of(value, ctx)

internal actual object BigDecimals {
    actual val zero: BigDecimal = CommonBigDecimal.ZERO

    actual val one: BigDecimal = CommonBigDecimal.ONE

    actual val two: BigDecimal = CommonBigDecimal.TWO

    actual val ten: BigDecimal = CommonBigDecimal.TEN

    actual val oneTenth: BigDecimal = CommonBigDecimal.ONE_TENTH

    actual val oneHalf: BigDecimal = CommonBigDecimal.ONE_HALF

    actual val pi: BigDecimal = CommonBigDecimal.PI

    actual val e: BigDecimal = CommonBigDecimal.E
}
