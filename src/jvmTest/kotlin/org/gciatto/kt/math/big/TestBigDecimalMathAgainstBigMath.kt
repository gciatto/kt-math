package org.gciatto.kt.math.big

import ch.obermuhlner.math.big.stream.BigDecimalStream
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.toJava
import kotlin.test.Test
import ch.obermuhlner.math.big.BigDecimalMath as JavaBigDecimalMath
import java.math.BigDecimal as JavaBigDecimal
import java.math.MathContext as JavaMathContext

/** big-math's `BigDecimalMath` and `BigDecimalStream` (see [TestAgainstBigMath]). */
class TestBigDecimalMathAgainstBigMath : TestAgainstBigMath() {
    private fun fn(
        java: (JavaBigDecimal, JavaMathContext) -> Any?,
        kotlin: (BigDecimal, MathContext) -> Any?,
    ) = java to kotlin

    private val unary: Map<String, Pair<(JavaBigDecimal, JavaMathContext) -> Any?, (BigDecimal, MathContext) -> Any?>> =
        mapOf(
            "sqrt" to fn(JavaBigDecimalMath::sqrt, ::sqrt),
            "log" to fn(JavaBigDecimalMath::log, BigDecimal::log),
            "log2" to fn(JavaBigDecimalMath::log2, BigDecimal::log2),
            "log10" to fn(JavaBigDecimalMath::log10, BigDecimal::log10),
            "exp" to fn(JavaBigDecimalMath::exp, BigDecimal::exp),
            "sin" to fn(JavaBigDecimalMath::sin, BigDecimal::sin),
            "cos" to fn(JavaBigDecimalMath::cos, BigDecimal::cos),
            "tan" to fn(JavaBigDecimalMath::tan, BigDecimal::tan),
            "asin" to fn(JavaBigDecimalMath::asin, BigDecimal::asin),
            "acos" to fn(JavaBigDecimalMath::acos, BigDecimal::acos),
            "atan" to fn(JavaBigDecimalMath::atan, BigDecimal::atan),
            "cot" to fn(JavaBigDecimalMath::cot, BigDecimal::cot),
            "acot" to fn(JavaBigDecimalMath::acot, BigDecimal::acot),
            "sinh" to fn(JavaBigDecimalMath::sinh, BigDecimal::sinh),
            "cosh" to fn(JavaBigDecimalMath::cosh, BigDecimal::cosh),
            "tanh" to fn(JavaBigDecimalMath::tanh, BigDecimal::tanh),
            "coth" to fn(JavaBigDecimalMath::coth, BigDecimal::coth),
            "asinh" to fn(JavaBigDecimalMath::asinh, BigDecimal::asinh),
            "acosh" to fn(JavaBigDecimalMath::acosh, BigDecimal::acosh),
            "atanh" to fn(JavaBigDecimalMath::atanh, BigDecimal::atanh),
            "acoth" to fn(JavaBigDecimalMath::acoth, BigDecimal::acoth),
            "toDegrees" to fn(JavaBigDecimalMath::toDegrees, BigDecimal::toDegrees),
            "toRadians" to fn(JavaBigDecimalMath::toRadians, BigDecimal::toRadians),
            "reciprocal" to fn(JavaBigDecimalMath::reciprocal, BigDecimal::reciprocal),
            "round" to fn(JavaBigDecimalMath::round, ::round),
            "roundWithTrailingZeroes" to
                fn(JavaBigDecimalMath::roundWithTrailingZeroes, BigDecimal::roundWithTrailingZeroes),
            "factorial" to
                fn({ x, mc -> JavaBigDecimalMath.factorial(x, mc) }, { x, mc -> x.factorial(mc) }),
            "gamma" to fn(JavaBigDecimalMath::gamma, BigDecimal::gamma),
        )

    // Huge arguments make these functions run (correctly, but) for too long: keep them to moderate values
    private val moderate = setOf("factorial", "gamma", "sinh", "cosh", "tanh", "coth", "sin", "cos", "tan", "cot")

