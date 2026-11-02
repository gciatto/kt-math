package org.gciatto.kt.math

import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.Test
import kotlin.test.fail
import java.math.BigDecimal as JavaBigDecimal
import java.math.BigInteger as JavaBigInteger

/**
 * Differential tests: on the JVM, [BigInteger.of]/[BigDecimal.of] delegate to `java.math`, so the common
 * (pure-Kotlin) implementation is only exercised here, by running the same operation through the shared
 * interfaces on both [CommonBigInteger]/[CommonBigDecimal] and the `java.math` adapters, then comparing
 * results (as strings) and thrown exception types.
 */
class TestAgainstJavaMath {
    private val rnd = Random(42)
    private val failures = mutableListOf<Pair<String, String>>()

    private fun outcome(f: () -> Any?): String =
        try {
            render(f())
        } catch (e: RuntimeException) {
            "throws ${e::class.simpleName}"
        }

    private fun render(x: Any?): String =
        when (x) {
            is Array<*> -> x.joinToString(prefix = "[", postfix = "]") { render(it) }
            is Pair<*, *> -> "(${render(x.first)}, ${render(x.second)})"
            is ByteArray -> x.contentToString()
            is BigDecimal -> "$x@${x.scale}"
            else -> x.toString()
        }

    private fun same(
        what: String,
        common: () -> Any?,
        java: () -> Any?,
    ) {
        val expected = outcome(java)
        val actual = outcome(common)
        if (expected != actual) failures += what.substringBefore('(') to "$what: expected <$expected>, got <$actual>"
    }

    private fun assertNoFailures() {
        if (failures.isEmpty()) return
        // One example per operation, so a single run shows every broken operation.
        val report =
            failures.groupBy({ it.first }, { it.second }).entries.joinToString("\n") { (op, cases) ->
                "[${cases.size}x $op] ${cases.first().take(MAX_EXAMPLE_LENGTH)}"
            }
        fail("${failures.size} mismatches against java.math:\n$report")
    }

    // --- BigInteger ---------------------------------------------------------------------------------------------

    private fun commonInt(s: String): BigInteger = CommonBigInteger.of(s)

    private fun javaInt(s: String): BigInteger = JavaBigIntegerAdapter(JavaBigInteger(s))

    private fun randomInt(bits: Int): String =
        JavaBigInteger(bits, rnd.asJavaRandom()).let { if (rnd.nextBoolean()) it.negate() else it }.toString()

    private val smallInts =
        listOf(0L, 1, -1, 2, -2, 3, 7, -10, 255, 256, -256, 65_535, Int.MAX_VALUE.toLong(), Int.MIN_VALUE.toLong())
            .flatMap { listOf(it, it + 1, it - 1) }
            .plus(listOf(Long.MAX_VALUE, Long.MIN_VALUE, 1L shl 32, 1L shl 62, -(1L shl 32), 1_000_000_007))
            .map { it.toString() }
            .distinct()

    // Bit lengths straddle the thresholds the JDK algorithms switch at (Karatsuba/Toom-Cook multiplication and
    // squaring, Burnikel-Ziegler division, Schoenhage toString), so every code path of the port gets exercised.
    private val bigInts =
        listOf(64, 65, 100, 640, 1_000, 2_600, 4_000, 7_000, 8_000, 10_000).map { randomInt(it) } +
            listOf("1" + "0".repeat(100), "-" + "9".repeat(300), JavaBigInteger.TWO.pow(4_096).toString())

    private val allInts = smallInts + bigInts

    private fun intBinary(
        name: String,
        op: (BigInteger, BigInteger) -> Any?,
    ) {
        for (a in allInts) {
            for (b in allInts) {
                same("$name($a, $b)", { op(commonInt(a), commonInt(b)) }, { op(javaInt(a), javaInt(b)) })
            }
        }
    }

    private fun intUnary(
        name: String,
        values: List<String> = allInts,
        op: (BigInteger) -> Any?,
    ) {
        for (a in values) same("$name($a)", { op(commonInt(a)) }, { op(javaInt(a)) })
    }

