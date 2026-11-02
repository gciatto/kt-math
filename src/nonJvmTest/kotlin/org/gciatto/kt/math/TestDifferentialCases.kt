package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.test.fail

/** The value of the `KT_MATH_DIFFERENTIAL_CASES` environment variable, if set. */
internal expect fun differentialCasesPath(): String?

internal expect fun readTextFile(path: String): String

/**
 * Runs, on JS and native targets, the [DifferentialOps] cases generated on the JVM (`generateDifferentialCases`
 * Gradle task, which passes the file path via the `KT_MATH_DIFFERENTIAL_CASES` environment variable) on the Kotlin
 * implementation, and compares outcomes with the `java.math` ones.
 */
class TestDifferentialCases {
    @Test
    fun commonImplementationMatchesJavaMath() {
        val path: String =
            differentialCasesPath() ?: fail("KT_MATH_DIFFERENTIAL_CASES is not set: run the tests through Gradle")
        val text = readTextFile(path)
        val failures = mutableMapOf<String, MutableList<String>>()
        var count = 0
        for (line in text.lineSequence().filter { it.isNotEmpty() }) {
            val fields = line.split('\t')
            val (op, args, expected) = Triple(fields.first(), fields.subList(1, fields.size - 1), fields.last())
            val actual = DifferentialOps.outcome(op, args)
            count++
            if (actual != expected) {
                failures.getOrPut(op) { mutableListOf() } +=
                    "$op${args.map(::abbreviate)}: expected <${abbreviate(expected)}>, got <${abbreviate(actual)}>"
            }
        }
        if (count == 0) fail("No differential cases in $path")
        if (failures.isNotEmpty()) {
            // One example per operation, so a single run shows every broken operation.
            val report =
                failures.entries.joinToString("\n") { (op, cases) ->
                    "[${cases.size}x $op] ${cases.first().take(MAX_EXAMPLE_LENGTH)}"
                }
            fail("${failures.values.sumOf { it.size }} of $count cases differ from java.math:\n$report")
        }
    }

    // Long numbers are shown as their first and last digits, and length.
    private fun abbreviate(s: String): String =
        if (s.length <= MAX_FIELD_LENGTH) s else "${s.take(FIELD_ENDS)}...${s.takeLast(FIELD_ENDS)} (${s.length} chars)"

    private companion object {
        const val MAX_FIELD_LENGTH = 60
        const val FIELD_ENDS = 20
        const val MAX_EXAMPLE_LENGTH = 400
    }
}
