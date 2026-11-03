package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.MathContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Known values, on every platform (the JVM tests compare everything with big-math itself). The documentation website
 * quotes the sections between the `--8<--` markers (use-big-math.md).
 */
class TestKnownValues {
    private val mc = MathContext(50)

    private fun dec(s: String) = BigDecimal.of(s)

    private fun assertDigits(
        expected: String,
        actual: BigDecimal,
    ) = assertEquals(expected, actual.toString())

    @Test
    fun constants() {
        // --8<-- [start:constants]
        assertDigits("3.1415926535897932384626433832795028841971693993751", pi(mc))
        assertDigits("2.7182818284590452353602874713526624977572470937000", e(mc))
        assertDigits("3.14159", pi(MathContext(6)))
        // --8<-- [end:constants]
    }

    @Test
    fun rootsAndLogarithms() {
        // --8<-- [start:rootsAndLogarithms]
        assertDigits("1.4142135623730950488016887242096980785696718753769", dec("2").sqrt(mc))
        assertDigits("1.2599210498948731647672106072782283505702514647015", dec("2").root(dec("3"), mc))
        assertDigits("0.69314718055994530941723212145817656807550013436026", dec("2").log(mc))
        assertDigits("3", dec("1000").log10(mc))
        assertDigits("1E+1", dec("1024").log2(mc))
        assertDigits("1024", dec("2").pow(dec("10"), mc))
        assertDigits(
            "1.4142135623730950488016887242096980785696718753769",
            dec("2").pow(dec("0.5"), mc),
        )
        // --8<-- [end:rootsAndLogarithms]
    }

    @Test
    fun trigonometry() {
        // --8<-- [start:trigonometry]
        assertDigits("0.84147098480789650665250232163029899962256306079837", dec("1").sin(mc))
        assertDigits("0.54030230586813971740093660744297660373231042061792", dec("1").cos(mc))
        assertDigits("0.78539816339744830961566084581987572104929234984378", dec("1").atan(mc))
        assertDigits("1.1752011936438014568823818505956008151557179813341", dec("1").sinh(mc))
        assertFailsWith<ArithmeticException> { dec("1.5").asin(mc) }
        // --8<-- [end:trigonometry]
    }

    @Test
    fun factorialGammaBernoulli() {
        // --8<-- [start:factorialGammaBernoulli]
        assertDigits("2432902008176640000", factorial(20))
        assertDigits("0.88622692545275801364908374167057259139877472806119", dec("1.5").gamma(mc))
        assertEquals("1/6", BigRational.bernoulli(2).reduce().toRationalString())
        assertEquals("-1/30", BigRational.bernoulli(4).reduce().toRationalString())
        assertFailsWith<UnsupportedOperationException> { dec("1").exp(MathContext.UNLIMITED) }
        // --8<-- [end:factorialGammaBernoulli]
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

    @Test
    fun complexes() {
        // --8<-- [start:complexes]
        val mc = MathContext(30)
        val i = BigComplex.I
        val z = BigComplex.valueOf(3.0, 4.0)
        assertEquals(BigComplex.valueOf(-1.0), i * i)
        assertDigits("5.0", z.abs(mc))
        assertEquals("(2.20 - 0.40 i)", z.div(BigComplex.valueOf(1.0, 2.0), mc).toString())
        assertEquals("(-7.00 + 24.00 i)", z.pow(2, mc).toString())
        assertEquals("(0 + 3.14159265358979323846264338328 i)", BigComplex.valueOf(-1.0).log(mc).toString())
        assertEquals("(0E-33 + 1.17520119364380145688238185060 i)", i.sin(mc).toString())
        // the cube roots of 8: 2 (the principal one), -1 + 1.732... i, -1 - 1.732... i
        assertEquals(BigComplex.valueOf(2.0), BigComplex.valueOf(8.0).root(3, 0, mc))
        assertEquals(
            "(-1.00000000000000000000000000000 + 1.73205080756887729352744634151 i)",
            BigComplex.valueOf(8.0).root(3, 1, mc).toString(),
        )
        // --8<-- [end:complexes]
    }

    @Test
    fun complexSquareRootsOfNonPositiveReals() {
        // they throw ArithmeticException in big-math, and so do asin and acos of many reals
        val mc = MathContext(20)
        assertEquals(BigComplex.valueOf(0.0, 2.0), BigComplex.valueOf(-4.0).sqrt(mc))
        assertEquals(BigComplex.ZERO, BigComplex.ZERO.sqrt(mc))
        assertEquals(
            "(1.0471975511965977462 + 1.4787638050566930559E-25 i)",
            BigComplex.valueOf(0.5).acos(mc).toString(),
        )
        assertEquals("(1.5707963267948966192 - 1.3169578969248167086 i)", BigComplex.valueOf(2.0).asin(mc).toString())
    }

    @Test
    fun ranges() {
        // --8<-- [start:ranges]
        val quarter = dec("0.25")
        assertEquals("0, 0.25, 0.50, 0.75, 1.00", (dec("0")..dec("1") step quarter).joinToString())
        assertEquals("0, 0.25, 0.50, 0.75", (dec("0")..<dec("1") step quarter).joinToString())
        assertEquals("1, 0.75, 0.50", (dec("1")..<dec("0.25") step -quarter).toList().joinToString())
        assertEquals("1, 1.3333", rangeClosed(dec("1"), dec("1.5"), dec("0.3333"), mc).joinToString())
        // --8<-- [end:ranges]
    }
}