    @Test
    fun integerArithmetic() {
        intBinary("plus") { a, b -> a + b }
        intBinary("minus") { a, b -> a - b }
        intBinary("times") { a, b -> a * b }
        intBinary("div") { a, b -> a / b }
        intBinary("rem") { a, b -> a % b }
        intBinary("remainder") { a, b -> a.remainder(b) }
        intBinary("divideAndRemainder") { a, b -> a.divideAndRemainder(b) }
        intBinary("gcd") { a, b -> a.gcd(b) }
        intBinary("compareTo") { a, b -> a.compareTo(b) }
        intBinary("equals") { a, b -> a == b }
        intBinary("min") { a, b -> a.min(b) }
        intBinary("max") { a, b -> a.max(b) }
        intUnary("square") { it * it }
        intUnary("unaryMinus") { -it }
        intUnary("unaryPlus") { +it }
        intUnary("absoluteValue") { it.absoluteValue }
        intUnary("signum") { it.signum }
        intUnary("sqrt") { it.sqrt() }
        intUnary("sqrtAndRemainder") { it.sqrtAndRemainder() }
        for (n in listOf(-1, 0, 1, 2, 3, 5, 17, 40)) intUnary("pow $n", smallInts) { it pow n }
        intUnary("pow 3", bigInts) { it pow 3 }
        assertNoFailures()
    }

    @Test
    fun integerModularArithmetic() {
        val moduli = listOf("1", "2", "7", "97", "-5", "0", "4294967296", "1000000007", "65537") + bigInts.take(5)
        val exponents = listOf("0", "1", "2", "65537", "-3") + bigInts.take(2)
        for (a in allInts.take(30) + bigInts.take(4)) {
            for (m in moduli) {
                same(
                    "modInverse($a, $m)",
                    { commonInt(a).modInverse(commonInt(m)) },
                    { javaInt(a).modInverse(javaInt(m)) },
                )
                for (e in exponents) {
                    same(
                        "modPow($a, $e, $m)",
                        { commonInt(a).modPow(commonInt(e), commonInt(m)) },
                        { javaInt(a).modPow(javaInt(e), javaInt(m)) },
                    )
                }
            }
        }
        assertNoFailures()
    }

    @Test
    fun integerBitOperations() {
        intBinary("and") { a, b -> a.and(b) }
        intBinary("or") { a, b -> a.or(b) }
        intBinary("xor") { a, b -> a.xor(b) }
        intBinary("andNot") { a, b -> a.andNot(b) }
        intUnary("not") { !it }
        intUnary("bitLength") { it.bitLength }
        intUnary("bitCount") { it.bitCount }
        for (n in listOf(-1, 0, 1, 5, 31, 32, 33, 63, 64, 65, 100, 1_000)) {
            intUnary("shl $n") { it shl n }
            intUnary("shr $n") { it shr n }
            intUnary("testBit $n") { it.testBit(n) }
            intUnary("setBit $n") { it.setBit(n) }
            intUnary("clearBit $n") { it.clearBit(n) }
            intUnary("flipBit $n") { it.flipBit(n) }
        }
        assertNoFailures()
    }

    @Test
    fun integerConversions() {
        for (radix in listOf(2, 3, 8, 10, 16, 36, 1, 37)) intUnary("toString $radix") { it.toString(radix) }
        intUnary("toString") { it.toString() }
        intUnary("toByteArray") { it.toByteArray() }
        intUnary("toInt") { it.toInt() }
        intUnary("toLong") { it.toLong() }
        intUnary("toShort") { it.toShort() }
        intUnary("toByte") { it.toByte() }
        intUnary("toChar") { it.toChar() }
        intUnary("toFloat") { it.toFloat() }
        intUnary("toDouble") { it.toDouble() }
        intUnary("toLongExact") { it.toLongExact() }
        intUnary("toIntExact") { it.toIntExact() }
        intUnary("toShortExact") { it.toShortExact() }
        intUnary("toByteExact") { it.toByteExact() }
        for (a in allInts) {
            val bytes = JavaBigInteger(a).toByteArray()
            same("of(bytes of $a)", { CommonBigInteger(bytes) }, { JavaBigIntegerAdapter(JavaBigInteger(bytes)) })
        }
        val texts =
            listOf(
                "0",
                "-0",
                "+12",
                "007",
                "ff",
                "-zz",
                "1_000",
                "",
                "-",
                "12a",
                " 1",
                "123456789012345678901234567890",
            )
        for (s in texts) {
            for (radix in listOf(10, 16, 36, 2)) {
                same(
                    "of(\"$s\", $radix)",
                    { CommonBigInteger.of(s, radix) },
                    { JavaBigIntegerAdapter(JavaBigInteger(s, radix)) },
                )
            }
        }
        assertNoFailures()
    }

