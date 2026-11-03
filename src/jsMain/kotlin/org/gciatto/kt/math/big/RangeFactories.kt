@file:JsExport

package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext

// The BigDecimal ranges for JS, as arrays: Kotlin sequences aren't exportable

/** The values of [range], as an array (exported to JS as `range`). */
@JsName("range")
fun rangeArray(
    startInclusive: BigDecimal,
    endExclusive: BigDecimal,
    step: BigDecimal,
    mathContext: MathContext,
): Array<BigDecimal> = range(startInclusive, endExclusive, step, mathContext).toList().toTypedArray()

/** The values of [rangeClosed], as an array (exported to JS as `rangeClosed`). */
@JsName("rangeClosed")
fun rangeClosedArray(
    startInclusive: BigDecimal,
    endInclusive: BigDecimal,
    step: BigDecimal,
    mathContext: MathContext,
): Array<BigDecimal> = rangeClosed(startInclusive, endInclusive, step, mathContext).toList().toTypedArray()
