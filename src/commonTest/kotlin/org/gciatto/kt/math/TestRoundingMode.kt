package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.test.assertEquals

class TestRoundingMode {
    @Test
    @Suppress("DEPRECATION")
    fun testOldModesAreCorrect() {
        assertEquals(CommonBigDecimal.ROUND_CEILING, RoundingMode.CEILING.oldMode)
        assertEquals(CommonBigDecimal.ROUND_DOWN, RoundingMode.DOWN.oldMode)
        assertEquals(CommonBigDecimal.ROUND_FLOOR, RoundingMode.FLOOR.oldMode)
        assertEquals(CommonBigDecimal.ROUND_HALF_DOWN, RoundingMode.HALF_DOWN.oldMode)
        assertEquals(CommonBigDecimal.ROUND_HALF_EVEN, RoundingMode.HALF_EVEN.oldMode)
        assertEquals(CommonBigDecimal.ROUND_HALF_UP, RoundingMode.HALF_UP.oldMode)
        assertEquals(CommonBigDecimal.ROUND_UP, RoundingMode.UP.oldMode)
        assertEquals(CommonBigDecimal.ROUND_UNNECESSARY, RoundingMode.UNNECESSARY.oldMode)
    }
}