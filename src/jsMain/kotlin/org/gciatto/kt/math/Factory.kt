package org.gciatto.kt.math

@JsName("bigInteger")
fun bigInteger(x: dynamic): BigInteger {
    return when (x) {
        is Long -> BigInteger.of(x as Long)
        is Int -> BigInteger.of(x as Int)
        is Number -> BigInteger.of((x as Number).toLong())
        is String -> BigInteger.of(x.toString())
        is Array<dynamic> -> {
            val y = x as Array<dynamic>
            require(y.size == 2 && y[0] is String && y[1] is Number)
            return BigInteger.of(y[0] as String, y[1] as Int)
        }
        else -> throw IllegalArgumentException()
    }
}