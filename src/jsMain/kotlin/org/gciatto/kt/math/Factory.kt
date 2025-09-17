package org.gciatto.kt.math

@Suppress("USELESS_CAST")
@JsName("bigInteger")
@JsExport
fun bigInteger(x: dynamic): BigInteger {
    return when (x) {
        is Long -> CommonBigInteger.of(x as Long)
        is Int -> CommonBigInteger.of(x as Int)
        is Number -> CommonBigInteger.of((x as Number).toLong())
        is String -> CommonBigInteger.of(x.toString())
        is Array<dynamic> -> {
            val y = x as Array<dynamic>
            require(y.size == 2 && y[0] is String && y[1] is Number)
            return CommonBigInteger.of(y[0] as String, y[1] as Int)
        }
        else -> throw IllegalArgumentException()
    }
}

@JsName("bigDecimal")
@JsExport
fun bigDecimal(x: dynamic): BigDecimal =
    when (x) {
        is Array<dynamic> -> {
            require(x.size == 2 && x[1] is MathContext)
            when (x[1]) {
                is MathContext -> BigDecimal.of(x[0].toString(), x[1] as MathContext)
                else -> throw IllegalArgumentException()
            }
        }
        is Any -> BigDecimal.of(x.toString())
        else -> throw IllegalArgumentException()
    }
