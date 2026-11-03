package org.gciatto.kt.math

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Every method of [BigFactory] (through [BigFactory.default]), and the conversions among numbers. */
class TestBigFactory {
    private val factory = BigFactory.default

    private fun BigRational.reduced(): String = reduce().toRationalString()

    @Test
    fun bigIntegers() {
        assertEquals("42", factory.bigIntegerOf(42).toString())
        assertEquals("-2147483648", factory.bigIntegerOf(Int.MIN_VALUE).toString())
        assertEquals("9223372036854775807", factory.bigIntegerOf(Long.MAX_VALUE).toString())
        assertEquals("-1", factory.bigIntegerOf(intArrayOf(-1)).toString())
        assertEquals("4294967296", factory.bigIntegerOf(intArrayOf(1, 0)).toString())
        assertEquals("256", factory.bigIntegerOf(1, byteArrayOf(1, 0)).toString())
        assertEquals("-256", factory.bigIntegerOf(-1, byteArrayOf(1, 0)).toString())
        assertEquals("1", factory.bigIntegerOf(1, byteArrayOf(9, 1, 0), 1, 1).toString())
        assertEquals("0", factory.bigIntegerOf(0, byteArrayOf()).toString())
        assertFailsWith<NumberFormatException> { factory.bigIntegerOf(0, byteArrayOf(1)) }
        assertFailsWith<NumberFormatException> { factory.bigIntegerOf(2, byteArrayOf(1)) }
    }

    @Test
    fun probablePrimes() {
        val prime = factory.probablePrime(64, Random(1))
        assertEquals(64, prime.bitLength)
        assertTrue(prime.isProbablePrime(50))
        assertEquals(prime, factory.probablePrime(64, Random(1)))
        assertEquals(16, factory.probablePrime(16).bitLength)
        assertFailsWith<ArithmeticException> { factory.probablePrime(1) }
    }

    @Test
    fun parsingBigIntegers() {
        assertEquals("123", factory.parseBigInteger("123").toString())
        assertEquals("-255", factory.parseBigInteger("-ff", 16).toString())
        assertEquals("5", factory.parseBigInteger("101", 2).toString())
        assertFailsWith<NumberFormatException> { factory.parseBigInteger("12a") }
        assertFailsWith<NumberFormatException> { factory.parseBigInteger("") }
        assertEquals("-255", factory.parseBigIntegerOrNull("-ff", 16).toString())
        assertNull(factory.parseBigIntegerOrNull("12a"))
        assertNull(factory.parseBigIntegerOrNull("2", 2))
        assertNull(factory.parseBigIntegerOrNull(""))
    }

    @Test
    fun bigDecimals() {
        val twoDigits = MathContext(2)
        assertEquals("15", factory.bigDecimalOf(15).toString())
        assertEquals("2E+1", factory.bigDecimalOf(15, MathContext(1)).toString())
        assertEquals("9223372036854775807", factory.bigDecimalOf(Long.MAX_VALUE).toString())
        assertEquals("9.2E+18", factory.bigDecimalOf(Long.MAX_VALUE, twoDigits).toString())
        assertEquals("0.1000000000000000055511151231257827021181583404541015625", factory.bigDecimalOf(0.1).toString())
        assertEquals("0.10", factory.bigDecimalOf(0.1, twoDigits).toString())
        assertFailsWith<NumberFormatException> { factory.bigDecimalOf(Double.NaN) }
        assertEquals("12345", factory.bigDecimalOf(BigInteger.of(12345)).toString())
        assertEquals("1.2E+4", factory.bigDecimalOf(BigInteger.of(12345), twoDigits).toString())
        assertEquals("123.45", factory.bigDecimalOf(BigInteger.of(12345), 2).toString())
        assertEquals("1.2E+2", factory.bigDecimalOf(BigInteger.of(12345), 2, twoDigits).toString())
        assertEquals("12.345", factory.bigDecimalOf(12345L, 3).toString())
        assertEquals("1.2345E+7", factory.bigDecimalOf(12345L, -3).toString())
    }

