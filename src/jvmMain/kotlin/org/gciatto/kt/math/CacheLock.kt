package org.gciatto.kt.math

internal actual class CacheLock actual constructor() {
    actual fun <R> withLock(action: () -> R): R = synchronized(this, action)
}
