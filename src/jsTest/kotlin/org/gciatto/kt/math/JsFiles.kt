package org.gciatto.kt.math

internal actual fun differentialCasesPath(): String? = js("process.env.KT_MATH_DIFFERENTIAL_CASES") as String?

internal actual fun readTextFile(path: String): String = js("require('fs')").readFileSync(path, "utf8") as String
