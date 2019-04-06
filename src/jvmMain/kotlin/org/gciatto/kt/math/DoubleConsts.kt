/*
 * Copyright (c) 2003, 2016, Oracle and/or its affiliates. All rights reserved.
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
 */

package org.gciatto.kt.math

/**
 * This class contains additional constants documenting limits of the
 * `double` type.
 *
 * @author Joseph D. Darcy
 */

object DoubleConsts {

    /**
     * The number of logical bits in the significand of a
     * `double` number, including the implicit bit.
     */
    const val SIGNIFICAND_WIDTH = 53

    /**
     * Bias used in representing a `double` exponent.
     */
    const val EXP_BIAS = 1023

    /**
     * Bit mask to isolate the sign bit of a `double`.
     */
    const val SIGN_BIT_MASK = 1L shl 63

    /**
     * Bit mask to isolate the exponent field of a
     * `double`.
     */
    const val EXP_BIT_MASK = 0x7FF0000000000000L

    /**
     * Bit mask to isolate the significand field of a
     * `double`.
     */
    const val SIGNIF_BIT_MASK = 0x000FFFFFFFFFFFFFL

}
/**
 * Don't let anyone instantiate this class.
 */
