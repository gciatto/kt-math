package org.gciatto.kt.math

import kotlin.test.Test
import kotlin.time.ExperimentalTime
import java.math.BigInteger

class JvmIntBenchmark : AbstractIntBenchmark<BigInteger>(JvmIntAlgos) {
    override fun bigInt(value: Int): BigInteger = BigInteger.valueOf(value.toLong())

    @Test
    @ExperimentalTime
    override fun benchmarkSumsViaFibonacci() {
        super.benchmarkSumsViaFibonacci()
    }

    @Test
    @ExperimentalTime
    override fun benchmarkMultiplicationsViaFactorial() {
        super.benchmarkMultiplicationsViaFactorial()
    }
}