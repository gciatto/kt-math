package org.gciatto.kt.math

import kotlin.random.asJavaRandom
import kotlin.test.Test
import java.math.BigInteger as JavaBigInteger

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
        if (isJdk9OrLater) {
            for (a in allInts) {
                same("sqrt (JDK)($a)", { commonInt(a).sqrt() }) {
                    JavaBigIntegerAdapter(jdk9(JavaBigInteger(a), "sqrt") as JavaBigInteger)
                }
                same("sqrtAndRemainder (JDK)($a)", { commonInt(a).sqrtAndRemainder() }) {
                    (
                        jdk9(
                            JavaBigInteger(a),
                            "sqrtAndRemainder",
                        ) as Array<*>
                    ).map { JavaBigIntegerAdapter(it as JavaBigInteger) }
                        .toTypedArray()
                }
            }
        }
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
                listOf(40, 64, 65, 100, 300).map { JavaBigInteger.valueOf(2).pow(it).toString() } +
                JavaBigInteger
                    .valueOf(2)
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
            for (count in listOf(-1, 0, 1, 8, 16, 31, 32, 33, 63, 64, 65)) {
                intUnary("getIntBits $n $count") { it.getIntBits(n, count) }
                intUnary("getLongBits $n $count") { it.getLongBits(n, count) }
            }
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
                        {
                            // java.math.BigInteger(signum, bytes, off, len) is JDK 9+: same checks, on a copy
                            if (signum !in -1..1) throw NumberFormatException("Invalid signum value")
                            if (off < 0 || len < 0 || off + len > bytes.size) throw IndexOutOfBoundsException()
                            JavaBigIntegerAdapter(JavaBigInteger(signum, bytes.copyOfRange(off, off + len)))
                        },
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
