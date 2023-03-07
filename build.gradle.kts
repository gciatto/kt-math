import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.gitSemVer)
    `publish-on-maven`
    `publish-on-npm`
    `print-versions`
}

group = "io.github.gciatto"

gitSemVer {
    excludeLightweightTags()
    assignGitSemanticVersion()
}

logger.log(LogLevel.LIFECYCLE, "${rootProject.name} version: $version")

repositories {
    mavenCentral()
}

jvmVersion(libs.versions.jvm)
nodeVersion(libs.versions.node, rootProject.findProperty("nodeVersion"))

kotlin {
    js {
        nodejs {
            binaries.library()
        }
    }
}
