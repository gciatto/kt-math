/*
 * Ported from big-math 2.3.2 (https://github.com/eobermuhlner/big-math),
 * ch.obermuhlner.math.big.stream.BigDecimalStream (Java streams are Kotlin sequences here).
 *
 * MIT License
 *
 * Copyright (c) 2017 Eric Obermühlner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@file:JvmName("BigDecimalStream")

package org.gciatto.kt.math.big

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.MathContext
import kotlin.jvm.JvmName

/**
 * Returns the sequence of [BigDecimal]s from [startInclusive] (inclusive) to [endExclusive] (exclusive) by an
 * incremental [step], computed with [mathContext]: as in the loop
 * `var i = startInclusive; while (i < endExclusive) { yield(i); i = i.plus(step, mathContext) }`, also for negative
 * steps (with `>` in place of `<`).
 *
 * @throws IllegalArgumentException if [step] is 0
 */
fun range(
    startInclusive: BigDecimal,
    endExclusive: BigDecimal,
    step: BigDecimal,
    mathContext: MathContext,
): Sequence<BigDecimal> {
    if (step.signum == 0) {
        throw IllegalArgumentException("invalid step: 0")
    }
    if (endExclusive.minus(startInclusive).signum != step.signum) {
        return emptySequence()
    }
    return bigDecimalSequence(startInclusive, endExclusive, false, step, mathContext)
}

/**
 * Returns the sequence of [BigDecimal]s from [startInclusive] (inclusive) to [endInclusive] (inclusive) by an
 * incremental [step], computed with [mathContext] (see [range]).
 *
 * @throws IllegalArgumentException if [step] is 0
 */
fun rangeClosed(
    startInclusive: BigDecimal,
    endInclusive: BigDecimal,
    step: BigDecimal,
    mathContext: MathContext,
): Sequence<BigDecimal> {
    if (step.signum == 0) {
        throw IllegalArgumentException("invalid step: 0")
    }
    if (endInclusive.minus(startInclusive).signum == -step.signum) {
        return emptySequence()
    }
    return bigDecimalSequence(startInclusive, endInclusive, true, step, mathContext)
}

/**
 * Returns the values of this range (`a..b`) by [step], `a` and `b` included, computed with
 * [MathContext.DECIMAL128] (see [rangeClosed]): `(0.toBigDecimal()..1.toBigDecimal() step "0.25".toBigDecimal())`.
 */
infix fun ClosedRange<BigDecimal>.step(step: BigDecimal): Sequence<BigDecimal> =
    rangeClosed(start, endInclusive, step, MathContext.DECIMAL128)

/**
 * Returns the values of this range (`a..<b`) by [step], `b` excluded, computed with [MathContext.DECIMAL128] (see
 * [range]).
 */
infix fun OpenEndRange<BigDecimal>.step(step: BigDecimal): Sequence<BigDecimal> =
    range(start, endExclusive, step, MathContext.DECIMAL128)

// BigDecimalSpliterator, as a sequence
private fun bigDecimalSequence(
    startInclusive: BigDecimal,
    end: BigDecimal,
    inclusive: Boolean,
    step: BigDecimal,
    mathContext: MathContext,
): Sequence<BigDecimal> {
    val count = estimatedCount(startInclusive, end, inclusive, step, mathContext)
    return sequence {
        var value = startInclusive
        for (i in 0 until count) {
            yield(value)
            value = value.plus(step, mathContext)
        }
    }
}

private fun estimatedCount(
    startInclusive: BigDecimal,
    end: BigDecimal,
    inclusive: Boolean,
    step: BigDecimal,
    mathContext: MathContext,
): Long {
    val count = end.minus(startInclusive).divide(step, mathContext)
    var result = count.toLong()
    if (count.fractionalPart().signum != 0) {
        result++
    } else {
        if (inclusive) {
            result++
        }
    }
    return result
}
