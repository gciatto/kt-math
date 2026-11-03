package org.gciatto.kt.math.big

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

// ponytail: a non-reentrant spin lock per cache (JS never contends; native threads spin while a cache is filled).
// No cache is filled while holding its own lock again, and the locks are never taken in a cycle, so no deadlocks.
@OptIn(ExperimentalAtomicApi::class)
internal actual class CacheLock actual constructor() {
    private val busy = AtomicBoolean(false)

    actual fun <R> withLock(action: () -> R): R {
        while (!busy.compareAndSet(expectedValue = false, newValue = true)) {
            // spin
        }
        try {
            return action()
        } finally {
            busy.store(false)
        }
    }
}
