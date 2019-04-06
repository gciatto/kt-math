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
 */

/*
 * Portions Copyright IBM Corporation, 1997, 2001. All Rights Reserved.
 */

package org.gciatto.kt.math

/**
 * Immutable objects which encapsulate the context settings which
 * describe certain rules for numerical operators, such as those
 * implemented by the [BigDecimal] class.
 *
 *
 * The base-independent settings are:
 *
 *  1. `precision`:
 * the number of digits to be used for an operation; results are
 * rounded to this precision
 *
 *  1. `roundingMode`:
 * a [RoundingMode] object which specifies the algorithm to be
 * used for rounding.
 *
 *
 * @see BigDecimal
 *
 * @see RoundingMode
 *
 * @author  Mike Cowlishaw
 * @author  Joseph D. Darcy
 * @since 1.5
 */

class MathContext {

    /* ----- Shared Properties ----- */
    /**
     * The number of digits to be used for an operation.  A value of 0
     * indicates that unlimited precision (as many digits as are
     * required) will be used.  Note that leading zeros (in the
     * coefficient of a number) are never significant.
     *
     *
     * `precision` will always be non-negative.
     *
     * @serial
     */
    /**
     * Returns the `precision` setting.
     * This value is always non-negative.
     *
     * @return an `int` which is the value of the `precision`
     * setting
     */
    val precision: Int

    /**
     * The rounding algorithm to be used for an operation.
     *
     * @see RoundingMode
     *
     * @serial
     */
    /**
     * Returns the roundingMode setting.
     * This will be one of
     * [RoundingMode.CEILING],
     * [RoundingMode.DOWN],
     * [RoundingMode.FLOOR],
     * [RoundingMode.HALF_DOWN],
     * [RoundingMode.HALF_EVEN],
     * [RoundingMode.HALF_UP],
     * [RoundingMode.UNNECESSARY], or
     * [RoundingMode.UP].
     *
     * @return a `RoundingMode` object which is the value of the
     * `roundingMode` setting
     */

    val roundingMode: RoundingMode?

    /* ----- Constructors ----- */

    /**
     * Constructs a new `MathContext` with the specified
     * precision and the [HALF_UP][RoundingMode.HALF_UP] rounding
     * mode.
     *
     * @param setPrecision The non-negative `int` precision setting.
     * @throws IllegalArgumentException if the `setPrecision` parameter is less
     * than zero.
     */
    constructor(setPrecision: Int) : this(setPrecision, DEFAULT_ROUNDINGMODE) {
        return
    }

    /**
     * Constructs a new `MathContext` with a specified
     * precision and rounding mode.
     *
     * @param setPrecision The non-negative `int` precision setting.
     * @param setRoundingMode The rounding mode to use.
     * @throws IllegalArgumentException if the `setPrecision` parameter is less
     * than zero.
     * @throws NullPointerException if the rounding mode argument is `null`
     */
    constructor(
        setPrecision: Int,
        setRoundingMode: RoundingMode?
    ) {
        if (setPrecision < MIN_DIGITS)
            throw IllegalArgumentException("Digits < 0")
        if (setRoundingMode == null)
            throw NullPointerException("null RoundingMode")

        precision = setPrecision
        roundingMode = setRoundingMode
        return
    }

    /**
     * Constructs a new `MathContext` from a string.
     *
     * The string must be in the same format as that produced by the
     * [.toString] method.
     *
     *
     * An `IllegalArgumentException` is thrown if the precision
     * section of the string is out of range (`< 0`) or the string is
     * not in the format created by the [.toString] method.
     *
     * @param val The string to be parsed
     * @throws IllegalArgumentException if the precision section is out of range
     * or of incorrect format
     * @throws NullPointerException if the argument is `null`
     */
    constructor(`val`: String?) {
        val bad = false
        val setPrecision: Int
        if (`val` == null)
            throw NullPointerException("null String")
        try { // any error here is a string format problem
            if (!`val`.startsWith("precision=")) throw RuntimeException()
            val fence = `val`.indexOf(' ')    // could be -1
            var off = 10                     // where value starts
            setPrecision = Integer.parseInt(`val`.substring(10, fence))

            if (!`val`.startsWith("roundingMode=", fence + 1))
                throw RuntimeException()
            off = fence + 1 + 13
            val str = `val`.substring(off, `val`.length)
            roundingMode = RoundingMode.valueOf(str)
        } catch (re: RuntimeException) {
            throw IllegalArgumentException("bad string format")
        }

        if (setPrecision < MIN_DIGITS)
            throw IllegalArgumentException("Digits < 0")
        // the other parameters cannot be invalid if we got here
        precision = setPrecision
    }

