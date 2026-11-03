package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode
import java.util.stream.Stream
import kotlin.test.fail
import ch.obermuhlner.math.big.BigComplex as JavaBigComplex
import ch.obermuhlner.math.big.BigRational as JavaBigRational
import java.math.BigDecimal as JavaBigDecimal

/**
 * Differential tests: every function of the port, on kt-math's BigDecimals, against the original big-math library,
 * on java.math's, one subclass per big-math class. Both keep caches (pi, e, logarithms, factorial constants, series
 * factors), filled in the same order here, since every case runs on big-math first, then on the port.
 */
sealed class TestAgainstBigMath {
    protected val failures = mutableListOf<Pair<String, String>>()

    protected fun outcome(f: () -> Any?): String =
        try {
            render(f())
        } catch (e: RuntimeException) {
            "throws ${e::class.simpleName}"
        }

    protected fun render(x: Any?): String =
        when (x) {
            is BigDecimal -> "$x@${x.scale}"
            is JavaBigDecimal -> "$x@${x.scale()}"
            is BigRational -> x.toRationalString()
            is JavaBigRational -> x.toRationalString()
            is BigComplex -> "(${render(x.re)}, ${render(x.im)})"
            is JavaBigComplex -> "(${render(x.re)}, ${render(x.im)})"
            is Sequence<*> -> x.toList().joinToString(prefix = "[", postfix = "]") { render(it) }
            is Stream<*> ->
                x
                    .iterator()
                    .asSequence()
                    .toList()
                    .joinToString(prefix = "[", postfix = "]") { render(it) }
            else -> x.toString()
        }

    protected fun same(
        what: String,
        java: () -> Any?,
        kotlin: () -> Any?,
    ) {
        val expected = outcome(java)
        val actual = outcome(kotlin)
        if (expected != actual) failures += what.substringBefore('(') to "$what: expected <$expected>, got <$actual>"
    }

    protected fun assertNoFailures() {
        if (failures.isEmpty()) return
        val report =
            failures.groupBy({ it.first }, { it.second }).entries.joinToString("\n") { (op, cases) ->
                "[${cases.size}x $op] ${cases.first().take(MAX_EXAMPLE_LENGTH)}"
            }
        fail("${failures.size} mismatches against big-math:\n$report")
    }

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

    protected companion object {
        const val MAX_EXAMPLE_LENGTH = 600
    }
}
