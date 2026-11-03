@file:OptIn(ExperimentalNativeApi::class)

package org.gciatto.kt.math

import kotlin.experimental.ExperimentalNativeApi

internal actual val platform: String = "Kotlin/Native ${Platform.osFamily} ${Platform.cpuArchitecture}"
