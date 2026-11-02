package org.gciatto.kt.math.big

internal actual class CacheLock actual constructor() {
    actual fun <R> withLock(action: () -> R): R = synchronized(this, action)
}
