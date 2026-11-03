@file:OptIn(ExperimentalForeignApi::class)

package org.gciatto.kt.math

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import org.gciatto.kt.math.kernels.ktm_montgomery_multiply
import org.gciatto.kt.math.kernels.ktm_multiply_to_len
import org.gciatto.kt.math.kernels.ktm_square_to_len

internal actual fun logImpl(lazyObject: () -> Any) {
    println(lazyObject())
}

internal actual inline fun <T, reified U : T> T?.castTo(): U = this as U

// The C kernels of src/nativeInterop/cinterop/kernels.def
internal actual fun multiplyToLenKernel(
    x: IntArray,
    xlen: Int,
    y: IntArray,
    ylen: Int,
    z: IntArray,
): Boolean = ktm_multiply_to_len(x.refTo(0), xlen, y.refTo(0), ylen, z.refTo(0)) != 0

internal actual fun squareToLenKernel(
    x: IntArray,
    len: Int,
    z: IntArray,
): Boolean = ktm_square_to_len(x.refTo(0), len, z.refTo(0)) != 0

internal actual fun montgomeryMultiplyKernel(
    a: IntArray,
    b: IntArray,
    n: IntArray,
    len: Int,
    inv: Long,
    product: IntArray,
): Boolean = ktm_montgomery_multiply(a.refTo(0), b.refTo(0), n.refTo(0), len, inv, product.refTo(0)) != 0
