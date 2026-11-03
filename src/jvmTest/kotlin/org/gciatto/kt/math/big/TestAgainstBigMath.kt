package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode
import org.gciatto.kt.math.toJava
import kotlin.test.Test
import kotlin.test.fail
import ch.obermuhlner.math.big.BigDecimalMath as JavaBigDecimalMath
import ch.obermuhlner.math.big.BigRational as JavaBigRational
import java.math.BigDecimal as JavaBigDecimal
import java.math.BigInteger as JavaBigInteger
import java.math.MathContext as JavaMathContext

/**
 * Differential tests: every function of the port, on kt-math's BigDecimals, against the original big-math library,
 * on java.math's. Both keep caches (pi, e, logarithms, factorial constants, series factors), filled in the same order
 * here, since every case runs on big-math first, then on the port.
 */
class TestAgainstBigMath {
    private val failures = mutableListOf<Pair<String, String>>()

    private fun outcome(f: () -> Any?): String =
        try {
            render(f())
        } catch (e: RuntimeException) {
            "throws ${e::class.simpleName}"
        }

    private fun render(x: Any?): String =
        when (x) {
            is BigDecimal -> "$x@${x.scale}"
            is JavaBigDecimal -> "$x@${x.scale()}"
            is BigRational -> x.toRationalString()
            is JavaBigRational -> x.toRationalString()
            else -> x.toString()
        }

    private fun same(
        what: String,
        java: () -> Any?,
        kotlin: () -> Any?,
    ) {
        val expected = outcome(java)
        val actual = outcome(kotlin)
        if (expected != actual) failures += what.substringBefore('(') to "$what: expected <$expected>, got <$actual>"
    }

    private fun assertNoFailures() {
        if (failures.isEmpty()) return
        val report =
            failures.groupBy({ it.first }, { it.second }).entries.joinToString("\n") { (op, cases) ->
                "[${cases.size}x $op] ${cases.first().take(MAX_EXAMPLE_LENGTH)}"
            }
        fail("${failures.size} mismatches against big-math:\n$report")
    }

    private fun dec(s: String): BigDecimal = BigDecimal.of(s)

    private fun jdec(s: String): JavaBigDecimal = JavaBigDecimal(s)

    private val values =
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

    private val contexts =
        listOf(
            MathContext(10),
            MathContext(20, RoundingMode.HALF_EVEN),
            MathContext(50),
            MathContext(7, RoundingMode.DOWN),
        )

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

