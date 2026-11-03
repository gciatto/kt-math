package org.gciatto.kt.math

/**
 * Converts any JS value into a [BigInteger]: a `bigint` (taken as is), a number (truncated towards zero), a string
 * (as by [BigInteger.of]) or an array `[string, radix]`.
 *
 * @throws IllegalArgumentException if [x] is none of those, or a non-finite number
 */
@Suppress("USELESS_CAST")
@JsName("bigInteger")
@JsExport
fun bigInteger(x: dynamic): BigInteger {
    return when (x) {
        // `is Long` holds for any JS bigint (Longs are BigInts): taken as is, whatever its size
        is Long -> JsBigInteger(x)
        // no `is Int` branch: on JS it holds for every number, and Int.toLong() fails on non-integers
        is Number -> {
            val d = (x as Number).toDouble()
            require(d.isFinite()) { "Cannot convert $x into a BigInteger" }
            JsBigInteger(js("BigInt(Math.trunc(d))")) // truncates towards zero, exact for any integer-valued double
        }
        is String -> BigInteger.of(x as String)
        is Array<dynamic> -> {
            val y = x as Array<dynamic>
            require(y.size == 2 && y[0] is String && y[1] is Number)
            return BigInteger.of(y[0] as String, y[1] as Int)
        }
        else -> throw IllegalArgumentException("Cannot convert $x into a BigInteger")
    }
}

/**
 * Converts any JS value into a [BigDecimal], through its string representation (so `0.1` is 0.1, unlike
 * `BigDecimal.ofDouble(0.1)`), or an array `[value, mathContext]`, rounding `value` with `mathContext`.
 *
 * @throws NumberFormatException if the string representation isn't a number
 */
@JsName("bigDecimal")
@JsExport
fun bigDecimal(x: dynamic): BigDecimal =
    when (x) {
        is Array<dynamic> -> {
            require(x.size == 2 && x[1] is MathContext)
            when (x[1]) {
                is MathContext -> BigDecimal.of(x[0].toString(), x[1] as MathContext)
                else -> throw IllegalArgumentException("Expected a MathContext as second element, got ${x[1]}")
            }
        }
        is Any -> BigDecimal.of(x.toString())
        else -> throw IllegalArgumentException("Cannot convert $x into a BigDecimal")
    }

/**
 * Converts any JS value into a [BigRational], through its string representation (as by [BigRational.valueOf], e.g.
 * `"1/3"` or `0.25`), or an array `[numerator, denominator]` of values accepted by [bigInteger].
 */
@JsName("bigRational")
@JsExport
fun bigRational(x: dynamic): BigRational =
    when (x) {
        // [numerator, denominator], each as accepted by bigInteger
        is Array<dynamic> -> {
            require(x.size == 2) { "Expected [numerator, denominator], got $x" }
            BigRational.valueOf(bigInteger(x[0]), bigInteger(x[1]))
        }
        is Any -> BigRational.valueOf(x.toString())
        else -> throw IllegalArgumentException("Cannot convert $x into a BigRational")
    }
