package org.gciatto.kt.math

import kotlin.random.Random

internal actual fun logImpl(lazyObject: () -> Any) {
    console.warn(lazyObject())
}

internal actual inline fun <T, reified U : T> T.castTo(): U {
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
