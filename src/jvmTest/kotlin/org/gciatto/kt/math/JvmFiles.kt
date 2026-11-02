package org.gciatto.kt.math

import java.io.File

internal actual fun differentialCasesPath(): String? = System.getenv("KT_MATH_DIFFERENTIAL_CASES")

internal actual fun readTextFile(path: String): String = File(path).readText()
