package org.gciatto.kt.math

internal actual object BigIntegers {
    actual val zero: BigInteger = JsBigInteger.ZERO

    actual val one: BigInteger = JsBigInteger.of(1)

    actual val two: BigInteger = JsBigInteger.of(2)

    actual val ten: BigInteger = JsBigInteger.TEN

    actual val negativeOne: BigInteger = JsBigInteger.of(-1)
}
