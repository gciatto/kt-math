package org.gciatto.kt.math

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@PublishedApi
internal val globalLock = AtomicBoolean(false)

// ponytail: one global spin lock, only guarding the rare expansion of a cache; per-object locks if contention shows up
@OptIn(ExperimentalAtomicApi::class)
internal actual inline fun <R> lock(
    any: Any,
    action: () -> R,
): R {
    while (!globalLock.compareAndSet(expectedValue = false, newValue = true)) {
        // spin
    }
    try {
        return action()
    } finally {
        globalLock.store(false)
    }
}
