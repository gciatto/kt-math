package org.gciatto.kt.math

internal expect inline fun <R> lock(
    any: Any,
    action: () -> R,
): R
