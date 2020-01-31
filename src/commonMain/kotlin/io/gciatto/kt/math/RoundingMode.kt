package io.gciatto.kt.math

enum class RoundingMode {
    UP,
    DOWN,
    CEILING,
    FLOOR,
    HALF_UP,
    HALF_DOWN,
    HALF_EVEN,
    UNNECESSARY;

    companion object {
        fun valueOf(value: Int): RoundingMode {
            return RoundingModeFactory.roundingModeOf(value)
        }
    }
}