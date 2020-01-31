package io.gciatto.kt.math

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface BigInteger : Comparable<BigInteger> {

    @JsName("nextProbablePrime")
    fun nextProbablePrime(): BigInteger

    @JsName("minus")
    operator fun minus(`val`: BigInteger): BigInteger

    @JsName("times")
    operator fun times(`val`: BigInteger): BigInteger

    @JsName("div")
    operator fun div(`val`: BigInteger): BigInteger

    @JsName("divideAndRemainder")
    fun divideAndRemainder(`val`: BigInteger): Pair<BigInteger, BigInteger>

    @JsName("reminder")
    fun remainder(`val`: BigInteger): BigInteger

    @JsName("pow")
    infix fun pow(exponent: Int): BigInteger

    @JsName("sqrt")
    fun sqrt(): BigInteger

    @JsName("sqrtAndRemainder")
    fun sqrtAndRemainder(): Pair<BigInteger, BigInteger>

    @JsName("gcd")
    fun gcd(`val`: BigInteger): BigInteger

    val absoluteValue: BigInteger

    @JsName("unaryMinus")
    operator fun unaryMinus(): BigInteger

    @JsName("unaryPlus")
    operator fun unaryPlus(): BigInteger

    val signum: Int

    @JsName("rem")
    operator fun rem(m: BigInteger): BigInteger

    @JsName("modPow")
    fun modPow(exponent: BigInteger, m: BigInteger): BigInteger

    @JsName("modInverse")
    fun modInverse(m: BigInteger): BigInteger

    @JsName("shl")
    infix fun shl(n: Int): BigInteger

    @JsName("shr")
    infix fun shr(n: Int): BigInteger

    @JsName("and")
    fun and(`val`: BigInteger): BigInteger

    @JsName("or")
    fun or(`val`: BigInteger): BigInteger

    @JsName("xor")
    fun xor(`val`: BigInteger): BigInteger

    @JsName("not")
    operator fun not(): BigInteger

    @JsName("andNot")
    fun andNot(`val`: BigInteger): BigInteger

    @JsName("testBit")
    fun testBit(n: Int): Boolean

    @JsName("get")
    operator fun get(n: Int): Boolean

    @JsName("set")
    operator fun set(n: Int, b: Boolean)

    @JsName("setBit")
    fun setBit(n: Int): BigInteger

    @JsName("clearBit")
    fun clearBit(n: Int): BigInteger

    @JsName("flipBit")
    fun flipBit(n: Int): BigInteger

    @JsName("isProbablePrime")
    fun isProbablePrime(certainty: Int): Boolean

    override fun compareTo(other: BigInteger): Int

    override fun equals(other: Any?): Boolean

    @JsName("min")
    fun min(`val`: BigInteger): BigInteger

    @JsName("max")
    fun max(`val`: BigInteger): BigInteger

    override fun hashCode(): Int

    @JsName("toStringWithRadix")
    fun toString(radix: Int): String

    override fun toString(): String

    @JsName("toByteArray")
    fun toByteArray(): ByteArray

    @JsName("toInt")
    fun toInt(): Int

    @JsName("toLong")
    fun toLong(): Long

    @JsName("toByte")
    fun toByte(): Byte

    @JsName("toChar")
    fun toChar(): Char

    @JsName("toShort")
    fun toShort(): Short

    @JsName("toFloat")
    fun toFloat(): Float

    @JsName("toDouble")
    fun toDouble(): Double

    @JsName("toIntExact")
    fun toIntExact(): Int

    @JsName("toShortExact")
    fun toShortExact(): Short

    @JsName("toByteExact")
    fun toByteExact(): Byte

    companion object {

        @JsName("ofLong")
        @JvmStatic
        fun of(value: Long): BigInteger = bigIntegerOf(value)

        @JsName("of")
        @JvmStatic
        fun of(value: Int): BigInteger = bigIntegerOf(value)

        @JsName("parse")
        @JvmStatic
        fun of(value: String): BigInteger = bigIntegerOf(value)

        @JsName("parseWithRadix")
        @JvmStatic
        fun of(value: String, radix: Int): BigInteger = bigIntegerOf(value, radix)

        @JvmStatic
        val ZERO = of(0)

        @JvmStatic
        val ONE = of(1)

        @JvmStatic
        val TWO = of(2)

        @JvmStatic
        val NEGATIVE_ONE = of(-1)

        @JvmStatic
        val TEN = of(10)
    }
}

internal expect fun bigIntegerOf(value: Long): BigInteger

internal expect fun bigIntegerOf(value: Int): BigInteger

internal expect fun bigIntegerOf(value: String): BigInteger

internal expect fun bigIntegerOf(value: String, radix: Int): BigInteger