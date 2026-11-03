// one actual per expect factory declared in commonMain's Utils.kt
@file:JvmName("JvmUtils")
@file:Suppress("TooManyFunctions")

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

/** Returns this as a `java.math.BigInteger`: the one it wraps, or a copy if backed by the Kotlin implementation. */
fun BigInteger.toJava(): JavaBigInteger =
    when (this) {
        is JavaBigIntegerAdapter -> value
        else -> JavaBigInteger(this.toByteArray())
    }

/** Returns this as a `java.math.BigDecimal`: the one it wraps, or a copy if backed by the Kotlin implementation. */
fun BigDecimal.toJava(): JavaBigDecimal =
    when (this) {
        is JavaBigDecimalAdapter -> value
        else -> JavaBigDecimal(unscaledValue.toJava(), scale, JavaMathContext(precision, JavaRoundingMode.UNNECESSARY))
    }

/** Returns this `java.math.RoundingMode` as kt-math's [RoundingMode]. */
fun JavaRoundingMode.toKotlin(): RoundingMode = RoundingMode.valueOf(ordinal)

/** Returns this as a `java.math.RoundingMode`. */
fun RoundingMode.toJava(): JavaRoundingMode = JavaRoundingMode.valueOf(value)

/** Returns this `java.math.MathContext` as kt-math's [MathContext]. */
fun JavaMathContext.toKotlin(): MathContext = MathContext(precision, roundingMode.toKotlin())

/** Returns this as a `java.math.MathContext`. */
fun MathContext.toJava(): JavaMathContext = JavaMathContext(precision, roundingMode.toJava())

/** Returns the [BigInteger] wrapping [value], with no copy (see [toKotlin]). */
fun fromJava(value: JavaBigInteger): BigInteger = JavaBigIntegerAdapter(value)

/** Returns this `java.math.BigInteger` as a kt-math [BigInteger], wrapping it with no copy. */
fun JavaBigInteger.toKotlin(): BigInteger = fromJava(this)

/** Returns the [BigDecimal] wrapping [value], with no copy (see [toKotlin]). */
fun fromJava(value: JavaBigDecimal): BigDecimal = JavaBigDecimalAdapter(value)

/** Returns this `java.math.BigDecimal` as a kt-math [BigDecimal], wrapping it with no copy. */
fun JavaBigDecimal.toKotlin(): BigDecimal = fromJava(this)

internal actual inline fun <T, reified U : T> T?.castTo(): U = this as U

internal actual fun bigProbablePrimeInteger(
    bitLength: Int,
    rnd: Random,
): BigInteger = JavaBigIntegerAdapter(JavaBigInteger.probablePrime(bitLength, rnd.asJavaRandom()))

internal actual fun bigIntegerOf(value: Long): BigInteger = JavaBigIntegerAdapter(JavaBigInteger.valueOf(value))

internal actual fun bigIntegerOf(value: String): BigInteger {
    val (radix, string) = value.getRadix()
    return bigIntegerOf(string, radix)
}

internal actual fun bigIntegerOf(value: Int): BigInteger = JavaBigIntegerAdapter(JavaBigInteger.valueOf(value.toLong()))

internal actual fun bigIntegerOf(
    value: String,
    radix: Int,
): BigInteger = JavaBigIntegerAdapter(JavaBigInteger(value, radix))

internal actual fun bigIntegerOf(value: IntArray): BigInteger =
    JavaBigIntegerAdapter(CommonBigInteger.of(value).toJava())

internal actual fun bigDecimalOf(
    unscaledVal: Long,
    scale: Int,
): BigDecimal = JavaBigDecimalAdapter(JavaBigDecimal.valueOf(unscaledVal, scale))

internal actual fun bigDecimalOf(
    unscaledVal: Long,
    scale: Int,
    prec: Int,
): BigDecimal =
    JavaBigDecimalAdapter(
        JavaBigDecimal(
            JavaBigInteger.valueOf(unscaledVal),
            scale,
            JavaMathContext(prec, JavaRoundingMode.UNNECESSARY),
        ),
    )

internal actual fun bigDecimalOf(value: Int): BigDecimal = JavaBigDecimalAdapter(JavaBigDecimal.valueOf(value.toLong()))

internal actual fun bigDecimalOf(value: Long): BigDecimal = JavaBigDecimalAdapter(JavaBigDecimal.valueOf(value))

internal actual fun bigDecimalOf(
    intVal: BigInteger,
    scale: Int,
    prec: Int,
): BigDecimal =
    JavaBigDecimalAdapter(
        JavaBigDecimal(
            intVal.toJava(),
            scale,
            JavaMathContext(prec, JavaRoundingMode.UNNECESSARY),
        ),
    )

internal actual fun bigDecimalOf(
    value: Double,
    ctx: MathContext?,
): BigDecimal =
    if (ctx == null) {
        JavaBigDecimalAdapter(JavaBigDecimal(value))
    } else {
        JavaBigDecimalAdapter(JavaBigDecimal(value, ctx.toJava()))
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
        JavaBigDecimalAdapter(JavaBigDecimal(value))
    } else {
        JavaBigDecimalAdapter(JavaBigDecimal(value, ctx.toJava()))
    }

internal actual fun bigDecimalOf(
    value: BigInteger,
    ctx: MathContext?,
): BigDecimal =
    if (ctx == null) {
        JavaBigDecimalAdapter(JavaBigDecimal(value.toJava()))
    } else {
        JavaBigDecimalAdapter(JavaBigDecimal(value.toJava(), ctx.toJava()))
    }

internal actual fun bigDecimalOf(
    value: Int,
    ctx: MathContext,
): BigDecimal = bigDecimalOf(value.toLong(), ctx)

internal actual fun bigDecimalOf(
    value: Long,
    ctx: MathContext,
): BigDecimal = JavaBigDecimalAdapter(JavaBigDecimal(value, ctx.toJava()))

internal actual fun bigIntegerOf(
    signum: Int,
    magnitude: ByteArray,
    off: Int,
    len: Int,
): BigInteger = CommonBigInteger(signum, magnitude, off, len).toJava().toKotlin()
