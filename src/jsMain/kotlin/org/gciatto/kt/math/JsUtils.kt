package org.gciatto.kt.math

internal actual fun logImpl(lazyObject: () -> Any) {
    console.warn(lazyObject())
}

internal actual inline fun <T, reified U : T> T?.castTo(): U = this.unsafeCast<U>()

// No kernels: BigIntegers are native BigInts here (JsBigInteger)
internal actual fun multiplyToLenKernel(
    x: IntArray,
    xlen: Int,
    y: IntArray,
    ylen: Int,
    z: IntArray,
): Boolean = false

internal actual fun squareToLenKernel(
    x: IntArray,
    len: Int,
    z: IntArray,
): Boolean = false

@Suppress("LongParameterList") // implMontgomeryMultiply's
internal actual fun montgomeryMultiplyKernel(
    a: IntArray,
    b: IntArray,
    n: IntArray,
    len: Int,
    inv: Long,
    product: IntArray,
): Boolean = false
