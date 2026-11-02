package org.gciatto.kt.math

internal actual fun logImpl(lazyObject: () -> Any) {
    println(lazyObject())
}

internal actual inline fun <T, reified U : T> T?.castTo(): U = this as U
