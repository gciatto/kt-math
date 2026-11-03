package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.toJava
import kotlin.test.Test
import ch.obermuhlner.math.big.BigComplex as JavaBigComplex
import ch.obermuhlner.math.big.BigComplexMath as JavaBigComplexMath
import java.math.BigDecimal as JavaBigDecimal
import java.math.MathContext as JavaMathContext

/** big-math's `BigComplex` and `BigComplexMath` (functions as [BigComplex] members): see [TestAgainstBigMath]. */
class TestBigComplexAgainstBigMath : TestAgainstBigMath() {
    // Moderate values: the factorial and the exponentials of large ones take long, in both libraries
    private val complexes =
        listOf(
            "0" to "0",
            "1" to "0",
            "0" to "1",
            "-1" to "0",
            "0" to "-1",
            "1" to "1",
            "-2.5" to "0.5",
            "3.14159" to "-1",
            "0.5" to "0.50",
            "10" to "-20",
            "1E-10" to "1",
            "-0.001" to "-7",
            "123.456" to "7.0",
            "2" to "0",
        )

    private fun c(p: Pair<String, String>) = BigComplex.valueOf(dec(p.first), dec(p.second))

    private fun jc(p: Pair<String, String>) = JavaBigComplex.valueOf(jdec(p.first), jdec(p.second))

    private fun show(p: Pair<String, String>) = "${p.first}+${p.second}i"

    private val unary: Map<String, Pair<(JavaBigComplex, JavaMathContext) -> Any?, (BigComplex, MathContext) -> Any?>> =
        mapOf(
            "reciprocal" to Pair({ x, mc -> x.reciprocal(mc) }, { x, mc -> x.reciprocal(mc) }),
            "abs" to Pair({ x, mc -> x.abs(mc) }, { x, mc -> x.abs(mc) }),
            "absSquare" to Pair({ x, mc -> x.absSquare(mc) }, { x, mc -> x.absSquare(mc) }),
            "angle" to Pair({ x, mc -> x.angle(mc) }, { x, mc -> x.angle(mc) }),
            "round" to Pair({ x, mc -> x.round(mc) }, { x, mc -> x.round(mc) }),
            "factorial" to Pair(JavaBigComplexMath::factorial, BigComplex::factorial),
            "gamma" to Pair(JavaBigComplexMath::gamma, BigComplex::gamma),
            "exp" to Pair(JavaBigComplexMath::exp, BigComplex::exp),
            "sin" to Pair(JavaBigComplexMath::sin, BigComplex::sin),
            "cos" to Pair(JavaBigComplexMath::cos, BigComplex::cos),
            "tan" to Pair(JavaBigComplexMath::tan, BigComplex::tan),
            "atan" to Pair(JavaBigComplexMath::atan, BigComplex::atan),
            "acot" to Pair(JavaBigComplexMath::acot, BigComplex::acot),
            "asin" to Pair(JavaBigComplexMath::asin, BigComplex::asin),
            "acos" to Pair(JavaBigComplexMath::acos, BigComplex::acos),
            "sqrt" to Pair(JavaBigComplexMath::sqrt, BigComplex::sqrt),
            "log" to Pair(JavaBigComplexMath::log, BigComplex::log),
        )

    private val slow = setOf("factorial", "gamma")

    private val sqrtBased = setOf("sqrt", "asin", "acos")

    @Test
    fun functions() {
        for ((name, functions) in unary) {
            val mcs = if (name in slow) contexts.take(2) else contexts
            for (x in complexes) {
                mcs.forEach { mc -> function(name, functions, x, mc) }
            }
        }
        assertNoFailures()
    }

    private fun function(
        name: String,
        functions: Pair<(JavaBigComplex, JavaMathContext) -> Any?, (BigComplex, MathContext) -> Any?>,
        x: Pair<String, String>,
        mc: MathContext,
    ) {
        val (java, kotlin) = functions
        // big-math's sqrt divides 0 by 0 for 0 and negative reals, fixed here (see TestKnownValues)
        if (name in sqrtBased && outcome { java(jc(x), mc.toJava()) }.startsWith("throws")) return
        same("$name(${show(x)}, $mc)", { java(jc(x), mc.toJava()) }) { kotlin(c(x), mc) }
    }

