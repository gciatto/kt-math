package io.gciatto.kt.math

data class MathContext(val precision: Int = 9, val roundingMode: RoundingMode = RoundingMode.HALF_UP) {
    init {
        require(precision >= 0)
    }

    companion object {

        val UNLIMITED = MathContext(0, RoundingMode.HALF_UP)

        val DECIMAL32 = MathContext(7, RoundingMode.HALF_EVEN)

        val DECIMAL64 = MathContext(16, RoundingMode.HALF_EVEN)

        val DECIMAL128 = MathContext(34, RoundingMode.HALF_EVEN)
    }

    override fun toString(): String {
        return "precision=" + precision + " " +
                "roundingMode=" + roundingMode.toString()
    }
}