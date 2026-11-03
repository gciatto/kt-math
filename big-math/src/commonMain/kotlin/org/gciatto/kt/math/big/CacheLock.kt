package org.gciatto.kt.math.big

/** A lock guarding one of big-math's caches (Java `synchronized` blocks). Not reentrant on every platform. */
internal expect class CacheLock() {
    fun <R> withLock(action: () -> R): R
}
