package org.gciatto.kt.math

/**
 * A lock guarding a cache (Java `synchronized` blocks): `synchronized` on the JVM, a spin lock elsewhere, not reentrant.
 */
internal expect class CacheLock() {
    fun <R> withLock(action: () -> R): R
}
