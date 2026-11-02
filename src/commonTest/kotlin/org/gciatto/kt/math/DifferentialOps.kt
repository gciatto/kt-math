package org.gciatto.kt.math

/**
 * Named operations over the [BigInteger]/[BigDecimal] interfaces, with platform-independent rendering of their
 * outcomes. On the JVM, [BigInteger.of]/[BigDecimal.of] build `java.math` adapters, so running these operations there
 * yields the expected outcomes (`GenerateDifferentialCases`, in jvmTest); on JS, they run the common implementation
 * (`TestDifferentialCases`, in jsTest). Arguments are strings: numbers, `precision:MODE` math contexts, rounding mode
 * names, or plain integers (scales, exponents, bit indices, radixes).
 */
object DifferentialOps {
    private fun int(s: String): BigInteger = BigInteger.of(s)

    private fun dec(s: String): BigDecimal = BigDecimal.of(s)

    private fun mc(s: String): MathContext =
        s.split(':').let { (precision, mode) -> MathContext(precision.toInt(), RoundingMode.valueOf(mode)) }

    private fun rm(s: String): RoundingMode = RoundingMode.valueOf(s)

    private val integerBinary: Map<String, (BigInteger, BigInteger) -> Any?> =
        mapOf(
            "plus" to { a, b -> a + b },
            "minus" to { a, b -> a - b },
            "times" to { a, b -> a * b },
            "div" to { a, b -> a / b },
            "rem" to { a, b -> a % b },
            "divideAndRemainder" to { a, b -> a.divideAndRemainder(b) },
            "gcd" to { a, b -> a.gcd(b) },
            "compareTo" to { a, b -> a.compareTo(b) },
            "and" to { a, b -> a.and(b) },
            "or" to { a, b -> a.or(b) },
            "xor" to { a, b -> a.xor(b) },
            "andNot" to { a, b -> a.andNot(b) },
            "modInverse" to { a, b -> a.modInverse(b) },
        )

    private val integerUnary: Map<String, (BigInteger) -> Any?> =
        mapOf(
            "unaryMinus" to { a -> -a },
            "absoluteValue" to { a -> a.absoluteValue },
            "signum" to { a -> a.signum },
            "sqrt" to { a -> a.sqrt() },
            "sqrtAndRemainder" to { a -> a.sqrtAndRemainder() },
            "bitLength" to { a -> a.bitLength },
            "bitCount" to { a -> a.bitCount },
            "not" to { a -> !a },
            "toString" to { a -> a.toString() },
            "toByteArray" to { a -> a.toByteArray() },
            "toInt" to { a -> a.toInt() },
            "toLong" to { a -> a.toLong() },
            "toShort" to { a -> a.toShort() },
            "toByte" to { a -> a.toByte() },
            "toChar" to { a -> a.toChar().code },
            "toDouble" to { a -> a.toDouble().toBits() },
            "toFloat" to { a -> a.toFloat().toBits() },
            "toIntExact" to { a -> a.toIntExact() },
            "toLongExact" to { a -> a.toLongExact() },
            "isProbablePrime" to { a -> a.isProbablePrime(50) },
            "nextProbablePrime" to { a -> a.nextProbablePrime() },
        )

    private val integerWithInt: Map<String, (BigInteger, Int) -> Any?> =
        mapOf(
            "pow" to { a, n -> a pow n },
            "shl" to { a, n -> a shl n },
            "shr" to { a, n -> a shr n },
            "testBit" to { a, n -> a.testBit(n) },
            "setBit" to { a, n -> a.setBit(n) },
            "clearBit" to { a, n -> a.clearBit(n) },
            "flipBit" to { a, n -> a.flipBit(n) },
            "toStringRadix" to { a, n -> a.toString(n) },
        )

    private val decimalBinary: Map<String, (BigDecimal, BigDecimal) -> Any?> =
        mapOf(
            "plus" to { a, b -> a + b },
            "minus" to { a, b -> a - b },
            "times" to { a, b -> a * b },
            "div" to { a, b -> a / b },
            "rem" to { a, b -> a % b },
            "divideToIntegralValue" to { a, b -> a.divideToIntegralValue(b) },
            "divideAndRemainder" to { a, b -> a.divideAndRemainder(b) },
            "compareTo" to { a, b -> a.compareTo(b) },
        )

    private val decimalBinaryWithContext: Map<String, (BigDecimal, BigDecimal, MathContext) -> Any?> =
        mapOf(
            "plus" to { a, b, mc -> a.plus(b, mc) },
            "minus" to { a, b, mc -> a.minus(b, mc) },
            "times" to { a, b, mc -> a.times(b, mc) },
            "div" to { a, b, mc -> a.div(b, mc) },
            "rem" to { a, b, mc -> a.rem(b, mc) },
            "divideToIntegralValue" to { a, b, mc -> a.divideToIntegralValue(b, mc) },
        )

    private val decimalUnary: Map<String, (BigDecimal) -> Any?> =
        mapOf(
            "unaryMinus" to { a -> -a },
            "signum" to { a -> a.signum },
            "precision" to { a -> a.precision },
            "unscaledValue" to { a -> a.unscaledValue },
            "stripTrailingZeros" to { a -> a.stripTrailingZeros() },
            "ulp" to { a -> a.ulp() },
            "toString" to { a -> a.toString() },
            "toEngineeringString" to { a -> a.toEngineeringString() },
            "toPlainString" to { a -> a.toPlainString() },
            "toBigInteger" to { a -> a.toBigInteger() },
            "toBigIntegerExact" to { a -> a.toBigIntegerExact() },
            "toLong" to { a -> a.toLong() },
            "toInt" to { a -> a.toInt() },
            "toLongExact" to { a -> a.toLongExact() },
            "toDouble" to { a -> a.toDouble().toBits() },
            "toFloat" to { a -> a.toFloat().toBits() },
        )

