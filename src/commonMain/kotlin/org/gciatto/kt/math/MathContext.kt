/*
 * Copyright (c) 2003, 2007, Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyright IBM Corporation, 1997, 2001. All Rights Reserved.
 */

package org.gciatto.kt.math

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.js.JsStatic
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

/**
 * Immutable context settings describing certain rules for numerical operators, such as those of [BigDecimal], as
 * Java's `java.math.MathContext`.
 *
 * The settings are:
 * 1. [precision]: the number of digits to be used for an operation; results are rounded to this precision;
 * 2. [roundingMode]: the [RoundingMode] specifying the algorithm to be used for rounding.
 *
 * @property precision the number of digits to be used for an operation (non-negative): results are rounded to this
 * precision. `0` means unlimited precision: operations then compute exact results, or throw an [ArithmeticException]
 * if they can't (e.g. a non-terminating decimal expansion).
 * @property roundingMode the rounding mode to use when rounding results to [precision].
 * @constructor Creates a [MathContext] with the given [precision] and [roundingMode], by default 9 digits and
 * [RoundingMode.HALF_UP].
 * @throws IllegalArgumentException if [precision] is negative.
 * @see BigDecimal
 * @see RoundingMode
 * @author Mike Cowlishaw
 * @author Joseph D. Darcy
 */
@JsExport
data class MathContext
    @JvmOverloads // new MathContext(precision) in Java, as java.math's
    constructor(
        val precision: Int = 9,
        val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    ) {
        init {
            require(precision >= 0)
        }

        companion object {
            /**
             * A [MathContext] whose settings have the values required for unlimited precision arithmetic:
             * `precision = 0` and `roundingMode = HALF_UP`.
             */
            @JvmField
            @JsName("UNLIMITED")
            @JsStatic
            val UNLIMITED = MathContext(0, RoundingMode.HALF_UP)

            /**
             * A [MathContext] with a precision setting
             * matching the IEEE 754-2008 Decimal32 format, 7 digits, and a
             * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
             * IEEE 754-2008 default.
             */
            @JvmField
            @JsName("DECIMAL32")
            @JsStatic
            val DECIMAL32 = MathContext(7, RoundingMode.HALF_EVEN)

            /**
             * A [MathContext] with a precision setting
             * matching the IEEE 754-2008 Decimal64 format, 16 digits, and a
             * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
             * IEEE 754-2008 default.
             */
            @JvmField
            @JsName("DECIMAL64")
            @JsStatic
            val DECIMAL64 = MathContext(16, RoundingMode.HALF_EVEN)

            /**
             * A [MathContext] with a precision setting
             * matching the IEEE 754-2008 Decimal128 format, 34 digits, and a
             * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
             * IEEE 754-2008 default.
             */
            @JvmField
            @JsName("DECIMAL128")
            @JsStatic
            val DECIMAL128 = MathContext(34, RoundingMode.HALF_EVEN)
        }
    }
