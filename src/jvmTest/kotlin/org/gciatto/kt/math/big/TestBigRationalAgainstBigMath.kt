package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigInteger
import org.gciatto.kt.math.BigRational
import org.gciatto.kt.math.toJava
import kotlin.test.Test
import ch.obermuhlner.math.big.BigRational as JavaBigRational
import java.math.BigInteger as JavaBigInteger

/** big-math's `BigRational` (see [TestAgainstBigMath]). */
class TestBigRationalAgainstBigMath : TestAgainstBigMath() {
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
}
