package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

class BenchmarkIntegers {

    @ExperimentalTime
    inline fun <R> chrono(f: () -> R): Pair<R, Duration> {
        val start = TimeSource.Monotonic.markNow()
        val result = f()
        return result to start.elapsedNow()
    }

    @Test
    @ExperimentalTime
    fun benchMarkSumsViaFibonacci() {
        for (i in 0 .. 1000) {
            chrono {
                fibonacci(BigInteger.of(i))
            }.let { (_, time) ->
                println("Computed fibonacci($i) in $time")
            }
        }
    }

    @Test
    @ExperimentalTime
    fun benchMarkMultiplicationsViaFactorial() {
        for (i in 0 .. 1000) {
            chrono {
                factorial(BigInteger.of(i))
            }.let { (_, time) ->
                println("Computed factorial($i) in $time")
            }
        }
    }
}