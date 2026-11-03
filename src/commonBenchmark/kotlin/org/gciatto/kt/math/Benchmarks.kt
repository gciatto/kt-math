package org.gciatto.kt.math

import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/** The running platform, as shown in the benchmark results. */
internal expect val platform: String

private val budget = 500.milliseconds

private var sink = 0

/** Average duration of [operation], timed over about [budget] after as long a warm-up. */
private fun measure(operation: () -> Any?): Duration {
    var warmupRuns = 0
    val warmup = TimeSource.Monotonic.markNow()
    while (warmup.elapsedNow() < budget) {
        sink += operation().hashCode()
        warmupRuns++
    }
    val start = TimeSource.Monotonic.markNow()
    repeat(warmupRuns) { sink += operation().hashCode() }
    return start.elapsedNow() / warmupRuns
}

/** A random non-negative integer of exactly [bits] bits, a multiple of 32. */
private fun Random.bigInteger(bits: Int): BigInteger =
    BigInteger.of(IntArray(bits / Int.SIZE_BITS) { nextInt() }).absoluteValue.setBit(bits - 1)

private fun fibonacci(n: Int): BigInteger {
    var previous = BigInteger.ZERO
    var current = BigInteger.ONE
    repeat(n - 1) { current = (current + previous).also { previous = current } }
    return current
}

private fun factorial(n: Int): BigInteger = (2..n).fold(BigInteger.ONE) { product, i -> product * BigInteger.of(i) }

/**
 * Benchmarks of the [BigInteger] and [BigDecimal] implementations of the running platform (the ones [BigInteger.of]
 * and [BigDecimal.of] return), printed as the rows of a Markdown table. Run by `./gradlew benchmark`.
 */
fun main() {
    val rnd = Random(42)
    val x2048 = rnd.bigInteger(2048)
    val y2048 = rnd.bigInteger(2048)
    val x8192 = rnd.bigInteger(8192)
    val y8192 = rnd.bigInteger(8192)
    val digits = x8192.toString()
    val pi = BigDecimal.of(PI_STRING)
    val e = BigDecimal.of(E_STRING)
    val benchmarks =
        listOf<Pair<String, () -> Any?>>(
            "fibonacci(10000), additions" to { fibonacci(10_000) },
            "1000!, products by small integers" to { factorial(1_000) },
            "2048 x 2048 bits product" to { x2048 * y2048 },
            "8192 x 8192 bits product" to { x8192 * y8192 },
            "8192 bits square" to { x8192 * x8192 },
            "8192 / 2048 bits division" to { x8192 / y2048 },
            "8192 bits toString" to { x8192.toString() },
            "8192 bits parsing" to { BigInteger.of(digits) },
        ) +
            listOf(1024, 2048, 4096).map { bits ->
                val modulus = rnd.bigInteger(bits).setBit(0)
                val base = rnd.bigInteger(bits) % modulus
                val exponent = rnd.bigInteger(bits)
                "$bits bits modPow, odd modulus" to { base.modPow(exponent, modulus) }
            } +
            listOf<Pair<String, () -> Any?>>(
                "decimal product, 300 digits" to { pi * e },
                "decimal division, 100 digits" to { pi.div(e, MathContext(100)) },
                "decimal sqrt, 1000 digits" to { pi.sqrt(MathContext(1_000)) },
                "decimal pow(100), 50 digits" to { pi.pow(100, MathContext(50)) },
            )
    println("| $platform | average time |")
    println("|---|---:|")
    for ((name, operation) in benchmarks) {
        println("| $name | ${measure(operation)} |")
    }
    if (sink == Int.MIN_VALUE) println() // keeps the results alive
}
