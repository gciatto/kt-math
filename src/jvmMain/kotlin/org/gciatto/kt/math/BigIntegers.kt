package org.gciatto.kt.math

import java.math.BigInteger as JavaBigInteger

internal actual object BigIntegers {
    actual val zero: BigInteger = JavaBigInteger.ZERO.toKotlin()

    actual val one: BigInteger = JavaBigInteger.ONE.toKotlin()

    actual val two: BigInteger = JavaBigInteger.valueOf(2).toKotlin()

    actual val ten: BigInteger = JavaBigInteger.TEN.toKotlin()

    actual val negativeOne: BigInteger = JavaBigInteger.ONE.negate().toKotlin()
}
