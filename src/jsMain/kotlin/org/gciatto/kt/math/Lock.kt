package org.gciatto.kt.math

actual inline fun <R> lock(
    any: Any,
    action: () -> R,
): R {
    return action()
}
