package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.test.assertEquals

class TestRoundingMode {
    @Test
    @Suppress("DEPRECATION")
    fun testOldModesAreCorrect() {
        assertEquals(BigDecimal.ROUND_CEILING, RoundingMode.CEILING.oldMode)
        assertEquals(BigDecimal.ROUND_DOWN, RoundingMode.DOWN.oldMode)
        assertEquals(BigDecimal.ROUND_FLOOR, RoundingMode.FLOOR.oldMode)
        assertEquals(BigDecimal.ROUND_HALF_DOWN, RoundingMode.HALF_DOWN.oldMode)
        assertEquals(BigDecimal.ROUND_HALF_EVEN, RoundingMode.HALF_EVEN.oldMode)
        assertEquals(BigDecimal.ROUND_HALF_UP, RoundingMode.HALF_UP.oldMode)
        assertEquals(BigDecimal.ROUND_UP, RoundingMode.UP.oldMode)
        assertEquals(BigDecimal.ROUND_UNNECESSARY, RoundingMode.UNNECESSARY.oldMode)
    }
}