    @Test
    fun integerPrimality() {
        val candidates =
            (0..200).map { it.toString() } + listOf("1000000007", "561", "2147483647", "4294967297", "-7") +
                (1..10).map { JavaBigInteger(80 + it * 30, rnd.asJavaRandom()).toString() }
        intUnary("isProbablePrime", candidates) { it.isProbablePrime(100) }
        intUnary("nextProbablePrime", candidates) { it.nextProbablePrime() }
        for (bits in listOf(2, 3, 16, 64, 95, 96, 256, 1_024)) {
            val p = CommonBigInteger.probablePrime(bits, rnd)
            same("probablePrime($bits) is prime", { p.bitLength to p.isProbablePrime(100) }, { bits to true })
            same("probablePrime($bits) (java)", { JavaBigInteger(p.toString()).isProbablePrime(100) }, { true })
        }
        assertNoFailures()
    }

    // --- BigDecimal ---------------------------------------------------------------------------------------------

    private fun commonDec(s: String): BigDecimal = CommonBigDecimal.of(s)

    private fun javaDec(s: String): BigDecimal = JavaBigDecimalAdapter(JavaBigDecimal(s))

    private val decimals =
        listOf(
            "0",
            "0.00",
            "1",
            "-1",
            "2",
            "10",
            "0.1",
            "-0.5",
            "1.5",
            "2.5",
            "-2.5",
            "123.456",
            "1E+10",
            "-1.5E-7",
            "1000",
            "1.000",
            "0.333",
            "9.999",
            "99999999999",
            "9223372036854775807",
            "-9223372036854775808",
            "3.14159265358979323846264338327950288419716939937510",
            "1E-40",
            "123456789.987654321",
        ) +
            (1..8).map {
                JavaBigDecimal(JavaBigInteger(rnd.nextInt(1, 300), rnd.asJavaRandom()), rnd.nextInt(-20, 60)).toString()
            }

    private val contexts =
        RoundingMode.entries.map { MathContext(5, it) } +
            listOf(
                MathContext.DECIMAL32,
                MathContext.DECIMAL64,
                MathContext.DECIMAL128,
                MathContext.UNLIMITED,
                MathContext(1),
            )

    private fun decBinary(
        name: String,
        op: (BigDecimal, BigDecimal) -> Any?,
    ) {
        for (a in decimals) {
            for (b in decimals) {
                same("$name($a, $b)", { op(commonDec(a), commonDec(b)) }, { op(javaDec(a), javaDec(b)) })
            }
        }
    }

    private fun decUnary(
        name: String,
        op: (BigDecimal) -> Any?,
    ) {
        for (a in decimals) same("$name($a)", { op(commonDec(a)) }, { op(javaDec(a)) })
    }

    @Test
    fun decimalArithmetic() {
        decBinary("plus") { a, b -> a + b }
        decBinary("minus") { a, b -> a - b }
        decBinary("times") { a, b -> a * b }
        decBinary("div") { a, b -> a / b }
        decBinary("rem") { a, b -> a % b }
        decBinary("divideToIntegralValue") { a, b -> a.divideToIntegralValue(b) }
        decBinary("divideAndRemainder") { a, b -> a.divideAndRemainder(b) }
        decBinary("compareTo") { a, b -> a.compareTo(b) }
        decBinary("equals") { a, b -> a == b }
        decBinary("min") { a, b -> a.min(b) }
        decBinary("max") { a, b -> a.max(b) }
        for (mc in contexts) {
            decBinary("plus $mc") { a, b -> a.plus(b, mc) }
            decBinary("minus $mc") { a, b -> a.minus(b, mc) }
            decBinary("times $mc") { a, b -> a.times(b, mc) }
            decBinary("div $mc") { a, b -> a.div(b, mc) }
            decBinary("rem $mc") { a, b -> a.rem(b, mc) }
            decBinary("divideToIntegralValue $mc") { a, b -> a.divideToIntegralValue(b, mc) }
            decUnary("round $mc") { it.round(mc) }
            decUnary("absoluteValue $mc") { it.absoluteValue(mc) }
            decUnary("unaryMinus $mc") { it.unaryMinus(mc) }
            decUnary("unaryPlus $mc") { it.unaryPlus(mc) }
            decUnary("sqrt $mc") { it.absoluteValue.sqrt(mc) }
            for (n in listOf(-2, 0, 1, 3, 10)) decUnary("pow $n $mc") { it.pow(n, mc) }
        }
        for (n in listOf(-1, 0, 1, 2, 7)) decUnary("pow $n") { it pow n }
        decUnary("unaryMinus") { -it }
        decUnary("absoluteValue") { it.absoluteValue }
        assertNoFailures()
    }

