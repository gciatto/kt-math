package org.gciatto.kt.math

internal actual inline fun <R> lock(
    any: Any,
    action: () -> R,
): R = action()
