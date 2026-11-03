package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.DifferentialTest
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode
import java.util.stream.Stream
import ch.obermuhlner.math.big.BigComplex as JavaBigComplex
import ch.obermuhlner.math.big.BigRational as JavaBigRational
import java.math.BigDecimal as JavaBigDecimal

/**
 * Differential tests: every function of the port, on kt-math's BigDecimals, against the original big-math library,
 * on java.math's, one subclass per big-math class. Both keep caches (pi, e, logarithms, factorial constants, series
 * factors), filled in the same order here, since every case runs on big-math first, then on the port.
 */
sealed class TestAgainstBigMath : DifferentialTest("big-math", maxExampleLength = 600) {
    override fun render(x: Any?): String =
        when (x) {
            is JavaBigDecimal -> "$x@${x.scale()}"
            is BigRational -> x.toRationalString()
            is JavaBigRational -> x.toRationalString()
            is BigComplex -> "(${render(x.re)}, ${render(x.im)})"
            is JavaBigComplex -> "(${render(x.re)}, ${render(x.im)})"
            is Stream<*> -> render(x.iterator().asSequence())
            else -> super.render(x)
        }

    /** Records a mismatch if [kotlin] has another outcome than [java]. */
    protected fun same(
        what: String,
        java: () -> Any?,
        kotlin: () -> Any?,
    ) = compare(what, java, kotlin)

    protected fun dec(s: String): BigDecimal = BigDecimal.of(s)

    protected fun jdec(s: String): JavaBigDecimal = JavaBigDecimal(s)

    protected val values =
        listOf(
            "0",
            "1",
            "-1",
            "2",
            "0.5",
            "-0.5",
            "10",
            "0.1",
            "0.01",
            "0.001",
            "3.14159",
            "100",
            "-2.5",
            "123.456",
            "0.999",
            "1.0001",
            "7",
            "20",
            "6.28318530718",
            "-7.5",
            "1E+10",
            "1E-10",
            "1E-50",
            "1E+100",
            "-1E+100",
            "0.707107",
            "0.70710678",
            "1.5",
            "8.6",
            "1000",
            "12345678901234567890.123456789",
        )

    protected val contexts =
        listOf(
            MathContext(10),
            MathContext(20, RoundingMode.HALF_EVEN),
            MathContext(50),
            MathContext(7, RoundingMode.DOWN),
        )
}
