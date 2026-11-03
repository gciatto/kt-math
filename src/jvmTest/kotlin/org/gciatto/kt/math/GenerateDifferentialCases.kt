package org.gciatto.kt.math

import java.io.File

/**
 * Writes every [DifferentialOps] case, with its outcome on the JVM (where [BigInteger.of]/[BigDecimal.of] delegate to
 * `java.math`), to the file given as the first argument: one tab-separated line per case, the operation name, its
 * arguments, then the expected outcome. Run by the `generateDifferentialCases` Gradle task, read by the JS tests.
 */
fun main(args: Array<String>) {
    val cases = DifferentialCases().run { integerCases() + decimalCases() + parsingCases() }
    File(args.single()).apply { parentFile.mkdirs() }.bufferedWriter().use { out ->
        for (case in cases) {
            out.append(case.joinToString("\t")).append('\t').append(DifferentialOps.outcome(case[0], case.drop(1)))
            out.newLine()
        }
    }
    println("Wrote ${cases.size} differential cases to ${args.single()}")
}
