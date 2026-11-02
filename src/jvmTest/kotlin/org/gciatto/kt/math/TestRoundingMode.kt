package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.test.assertEquals
import java.math.RoundingMode as JavaRoundingMode

class TestRoundingMode {
    @Test
    fun testOldModesAreCorrect() {
        // java.math.RoundingMode.valueOf(Int) maps the legacy BigDecimal.ROUND_* constants, i.e. the `value`s
        for (mode in RoundingMode.entries) {
            assertEquals(mode.name, JavaRoundingMode.valueOf(mode.value).name)
        }
    }
}
