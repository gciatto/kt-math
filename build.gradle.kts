import io.github.gciatto.kt.mpp.log
import io.github.gciatto.kt.mpp.nodeVersion

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.ktMpp.multiplatform)
    alias(libs.plugins.ktMpp.versions)
    alias(libs.plugins.ktMpp.linter)
//    alias(libs.plugins.ktMpp.bugFinder)
    alias(libs.plugins.ktMpp.documentation)
    alias(libs.plugins.ktMpp.mavenPublish)
    alias(libs.plugins.ktMpp.npmPublish)
}

group = "io.github.gciatto"

gitSemVer {
    excludeLightweightTags()
    assignGitSemanticVersion()
}

log("version: $version", LogLevel.LIFECYCLE)

repositories {
    mavenCentral()
}

kotlin {
    js {
        nodejs {
            binaries.library()
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlin.reflect)
            }
        }
    }
}

project.findProperty("nodeVersion")?.let { it.toString() }?.takeIf { it.isNotBlank() }?.let {
    nodeVersion(it)
    log("override NodeJS version: $it", LogLevel.LIFECYCLE)
}
