/*
 * Copyright (c) 2003, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 * Portions Copyright IBM Corporation, 2001. All Rights Reserved.
 */
package org.gciatto.kt.math

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Specifies a *rounding behavior* for numerical operations capable of discarding precision, as Java's
 * `java.math.RoundingMode`. Each rounding mode indicates how the least significant returned digit of a rounded result
 * is to be calculated. If fewer digits are returned than the digits needed to represent the exact numerical result,
 * the discarded digits are referred to as the *discarded fraction*, regardless of the digits' contribution to the
 * value of the number. In other words, considered as a numerical value, the discarded fraction could have an absolute
 * value greater than one.
 *
 * Each rounding mode description includes a table listing how different two-digit decimal values would round to a
 * one-digit decimal value under the rounding mode in question. The results can be obtained by creating a
 * [BigDecimal] with the specified value, and calling [BigDecimal.round] on it with a [MathContext] whose `precision`
 * is `1` and whose `roundingMode` is the rounding mode in question. The following table summarizes the results of
 * these rounding operations for all rounding modes (`throws` means that an [ArithmeticException] is thrown):
 *
 * | Input | [UP] | [DOWN] | [CEILING] | [FLOOR] | [HALF_UP] | [HALF_DOWN] | [HALF_EVEN] | [UNNECESSARY] |
 * |------:|-----:|-------:|----------:|--------:|----------:|------------:|------------:|--------------:|
 * |   5.5 |    6 |      5 |         6 |       5 |         6 |           5 |           6 |        throws |
 * |   2.5 |    3 |      2 |         3 |       2 |         3 |           2 |           2 |        throws |
 * |   1.6 |    2 |      1 |         2 |       1 |         2 |           2 |           2 |        throws |
 * |   1.1 |    2 |      1 |         2 |       1 |         1 |           1 |           1 |        throws |
 * |   1.0 |    1 |      1 |         1 |       1 |         1 |           1 |           1 |             1 |
 * |  -1.0 |   -1 |     -1 |        -1 |      -1 |        -1 |          -1 |          -1 |            -1 |
 * |  -1.1 |   -2 |     -1 |        -1 |      -2 |        -1 |          -1 |          -1 |        throws |
 * |  -1.6 |   -2 |     -1 |        -1 |      -2 |        -2 |          -2 |          -2 |        throws |
 * |  -2.5 |   -3 |     -2 |        -2 |      -3 |        -3 |          -2 |          -2 |        throws |
 * |  -5.5 |   -6 |     -5 |        -5 |      -6 |        -6 |          -5 |          -6 |        throws |
 *
 * Each rounding mode also has a legacy integer code, its [value], accepted by the deprecated
 * `BigDecimal.setScale(Int, Int)` overload and by [RoundingMode.valueOf].
 *
 * @property value the legacy integer code of this rounding mode, from `0` ([UP]) to `7` ([UNNECESSARY]), as the
 * `BigDecimal.ROUND_*` constants of Java.
 * @see BigDecimal
 * @see MathContext
 * @author Josh Bloch
 * @author Mike Cowlishaw
 * @author Joseph D. Darcy
 */
