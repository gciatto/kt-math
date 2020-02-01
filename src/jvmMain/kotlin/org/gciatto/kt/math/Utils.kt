package org.gciatto.kt.math

internal actual fun logImpl(lazyObject: () -> Any) {
    System.err.println(lazyObject)
}