import io.github.gciatto.kt.mpp.utils.log

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
    buildMetadataSeparator.set("-")
}

log("version: $version", LogLevel.LIFECYCLE)

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlin.reflect)
            }
        }
    }
}

ktlint {
    filter {
        exclude("standard:backing-property-naming")
    }
}