    @Test
    fun powersAndRoots() {
        val operands = complexes.filter { (re, im) -> JavaBigDecimal(re).abs() < JavaBigDecimal(100) && im != "-20" }
        for (x in operands) {
            for (mc in contexts.take(3)) {
                val jmc = mc.toJava()
                for (n in listOf(-3L, -1L, 0L, 1L, 2L, 7L)) {
                    same(
                        "pow(${show(x)}, $n: Long, $mc)",
                        { JavaBigComplexMath.pow(jc(x), n, jmc) },
                    ) { c(x).pow(n, mc) }
                }
                for (y in listOf("0", "1", "-1", "0.5", "2", "-2.5", "3")) {
                    same("pow(${show(x)}, $y, $mc)", { JavaBigComplexMath.pow(jc(x), jdec(y), jmc) }) {
                        c(x).pow(dec(y), mc)
                    }
                    same("root(${show(x)}, $y, $mc)", { JavaBigComplexMath.root(jc(x), jdec(y), jmc) }) {
                        c(x).root(dec(y), mc)
                    }
                }
                for (y in operands.take(8)) {
                    same("pow(${show(x)}, ${show(y)}, $mc)", { JavaBigComplexMath.pow(jc(x), jc(y), jmc) }) {
                        c(x).pow(c(y), mc)
                    }
                    same("root(${show(x)}, ${show(y)}, $mc)", { JavaBigComplexMath.root(jc(x), jc(y), jmc) }) {
                        c(x).root(c(y), mc)
                    }
                }
            }
        }
        assertNoFailures()
    }

    @Test
    fun arithmetic() {
        val reals = listOf("0", "1", "-2.5", "0.1", "1E+3")
        for (x in complexes) {
            val what = show(x)
            same("conjugate($what)", { jc(x).conjugate() }) { c(x).conjugate() }
            same("negate($what)", { jc(x).negate() }) { -c(x) }
            same("isReal($what)", { jc(x).isReal }) { c(x).isReal() }
            same("re($what)", { jc(x).re() }) { c(x).re() }
            same("im($what)", { jc(x).im() }) { c(x).im() }
            same("toString($what)", { jc(x).toString() }) { c(x).toString() }
            for (y in complexes) {
                val both = "$what, ${show(y)}"
                same("add($both)", { jc(x).add(jc(y)) }) { c(x) + c(y) }
                same("subtract($both)", { jc(x).subtract(jc(y)) }) { c(x) - c(y) }
                same("multiply($both)", { jc(x).multiply(jc(y)) }) { c(x) * c(y) }
                same("equals($both)", { jc(x) == jc(y) }) { c(x) == c(y) }
                same("strictEquals($both)", { jc(x).strictEquals(jc(y)) }) { c(x).strictEquals(c(y)) }
                for (mc in contexts) {
                    val jmc = mc.toJava()
                    same("add($both, $mc)", { jc(x).add(jc(y), jmc) }) { c(x).plus(c(y), mc) }
                    same("subtract($both, $mc)", { jc(x).subtract(jc(y), jmc) }) { c(x).minus(c(y), mc) }
                    same("multiply($both, $mc)", { jc(x).multiply(jc(y), jmc) }) { c(x).times(c(y), mc) }
                    same("divide($both, $mc)", { jc(x).divide(jc(y), jmc) }) { c(x).div(c(y), mc) }
                }
            }
            for (r in reals) {
                val both = "$what, $r"
                val d = r.toDouble()
                same("add($both)", { jc(x).add(jdec(r)) }) { c(x) + dec(r) }
                same("subtract($both)", { jc(x).subtract(jdec(r)) }) { c(x) - dec(r) }
                same("multiply($both)", { jc(x).multiply(jdec(r)) }) { c(x) * dec(r) }
                same("add($both: Double)", { jc(x).add(d) }) { c(x) + d }
                same("subtract($both: Double)", { jc(x).subtract(d) }) { c(x) - d }
                same("multiply($both: Double)", { jc(x).multiply(d) }) { c(x) * d }
                for (mc in contexts) {
                    val jmc = mc.toJava()
                    same("add($both, $mc)", { jc(x).add(jdec(r), jmc) }) { c(x).plus(dec(r), mc) }
                    same("subtract($both, $mc)", { jc(x).subtract(jdec(r), jmc) }) { c(x).minus(dec(r), mc) }
                    same("multiply($both, $mc)", { jc(x).multiply(jdec(r), jmc) }) { c(x).times(dec(r), mc) }
                    same("divide($both, $mc)", { jc(x).divide(jdec(r), jmc) }) { c(x).div(dec(r), mc) }
                    same("divide($both: Double, $mc)", { jc(x).divide(d, jmc) }) { c(x).div(d, mc) }
                }
            }
        }
        assertNoFailures()
    }

