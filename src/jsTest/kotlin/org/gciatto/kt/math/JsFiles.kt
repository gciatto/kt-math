package org.gciatto.kt.math

internal actual fun differentialCasesPath(): String? = js("process.env.KT_MATH_DIFFERENTIAL_CASES") as String?

// the tests are ES modules, without require
internal actual fun readTextFile(path: String): String =
    js("process.getBuiltinModule('fs')").readFileSync(path, "utf8") as String
