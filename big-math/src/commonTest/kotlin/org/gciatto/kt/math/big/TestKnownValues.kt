package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.MathContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Known values, on every platform (the JVM tests compare everything with big-math itself). */
class TestKnownValues {
    private val mc = MathContext(50)

    private fun dec(s: String) = BigDecimal.of(s)

    private fun assertDigits(
        expected: String,
        actual: BigDecimal,
    ) = assertEquals(expected, actual.toString())

    @Test
    fun constants() {
        assertDigits("3.1415926535897932384626433832795028841971693993751", BigDecimalMath.pi(mc))
        assertDigits("2.7182818284590452353602874713526624977572470937000", BigDecimalMath.e(mc))
        assertDigits("3.14159", BigDecimalMath.pi(MathContext(6)))
    }

    @Test
    fun rootsAndLogarithms() {
        assertDigits("1.4142135623730950488016887242096980785696718753769", BigDecimalMath.sqrt(dec("2"), mc))
        assertDigits("1.2599210498948731647672106072782283505702514647015", BigDecimalMath.root(dec("2"), dec("3"), mc))
        assertDigits("0.69314718055994530941723212145817656807550013436026", BigDecimalMath.log(dec("2"), mc))
        assertDigits("3", BigDecimalMath.log10(dec("1000"), mc))
        assertDigits("1E+1", BigDecimalMath.log2(dec("1024"), mc))
        assertDigits("1024", BigDecimalMath.pow(dec("2"), dec("10"), mc))
        assertDigits(
            "1.4142135623730950488016887242096980785696718753769",
            BigDecimalMath.pow(dec("2"), dec("0.5"), mc),
        )
    }

    @Test
    fun trigonometry() {
        assertDigits("0.84147098480789650665250232163029899962256306079837", BigDecimalMath.sin(dec("1"), mc))
        assertDigits("0.54030230586813971740093660744297660373231042061792", BigDecimalMath.cos(dec("1"), mc))
        assertDigits("0.78539816339744830961566084581987572104929234984378", BigDecimalMath.atan(dec("1"), mc))
        assertDigits("1.1752011936438014568823818505956008151557179813341", BigDecimalMath.sinh(dec("1"), mc))
        assertFailsWith<ArithmeticException> { BigDecimalMath.asin(dec("1.5"), mc) }
    }

    @Test
    fun factorialGammaBernoulli() {
        assertDigits("2432902008176640000", BigDecimalMath.factorial(20))
        assertDigits("0.88622692545275801364908374167057259139877472806119", BigDecimalMath.gamma(dec("1.5"), mc))
        assertEquals("1/6", BigRational.bernoulli(2).reduce().toRationalString())
        assertEquals("-1/30", BigRational.bernoulli(4).reduce().toRationalString())
        assertFailsWith<UnsupportedOperationException> { BigDecimalMath.exp(dec("1"), MathContext.UNLIMITED) }
    }

    @Test
    fun rationals() {
        val third = BigRational.valueOf(1, 3)
        assertEquals("1", (third * 3).reduce().toRationalString())
        assertEquals("5/6", (third + BigRational.valueOf(1, 2)).reduce().toRationalString())
        assertEquals("3 1/2", BigRational.valueOf(3, 1, 2).toIntegerRationalString())
        assertEquals("0.33333", third.toBigDecimal(MathContext(5)).toString())
        assertEquals(BigRational.valueOf("2/3"), BigRational.valueOf("4/6").reduce())
    }
}