    private fun isModerate(s: String) = JavaBigDecimal(s).abs() <= JavaBigDecimal(100)

    @Test
    fun unaryFunctions() {
        for ((name, functions) in unary) {
            val (java, kotlin) = functions
            for (x in values) {
                if (name in moderate && !isModerate(x)) continue
                for (mc in contexts) {
                    same("$name($x, $mc)", { java(jdec(x), mc.toJava()) }, { kotlin(dec(x), mc) })
                }
            }
        }
        for (x in values) {
            same("unlimited exp($x)", { JavaBigDecimalMath.exp(jdec(x), JavaMathContext.UNLIMITED) }) {
                dec(x).exp(MathContext.UNLIMITED)
            }
        }
        assertNoFailures()
    }

    @Test
    fun binaryFunctions() {
        // Curated: e.g. root(x, 1E-10) or pow(1E+20, 7.5) make both libraries compute huge powers for minutes
        val operands = listOf("0", "1", "-1", "2", "0.5", "-0.5", "10", "0.1", "3.14159", "-2.5", "7", "1.5", "1E-10")
        val exponents = listOf("0", "1", "-1", "2", "3", "0.5", "-0.5", "0.1", "-2.5", "1.5", "10", "1E+10")
        for (x in operands + "1E+10") {
            for (y in exponents) {
                for (mc in contexts.take(3)) {
                    same("pow($x, $y, $mc)", { JavaBigDecimalMath.pow(jdec(x), jdec(y), mc.toJava()) }) {
                        dec(x).pow(dec(y), mc)
                    }
                    same("root($x, $y, $mc)", { JavaBigDecimalMath.root(jdec(x), jdec(y), mc.toJava()) }) {
                        dec(x).root(dec(y), mc)
                    }
                    same("atan2($x, $y, $mc)", { JavaBigDecimalMath.atan2(jdec(x), jdec(y), mc.toJava()) }) {
                        atan2(dec(x), dec(y), mc)
                    }
                }
            }
            for (n in listOf(-3L, -1L, 0L, 1L, 2L, 7L, 100L, 12_345L)) {
                for (mc in (contexts + MathContext.UNLIMITED).filter { it.precision > 0 || n <= 100 }) {
                    same("pow($x, $n: Long, $mc)", { JavaBigDecimalMath.pow(jdec(x), n, mc.toJava()) }) {
                        dec(x).pow(n, mc)
                    }
                }
            }
        }
        assertNoFailures()
    }

    @Test
    fun constantsAndIntegerFunctions() {
        for (precision in listOf(5, 30, 10, 100, 60, 300)) {
            val mc = MathContext(precision)
            same("pi($precision)", { JavaBigDecimalMath.pi(mc.toJava()) }) { pi(mc) }
            same("e($precision)", { JavaBigDecimalMath.e(mc.toJava()) }) { e(mc) }
        }
        same(
            "pi(unlimited)",
            { JavaBigDecimalMath.pi(JavaMathContext.UNLIMITED) },
        ) { pi(MathContext.UNLIMITED) }
        for (n in listOf(-1, 0, 1, 2, 5, 20, 99, 100, 101, 250, 1_000)) {
            same("factorial($n: Int)", { JavaBigDecimalMath.factorial(n) }) { factorial(n) }
        }
        for (n in listOf(-1, 0, 1, 2, 3, 4, 10, 20, 31, 40)) {
            for (mc in contexts) {
                same(
                    "bernoulli($n, $mc)",
                    { JavaBigDecimalMath.bernoulli(n, mc.toJava()) },
                ) { bernoulli(n, mc) }
            }
        }
        assertNoFailures()
    }