    @Test
    fun helpers() {
        for (x in values) {
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
    @Suppress("LongMethod")
    fun rationals() {
        val ints = listOf(0, 1, -1, 2, 3, -7, 10, 12)
        val pairs = ints.flatMap { n -> ints.map { d -> n to d } }

        fun r(
            n: Int,
            d: Int,
        ) = BigRational.valueOf(n, d)

        fun jr(
            n: Int,
            d: Int,
        ) = JavaBigRational.valueOf(n, d)
        for ((n, d) in pairs) {
            same("valueOf($n, $d)", { jr(n, d) }) { r(n, d) }
            same("reduce($n/$d)", { jr(n, d).reduce() }) { r(n, d).reduce() }
            same("integerPart($n/$d)", { jr(n, d).integerPart() }) { r(n, d).integerPart() }
            same("fractionPart($n/$d)", { jr(n, d).fractionPart() }) { r(n, d).fractionPart() }
            same("negate($n/$d)", { jr(n, d).negate() }) { -r(n, d) }
            same("reciprocal($n/$d)", { jr(n, d).reciprocal() }) { r(n, d).reciprocal() }
            same("abs($n/$d)", { jr(n, d).abs() }) { r(n, d).absoluteValue }
            same("signum($n/$d)", { jr(n, d).signum() }) { r(n, d).signum }
            same("increment($n/$d)", { jr(n, d).increment() }) { r(n, d).increment() }
            same("decrement($n/$d)", { jr(n, d).decrement() }) { r(n, d).decrement() }
            same("isZero($n/$d)", { jr(n, d).isZero }) { r(n, d).isZero }
            same("isInteger($n/$d)", { jr(n, d).isInteger }) { r(n, d).isInteger }
            same("toString($n/$d)", { jr(n, d).toString() }) { r(n, d).toString() }
            same("toPlainString($n/$d)", { jr(n, d).toPlainString() }) { r(n, d).toPlainString() }
            same("toIntegerRationalString($n/$d)", { jr(n, d).toIntegerRationalString() }) {
                r(n, d).toIntegerRationalString()
            }
            same("toBigDecimal($n/$d)", { jr(n, d).toBigDecimal() }) { r(n, d).toBigDecimal() }
            same("toDouble($n/$d)", { jr(n, d).toDouble() }) { r(n, d).toDouble() }
            same("toFloat($n/$d)", { jr(n, d).toFloat() }) { r(n, d).toFloat() }
            same("toInt($n/$d)", { jr(n, d).toInt() }) { r(n, d).toInt() }
            same("toLong($n/$d)", { jr(n, d).toLong() }) { r(n, d).toLong() }
            same("hashCode($n/$d)", { jr(n, d).hashCode() }) { r(n, d).hashCode() }
            same("withPrecision($n/$d)", { jr(n, d).withPrecision(3) }) { r(n, d).withPrecision(3) }
            same("withScale($n/$d)", { jr(n, d).withScale(2) }) { r(n, d).withScale(2) }
            for (e in listOf(-3, -1, 0, 1, 2, 5)) same("pow($n/$d, $e)", { jr(n, d).pow(e) }) { r(n, d) pow e }
            for (mc in contexts) {
                same("toBigDecimal($n/$d, $mc)", {
                    jr(n, d).toBigDecimal(mc.toJava())
                }) { r(n, d).toBigDecimal(mc) }
            }
            for ((n2, d2) in pairs.take(20)) {
                val what = "$n/$d, $n2/$d2"
                same("add($what)", { jr(n, d).add(jr(n2, d2)) }) { r(n, d) + r(n2, d2) }
                same("subtract($what)", { jr(n, d).subtract(jr(n2, d2)) }) { r(n, d) - r(n2, d2) }
                same("multiply($what)", { jr(n, d).multiply(jr(n2, d2)) }) { r(n, d) * r(n2, d2) }
                same("divide($what)", { jr(n, d).divide(jr(n2, d2)) }) { r(n, d) / r(n2, d2) }
                same("compareTo($what)", { jr(n, d).compareTo(jr(n2, d2)) }) { r(n, d).compareTo(r(n2, d2)) }
                same("equals($what)", { jr(n, d) == jr(n2, d2) }) { r(n, d) == r(n2, d2) }
                same(
                    "min($what)",
                    { JavaBigRational.min(jr(n, d), jr(n2, d2)) },
                ) { BigRational.min(r(n, d), r(n2, d2)) }
                same(
                    "max($what)",
                    { JavaBigRational.max(jr(n, d), jr(n2, d2)) },
                ) { BigRational.max(r(n, d), r(n2, d2)) }
            }
            for (i in ints) {
                same("add($n/$d, $i: Int)", { jr(n, d).add(i) }) { r(n, d) + i }
                same("subtract($n/$d, $i: Int)", { jr(n, d).subtract(i) }) { r(n, d) - i }
                same("multiply($n/$d, $i: Int)", { jr(n, d).multiply(i) }) { r(n, d) * i }
                same("divide($n/$d, $i: Int)", { jr(n, d).divide(i) }) { r(n, d) / i }
                val bi = JavaBigInteger.valueOf(i.toLong())
                val kbi = BigInteger.of(i)
                same("add($n/$d, $i: BigInteger)", { jr(n, d).add(bi) }) { r(n, d) + kbi }
                same("multiply($n/$d, $i: BigInteger)", { jr(n, d).multiply(bi) }) { r(n, d) * kbi }
                same("divide($n/$d, $i: BigInteger)", { jr(n, d).divide(bi) }) { r(n, d) / kbi }
                same("valueOf($i, $n, $d)", { JavaBigRational.valueOf(i, n, d) }) { BigRational.valueOf(i, n, d) }
            }
        }
        // "/" left out on purpose: NumberFormatException here, ArrayIndexOutOfBoundsException in big-math
        for (s in values + listOf("1/3", "1.5/2.5", "2/0", "1/2/3", "abc", "", "4/", "-0.25")) {
            same("valueOf(\"$s\")", { JavaBigRational.valueOf(s) }) { BigRational.valueOf(s) }
            same("valueOf($s: BigDecimal)", { JavaBigRational.valueOf(jdec(s)) }) { BigRational.valueOf(dec(s)) }
        }
        for (d in listOf(
            0.0,
            1.0,
            -0.5,
            0.1,
            1e-7,
            1.5e7,
            123.456,
            Double.MAX_VALUE,
            Double.NaN,
            Double.NEGATIVE_INFINITY,
        )) {
            same("valueOf($d: Double)", { JavaBigRational.valueOf(d) }) { BigRational.valueOf(d) }
        }
        for (parts in listOf(
            listOf("3", "14", "15", "2"),
            listOf(null, "5", null, null),
            listOf("", "", "3", "-1"),
            listOf("12", null, "9", ""),
        )) {
            for (positive in listOf(true, false)) {
                same("valueOf($positive, $parts)", {
                    JavaBigRational.valueOf(positive, parts[0], parts[1], parts[2], parts[3])
                }) { BigRational.valueOf(positive, parts[0], parts[1], parts[2], parts[3]) }
            }
        }
        // big-math sums the terms of Bernoulli numbers in parallel: compare reduced rationals
        for (n in listOf(-1, 0, 1, 2, 3, 4, 10, 30)) {
            same("bernoulli($n)", { JavaBigRational.bernoulli(n).reduce() }) { BigRational.bernoulli(n).reduce() }
        }
        assertNoFailures()
    }

    private companion object {
        const val MAX_EXAMPLE_LENGTH = 600
    }
}