    private val decimalWithInt: Map<String, (BigDecimal, Int) -> Any?> =
        mapOf(
            "pow" to { a, n -> a pow n },
            "setScale" to { a, n -> a.setScale(n) },
            "movePointLeft" to { a, n -> a.movePointLeft(n) },
            "movePointRight" to { a, n -> a.movePointRight(n) },
            "scaleByPowerOfTen" to { a, n -> a.scaleByPowerOfTen(n) },
        )

    private val decimalWithContext: Map<String, (BigDecimal, MathContext) -> Any?> =
        mapOf(
            "round" to { a, mc -> a.round(mc) },
            // JDKs disagree on the scale of exact roots: compare values only
            "sqrt" to { a, mc -> a.sqrt(mc).stripTrailingZeros() },
        )

    /** Operation name (with a `int.`/`dec.` prefix and an arity-specific suffix) to implementation. */
    val all: Map<String, (List<String>) -> Any?> =
        buildMap {
            integerBinary.forEach { (k, f) -> put("int.$k", { a -> f(int(a[0]), int(a[1])) }) }
            integerUnary.forEach { (k, f) -> put("int.$k", { a -> f(int(a[0])) }) }
            integerWithInt.forEach { (k, f) -> put("int.$k", { a -> f(int(a[0]), a[1].toInt()) }) }
            put("int.modPow") { a -> int(a[0]).modPow(int(a[1]), int(a[2])) }
            put("int.getIntBits") { a -> int(a[0]).getIntBits(a[1].toInt(), a[2].toInt()) }
            put("int.getLongBits") { a -> int(a[0]).getLongBits(a[1].toInt(), a[2].toInt()) }
            put("int.parse") { a -> BigInteger.of(a[0], a[1].toInt()) }
            put("int.ofBytes") { a -> BigInteger.of(int(a[0]).toByteArray()) }
            put("int.ofSignMagnitude") { a -> BigInteger.of(a[1].toInt(), int(a[0]).absoluteValue.toByteArray()) }
            decimalBinary.forEach { (k, f) -> put("dec.$k", { a -> f(dec(a[0]), dec(a[1])) }) }
            decimalBinaryWithContext.forEach { (k, f) -> put("dec.$k/mc", { a -> f(dec(a[0]), dec(a[1]), mc(a[2])) }) }
            decimalUnary.forEach { (k, f) -> put("dec.$k", { a -> f(dec(a[0])) }) }
            decimalWithInt.forEach { (k, f) -> put("dec.$k", { a -> f(dec(a[0]), a[1].toInt()) }) }
            decimalWithContext.forEach { (k, f) -> put("dec.$k", { a -> f(dec(a[0]), mc(a[1])) }) }
            put("dec.pow/mc") { a -> dec(a[0]).pow(a[1].toInt(), mc(a[2])) }
            put("dec.setScale/rm") { a -> dec(a[0]).setScale(a[1].toInt(), rm(a[2])) }
            put("dec.div/scale") { a -> dec(a[0]).div(dec(a[1]), a[2].toInt(), rm(a[3])) }
            put("dec.parse") { a -> BigDecimal.of(a[0]) }
            put("dec.parse/mc") { a -> BigDecimal.of(a[0], mc(a[1])) }
            put("dec.ofDouble") { a -> BigDecimal.of(a[0].toDouble()) }
        }

    val integerBinaryOps: Set<String> get() = integerBinary.keys.mapTo(mutableSetOf()) { "int.$it" }
    val integerUnaryOps: Set<String> get() = integerUnary.keys.mapTo(mutableSetOf()) { "int.$it" }
    val integerWithIntOps: Set<String> get() = integerWithInt.keys.mapTo(mutableSetOf()) { "int.$it" }
    val decimalBinaryOps: Set<String> get() = decimalBinary.keys.mapTo(mutableSetOf()) { "dec.$it" }
    val decimalBinaryWithContextOps: Set<String> get() =
        decimalBinaryWithContext.keys.mapTo(mutableSetOf()) {
            "dec.$it/mc"
        }
    val decimalUnaryOps: Set<String> get() = decimalUnary.keys.mapTo(mutableSetOf()) { "dec.$it" }
    val decimalWithIntOps: Set<String> get() = decimalWithInt.keys.mapTo(mutableSetOf()) { "dec.$it" }
    val decimalWithContextOps: Set<String> get() = decimalWithContext.keys.mapTo(mutableSetOf()) { "dec.$it" }

    /** Runs [op] on [args], rendering the result, or the simple name of the exception it throws. */
    fun outcome(
        op: String,
        args: List<String>,
    ): String =
        try {
            render(all.getValue(op)(args))
        } catch (
            // Throwable: on JS, native errors (e.g. RangeError from BigInt-backed Long) aren't Exceptions
            @Suppress("TooGenericExceptionCaught") e: Throwable,
        ) {
            "throws ${e::class.simpleName}"
        }

    // Doubles, floats and chars must not get here: their toString differs between the JVM and JS, and on JS every number
    // passes `is Double`. Operations return their bits/codes instead.
    private fun render(x: Any?): String =
        when (x) {
            is Array<*> -> x.joinToString(prefix = "[", postfix = "]") { render(it) }
            is Pair<*, *> -> "(${render(x.first)}, ${render(x.second)})"
            is ByteArray -> x.contentToString()
            is BigDecimal -> "$x@${x.scale}"
            else -> x.toString()
        }
}
