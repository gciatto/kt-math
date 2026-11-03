package org.gciatto.kt.math

import kotlin.js.JsExport

/**
 * The closed range of [BigInteger]s from [start] to [endInclusive], both included, iterable with step 1:
 * what `a..b` returns for [BigInteger]s `a` and `b` (see [BigInteger.rangeTo]).
 * It is empty if [endInclusive] is less than [start].
 *
 * @property start the lower bound of the range (included).
 * @property endInclusive the upper bound of the range (included).
 */
@JsExport
data class BigIntegerRange(
    override val start: BigInteger,
    override val endInclusive: BigInteger,
) : ClosedRange<BigInteger>,
    Iterable<BigInteger> {
    /** Returns an iterator over the values of this range, in increasing order from [start] to [endInclusive]. */
    @JsExport.Ignore // a Kotlin Iterator is no JS iterator: see toArray
    override fun iterator(): Iterator<BigInteger> =
        object : Iterator<BigInteger> {
            private var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): BigInteger {
                val temp = current
                current += BigInteger.ONE
                return temp
            }
        }

    /** The first value of this range, [start] (which, as an override, JS can't see). */
    val first: BigInteger get() = start

    /** The last value of this range, [endInclusive] (which, as an override, JS can't see). */
    val last: BigInteger get() = endInclusive

    /** Returns the values of this range, in increasing order from [start] to [endInclusive]: an array in JS. */
    fun toArray(): Array<BigInteger> = toList().toTypedArray()
}
