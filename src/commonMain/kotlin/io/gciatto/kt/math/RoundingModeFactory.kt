package io.gciatto.kt.math

internal expect object RoundingModeFactory {
    fun roundingModeOf(value: Int): RoundingMode
}