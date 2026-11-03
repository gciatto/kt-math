package org.gciatto.kt.math

import kotlin.test.fail

/**
 * The base of the differential tests against an [oracle] (`java.math`, big-math): outcomes (results or exception
 * types) are rendered as strings and compared, mismatches reported at the end, one example per operation.
 */
open class DifferentialTest(
    private val oracle: String,
    private val maxExampleLength: Int,
) {
    private val failures = mutableListOf<Pair<String, String>>()

    protected fun outcome(f: () -> Any?): String =
        try {
            render(f())
        } catch (e: RuntimeException) {
            "throws ${e::class.simpleName}"
        }

    protected open fun render(x: Any?): String =
        when (x) {
            is Array<*> -> x.joinToString(prefix = "[", postfix = "]") { render(it) }
            is Sequence<*> -> x.joinToString(prefix = "[", postfix = "]") { render(it) }
            is Pair<*, *> -> "(${render(x.first)}, ${render(x.second)})"
            is ByteArray -> x.contentToString()
            is BigDecimal -> "$x@${x.scale}"
            else -> x.toString()
        }

    /** Records a mismatch if [actual] has another outcome than [expected], the oracle's. */
    protected fun compare(
        what: String,
        expected: () -> Any?,
        actual: () -> Any?,
    ) {
        val expectedOutcome = outcome(expected)
        val actualOutcome = outcome(actual)
        if (expectedOutcome != actualOutcome) {
            failures += what.substringBefore('(') to "$what: expected <$expectedOutcome>, got <$actualOutcome>"
        }
    }

    protected fun assertNoFailures() {
        if (failures.isEmpty()) return
        // One example per operation, so a single run shows every broken operation.
        val report =
            failures.groupBy({ it.first }, { it.second }).entries.joinToString("\n") { (op, cases) ->
                "[${cases.size}x $op] ${cases.first().take(maxExampleLength)}"
            }
        fail("${failures.size} mismatches against $oracle:\n$report")
    }
}
