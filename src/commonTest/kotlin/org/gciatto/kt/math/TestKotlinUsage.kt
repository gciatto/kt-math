package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * kt-math as Kotlin code sees it, on every platform. The documentation website quotes the sections between the
 * `--8<--` markers (documentation/docs/tutorials/getting-started-kotlin.md): keep them self-explanatory.
 */
class TestKotlinUsage {
    @Test
    fun bigIntegers() {
        // --8<-- [start:big-integers]
        val x = BigInteger.of("123456789012345678901234567890")
        val y = 42.toBigInteger()
        assertEquals("5185185138518518513851851851380", (x * y).toString())
        assertEquals("2939447357436801878600823045", (x / y).toString())
        assertEquals("7", (x % 11.toBigInteger()).toString())
        assertEquals("1000", (BigInteger.TEN pow 3).toString())
        assertEquals("ff", 255.toBigInteger().toString(16))
        assertTrue(x > y)
        assertFailsWith<ArithmeticException> { x / BigInteger.ZERO }
        // --8<-- [end:big-integers]
    }

    @Test
    fun bigDecimals() {
        // --8<-- [start:big-decimals]
        val a = "1.10".toBigDecimal()
        val b = 3.toBigDecimal()
        assertEquals("4.10", (a + b).toString())
        assertEquals("0.3666666667", a.div(b, MathContext(10, RoundingMode.HALF_EVEN)).toString())
        assertEquals("0.37", a.div(b, 2, RoundingMode.HALF_UP).toString())
        assertEquals("1.414213562", 2.toBigDecimal().sqrt(MathContext(10)).toString())
        assertEquals("0.1", 0.1.toBigDecimal().toString()) // the shortest decimal representation of the Double
        assertFailsWith<ArithmeticException> { BigDecimal.ONE / b } // 1/3 has no exact decimal representation
        // --8<-- [end:big-decimals]
    }

    @Test
    fun bigRationals() {
        // --8<-- [start:big-rationals]
        val third = "1/3".toBigRational()
        val sum = third + BigRational.valueOf(1, 6)
        assertEquals("1/2", sum.reduce().toRationalString())
        assertEquals("0.3333333333", third.toBigDecimal(MathContext(10)).toString())
        assertEquals(BigRational.ONE, (third * 3).reduce())
        // --8<-- [end:big-rationals]
    }

    @Test
    fun factoryAndConversions() {
        // --8<-- [start:factory]
        val factory: BigFactory = BigFactory.default // the most efficient implementation for the platform
        assertEquals("-255", factory.parseBigInteger("-ff", radix = 16).toString())
        assertNull(factory.parseBigDecimalOrNull("1..2"))
        val rational = factory.bigRationalOf(BigDecimal.of("1.25"))
        assertEquals("5/4", rational.reduce().toRationalString())
        assertEquals("1", rational.toBigInteger().toString())
        assertEquals("12.34", 1234.toBigInteger().toBigDecimal(scale = 2).toString())
        // --8<-- [end:factory]
    }
}
