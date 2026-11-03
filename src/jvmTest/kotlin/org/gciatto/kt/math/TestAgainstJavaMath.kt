package org.gciatto.kt.math

import java.lang.reflect.InvocationTargetException
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.fail
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

    /**
     * Calls the JDK 9+ method [name] on [receiver] by reflection (kt-math compiles against the Java 8 API, and its
     * JVM adapters use the Kotlin implementation for these methods), or returns `null` on Java 8.
     */
    protected fun jdk9(
        receiver: Any,
        name: String,
        vararg args: Any,
    ): Any? {
        val method = receiver.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == args.size }
        return try {
            method?.invoke(receiver, *args)
        } catch (e: InvocationTargetException) {
            throw e.cause ?: e
        }
    }

    protected val isJdk9OrLater = JavaBigInteger::class.java.methods.any { it.name == "sqrt" }

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
            listOf("1" + "0".repeat(100), "-" + "9".repeat(300), JavaBigInteger.valueOf(2).pow(4_096).toString())

    protected val allInts = smallInts + bigInts

    protected companion object {
        const val MAX_EXAMPLE_LENGTH = 400
    }
}