    // isIntValue and isLongValue limits
    private val limits =
        listOf(
            "2147483647",
            "2147483648",
            "-2147483648",
            "-2147483649",
            "2147483647.000",
            "2147483647.5",
            "9223372036854775807",
            "9223372036854775808",
            "-9223372036854775808",
            "-9223372036854775809",
            "-9223372036854775808.0",
            "1E+9",
            "1E+18",
            "1E+19",
            "0E+5",
            "0E-5",
            "1.5E+3",
            "1E-5",
            "1E+1000000",
        )

    @Test
    fun helpers() {
        for (x in values + limits) {
            same("isIntValue($x)", { JavaBigDecimalMath.isIntValue(jdec(x)) }) { dec(x).isIntValue() }
            same("isLongValue($x)", { JavaBigDecimalMath.isLongValue(jdec(x)) }) { dec(x).isLongValue() }
            same(
                "isDoubleValue($x)",
                { JavaBigDecimalMath.isDoubleValue(jdec(x)) },
            ) { dec(x).isDoubleValue() }
            same("mantissa($x)", { JavaBigDecimalMath.mantissa(jdec(x)) }) { dec(x).mantissa() }
            same("exponent($x)", { JavaBigDecimalMath.exponent(jdec(x)) }) { dec(x).exponent() }
            same("significantDigits($x)", { JavaBigDecimalMath.significantDigits(jdec(x)) }) {
                dec(x).significantDigits()
            }
            same(
                "integralPart($x)",
                { JavaBigDecimalMath.integralPart(jdec(x)) },
            ) { dec(x).integralPart() }
            same("fractionalPart($x)", { JavaBigDecimalMath.fractionalPart(jdec(x)) }) {
                dec(x).fractionalPart()
            }
        }
        val digits = (1..3_000).joinToString("") { ((it * 7) % 10).toString() }
        val texts =
            values + "1e5" + "abc" + "" +
                listOf(
                    digits,
                    "-$digits",
                    "+$digits.$digits",
                    "$digits.${digits}E-1234",
                    "-0.$digits",
                    "$digits.$digits.1",
                ) +
                listOf("$digits+1", "${digits}E+12E3", "$digits${"0".repeat(10_000)}", "--$digits", "${digits}e+-5")
        for (s in texts) {
            same(
                "toBigDecimal(${s.take(40)})",
                { JavaBigDecimalMath.toBigDecimal(s) },
            ) { toBigDecimal(s) }
            for (mc in contexts) {
                same("toBigDecimal(${s.take(40)}, $mc)", { JavaBigDecimalMath.toBigDecimal(s, mc.toJava()) }) {
                    toBigDecimal(s, mc)
                }
            }
        }
        assertNoFailures()
    }

    @Test
    fun streams() {
        val bounds = listOf("0", "1", "-1", "2.5", "10", "-3.75", "0.1")
        val steps = listOf("1", "-1", "0.5", "-0.25", "0.3", "3", "0")
        for (start in bounds) {
            for (end in bounds) {
                steps.forEach { step -> streams(start, end, step) }
            }
        }
        assertNoFailures()
    }

    private fun streams(
        start: String,
        end: String,
        step: String,
    ) {
        for (mc in contexts.take(2)) {
            val what = "$start, $end, $step, $mc"
            same("range($what)", {
                BigDecimalStream.range(jdec(start), jdec(end), jdec(step), mc.toJava())
            }) { range(dec(start), dec(end), dec(step), mc) }
            same("rangeClosed($what)", {
                BigDecimalStream.rangeClosed(jdec(start), jdec(end), jdec(step), mc.toJava())
            }) { rangeClosed(dec(start), dec(end), dec(step), mc) }
        }
        if (step == "0") return
        same("$start..$end step $step", {
            BigDecimalStream.rangeClosed(jdec(start), jdec(end), jdec(step), JavaMathContext.DECIMAL128)
        }) { dec(start)..dec(end) step dec(step) }
        same("$start..<$end step $step", {
            BigDecimalStream.range(jdec(start), jdec(end), jdec(step), JavaMathContext.DECIMAL128)
        }) { dec(start)..<dec(end) step dec(step) }
    }
}
