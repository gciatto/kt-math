@file:JvmName("JvmUtils")

package org.gciatto.kt.math

import kotlin.random.Random
import kotlin.random.asJavaRandom
import java.math.BigDecimal as JavaBigDecimal
import java.math.BigInteger as JavaBigInteger
import java.math.MathContext as JavaMathContext
import java.math.RoundingMode as JavaRoundingMode

internal actual fun logImpl(lazyObject: () -> Any) {
    System.err.println(lazyObject())
}

fun BigInteger.toJava(): JavaBigInteger = when (this) {
    is JavaBigIntegerAdapter -> value
    else -> JavaBigInteger(this.toByteArray())
}

fun fromJava(value: JavaBigInteger): BigInteger = JavaBigIntegerAdapter(value)

fun JavaBigInteger.toKotlin(): BigInteger = fromJava(this)

fun BigDecimal.toJava(): JavaBigDecimal =
    JavaBigDecimal(unscaledValue.toJava(), scale, JavaMathContext(precision, JavaRoundingMode.UNNECESSARY))

fun fromJava(value: JavaBigDecimal): BigDecimal =
    TODO()

fun JavaBigDecimal.toKotlin(): BigDecimal = fromJava(this)

internal actual inline fun <T, reified U : T> T.castTo(): U {
    return this as U
}

internal actual fun bigProbablePrimeInteger(bitLength: Int, rnd: Random): BigInteger {
    return JavaBigIntegerAdapter(JavaBigInteger.probablePrime(bitLength, rnd.asJavaRandom()))
}

internal actual fun bigIntegerOf(value: Long): BigInteger {
    return JavaBigIntegerAdapter(JavaBigInteger.valueOf(value))
}

internal actual fun bigIntegerOf(value: String): BigInteger {
    return JavaBigIntegerAdapter(JavaBigInteger(value))
}

internal actual fun bigIntegerOf(value: Int): BigInteger {
    return JavaBigIntegerAdapter(JavaBigInteger.valueOf(value.toLong()))
}

internal actual fun bigIntegerOf(value: String, radix: Int): BigInteger {
    return JavaBigIntegerAdapter(JavaBigInteger(value, radix))
}

internal actual fun bigIntegerOf(value: IntArray): BigInteger {
    return JavaBigIntegerAdapter(CommonBigInteger.of(value).toJava())
}
