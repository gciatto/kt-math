package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The `@JsExport`ed [bigInteger]/[bigDecimal] entry points, which accept any JS value. */
class TestFactory {
    @Test
    fun bigIntegerFromJsValues() {
        assertEquals("42", bigInteger(42).toString())
        assertEquals("-7", bigInteger(js("-7")).toString())
        assertEquals("9007199254740991", bigInteger(js("Number.MAX_SAFE_INTEGER")).toString())
        assertEquals("1", bigInteger(js("1.9")).toString()) // non-integer numbers are truncated
        assertEquals("123456789012345678901234567890", bigInteger("123456789012345678901234567890").toString())
        assertEquals("31", bigInteger("0x1f").toString())
        assertEquals("255", bigInteger(arrayOf<Any>("ff", 16)).toString())
        assertEquals("-1", bigInteger(js("-1.9")).toString())
        assertFailsWith<IllegalArgumentException> { bigInteger(js("NaN")) }
        assertFailsWith<IllegalArgumentException> { bigInteger(js("Infinity")) }
        assertFailsWith<IllegalArgumentException> { bigInteger(arrayOf<Any>("ff")) }
        assertFailsWith<IllegalArgumentException> { bigInteger(js("({})")) }
        assertFailsWith<IllegalArgumentException> { bigInteger(null) }
    }

    @Test
    fun bigDecimalFromJsValues() {
        assertEquals("1.50", bigDecimal("1.50").toString())
        assertEquals("0.1", bigDecimal(js("0.1")).toString())
        assertEquals("42", bigDecimal(42).toString())
        assertEquals("1.2E+3", bigDecimal(arrayOf<Any>("1234.5", MathContext(2))).toString())
        assertFailsWith<IllegalArgumentException> { bigDecimal(arrayOf<Any>("1.5", 2)) }
        assertFailsWith<NumberFormatException> { bigDecimal("abc") }
        assertFailsWith<IllegalArgumentException> { bigDecimal(null) }
    }
}
