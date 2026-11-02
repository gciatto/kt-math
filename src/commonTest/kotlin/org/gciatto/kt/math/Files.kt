package org.gciatto.kt.math

/** The value of the `KT_MATH_DIFFERENTIAL_CASES` environment variable, if set. */
internal expect fun differentialCasesPath(): String?

internal expect fun readTextFile(path: String): String
