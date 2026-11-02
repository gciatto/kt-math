// UnusedParameter: detekt doesn't see the uses inside js()
@file:Suppress("TooManyFunctions", "UnsafeCastFromDynamic", "UnusedParameter")

package org.gciatto.kt.math

// Operations on native JS BigInts, which Kotlin has no type for: `dynamic` arithmetic operators compile to the JS
// ones, the rest goes through js(). BigInt literals (`0n`) aren't supported by js(), hence big(0).
private fun big(x: dynamic): dynamic = js("BigInt(x)")

private fun bigAnd(
    a: dynamic,
    b: dynamic,
): dynamic = js("a & b")

private fun bigOr(
    a: dynamic,
    b: dynamic,
): dynamic = js("a | b")

private fun bigXor(
    a: dynamic,
    b: dynamic,
): dynamic = js("a ^ b")

private fun bigNot(a: dynamic): dynamic = js("~a")

private fun bigShl(
    a: dynamic,
    n: Int,
): dynamic = js("a << BigInt(n)")

private fun bigShr(
    a: dynamic,
    n: Int,
): dynamic = js("a >> BigInt(n)")

// js() doesn't support `**`: square-and-multiply (n >= 0)
private fun bigPow(
    a: dynamic,
    n: Int,
): dynamic {
    var result: dynamic = big(1)
    var base: dynamic = a
    var e = n
    while (e > 0) {
        if (e and 1 == 1) result = result * base
        e = e shr 1
        if (e > 0) base = base * base
    }
    return result
}

private fun bigCompare(
    a: dynamic,
    b: dynamic,
): Int = js("a < b ? -1 : (a > b ? 1 : 0)")

private fun bigAsIntN(
    bits: Int,
    a: dynamic,
): dynamic = js("BigInt.asIntN(bits, a)")

private fun bigAsUintN(
    bits: Int,
    a: dynamic,
): dynamic = js("BigInt.asUintN(bits, a)")

private fun bigToNumber(a: dynamic): Double = js("Number(a)")

private fun bigToString(
    a: dynamic,
    radix: Int,
): String = js("a.toString(radix)")

private fun bigFromString(s: String): dynamic = js("BigInt(s)")

private val BIG_ZERO: dynamic = big(0)
private val BIG_ONE: dynamic = big(1)

private val ASCII_NUMBER = Regex("[+-]?[0-9A-Za-z]+")

private const val DECIMAL_RADIX = 10
private const val OCTAL_RADIX = 8
private const val HEX_RADIX = 16
private const val MAX_RADIX = 36
private const val BYTE_MASK = 0xFF

private val RADIX_PREFIXES = mapOf(2 to "0b", OCTAL_RADIX to "0o", HEX_RADIX to "0x")

/**
 * A [BigInteger] backed by a native JS `BigInt`, much faster than the Kotlin implementation ([CommonBigInteger]),
 * which it falls back to for rarely used operations (primality tests, `toFloat`) and unusual inputs. Semantics
 * (results, exception types) are `java.math.BigInteger`'s, as for every other implementation.
 */
