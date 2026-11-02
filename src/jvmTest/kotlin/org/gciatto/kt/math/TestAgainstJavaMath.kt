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
sealed class TestAgainstJavaMath {
    protected val rnd = Random(42)
    private val failures = mutableListOf<Pair<String, String>>()

    protected fun outcome(f: () -> Any?): String =
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

    protected fun same(
        what: String,
        common: () -> Any?,
        java: () -> Any?,
    ) {
        val expected = outcome(java)
        val actual = outcome(common)
        if (expected != actual) failures += what.substringBefore('(') to "$what: expected <$expected>, got <$actual>"
    }

    protected fun assertNoFailures() {
        if (failures.isEmpty()) return
        // One example per operation, so a single run shows every broken operation.
        val report =
            failures.groupBy({ it.first }, { it.second }).entries.joinToString("\n") { (op, cases) ->
                "[${cases.size}x $op] ${cases.first().take(MAX_EXAMPLE_LENGTH)}"
            }
        fail("${failures.size} mismatches against java.math:\n$report")
    }

    // --- BigInteger ---------------------------------------------------------------------------------------------

    protected fun commonInt(s: String): BigInteger = CommonBigInteger.of(s)

    protected fun javaInt(s: String): BigInteger = JavaBigIntegerAdapter(JavaBigInteger(s))

    private fun randomInt(bits: Int): String =
        JavaBigInteger(bits, rnd.asJavaRandom()).let { if (rnd.nextBoolean()) it.negate() else it }.toString()

    protected val smallInts =
        listOf(0L, 1, -1, 2, -2, 3, 7, -10, 255, 256, -256, 65_535, Int.MAX_VALUE.toLong(), Int.MIN_VALUE.toLong())
            .flatMap { listOf(it, it + 1, it - 1) }
            .plus(listOf(Long.MAX_VALUE, Long.MIN_VALUE, 1L shl 32, 1L shl 62, -(1L shl 32), 1_000_000_007))
            .map { it.toString() }
            .distinct()

    // Bit lengths straddle the thresholds the JDK algorithms switch at (Karatsuba/Toom-Cook multiplication and
    // squaring, Burnikel-Ziegler division, Schoenhage toString), so every code path of the port gets exercised.
    protected val bigInts =
        listOf(64, 65, 100, 640, 1_000, 2_600, 4_000, 7_000, 8_000, 10_000).map { randomInt(it) } +
            listOf("1" + "0".repeat(100), "-" + "9".repeat(300), JavaBigInteger.TWO.pow(4_096).toString())

    protected val allInts = smallInts + bigInts

    protected companion object {
        const val MAX_EXAMPLE_LENGTH = 400
    }
}

