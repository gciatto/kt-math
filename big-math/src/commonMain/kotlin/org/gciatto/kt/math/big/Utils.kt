package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import kotlin.math.absoluteValue

// Helpers mapping the java.math API used by big-math onto kt-math's

/** `java.math.BigDecimal.divide(divisor, mc)`: kt-math's `div(divisor, mc)` is nullable, but never returns null. */
internal fun BigDecimal.divide(
    divisor: BigDecimal,
    mc: MathContext,
): BigDecimal = div(divisor, mc)!!

/** `java.math.BigDecimal.round(mc)`: kt-math's `round(mc)` is nullable, but never returns null. */
internal fun BigDecimal.rounded(mc: MathContext): BigDecimal = round(mc)!!

private const val PLAIN_MIN = 1e-3
private const val PLAIN_MAX = 1e7

/**
 * `java.math.BigDecimal.valueOf(double)`, i.e. `new BigDecimal(Double.toString(value))`: the shortest decimal that
 * rounds to [value], with the scale of Java's `Double.toString` format on every platform (plain notation with at
 * least one fractional digit in `[10^-3, 10^7)`, else scientific notation with at least one fractional digit).
 * Kotlin's `Double.toString` is Java's on the JVM only (e.g. `1.0` is `"1"` and `1e-7` is `"1e-7"` on JS).
 */
internal fun valueOf(value: Double): BigDecimal {
    if (value == 0.0) return BigDecimal.ZERO.setScale(1)
    val stripped = BigDecimal.of(value.toString()).stripTrailingZeros()
    val exponent = stripped.precision - stripped.scale - 1
    val scale =
        if (value.absoluteValue >= PLAIN_MIN && value.absoluteValue < PLAIN_MAX) {
            maxOf(1, stripped.scale)
        } else {
            maxOf(2, stripped.precision) - 1 - exponent
        }
    return stripped.setScale(scale)
}

/** A lock guarding one of big-math's caches (Java `synchronized` blocks). Not reentrant on every platform. */
internal expect class CacheLock() {
    fun <R> withLock(action: () -> R): R
}