    @Test
    fun shortestBigDecimalsOfDoubles() {
        // as java.math.BigDecimal.valueOf(double)
        mapOf(
            0.1 to "0.1",
            0.0 to "0.0",
            1.0 to "1.0",
            -123.456 to "-123.456",
            1e7 to "1.0E+7",
            1e-4 to "0.00010", // "1.0E-4" parsed, printed in plain notation (exponent >= -6)
            1.5e300 to "1.5E+300",
            0.001 to "0.001",
            9999999.0 to "9999999.0",
        ).forEach { (value, expected) -> assertEquals(expected, factory.bigDecimalValueOf(value).toString()) }
        assertFailsWith<NumberFormatException> { factory.bigDecimalValueOf(Double.POSITIVE_INFINITY) }
        assertFailsWith<NumberFormatException> { factory.bigDecimalValueOf(Double.NaN) }
    }

    @Test
    fun parsingBigDecimals() {
        assertEquals("-1.23E+4", factory.parseBigDecimal("-1.23E+4").toString())
        assertEquals("0.0010", factory.parseBigDecimal("0.0010").toString())
        assertEquals("-1.2E+4", factory.parseBigDecimal("-12345", MathContext(2)).toString())
        assertFailsWith<NumberFormatException> { factory.parseBigDecimal("1.2.3") }
        assertFailsWith<NumberFormatException> { factory.parseBigDecimal("") }
        assertEquals("1.5", factory.parseBigDecimalOrNull("1.5").toString())
        assertEquals("2", factory.parseBigDecimalOrNull("1.5", MathContext(1)).toString())
        assertNull(factory.parseBigDecimalOrNull("1.2.3"))
        assertNull(factory.parseBigDecimalOrNull("abc"))
    }

    @Test
    fun bigRationals() {
        assertEquals("3", factory.bigRationalOf(3).toRationalString())
        assertEquals("9223372036854775807", factory.bigRationalOf(Long.MAX_VALUE).toRationalString())
        assertEquals("-12", factory.bigRationalOf(BigInteger.of(-12)).toRationalString())
        assertEquals("125/100", factory.bigRationalOf(BigDecimal.of("1.25")).toRationalString())
        assertEquals("5/4", factory.bigRationalOf(BigDecimal.of("1.25")).reduced())
        assertEquals("1200", factory.bigRationalOf(BigDecimal.of("1.2E+3")).toRationalString())
        assertEquals("1/10", factory.bigRationalOf(0.1).reduced())
        assertEquals("-3/4", factory.bigRationalOf(-0.75).reduced())
        assertFailsWith<NumberFormatException> { factory.bigRationalOf(Double.NaN) }
        assertEquals("1/3", factory.bigRationalOf(1, 3).toRationalString())
        assertEquals("-2/4", factory.bigRationalOf(2, -4).toRationalString())
        assertFailsWith<ArithmeticException> { factory.bigRationalOf(1, 0) }
        assertEquals("6/4", factory.bigRationalOf(BigInteger.of(6), BigInteger.of(4)).toRationalString())
        assertFailsWith<ArithmeticException> { factory.bigRationalOf(BigInteger.ONE, BigInteger.ZERO) }
        assertEquals("3", factory.bigRationalOf(BigDecimal.of("1.5"), BigDecimal.of("0.5")).reduced())
        assertFailsWith<ArithmeticException> { factory.bigRationalOf(BigDecimal.ONE, BigDecimal.ZERO) }
    }

    @Test
    fun parsingBigRationals() {
        assertEquals("1/3", factory.parseBigRational("1/3").toRationalString())
        assertEquals("-3/5", factory.parseBigRational("-1.5/2.5").reduced())
        assertEquals("1/8", factory.parseBigRational("1/2/4").reduced())
        assertEquals("1/20", factory.parseBigRational("5E-2").reduced())
        assertFailsWith<NumberFormatException> { factory.parseBigRational("abc") }
        assertFailsWith<ArithmeticException> { factory.parseBigRational("1/0") }
        assertFailsWith<NumberFormatException> { factory.parseBigRational("/") }
        assertEquals("1/3", factory.parseBigRationalOrNull("1/3")?.toRationalString())
        assertNull(factory.parseBigRationalOrNull("abc"))
        assertNull(factory.parseBigRationalOrNull("1/0"))
        assertNull(factory.parseBigRationalOrNull("/"))
        assertNull(factory.parseBigRationalOrNull(""))
    }

