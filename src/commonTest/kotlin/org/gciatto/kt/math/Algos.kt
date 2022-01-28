package org.gciatto.kt.math

fun fibonacci(n: BigInteger): BigInteger = when {
    n < BigInteger.ZERO -> throw IllegalArgumentException("Cannot compute the fibonacci($n)")
    n <= BigInteger.ONE -> BigInteger.ONE
    else -> {
        var prev = BigInteger.ONE
        var curr = BigInteger.ONE
        for (i in BigInteger.TWO .. n) {
            val next = curr + prev
            prev = curr
            curr = next
        }
        curr
    }
}

fun factorial(n: BigInteger): BigInteger = when {
    n < BigInteger.ZERO -> throw IllegalArgumentException("Cannot compute the fib($n)")
    else -> {
        var curr = BigInteger.ONE
        for (i in BigInteger.TWO .. n) {
            curr *= i
        }
        curr
    }
}