class TestIntegersAgainstJavaMath : TestAgainstJavaMath() {
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
        for (n in listOf(-1, 0, 1, 2, 3, 5, 17, 40, 100, 1_000)) intUnary("pow $n", smallInts) { it pow n }
        intUnary("pow 3", bigInts) { it pow 3 }
        intUnary("hashCode") { it.hashCode() }
        intUnary("equals non-BigInteger") { it.equals(it.toString()) }
        assertNoFailures()
    }

    private fun intPrimitive(
        name: String,
        op: (BigInteger, BigInteger) -> Any?,
        onInt: (BigInteger, Int) -> Any?,
        onLong: (BigInteger, Long) -> Any?,
    ) {
        val ints = listOf(0, 1, -1, 7, -10, 256, Int.MAX_VALUE, Int.MIN_VALUE)
        val longs = ints.map { it.toLong() } + listOf(Long.MAX_VALUE, Long.MIN_VALUE, 1L shl 40)
        for (a in allInts) {
            for (i in ints) same("$name($a, $i: Int)", { op(commonInt(a), commonInt("$i")) }, { onInt(javaInt(a), i) })
            for (l in longs) {
                same(
                    "$name($a, $l: Long)",
                    { op(commonInt(a), commonInt("$l")) },
                    { onLong(javaInt(a), l) },
                )
            }
        }
    }

    // The Int/Long overloads are interface defaults delegating to BigInteger.of, which builds java.math adapters on
    // the JVM: they run on the adapters only, against the BigInteger overload of the common implementation.
    @Test
    fun integerPrimitiveOverloads() {
        intPrimitive("plus", { a, b -> a + b }, { a, b -> a + b }, { a, b -> a + b })
        intPrimitive("minus", { a, b -> a - b }, { a, b -> a - b }, { a, b -> a - b })
        intPrimitive("times", { a, b -> a * b }, { a, b -> a * b }, { a, b -> a * b })
        intPrimitive("div", { a, b -> a / b }, { a, b -> a / b }, { a, b -> a / b })
        intPrimitive("rem", { a, b -> a % b }, { a, b -> a % b }, { a, b -> a % b })
        intPrimitive("remainder", { a, b -> a.remainder(b) }, { a, b -> a.remainder(b) }, { a, b -> a.remainder(b) })
        intPrimitive(
            "divideAndRemainder",
            { a, b -> a.divideAndRemainder(b) },
            { a, b -> a.divideAndRemainder(b) },
            { a, b -> a.divideAndRemainder(b) },
        )
        intPrimitive("gcd", { a, b -> a.gcd(b) }, { a, b -> a.gcd(b) }, { a, b -> a.gcd(b) })
        intPrimitive(
            "modInverse",
            { a, b -> a.modInverse(b) },
            { a, b -> a.modInverse(b) },
            { a, b -> a.modInverse(b) },
        )
        intPrimitive("and", { a, b -> a.and(b) }, { a, b -> a.and(b) }, { a, b -> a.and(b) })
        intPrimitive("or", { a, b -> a.or(b) }, { a, b -> a.or(b) }, { a, b -> a.or(b) })
        intPrimitive("xor", { a, b -> a.xor(b) }, { a, b -> a.xor(b) }, { a, b -> a.xor(b) })
        intPrimitive("andNot", { a, b -> a.andNot(b) }, { a, b -> a.andNot(b) }, { a, b -> a.andNot(b) })
        for (a in listOf(-7, -1, 0, 3)) {
            for (n in listOf(-3, 0, 5)) {
                val expected = (a..n).map { it.toString() }
                same("rangeTo($a, $n: Int)", { expected }, { (javaInt("$a")..n).map { it.toString() } })
                same("rangeTo($a, $n: Long)", { expected }, { (javaInt("$a")..n.toLong()).map { it.toString() } })
            }
        }
        assertNoFailures()
    }

    @Test
    fun integerModularArithmetic() {
        val moduli =
            listOf("1", "2", "7", "97", "-5", "0", "4294967296", "1000000007", "65537") +
                listOf(40, 64, 65, 100, 300).map { JavaBigInteger.TWO.pow(it).toString() } +
                JavaBigInteger.TWO
                    .pow(70)
                    .multiply(JavaBigInteger.valueOf(3))
                    .toString() +
                bigInts.take(5)
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
            intUnary("get $n") { it[n] }
            intUnary("set $n") { it.set(n, true) to it.set(n, false) }
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
        assertNoFailures()
    }

    @Test
    fun integerFactories() {
        for (a in allInts) {
            val bytes = JavaBigInteger(a).toByteArray()
            same("of(bytes of $a)", { CommonBigInteger(bytes) }, { JavaBigIntegerAdapter(JavaBigInteger(bytes)) })
            same("BigInteger.of(bytes of $a)", { CommonBigInteger(bytes) }, { BigInteger.of(bytes) })
            val magnitude = JavaBigInteger(a).abs().toByteArray()
            for (signum in listOf(-1, 0, 1, 2)) {
                same(
                    "BigInteger.of($signum, magnitude of $a)",
                    { CommonBigInteger(signum, magnitude) },
                    { BigInteger.of(signum, magnitude) },
                )
            }
            for ((off, len) in listOf(0 to bytes.size, 1 to bytes.size - 1, 0 to 0, -1 to 1, 0 to bytes.size + 1)) {
                for (signum in listOf(-1, 0, 1, 2)) {
                    same(
                        "of($signum, bytes of $a, $off, $len)",
                        { BigInteger.of(signum, bytes, off, len) },
                        { JavaBigIntegerAdapter(JavaBigInteger(signum, bytes, off, len)) },
                    )
                }
            }
            val ints =
                JavaBigInteger(a).toByteArray().let { b ->
                    val padded = ByteArray((b.size + 3) / 4 * 4 - b.size) { if (b[0] < 0) -1 else 0 } + b
                    IntArray(padded.size / 4) {
                        java.nio.ByteBuffer
                            .wrap(padded, it * 4, 4)
                            .int
                    }
                }
            same("of(ints of $a)", { CommonBigInteger.of(ints) }, { JavaBigIntegerAdapter(JavaBigInteger(a)) })
            same("BigInteger.of(ints of $a)", { BigInteger.of(ints) }, { JavaBigIntegerAdapter(JavaBigInteger(a)) })
            same("toJava($a)", { JavaBigIntegerAdapter(commonInt(a).toJava()) }, { javaInt(a) })
            same("BigInteger.of($a)", { commonInt(a) }, { BigInteger.of(a) })
            a.toLongOrNull()?.let { l ->
                same("BigInteger.of($l: Long)", { CommonBigInteger.of(l) }, { BigInteger.of(l) })
            }
            a.toIntOrNull()?.let { i ->
                same("BigInteger.of($i: Int)", { CommonBigInteger.of(i) }, { BigInteger.of(i) })
            }
        }
        same("BigInteger.of(empty bytes)", { CommonBigInteger(ByteArray(0)) }, { BigInteger.of(ByteArray(0)) })
        same("BigInteger.of(1, empty bytes)", { CommonBigInteger(1, ByteArray(0)) }, { BigInteger.of(1, ByteArray(0)) })
        for (s in listOf("0x1F", "-0X1f", "0b101", "0B11", "0o17", "-0O7", "0x", "0xZ")) {
            same("BigInteger.of(\"$s\")", { CommonBigInteger.of(s) }, { BigInteger.of(s) })
        }
        assertNoFailures()
    }

    @Test
    fun integerParsing() {
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
                same(
                    "BigInteger.of(\"$s\", $radix)",
                    { CommonBigInteger.of(s, radix) },
                    { BigInteger.of(s, radix) },
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
            val q = BigInteger.probablePrime(bits, rnd)
            same("BigInteger.probablePrime($bits)", {
                commonInt(
                    "$q",
                ).let { it.bitLength to it.isProbablePrime(100) }
            }, {
                bits to
                    true
            })
        }
        assertNoFailures()
    }
}