    @Test
    fun decimalScaleAndRepresentation() {
        for (newScale in listOf(-3, 0, 1, 2, 5, 30)) {
            for (rm in RoundingMode.entries) decUnary("setScale $newScale $rm") { it.setScale(newScale, rm) }
            decUnary("setScale $newScale") { it.setScale(newScale) }
        }
        for (n in listOf(-5, 0, 1, 3, 50)) {
            decUnary("movePointLeft $n") { it.movePointLeft(n) }
            decUnary("movePointRight $n") { it.movePointRight(n) }
            decUnary("scaleByPowerOfTen $n") { it.scaleByPowerOfTen(n) }
        }
        decUnary("signum") { it.signum }
        decUnary("scale") { it.scale }
        decUnary("precision") { it.precision }
        decUnary("unscaledValue") { it.unscaledValue }
        decUnary("stripTrailingZeros") { it.stripTrailingZeros() }
        decUnary("ulp") { it.ulp() }
        decUnary("toString") { it.toString() }
        decUnary("toEngineeringString") { it.toEngineeringString() }
        decUnary("toPlainString") { it.toPlainString() }
        decUnary("toBigInteger") { it.toBigInteger() }
        decUnary("toBigIntegerExact") { it.toBigIntegerExact() }
        decUnary("toLong") { it.toLong() }
        decUnary("toInt") { it.toInt() }
        decUnary("toShort") { it.toShort() }
        decUnary("toByte") { it.toByte() }
        decUnary("toLongExact") { it.toLongExact() }
        decUnary("toIntExact") { it.toIntExact() }
        decUnary("toShortExact") { it.toShortExact() }
        decUnary("toByteExact") { it.toByteExact() }
        decUnary("toDouble") { it.toDouble() }
        decUnary("toFloat") { it.toFloat() }
        assertNoFailures()
    }

    @Test
    fun decimalConstruction() {
        val texts = listOf("1e3", "1E+3", "-0.000", "+.5", "5.", ".", "1e", "abc", "", "١٢٣", "0E-10")
        for (s in texts) same("of(\"$s\")", { CommonBigDecimal.of(s) }, { JavaBigDecimalAdapter(JavaBigDecimal(s)) })
        val doubles = listOf(0.0, -0.0, 0.1, 1.0 / 3, 1e300, -2.5e-300, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN)
        for (d in doubles) {
            same("of($d)", { CommonBigDecimal(d) }, { JavaBigDecimalAdapter(JavaBigDecimal(d)) })
            for (mc in contexts) {
                same(
                    "of($d, $mc)",
                    { CommonBigDecimal.of(d, mc) },
                    { JavaBigDecimalAdapter(JavaBigDecimal(d, mc.toJava())) },
                )
            }
        }
        for (a in allInts) {
            for (scale in listOf(-2, 0, 3)) {
                same(
                    "of($a, scale=$scale)",
                    { CommonBigDecimal(CommonBigInteger.of(a), scale) },
                    { JavaBigDecimalAdapter(JavaBigDecimal(JavaBigInteger(a), scale)) },
                )
            }
        }
        assertNoFailures()
    }

    private companion object {
        const val MAX_EXAMPLE_LENGTH = 400
    }
}
