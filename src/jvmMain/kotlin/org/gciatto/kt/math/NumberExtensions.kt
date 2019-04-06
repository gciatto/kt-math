package org.gciatto.kt.math

fun Long.numberOfLeadingZeros(): Int {
    // HD, Figure 5-6
    if (this == 0L)
        return 64
    var n = 1
    var x = this.ushr(32).toInt()
    if (x == 0) {
        n += 32
        x = this.toInt()
    }
    if (x.ushr(16) == 0) {
        n += 16
        x = x shl 16
    }
    if (x.ushr(24) == 0) {
        n += 8
        x = x shl 8
    }
    if (x.ushr(28) == 0) {
        n += 4
        x = x shl 4
    }
    if (x.ushr(30) == 0) {
        n += 2
        x = x shl 2
    }
    n -= x.ushr(31)
    return n
}
