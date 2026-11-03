package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext

// Helpers mapping the java.math API used by big-math onto kt-math's

/** `java.math.BigDecimal.divide(divisor, mc)`: kt-math's `div(divisor, mc)` is nullable, but never returns null. */
internal fun BigDecimal.divide(
    divisor: BigDecimal,
    mc: MathContext,
): BigDecimal = div(divisor, mc)!!

/** `java.math.BigDecimal.round(mc)`: kt-math's `round(mc)` is nullable, but never returns null. */
internal fun BigDecimal.rounded(mc: MathContext): BigDecimal = round(mc)!!

/** A lock guarding one of big-math's caches (Java `synchronized` blocks). Not reentrant on every platform. */
internal expect class CacheLock() {
    fun <R> withLock(action: () -> R): R
}
