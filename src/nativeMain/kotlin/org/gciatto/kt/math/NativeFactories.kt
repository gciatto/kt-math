// the BigInteger factories (see commonMain's Utils.kt): on native targets, the Kotlin implementation
@file:Suppress("TooManyFunctions")

package org.gciatto.kt.math

import kotlin.random.Random

internal actual fun bigProbablePrimeInteger(
    bitLength: Int,
    rnd: Random,
): BigInteger = CommonBigInteger.probablePrime(bitLength, rnd)

internal actual fun bigIntegerOf(value: Long): BigInteger = CommonBigInteger.of(value)

internal actual fun bigIntegerOf(value: String): BigInteger = CommonBigInteger.of(value)

internal actual fun bigIntegerOf(value: Int): BigInteger = CommonBigInteger.of(value)

internal actual fun bigIntegerOf(
    value: String,
    radix: Int,
): BigInteger = CommonBigInteger.of(value, radix)

internal actual fun bigIntegerOf(value: IntArray): BigInteger = CommonBigInteger.of(value)

internal actual fun bigIntegerOf(
    signum: Int,
    magnitude: ByteArray,
    off: Int,
    len: Int,
): BigInteger = CommonBigInteger(signum, magnitude, off, len)
