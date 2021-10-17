@file:JvmName("JvmUtils")

package org.gciatto.kt.math

import java.math.BigDecimal as JavaBigDecimal
import java.math.BigInteger as JavaBigInteger
import java.math.MathContext as JavaMathContext
import java.math.RoundingMode as JavaRoundingMode

internal actual fun logImpl(lazyObject: () -> Any) {
    System.err.println(lazyObject())
}

fun BigInteger.toJava(): JavaBigInteger = JavaBigInteger(this.toByteArray())

fun fromJava(value: JavaBigInteger): BigInteger = BigInteger(value.toByteArray())

fun JavaBigInteger.toKotlin(): BigInteger = fromJava(this)

fun BigDecimal.toJava(): JavaBigDecimal =
    JavaBigDecimal(unscaledValue.toJava(), scale, JavaMathContext(precision, JavaRoundingMode.UNNECESSARY))

fun fromJava(value: JavaBigDecimal): BigDecimal =
    BigDecimal(
        fromJava(value.unscaledValue()),
        value.scale(),
        MathContext(value.precision(), RoundingMode.UNNECESSARY)
    )

fun JavaBigDecimal.toKotlin(): BigDecimal = fromJava(this)