@Suppress("DEPRECATION")
@JsExport
enum class RoundingMode(
    @JsName("value") val value: Int,
) {
    /**
     * Rounding mode to round away from zero. Always increments the digit prior to a non-zero discarded fraction. Note
     * that this rounding mode never decreases the magnitude of the calculated value.
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   6 |   3 |   2 |   2 |   1 |   -1 |   -2 |   -2 |   -3 |   -6 |
     */
    UP(0),

    /**
     * Rounding mode to round towards zero. Never increments the digit prior to a discarded fraction (i.e.,
     * truncates). Note that this rounding mode never increases the magnitude of the calculated value.
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   5 |   2 |   1 |   1 |   1 |   -1 |   -1 |   -1 |   -2 |   -5 |
     */
    DOWN(1),

    /**
     * Rounding mode to round towards positive infinity. If the result is positive, behaves as for [UP]; if negative,
     * behaves as for [DOWN]. Note that this rounding mode never decreases the calculated value.
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   6 |   3 |   2 |   2 |   1 |   -1 |   -1 |   -1 |   -2 |   -5 |
     */
    CEILING(2),

    /**
     * Rounding mode to round towards negative infinity. If the result is positive, behaves as for [DOWN]; if
     * negative, behaves as for [UP]. Note that this rounding mode never increases the calculated value.
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   5 |   2 |   1 |   1 |   1 |   -1 |   -2 |   -2 |   -3 |   -6 |
     */
    FLOOR(3),

    /**
     * Rounding mode to round towards the "nearest neighbor", unless both neighbors are equidistant, in which case
     * round up. Behaves as for [UP] if the discarded fraction is ≥ 0.5; otherwise, behaves as for [DOWN]. Note that
     * this is the rounding mode commonly taught at school.
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   6 |   3 |   2 |   1 |   1 |   -1 |   -1 |   -2 |   -3 |   -6 |
     */
    HALF_UP(4),

    /**
     * Rounding mode to round towards the "nearest neighbor", unless both neighbors are equidistant, in which case
     * round down. Behaves as for [UP] if the discarded fraction is > 0.5; otherwise, behaves as for [DOWN].
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   5 |   2 |   2 |   1 |   1 |   -1 |   -1 |   -2 |   -2 |   -5 |
     */
    HALF_DOWN(5),

    /**
     * Rounding mode to round towards the "nearest neighbor", unless both neighbors are equidistant, in which case
     * round towards the even neighbor. Behaves as for [HALF_UP] if the digit to the left of the discarded fraction is
     * odd; behaves as for [HALF_DOWN] if it's even. Note that this is the rounding mode that statistically minimizes
     * cumulative error when applied repeatedly over a sequence of calculations. It is sometimes known as "Banker's
     * rounding", and is chiefly used in the USA. This rounding mode is analogous to the rounding policy used for
     * [Float] and [Double] arithmetic.
     *
     * | Input  | 5.5 | 2.5 | 1.6 | 1.1 | 1.0 | -1.0 | -1.1 | -1.6 | -2.5 | -5.5 |
     * |--------|----:|----:|----:|----:|----:|-----:|-----:|-----:|-----:|-----:|
     * | Result |   6 |   2 |   2 |   1 |   1 |   -1 |   -1 |   -2 |   -2 |   -6 |
     */
    HALF_EVEN(6),

    /**
     * Rounding mode to assert that the requested operation has an exact result, hence no rounding is necessary. If
     * this rounding mode is specified on an operation that yields an inexact result, an [ArithmeticException] is
     * thrown (`throws` below).
     *
     * | Input  | 5.5    | 2.5    | 1.6    | 1.1    | 1.0 | -1.0 | -1.1   | -1.6   | -2.5   | -5.5   |
     * |--------|-------:|-------:|-------:|-------:|----:|-----:|-------:|-------:|-------:|-------:|
     * | Result | throws | throws | throws | throws |   1 |   -1 | throws | throws | throws | throws |
     */
    UNNECESSARY(7),
    ;

    companion object {
        /**
         * Returns the [RoundingMode] corresponding to a legacy integer rounding mode code, i.e. the one whose
         * [value] is [rm].
         *
         * @param rm legacy integer rounding mode code to convert, from `0` ([UP]) to `7` ([UNNECESSARY]).
         * @return the [RoundingMode] corresponding to [rm].
         * @throws IllegalArgumentException if [rm] is out of range, as Java's `RoundingMode.valueOf(int)`.
         */
        @JvmStatic
        @JsName("valueOfInt")
        fun valueOf(rm: Int): RoundingMode =
            entries.getOrNull(rm) ?: throw IllegalArgumentException("argument out of range: $rm")
    }
}
