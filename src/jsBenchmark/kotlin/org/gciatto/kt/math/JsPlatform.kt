package org.gciatto.kt.math

internal actual val platform: String = "Node.js ${js("process.version")} (BigInt)"