internal class JsBigInteger(
    val value: dynamic,
) : BigInteger {
    private val isNegative: Boolean get() = bigCompare(value, BIG_ZERO) < 0

    private fun BigInteger.big(): dynamic =
        when (this) {
            is JsBigInteger -> value
            is CommonBigInteger -> of(this).value
            else -> throw IllegalArgumentException("Unsupported BigInteger implementation: ${this::class}")
        }

    private fun nonZero(divisor: BigInteger): dynamic {
        val d = divisor.big()
        if (bigCompare(d, BIG_ZERO) == 0) throw ArithmeticException("BigInteger divide by zero")
        return d
    }

    private fun positive(modulus: BigInteger): dynamic {
        val m = modulus.big()
        if (bigCompare(m, BIG_ZERO) <= 0) throw ArithmeticException("BigInteger: modulus not positive")
        return m
    }

    private fun bitAddress(n: Int): Int {
        if (n < 0) throw ArithmeticException("Negative bit address")
        return n
    }

    // The non-negative remainder of value modulo m (m > 0)
    private fun mod(m: dynamic): dynamic {
        val r = value % m
        return if (bigCompare(r, BIG_ZERO) < 0) r + m else r
    }

    fun toCommon(): CommonBigInteger = CommonBigInteger(toByteArray())

    override val absoluteValue: BigInteger
        get() = if (isNegative) JsBigInteger(-value) else this

    override val signum: Int
        get() = bigCompare(value, BIG_ZERO)

    // Bits of the two's complement representation, excluding the sign bit
    override val bitLength: Int
        get() {
            val x = if (isNegative) bigNot(value) else value
            return if (bigCompare(x, BIG_ZERO) == 0) 0 else bigToString(x, 2).length
        }

    override val bitCount: Int
        get() = bigToString(if (isNegative) bigNot(value) else value, 2).count { it == '1' }

    override fun nextProbablePrime(): BigInteger = of(toCommon().nextProbablePrime())

    override fun plus(other: BigInteger): BigInteger = JsBigInteger(value + other.big())

    override fun minus(other: BigInteger): BigInteger = JsBigInteger(value - other.big())

    override fun times(other: BigInteger): BigInteger = JsBigInteger(value * other.big())

    override fun div(other: BigInteger): BigInteger = JsBigInteger(value / nonZero(other))

    override fun divideAndRemainder(other: BigInteger): Array<out BigInteger> {
        val d = nonZero(other)
        return arrayOf(JsBigInteger(value / d), JsBigInteger(value % d))
    }

    override fun remainder(other: BigInteger): BigInteger = JsBigInteger(value % nonZero(other))

    override fun rem(modulus: BigInteger): BigInteger = remainder(modulus)

    override fun pow(exponent: Int): BigInteger {
        if (exponent < 0) throw ArithmeticException("Negative exponent")
        return JsBigInteger(bigPow(value, exponent))
    }

    override fun sqrt(): BigInteger {
        if (isNegative) throw ArithmeticException("Negative BigInteger")
        if (bigCompare(value, big(2)) < 0) return this
        // Newton's iteration from a power of two above the root: decreases monotonically to floor(sqrt(value))
        var x: dynamic = bigShl(BIG_ONE, (bitLength + 1) / 2)
        while (true) {
            val y: dynamic = bigShr(x + value / x, 1)
            if (bigCompare(y, x) >= 0) return JsBigInteger(x)
            x = y
        }
    }

    override fun sqrtAndRemainder(): Array<out BigInteger> {
        val s = sqrt() as JsBigInteger
        return arrayOf(s, JsBigInteger(value - s.value * s.value))
    }

    override fun gcd(other: BigInteger): BigInteger {
        var a: dynamic = absoluteValue.big()
        var b: dynamic = other.absoluteValue.big()
        while (bigCompare(b, BIG_ZERO) != 0) {
            val t = a % b
            a = b
            b = t
        }
        return JsBigInteger(a)
    }

    override fun unaryMinus(): BigInteger = JsBigInteger(-value)

    override fun unaryPlus(): BigInteger = this

    override fun modPow(
        exponent: BigInteger,
        modulus: BigInteger,
    ): BigInteger {
        val m = positive(modulus)
        if (bigCompare(m, BIG_ONE) == 0) return ZERO
        var e: dynamic = exponent.big()
        var base: dynamic = if (bigCompare(e, BIG_ZERO) < 0) (modInverse(modulus) as JsBigInteger).value else mod(m)
        if (bigCompare(e, BIG_ZERO) < 0) e = -e
        // Right-to-left binary exponentiation: BigInt multiplication and remainder are native
        var result: dynamic = BIG_ONE
        while (bigCompare(e, BIG_ZERO) > 0) {
            if (bigCompare(bigAnd(e, BIG_ONE), BIG_ZERO) != 0) result = result * base % m
            base = base * base % m
            e = bigShr(e, 1)
        }
        return JsBigInteger(result)
    }

    override fun modInverse(modulus: BigInteger): BigInteger {
        val m = positive(modulus)
        if (bigCompare(m, BIG_ONE) == 0) return ZERO
        // Extended Euclid on (value mod m, m), tracking the coefficient of value
        var r0: dynamic = mod(m)
        var r1: dynamic = m
        var s0: dynamic = BIG_ONE
        var s1: dynamic = BIG_ZERO
        while (bigCompare(r1, BIG_ZERO) != 0) {
            val q = r0 / r1
            val r = r0 - q * r1
            r0 = r1
            r1 = r
            val s = s0 - q * s1
            s0 = s1
            s1 = s
        }
        if (bigCompare(r0, BIG_ONE) != 0) throw ArithmeticException("BigInteger not invertible.")
        return JsBigInteger(if (bigCompare(s0, BIG_ZERO) < 0) s0 + m else s0)
    }

    // BigInt shifts have java.math semantics: arithmetic (floor) right shifts, negative distances shift the other way
    override fun shl(n: Int): BigInteger = JsBigInteger(bigShl(value, n))

    override fun shr(n: Int): BigInteger = JsBigInteger(bigShr(value, n))

    // BigInt bitwise operations work on (infinite) two's complement representations, as java.math's
    override fun and(other: BigInteger): BigInteger = JsBigInteger(bigAnd(value, other.big()))

    override fun or(other: BigInteger): BigInteger = JsBigInteger(bigOr(value, other.big()))

    override fun xor(other: BigInteger): BigInteger = JsBigInteger(bigXor(value, other.big()))

    override fun not(): BigInteger = JsBigInteger(bigNot(value))

    override fun andNot(other: BigInteger): BigInteger = JsBigInteger(bigAnd(value, bigNot(other.big())))

    override fun testBit(n: Int): Boolean = bigCompare(bigAnd(bigShr(value, bitAddress(n)), BIG_ONE), BIG_ZERO) != 0

    override fun getIntBits(
        index: Int,
        count: Int,
    ): Int {
        require(count in 0..Int.SIZE_BITS) { "Bit count must be in 0..${Int.SIZE_BITS}: $count" }
        return getLongBits(index, count).toInt()
    }

    override fun getLongBits(
        index: Int,
        count: Int,
    ): Long {
        val shifted = bigShr(value, bitAddress(index))
        require(count in 0..Long.SIZE_BITS) { "Bit count must be in 0..${Long.SIZE_BITS}: $count" }
        return bigAsIntN(Long.SIZE_BITS, bigAsUintN(count, shifted))
    }

    override fun get(n: Int): Boolean = testBit(n)

    override fun set(
        n: Int,
        b: Boolean,
    ): BigInteger = if (b) setBit(n) else clearBit(n)

    override fun setBit(n: Int): BigInteger = JsBigInteger(bigOr(value, bigShl(BIG_ONE, bitAddress(n))))

    override fun clearBit(n: Int): BigInteger = JsBigInteger(bigAnd(value, bigNot(bigShl(BIG_ONE, bitAddress(n)))))

    override fun flipBit(n: Int): BigInteger = JsBigInteger(bigXor(value, bigShl(BIG_ONE, bitAddress(n))))

    override fun isProbablePrime(certainty: Int): Boolean = toCommon().isProbablePrime(certainty)

    override fun compareTo(other: BigInteger): Int = bigCompare(value, other.big())

    override fun equals(other: Any?): Boolean = other is JsBigInteger && bigCompare(value, other.value) == 0

    // java.math.BigInteger's: the magnitude's 32-bit words, most significant first, combined by 31 * h + word
    override fun hashCode(): Int {
        val magnitude: dynamic = (absoluteValue as JsBigInteger).value
        var hash = 0
        for (i in (absoluteValue.bitLength + WORD_BITS - 1) / WORD_BITS - 1 downTo 0) {
            hash =
                WORD_HASH_FACTOR * hash +
                bigToNumber(bigAsUintN(WORD_BITS, bigShr(magnitude, i * WORD_BITS))).toLong().toInt()
        }
        return hash * signum
    }

    override fun min(other: BigInteger): BigInteger = if (this <= other) this else other

    override fun max(other: BigInteger): BigInteger = if (this >= other) this else other

    override fun toString(radix: Int): String = bigToString(value, if (radix in 2..MAX_RADIX) radix else DECIMAL_RADIX)

    override fun toString(): String = bigToString(value, DECIMAL_RADIX)

    // Big-endian two's complement, minimal length (at least one byte), as java.math.BigInteger.toByteArray
    override fun toByteArray(): ByteArray {
        val length = bitLength / Byte.SIZE_BITS + 1
        val hex = bigToString(bigAsUintN(length * Byte.SIZE_BITS, value), HEX_RADIX).padStart(length * 2, '0')
        return ByteArray(length) { hex.substring(it * 2, it * 2 + 2).toInt(HEX_RADIX).toByte() }
    }

    override fun toInt(): Int = bigToNumber(bigAsIntN(Int.SIZE_BITS, value)).toInt()

    // Kotlin Longs are BigInts on JS (-Xes-long-as-bigint): a 64-bit BigInt is one
    override fun toLong(): Long = bigAsIntN(Long.SIZE_BITS, value)

    override fun toByte(): Byte = toInt().toByte()

    override fun toChar(): Char = toInt().toChar()

    override fun toShort(): Short = toInt().toShort()

    // Correct rounding straight to Float isn't Math.fround(Number(value)) (double rounding): use the Kotlin one
    override fun toFloat(): Float = toCommon().toFloat()

    // Number(bigint) rounds to nearest, ties to even, as java.math.BigInteger.doubleValue
    override fun toDouble(): Double = bigToNumber(value)

    private fun checkBitLength(
        bits: Int,
        type: String,
    ) {
        if (bitLength >= bits) throw ArithmeticException("BigInteger out of $type range")
    }

    override fun toLongExact(): Long = toLong().also { checkBitLength(Long.SIZE_BITS, "long") }

    override fun toIntExact(): Int = toInt().also { checkBitLength(Int.SIZE_BITS, "int") }

    override fun toShortExact(): Short = toShort().also { checkBitLength(Short.SIZE_BITS, "short") }

    override fun toByteExact(): Byte = toByte().also { checkBitLength(Byte.SIZE_BITS, "byte") }

    companion object {
        private const val WORD_BITS = 32
        private const val WORD_HASH_FACTOR = 31

        val ZERO = JsBigInteger(BIG_ZERO)

        val TEN = JsBigInteger(big(DECIMAL_RADIX))

        fun of(value: Long): JsBigInteger = JsBigInteger(big(value))

        fun of(value: Int): JsBigInteger = JsBigInteger(big(value))

        fun of(common: CommonBigInteger): JsBigInteger {
            val bytes = common.toByteArray()
            val hex = bytes.joinToString("") { (it.toInt() and BYTE_MASK).toString(HEX_RADIX).padStart(2, '0') }
            var value: dynamic = bigFromString("0x$hex")
            if (bytes[0] < 0) value = value - bigShl(BIG_ONE, bytes.size * Byte.SIZE_BITS) // negative
            return JsBigInteger(value)
        }

        /**
         * Parses as `java.math.BigInteger(value, radix)`: natively for plain ASCII digits, else via [CommonBigInteger].
         */
        fun parse(
            value: String,
            radix: Int,
        ): JsBigInteger {
            val isPlain =
                radix in 2..MAX_RADIX &&
                    ASCII_NUMBER.matches(value) &&
                    value.all { it == '+' || it == '-' || it.digitToIntOrNull(radix) != null }
            if (!isPlain) return of(CommonBigInteger(value, radix))
            val negative = value[0] == '-'
            val digits = value.trimStart('+', '-')
            val prefix = RADIX_PREFIXES[radix]
            val magnitude: dynamic =
                when {
                    radix == DECIMAL_RADIX -> bigFromString(digits)
                    prefix != null -> bigFromString(prefix + digits)
                    else -> parseChunked(digits, radix)
                }
            return JsBigInteger(if (negative) -magnitude else magnitude)
        }

        // Digit chunks small enough to be exact as Numbers (36^9 < 2^53)
        private fun parseChunked(
            digits: String,
            radix: Int,
        ): dynamic {
            var result: dynamic = BIG_ZERO
            for (chunk in digits.chunked(CHUNK_DIGITS)) {
                val scale: dynamic = bigPow(big(radix), chunk.length)
                result = result * scale + big(chunk.toLong(radix))
            }
            return result
        }

        private const val CHUNK_DIGITS = 9
    }
}