    @Test
    fun conversionsOfBigIntegers() {
        val x = BigInteger.of(12345)
        assertEquals("12345", x.toBigDecimal().toString())
        assertEquals(0, x.toBigDecimal().scale)
        assertEquals("123.45", x.toBigDecimal(2).toString())
        assertEquals("1.2345E+7", x.toBigDecimal(-3).toString())
        assertEquals("12345", x.toBigRational().toRationalString())
        assertEquals(x, x.toBigDecimal().toBigInteger())
        assertEquals(x, x.toBigRational().toBigInteger())
    }

    @Test
    fun conversionsOfBigDecimals() {
        val x = BigDecimal.of("-1.25")
        assertEquals("-5/4", x.toBigRational().reduced())
        assertEquals("-1", x.toBigInteger().toString())
        assertEquals(0, x.toBigRational().toBigDecimal().compareTo(x))
        assertEquals("1000", BigDecimal.of("1E+3").toBigRational().toRationalString())
    }

    @Test
    fun conversionsOfBigRationals() {
        assertEquals("3", BigRational.valueOf(7, 2).toBigInteger().toString())
        assertEquals("-3", BigRational.valueOf(-7, 2).toBigInteger().toString())
        assertEquals("0", BigRational.valueOf(1, 3).toBigInteger().toString())
        assertEquals("0.25", BigRational.valueOf(1, 4).toBigDecimal().toString())
        assertEquals("0.333", BigRational.valueOf(1, 3).toBigDecimal(MathContext(3)).toString())
        assertEquals("3.5", BigRational.valueOf(7, 2).toPlainString())
    }

    @Test
    fun conversionsOfKotlinNumbersAndStrings() {
        assertEquals("42", 42.toBigInteger().toString())
        assertEquals("-9223372036854775808", Long.MIN_VALUE.toBigInteger().toString())
        assertEquals("255", "ff".toBigInteger(16).toString())
        assertEquals("123", "123".toBigInteger().toString())
        assertFailsWith<NumberFormatException> { "1x".toBigInteger() }
        assertEquals("7", "7".toBigIntegerOrNull().toString())
        assertNull("1x".toBigIntegerOrNull())
        assertNull("2".toBigIntegerOrNull(2))

        assertEquals("5", 5.toBigDecimal().toString())
        assertEquals("2E+1", 15.toBigDecimal(MathContext(1)).toString())
        assertEquals("123456789012", 123456789012L.toBigDecimal().toString())
        assertEquals("1.2E+11", 123456789012L.toBigDecimal(MathContext(2)).toString())
        assertEquals("0.1", 0.1.toBigDecimal().toString())
        assertEquals("1.50", "1.50".toBigDecimal().toString())
        assertEquals("2", "1.5".toBigDecimal(MathContext(1)).toString())
        assertFailsWith<NumberFormatException> { "1..5".toBigDecimal() }
        assertEquals("1.5", "1.5".toBigDecimalOrNull().toString())
        assertNull("1..5".toBigDecimalOrNull())

        assertEquals("3", 3.toBigRational().toRationalString())
        assertEquals("9223372036854775807", Long.MAX_VALUE.toBigRational().toRationalString())
        assertEquals("1/4", 0.25.toBigRational().reduced())
        assertEquals("1/3", "2/6".toBigRational().reduced())
        assertFailsWith<NumberFormatException> { "two".toBigRational() }
        assertEquals("1/3", "1/3".toBigRationalOrNull()?.toRationalString())
        assertNull("two".toBigRationalOrNull())
    }

    @Test
    fun compareIsCompareTo() {
        assertEquals(0, BigDecimal.of("1.0").compare(BigDecimal.of("1.00")))
        assertTrue(BigInteger.ONE.compare(BigInteger.TEN) < 0)
        assertTrue(BigRational.valueOf(1, 2).compare(BigRational.valueOf(1, 3)) > 0)
    }
}
