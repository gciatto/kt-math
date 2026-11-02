package org.gciatto.kt.math

internal actual fun logImpl(lazyObject: () -> Any) {
    console.warn(lazyObject())
}

internal actual inline fun <T, reified U : T> T?.castTo(): U = this.unsafeCast<U>()
