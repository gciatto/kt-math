package org.gciatto.kt.math

fun bigInteger(vararg x: Any): BigInteger {
    if (x.size == 1) {
        return when (x[0]) {
            is Long -> BigInteger.of(x[0] as Long)
            is Int -> BigInteger.of(x[0] as Int)
            is Number -> BigInteger.of((x[0] as Number).toLong())
            is String -> BigInteger.of(x[0].toString())
            else -> throw IllegalArgumentException()
        }
    } else if (x.size == 2) {
        require(x[0] is String && x[1] is Int)

        return BigInteger.of(x[0] as String, x[1] as Int)
    } else {
        throw IllegalArgumentException()
    }
}