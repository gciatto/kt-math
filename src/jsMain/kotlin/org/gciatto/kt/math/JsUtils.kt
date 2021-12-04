package org.gciatto.kt.math

import kotlin.random.Random

internal actual fun logImpl(lazyObject: () -> Any) {
    console.warn(lazyObject())
}

internal actual inline fun <T, reified U : T> T?.castTo(): U {
    return this.unsafeCast<U>()
}

internal actual fun bigProbablePrimeInteger(bitLength: Int, rnd: Random): BigInteger {
    return CommonBigInteger.probablePrime(bitLength, rnd)
}

internal actual fun bigIntegerOf(value: Long): BigInteger {
    return CommonBigInteger.of(value)
}

internal actual fun bigIntegerOf(value: String): BigInteger {
    return CommonBigInteger.of(value)
}

internal actual fun bigIntegerOf(value: Int): BigInteger {
    return CommonBigInteger.of(value)
}

internal actual fun bigIntegerOf(value: String, radix: Int): BigInteger {
    return CommonBigInteger.of(value, radix)
}

internal actual fun bigIntegerOf(value: IntArray): BigInteger {
    return CommonBigInteger.of(value)
}

internal actual fun bigDecimalOf(unscaledVal: Long, scale: Int): BigDecimal {
    return CommonBigDecimal.of(unscaledVal, scale)
}

internal actual fun bigDecimalOf(unscaledVal: Long, scale: Int, prec: Int): BigDecimal {
    return CommonBigDecimal.of(unscaledVal, scale, prec)
}

internal actual fun bigDecimalOf(`val`: Int): BigDecimal {
    return CommonBigDecimal.of(`val`)
}

internal actual fun bigDecimalOf(`val`: Long): BigDecimal {
    return CommonBigDecimal.of(`val`)
}

internal actual fun bigDecimalOf(intVal: BigInteger, scale: Int, prec: Int): BigDecimal {
    return CommonBigDecimal.of(intVal.castTo<BigInteger, CommonBigInteger>(), scale, prec)
}

internal actual fun bigDecimalOf(`val`: Double, ctx: MathContext?): BigDecimal =
    if (ctx == null) {
        CommonBigDecimal(`val`)
    } else {
        CommonBigDecimal.of(`val`, ctx)
    }

internal actual fun bigDecimalOf(`val`: Float, ctx: MathContext?): BigDecimal =
    bigDecimalOf(`val`.toDouble(), ctx)

internal actual fun bigDecimalOf(`val`: String, ctx: MathContext?): BigDecimal =
    if (ctx == null) {
        CommonBigDecimal(`val`)
    } else {
        CommonBigDecimal.of(`val`, ctx)
    }

internal actual fun bigDecimalOf(`val`: BigInteger, ctx: MathContext?): BigDecimal {
    val `val`: CommonBigInteger = `val`.castTo()
    return if (ctx == null) {
        CommonBigDecimal(`val`)
    } else {
        CommonBigDecimal.of(`val`, ctx)
    }
}

internal actual fun bigDecimalOf(`val`: Int, ctx: MathContext): BigDecimal = CommonBigDecimal.of(`val`, ctx)

internal actual fun bigDecimalOf(`val`: Long, ctx: MathContext): BigDecimal = CommonBigDecimal.of(`val`, ctx)
