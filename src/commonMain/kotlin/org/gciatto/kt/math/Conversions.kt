@file:JvmName("Conversions")
@file:Suppress("TooManyFunctions")

package org.gciatto.kt.math

import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

// Conversions of Kotlin numbers and strings into kt-math's, through BigFactory.default. On the JVM, Kotlin's own
// toBigInteger()/toBigDecimal() (on java.math) are only picked when imported explicitly, or without these.

/** This number as a [BigInteger]. */
fun Int.toBigInteger(): BigInteger = BigFactory.default.bigIntegerOf(this)

/** This number as a [BigInteger]. */
fun Long.toBigInteger(): BigInteger = BigFactory.default.bigIntegerOf(this)

/** Parses this string as a [BigInteger] in [radix], see [BigFactory.parseBigInteger]. */
@JvmOverloads
fun String.toBigInteger(radix: Int = BigFactory.DEFAULT_RADIX): BigInteger =
    BigFactory.default.parseBigInteger(this, radix)

/** Parses this string as a [BigInteger] in [radix], or returns `null`, see [BigFactory.parseBigIntegerOrNull]. */
@JvmOverloads
fun String.toBigIntegerOrNull(radix: Int = BigFactory.DEFAULT_RADIX): BigInteger? =
    BigFactory.default.parseBigIntegerOrNull(this, radix)

/** This number as a [BigDecimal], rounded according to [context] if any. */
@JvmOverloads
fun Int.toBigDecimal(context: MathContext? = null): BigDecimal = BigFactory.default.bigDecimalOf(this, context)

/** This number as a [BigDecimal], rounded according to [context] if any. */
@JvmOverloads
fun Long.toBigDecimal(context: MathContext? = null): BigDecimal = BigFactory.default.bigDecimalOf(this, context)

/**
 * This number as a [BigDecimal]: its shortest decimal representation (`0.1` is `0.1`), as Kotlin's
 * `Double.toBigDecimal()` on the JVM, see [BigFactory.bigDecimalValueOf].
 */
fun Double.toBigDecimal(): BigDecimal = BigFactory.default.bigDecimalValueOf(this)

/** Parses this string as a [BigDecimal], see [BigFactory.parseBigDecimal]. */
@JvmOverloads
fun String.toBigDecimal(context: MathContext? = null): BigDecimal = BigFactory.default.parseBigDecimal(this, context)

/** Parses this string as a [BigDecimal], or returns `null`, see [BigFactory.parseBigDecimalOrNull]. */
@JvmOverloads
fun String.toBigDecimalOrNull(context: MathContext? = null): BigDecimal? =
    BigFactory.default.parseBigDecimalOrNull(this, context)

/** This number as a [BigRational]. */
fun Int.toBigRational(): BigRational = BigFactory.default.bigRationalOf(this)

/** This number as a [BigRational]. */
fun Long.toBigRational(): BigRational = BigFactory.default.bigRationalOf(this)

/** This number as a [BigRational]: its shortest decimal representation, exactly (`0.1` is `1/10`). */
fun Double.toBigRational(): BigRational = BigFactory.default.bigRationalOf(this)

/** Parses this string as a [BigRational] (`"1/3"`, `"-1.5"`), see [BigFactory.parseBigRational]. */
fun String.toBigRational(): BigRational = BigFactory.default.parseBigRational(this)

/** Parses this string as a [BigRational], or returns `null`, see [BigFactory.parseBigRationalOrNull]. */
fun String.toBigRationalOrNull(): BigRational? = BigFactory.default.parseBigRationalOrNull(this)
