package org.gciatto.kt.math

import java.io.File
import kotlin.random.Random
import kotlin.random.asJavaRandom
import java.math.BigDecimal as JavaBigDecimal
import java.math.BigInteger as JavaBigInteger

/**
 * Writes every [DifferentialOps] case, with its outcome on the JVM (where [BigInteger.of]/[BigDecimal.of] delegate to
 * `java.math`), to the file given as the first argument: one tab-separated line per case, the operation name, its
 * arguments, then the expected outcome. Run by the `generateDifferentialCases` Gradle task, read by the JS tests.
 */
fun main(args: Array<String>) {
    val cases = DifferentialCases().run { integerCases() + decimalCases() + parsingCases() }
    File(args.single()).apply { parentFile.mkdirs() }.bufferedWriter().use { out ->
        for (case in cases) {
            out.append(case.joinToString("\t")).append('\t').append(DifferentialOps.outcome(case[0], case.drop(1)))
            out.newLine()
        }
    }
    println("Wrote ${cases.size} differential cases to ${args.single()}")
}

private class DifferentialCases {
    private val rnd = Random(42)

    private val smallInts =
        listOf(0L, 1, -1, 2, 3, 7, -10, 255, 256, 65_535, Int.MAX_VALUE.toLong(), Int.MIN_VALUE.toLong())
            .flatMap { listOf(it, it + 1, it - 1) }
            .plus(listOf(Long.MAX_VALUE, Long.MIN_VALUE, 1L shl 32, -(1L shl 62), 1_000_000_007))
            .map { it.toString() }
            .distinct()

    // Bit lengths straddle the thresholds of the JDK algorithms (Karatsuba/Toom-Cook, Burnikel-Ziegler, Schoenhage).
    private val bigInts =
        listOf(64, 100, 700, 2_600, 4_200, 7_000, 8_000).map { bits ->
            JavaBigInteger(bits, rnd.asJavaRandom()).let { if (rnd.nextBoolean()) it.negate() else it }.toString()
        } + ("-" + "9".repeat(300))

    private val ints = smallInts + bigInts

    private val decimals =
        listOf("0", "0.00", "1", "-1", "2", "10", "0.1", "-0.5", "2.5", "-2.5", "123.456", "1E+10", "-1.5E-7") +
            listOf("1.000", "0.333", "9.999", "9223372036854775807", "-9223372036854775808", "18446744073709551616") +
            listOf("7E+25", "3.14159265358979323846264338327950288419716939937510", "1E-40") +
            listOf("-0.0000000000000000000098765432109876543") +
            (1..6).map {
                JavaBigDecimal(JavaBigInteger(rnd.nextInt(1, 300), rnd.asJavaRandom()), rnd.nextInt(-20, 60)).toString()
            }

    private val contexts =
        listOf("5:HALF_EVEN", "5:DOWN", "5:UP", "16:HALF_UP", "34:HALF_EVEN", "40:CEILING", "0:HALF_UP")

    private val roundingModes = RoundingMode.entries.map { it.name }

    private fun numbers(vararg xs: Int): List<String> = xs.map { it.toString() }

    /** Every combination of [ops] and [args] (one list of candidates per argument position). */
    private fun cases(
        ops: Collection<String>,
        vararg args: List<String>,
    ): List<List<String>> =
        args.fold(ops.map { listOf(it) }) { partial, candidates ->
            partial.flatMap { prefix -> candidates.map { prefix + it } }
        }

    private fun casesOf(
        op: String,
        vararg args: List<String>,
    ): List<List<String>> = cases(listOf(op), *args)

    fun integerCases(): List<List<String>> =
        with(DifferentialOps) {
            val bitIndices = numbers(-1, 0, 1, 31, 32, 33, 64, 100, 1_000)
            val moduli = listOf("1", "7", "-5", "0", "4294967296", "1000000007") + bigInts.take(3)
            cases(integerBinaryOps, ints, ints) +
                cases(integerUnaryOps - "int.nextProbablePrime", ints) +
                casesOf("int.nextProbablePrime", smallInts) +
                cases(integerWithIntOps - "int.pow" - "int.toStringRadix", ints, bitIndices) +
                casesOf("int.pow", smallInts, numbers(-1, 0, 1, 2, 3, 17, 100)) +
                casesOf("int.pow", bigInts, numbers(3)) +
                casesOf("int.toStringRadix", ints, numbers(2, 3, 10, 16, 36)) +
                casesOf("int.modPow", ints.take(20), listOf("0", "1", "65537", "-3") + bigInts.take(1), moduli)
        }

    fun decimalCases(): List<List<String>> =
        with(DifferentialOps) {
            cases(decimalBinaryOps, decimals, decimals) +
                cases(decimalBinaryWithContextOps, decimals, decimals, contexts) +
                cases(decimalUnaryOps, decimals) +
                // n = 0 is left out: JDK versions disagree on movePointLeft/Right(0) with negative scales
                cases(decimalWithIntOps, decimals, numbers(-3, 1, 2, 7, 30)) +
                cases(decimalWithContextOps, decimals, contexts) +
                casesOf("dec.pow/mc", decimals, numbers(-2, 0, 3, 10), contexts) +
                casesOf("dec.setScale/rm", decimals, numbers(-3, 0, 2, 30), roundingModes) +
                casesOf("dec.div/scale", decimals, decimals, numbers(-3, 0, 25), roundingModes)
        }

    fun parsingCases(): List<List<String>> {
        val integerTexts =
            listOf("0", "-0", "+12", "ff", "-zz", "1_000", "", "-", "12a", "123456789012345678901234567890")
        val decimalTexts =
            listOf("1e3", "-0.000", "+.5", "5.", ".", "1e", "abc", "", "1.2.3", "1e+0005", "1e99999999999")
        val doubles =
            listOf("0.0", "-0.0", "0.1", "0.3333333333333333", "1e300", "-2.5e-300", "4.9E-324", "NaN", "Infinity")
        return casesOf("int.parse", integerTexts, numbers(10, 16, 36, 2)) +
            casesOf("dec.parse", decimalTexts + decimals) +
            casesOf("dec.parse/mc", decimalTexts + decimals, contexts) +
            casesOf("dec.ofDouble", doubles)
    }
}