    @Test
    fun factories() {
        val reals = listOf("0", "1", "-1", "0.0", "1.00", "2.5", "-3E+5")
        for (re in reals) {
            same("valueOf($re)", { JavaBigComplex.valueOf(jdec(re)) }) { BigComplex.valueOf(dec(re)) }
            same("valueOf(${re.toDouble()}: Double)", { JavaBigComplex.valueOf(re.toDouble()) }) {
                BigComplex.valueOf(re.toDouble())
            }
            for (im in reals) {
                same("valueOf($re, $im)", { JavaBigComplex.valueOf(jdec(re), jdec(im)) }) {
                    BigComplex.valueOf(dec(re), dec(im))
                }
                val (dr, di) = re.toDouble() to im.toDouble()
                same("valueOf($dr, $di: Double)", { JavaBigComplex.valueOf(dr, di) }) { BigComplex.valueOf(dr, di) }
                for (mc in contexts) {
                    same("valueOfPolar($re, $im, $mc)", {
                        JavaBigComplex.valueOfPolar(jdec(re), jdec(im), mc.toJava())
                    }) { BigComplex.valueOfPolar(dec(re), dec(im), mc) }
                    same("valueOfPolar($dr, $di: Double, $mc)", { JavaBigComplex.valueOfPolar(dr, di, mc.toJava()) }) {
                        BigComplex.valueOfPolar(dr, di, mc)
                    }
                }
            }
        }
        same("ZERO", { JavaBigComplex.ZERO }) { BigComplex.ZERO }
        same("ONE", { JavaBigComplex.ONE }) { BigComplex.ONE }
        same("I", { JavaBigComplex.I }) { BigComplex.I }
        assertNoFailures()
    }

    @Test
    fun hashCodeIsConsistentWithEquals() {
        // big-math hashes the BigDecimals, so 1.0 and 1.00 are equal with different hash codes: not here
        for (x in complexes) {
            for (y in complexes) {
                if (c(x) == c(y)) kotlin.test.assertEquals(c(x).hashCode(), c(y).hashCode(), "${show(x)}, ${show(y)}")
            }
        }
        val a = BigComplex.valueOf(BigDecimal.of("1.0"), BigDecimal.of("2"))
        val b = BigComplex.valueOf(BigDecimal.of("1.00"), BigDecimal.of("2.000"))
        kotlin.test.assertEquals(a, b)
        kotlin.test.assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun kthRoots() {
        val mc = MathContext(20)
        val tolerance = BigDecimal.of("1E-15")
        for (x in complexes.map { c(it) }) {
            for (n in 1..5) {
                for (k in -1..n) {
                    val root = x.root(n, k, mc)
                    val error = root.pow(n.toLong(), mc).minus(x, mc).abs(mc)
                    kotlin.test.assertTrue(
                        error <= tolerance * (x.abs(mc) + BigDecimal.ONE),
                        "root($x, $n, $k)^$n = $x",
                    )
                }
                if (x == BigComplex.ZERO) continue // big-math's root throws: the angle of 0 is undefined
                val principal = x.root(BigDecimal.of(n), mc)
                kotlin.test.assertTrue(x.root(n, 0, mc).minus(principal, mc).abs(mc) <= tolerance, "root($x, $n, 0)")
            }
        }
    }
}
