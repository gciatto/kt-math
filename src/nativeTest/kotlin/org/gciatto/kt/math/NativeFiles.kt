package org.gciatto.kt.math

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.posix.getenv
import kotlin.test.fail

@OptIn(ExperimentalForeignApi::class)
internal actual fun differentialCasesPath(): String? = getenv("KT_MATH_DIFFERENTIAL_CASES")?.toKString()

@OptIn(ExperimentalForeignApi::class)
internal actual fun readTextFile(path: String): String {
    val file = fopen(path, "r") ?: fail("Cannot open $path")
    try {
        return buildString {
            memScoped {
                val buffer = allocArray<ByteVar>(BUFFER_SIZE)
                while (true) {
                    append(fgets(buffer, BUFFER_SIZE, file)?.toKString() ?: break)
                }
            }
        }
    } finally {
        fclose(file)
    }
}

private const val BUFFER_SIZE = 1 shl 16
