@file:Suppress("UnstableApiUsage")

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

pluginManagement {
    repositories {
        google()
        // Google mirror of Maven Central, used for resolution only (avoids Maven Central rate-limits)
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity") version "4.6.1"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.1.28"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

fun tag(
    format: String,
    vararg properties: String,
): String = format.format(*properties.map { System.getProperty(it) ?: error("Missing property $it") }.toTypedArray())

val ci = !System.getenv("CI").isNullOrBlank()
val ciTag = if (ci) "CI" else "Local"
val osTag = "OS: " + tag("%s (%s) v. %s", "os.name", "os.arch", "os.version")
val jvmTag = "JVM: " + tag("%s v. %s", "java.vm.name", "java.vm.version")
val whenTag = "When: ${ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)}"

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        tag(ciTag)
        tag(osTag)
        tag(jvmTag)
        tag(whenTag)
        uploadInBackground = !ci
        publishing.onlyIf { it.buildResult.failures.isNotEmpty() }
        buildScanPublished {
            if (ci) {
                println("::error title=Gradle scan for $osTag, $jvmTag, $whenTag::$buildScanUri")
                // collected into the CI run summary by .github/scripts/ci-summary.py
                File(settingsDir, "build/gradle-scans.txt").apply { parentFile.mkdirs() }.appendText("$buildScanUri\n")
            }
        }
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    createHooks(true)
}

rootProject.name = "kt-math"

include(":documentation")
