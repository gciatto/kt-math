// the BigInteger factories (see commonMain's Utils.kt): on JS, native BigInts (JsBigInteger)
@file:Suppress("TooManyFunctions")

package org.gciatto.kt.math

import kotlin.random.Random

internal actual fun bigProbablePrimeInteger(
    bitLength: Int,
    rnd: Random,
): BigInteger = JsBigInteger.of(CommonBigInteger.probablePrime(bitLength, rnd))

internal actual fun bigIntegerOf(value: Long): BigInteger = JsBigInteger.of(value)

internal actual fun bigIntegerOf(value: String): BigInteger {
    val (radix, digits) = value.getRadix()
    return JsBigInteger.parse(digits, radix)
}

internal actual fun bigIntegerOf(value: Int): BigInteger = JsBigInteger.of(value)

internal actual fun bigIntegerOf(
    value: String,
    radix: Int,
): BigInteger = JsBigInteger.parse(value, radix)

internal actual fun bigIntegerOf(value: IntArray): BigInteger = JsBigInteger.of(CommonBigInteger.of(value))

internal actual fun bigIntegerOf(
    signum: Int,
    magnitude: ByteArray,
    off: Int,
    len: Int,
): BigInteger = JsBigInteger.of(CommonBigInteger(signum, magnitude, off, len))
