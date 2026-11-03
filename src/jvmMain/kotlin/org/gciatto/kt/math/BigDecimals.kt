package org.gciatto.kt.math

import java.math.BigDecimal as JavaBigDecimal

internal actual object BigDecimals {
    actual val zero: BigDecimal = JavaBigDecimal.ZERO.toKotlin()

    actual val one: BigDecimal = JavaBigDecimal.ONE.toKotlin()

    actual val two: BigDecimal = bigDecimalOf(2)

    actual val ten: BigDecimal = JavaBigDecimal.TEN.toKotlin()

    actual val oneTenth: BigDecimal = bigDecimalOf("0.1")

    actual val oneHalf: BigDecimal = bigDecimalOf("0.5")

    actual val pi: BigDecimal = bigDecimalOf(PI_STRING)

    actual val e: BigDecimal = bigDecimalOf(E_STRING)
}