class TestDecimalsAgainstJavaMath : TestAgainstJavaMath() {
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
            // engineering-notation layouts, scales needing powers of ten beyond the Long table, >18-digit values
            "1E+11",
            "1.0E+12",
            "12E+2",
            "0E+3",
            "0E-7",
            "1E-7",
            "7E+25",
            "-1E-30",
            "9223372036854775808",
            "18446744073709551616",
            "1234567890123456789012345",
            "99999999999999999999.99999",
            "-0.0000000000000000000098765432109876543210987",
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
                MathContext(25),
                MathContext(40, RoundingMode.HALF_DOWN),
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
            decBinary("divideAndRemainder $mc") { a, b -> a.divideAndRemainder(b, mc) }
            decUnary("round $mc") { it.round(mc) }
            decUnary("absoluteValue $mc") { it.absoluteValue(mc) }
            decUnary("unaryMinus $mc") { it.unaryMinus(mc) }
            decUnary("unaryPlus $mc") { it.unaryPlus(mc) }
            // JDKs disagree on the scale of exact roots (JDK 26+ computes sqrt via rootn): compare values only.
            decUnary("sqrt $mc") { it.sqrt(mc).stripTrailingZeros() }
            for (n in listOf(-2, 0, 1, 3, 10, -1_000_000_000)) decUnary("pow $n $mc") { it.pow(n, mc) }
        }
        for (n in listOf(-1, 0, 1, 2, 7, 1_000_000_000)) decUnary("pow $n") { it pow n }
        for (rm in RoundingMode.entries) {
            decBinary("div $rm") { a, b -> a.div(b, rm) }
            for (scale in listOf(-3, 0, 2, 25)) decBinary("div $scale $rm") { a, b -> a.div(b, scale, rm) }
        }
        decUnary("unaryMinus") { -it }
        decUnary("unaryPlus") { +it }
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
            // JDK 17.0.21 returns negative scales unchanged for n = 0, newer JDKs (and the spec) don't: skip those.
            decUnary("movePointLeft $n") { if (n == 0 && it.scale < 0) null else it.movePointLeft(n) }
            decUnary("movePointRight $n") { if (n == 0 && it.scale < 0) null else it.movePointRight(n) }
            decUnary("scaleByPowerOfTen $n") { it.scaleByPowerOfTen(n) }
        }
        // Scale overflows; other huge shifts would build 2^31-digit numbers before failing.
        for (n in listOf(Int.MAX_VALUE, Int.MIN_VALUE)) decUnary("scaleByPowerOfTen $n") { it.scaleByPowerOfTen(n) }
        decUnary("movePointLeft ${Int.MAX_VALUE}") { it.movePointLeft(Int.MAX_VALUE) }
        decUnary("hashCode") { it.hashCode() }
        decUnary("equals non-BigDecimal") { it.equals(it.toString()) }
        decUnary("toChar") { it.toChar() }
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

    /** Compares [common] with both the `java.math` constructor ([java]) and the JVM `BigDecimal.of` ([factory]). */
    private fun sameDec(
        what: String,
        common: () -> BigDecimal,
        java: () -> JavaBigDecimal,
        factory: () -> BigDecimal,
    ) {
        same(what, common) { JavaBigDecimalAdapter(java()) }
        same("BigDecimal.$what", common, factory)
    }

    @Test
    fun decimalFromStrings() {
        val texts =
            listOf("1e3", "1E+3", "-0.000", "+.5", "5.", ".", "1e", "abc", "", "١٢٣", "0E-10") +
                listOf("1.2.3", "1e+0005", "1E-2147483648", "1e99999999999", "1e+", "1ex", "1e-x") +
                listOf("-1.23e-4", "+0.0E+2", "1234567890123456789012345.6789e-3", "-98765432109876543210", "1e0")
        for (s in texts + decimals) {
            sameDec("of(\"$s\")", { CommonBigDecimal.of(s) }, { JavaBigDecimal(s) }, { BigDecimal.of(s) })
            for (mc in contexts) {
                sameDec(
                    "of(\"$s\", $mc)",
                    { CommonBigDecimal.of(s, mc) },
                    { JavaBigDecimal(s, mc.toJava()) },
                    { BigDecimal.of(s, mc) },
                )
            }
            val chars = "x$s!".toCharArray()
            for ((off, len) in listOf(1 to s.length, 0 to s.length + 2, -1 to 2, 1 to s.length + 5, 1 to 0)) {
                same(
                    "of(chars of \"$s\", $off, $len)",
                    { CommonBigDecimal(chars, off, len, MathContext.DECIMAL32) },
                    { JavaBigDecimalAdapter(JavaBigDecimal(chars, off, len, MathContext.DECIMAL32.toJava())) },
                )
            }
            same("toJava($s)", { JavaBigDecimalAdapter(commonDec(s).toJava()) }, { javaDec(s) })
        }
        assertNoFailures()
    }

    @Test
    fun decimalFromLongs() {
        val longs =
            smallInts.map { it.toLong() } + listOf(99_999L, 123_456_789_012_345_678L, -999_999_999_999_999_999L)
        for (l in longs) {
            sameDec("of($l: Long)", { CommonBigDecimal.of(l) }, { JavaBigDecimal.valueOf(l) }, { BigDecimal.of(l) })
            same("new($l: Long)", { CommonBigDecimal(l) }, { JavaBigDecimalAdapter(JavaBigDecimal(l)) })
            for (scale in listOf(-3, 0, 2, 20)) {
                sameDec(
                    "of($l, scale=$scale)",
                    { CommonBigDecimal.of(l, scale) },
                    { JavaBigDecimal.valueOf(l, scale) },
                    { BigDecimal.of(l, scale) },
                )
                val prec = JavaBigDecimal.valueOf(l).precision()
                val mc = java.math.MathContext(prec)
                sameDec(
                    "of($l, scale=$scale, prec=$prec)",
                    { CommonBigDecimal.of(l, scale, prec) },
                    { JavaBigDecimal(JavaBigInteger.valueOf(l), scale, mc) },
                    { BigDecimal.of(l, scale, prec) },
                )
                sameDec(
                    "of(BigInteger $l, scale=$scale, prec=$prec)",
                    { CommonBigDecimal.of(CommonBigInteger.of(l), scale, prec) },
                    { JavaBigDecimal(JavaBigInteger.valueOf(l), scale, mc) },
                    { BigDecimal.of(BigInteger.of(l), scale, prec) },
                )
            }
            for (mc in contexts) {
                sameDec(
                    "of($l: Long, $mc)",
                    { CommonBigDecimal.of(l, mc) },
                    { JavaBigDecimal(l, mc.toJava()) },
                    { BigDecimal.of(l, mc) },
                )
            }
            val i = l.toInt()
            if (i.toLong() != l) continue
            sameDec("of($i: Int)", { CommonBigDecimal.of(i) }, { JavaBigDecimal(i) }, { BigDecimal.of(i) })
            same("new($i: Int)", { CommonBigDecimal(i) }, { JavaBigDecimalAdapter(JavaBigDecimal(i)) })
            for (mc in contexts) {
                sameDec("of($i: Int, $mc)", { CommonBigDecimal.of(i, mc) }, { JavaBigDecimal(i, mc.toJava()) }) {
                    BigDecimal.of(i, mc)
                }
                same(
                    "new($i: Int, $mc)",
                    { CommonBigDecimal(i, mc) },
                    { JavaBigDecimalAdapter(JavaBigDecimal(i, mc.toJava())) },
                )
            }
        }
        for (mc in contexts) same("$mc toJava toKotlin", { mc.toJava().toKotlin() }, { mc })
        assertNoFailures()
    }

    @Test
    fun decimalFromDoubles() {
        val doubles = listOf(0.0, -0.0, 0.1, 1.0 / 3, 1e300, -2.5e-300, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN)
        for (d in doubles) {
            val f = d.toFloat()
            sameDec("of($d)", { CommonBigDecimal(d) }, { JavaBigDecimal(d) }, { BigDecimal.of(d) })
            sameDec(
                "of(${f}f)",
                { CommonBigDecimal(f.toDouble()) },
                { JavaBigDecimal(f.toDouble()) },
                { BigDecimal.of(f) },
            )
            for (mc in contexts) {
                sameDec("of($d, $mc)", { CommonBigDecimal.of(d, mc) }, { JavaBigDecimal(d, mc.toJava()) }) {
                    BigDecimal.of(d, mc)
                }
                sameDec(
                    "of(${f}f, $mc)",
                    { CommonBigDecimal.of(f, mc) },
                    { JavaBigDecimal(f.toDouble(), mc.toJava()) },
                ) {
                    BigDecimal.of(f, mc)
                }
            }
        }
        assertNoFailures()
    }

    @Test
    fun decimalFromBigIntegers() {
        for (a in allInts) {
            for (scale in listOf(-2, 0, 3)) {
                same(
                    "of($a, scale=$scale)",
                    { CommonBigDecimal(CommonBigInteger.of(a), scale) },
                    { JavaBigDecimalAdapter(JavaBigDecimal(JavaBigInteger(a), scale)) },
                )
                for (mc in contexts) {
                    same(
                        "of($a, scale=$scale, $mc)",
                        { CommonBigDecimal(CommonBigInteger.of(a), scale, mc) },
                        { JavaBigDecimalAdapter(JavaBigDecimal(JavaBigInteger(a), scale, mc.toJava())) },
                    )
                }
            }
            sameDec(
                "of($a: BigInteger)",
                { CommonBigDecimal.of(CommonBigInteger.of(a)) },
                { JavaBigDecimal(JavaBigInteger(a)) },
                { BigDecimal.of(BigInteger.of(a)) },
            )
            for (mc in contexts) {
                sameDec(
                    "of($a: BigInteger, $mc)",
                    { CommonBigDecimal.of(CommonBigInteger.of(a), mc) },
                    { JavaBigDecimal(JavaBigInteger(a), mc.toJava()) },
                    { BigDecimal.of(BigInteger.of(a), mc) },
                )
            }
        }
        assertNoFailures()
    }
}
