package org.gciatto.kt.math

import java.math.BigDecimal as JavaBigDecimal

class JavaBigDecimalAdapter(val value: JavaBigDecimal) : BigDecimal {
    override val absoluteValue: BigDecimal
        get() = TODO("Not yet implemented")

    override val signum: Int
        get() = TODO("Not yet implemented")

    override val scale: Int
        get() = TODO("Not yet implemented")

    override val precision: Int
        get() = TODO("Not yet implemented")

    override val unscaledValue: BigInteger
        get() = TODO("Not yet implemented")

    override fun plus(augend: BigDecimal?): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun plus(augend: BigDecimal?, mc: MathContext): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun minus(subtrahend: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun minus(subtrahend: BigDecimal, mc: MathContext): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun times(multiplicand: BigDecimal?): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun times(multiplicand: BigDecimal, mc: MathContext): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun div(divisor: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun div(divisor: BigDecimal, mc: MathContext): BigDecimal? {
        TODO("Not yet implemented")
    }

    override fun divideToIntegralValue(divisor: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun divideToIntegralValue(divisor: BigDecimal, mc: MathContext): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun rem(divisor: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun rem(divisor: BigDecimal, mc: MathContext): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun divideAndRemainder(divisor: BigDecimal): Pair<BigDecimal, BigDecimal> {
        TODO("Not yet implemented")
    }

    override fun divideAndRemainder(divisor: BigDecimal, mc: MathContext): Pair<BigDecimal, BigDecimal> {
        TODO("Not yet implemented")
    }

    override fun sqrt(mc: MathContext): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun pow(n: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun pow(n: Int, mc: MathContext): BigDecimal? {
        TODO("Not yet implemented")
    }

    override fun absoluteValue(mc: MathContext): BigDecimal? {
        TODO("Not yet implemented")
    }

    override fun unaryMinus(): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun unaryMinus(mc: MathContext): BigDecimal? {
        TODO("Not yet implemented")
    }

    override fun unaryPlus(): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun unaryPlus(mc: MathContext): BigDecimal? {
        TODO("Not yet implemented")
    }

    override fun round(mc: MathContext): BigDecimal? {
        TODO("Not yet implemented")
    }

    override fun setScale(newScale: Int, roundingMode: RoundingMode): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun setScale(newScale: Int, roundingMode: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun setScale(newScale: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun movePointLeft(n: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun movePointRight(n: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun scaleByPowerOfTen(n: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun stripTrailingZeros(): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun compareTo(other: BigDecimal): Int {
        TODO("Not yet implemented")
    }

    override fun compareMagnitude(`val`: BigDecimal): Int {
        TODO("Not yet implemented")
    }

    override fun min(`val`: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun max(`val`: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun toEngineeringString(): String {
        TODO("Not yet implemented")
    }

    override fun toPlainString(): String {
        TODO("Not yet implemented")
    }

    override fun toBigInteger(): BigInteger {
        TODO("Not yet implemented")
    }

    override fun toBigIntegerExact(): BigInteger {
        TODO("Not yet implemented")
    }

    override fun toLong(): Long {
        TODO("Not yet implemented")
    }

    override fun toLongExact(): Long {
        TODO("Not yet implemented")
    }

    override fun toInt(): Int {
        TODO("Not yet implemented")
    }

    override fun toByte(): Byte {
        TODO("Not yet implemented")
    }

    override fun toChar(): Char {
        TODO("Not yet implemented")
    }

    override fun toShort(): Short {
        TODO("Not yet implemented")
    }

    override fun toIntExact(): Int {
        TODO("Not yet implemented")
    }

    override fun toShortExact(): Short {
        TODO("Not yet implemented")
    }

    override fun toByteExact(): Byte {
        TODO("Not yet implemented")
    }

    override fun toFloat(): Float {
        TODO("Not yet implemented")
    }

    override fun toDouble(): Double {
        TODO("Not yet implemented")
    }

    override fun ulp(): BigDecimal {
        TODO("Not yet implemented")
    }
}