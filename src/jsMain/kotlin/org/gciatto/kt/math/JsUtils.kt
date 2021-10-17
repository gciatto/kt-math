package org.gciatto.kt.math

internal actual fun logImpl(lazyObject: () -> Any) {
    console.warn(lazyObject())
}