    /**
     * Compares this `MathContext` with the specified
     * `Object` for equality.
     *
     * @param  x `Object` to which this `MathContext` is to
     * be compared.
     * @return `true` if and only if the specified `Object` is
     * a `MathContext` object which has exactly the same
     * settings as this object
     */
    override fun equals(x: Any?): Boolean {
        val mc: MathContext
        if (x !is MathContext)
            return false
        mc = x
        return mc.precision == this.precision && mc.roundingMode == this.roundingMode // no need for .equals()
    }

    /**
     * Returns the hash code for this `MathContext`.
     *
     * @return hash code for this `MathContext`
     */
    override fun hashCode(): Int {
        return this.precision + roundingMode!!.hashCode() * 59
    }

    /**
     * Returns the string representation of this `MathContext`.
     * The `String` returned represents the settings of the
     * `MathContext` object as two space-delimited words
     * (separated by a single space character, `'&#92;u0020'`,
     * and with no leading or trailing white space), as follows:
     *
     *  1.
     * The string `"precision="`, immediately followed
     * by the value of the precision setting as a numeric string as if
     * generated by the [Integer.toString]
     * method.
     *
     *  1.
     * The string `"roundingMode="`, immediately
     * followed by the value of the `roundingMode` setting as a
     * word.  This word will be the same as the name of the
     * corresponding public constant in the [RoundingMode]
     * enum.
     *
     *
     *
     * For example:
     * <pre>
     * precision=9 roundingMode=HALF_UP
    </pre> *
     *
     * Additional words may be appended to the result of
     * `toString` in the future if more properties are added to
     * this class.
     *
     * @return a `String` representing the context settings
     */
    override fun toString(): String {
        return "precision=" + precision + " " +
                "roundingMode=" + roundingMode!!.toString()
    }

    // Private methods

    /**
     * Reconstitute the `MathContext` instance from a stream (that is,
     * deserialize it).
     *
     * @param s the stream being read.
     */
    @Throws(java.io.IOException::class, ClassNotFoundException::class)
    private fun readObject(s: java.io.ObjectInputStream) {
        s.defaultReadObject()     // read in all fields
        // validate possibly bad fields
        if (precision < MIN_DIGITS) {
            val message = "MathContext: invalid digits in stream"
            throw java.io.StreamCorruptedException(message)
        }
        if (roundingMode == null) {
            val message = "MathContext: null roundingMode in stream"
            throw java.io.StreamCorruptedException(message)
        }
    }

    companion object {

        /* ----- Constants ----- */

        // defaults for constructors
        private const val DEFAULT_DIGITS = 9
        private val DEFAULT_ROUNDINGMODE = RoundingMode.HALF_UP
        // Smallest values for digits (Maximum is Integer.MAX_VALUE)
        private const val MIN_DIGITS = 0

        // Serialization version
        private const val serialVersionUID = 5579720004786848255L

        /* ----- Public Properties ----- */
        /**
         * A `MathContext` object whose settings have the values
         * required for unlimited precision arithmetic.
         * The values of the settings are:
         * `
         * precision=0 roundingMode=HALF_UP
        ` *
         */
        val UNLIMITED = MathContext(0, RoundingMode.HALF_UP)

        /**
         * A `MathContext` object with a precision setting
         * matching the IEEE 754R Decimal32 format, 7 digits, and a
         * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
         * IEEE 754R default.
         */
        val DECIMAL32 = MathContext(7, RoundingMode.HALF_EVEN)

        /**
         * A `MathContext` object with a precision setting
         * matching the IEEE 754R Decimal64 format, 16 digits, and a
         * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
         * IEEE 754R default.
         */
        val DECIMAL64 = MathContext(16, RoundingMode.HALF_EVEN)

        /**
         * A `MathContext` object with a precision setting
         * matching the IEEE 754R Decimal128 format, 34 digits, and a
         * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
         * IEEE 754R default.
         */
        val DECIMAL128 = MathContext(34, RoundingMode.HALF_EVEN)
    }

}
