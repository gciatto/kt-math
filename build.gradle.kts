import io.github.gciatto.kt.mpp.utils.log

buildscript {
    configurations.classpath {
        resolutionStrategy {
            // publish-on-central 9.2.5+ bundles a Ktor client compiled against kotlinx-coroutines >= 1.11.0,
            // while Gradle's own classloader always provides kotlinx-coroutines-core 1.10.2: every
            // releaseMavenCentralPortalPublication then crashes with
            // `NoSuchMethodError: Job.invokeOnCompletion$default`.
            // Same workaround as tuProlog/2p-kt. Remove once Gradle bundles coroutines >= 1.11.0, or upstream
            // fixes https://github.com/DanySK/publish-on-central/issues/1804
            force("org.danilopianini:publish-on-central:9.2.4")
        }
    }
}

plugins {
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktMpp.multiplatform)
    alias(libs.plugins.ktMpp.versions)
    alias(libs.plugins.ktMpp.linter)
    alias(libs.plugins.ktMpp.bugFinder)
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
    // Google mirror of Maven Central, used for resolution only (avoids Maven Central rate-limits)
    maven("https://maven-central.storage-download.googleapis.com/maven2/")
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

// Differential tests on JS: the JVM computes the expected outcomes of the DifferentialOps cases with java.math
// (GenerateDifferentialCases, in jvmTest), the JS tests run the same cases on the common implementation.
val differentialCases = layout.buildDirectory.file("differential/cases.tsv")

val generateDifferentialCases by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Computes the expected outcomes of the differential cases on the JVM, for the JS tests"
    val testCompilation = kotlin.jvm().compilations.getByName("test")
    classpath(testCompilation.output.allOutputs, testCompilation.runtimeDependencyFiles)
    mainClass.set("org.gciatto.kt.math.GenerateDifferentialCasesKt")
    outputs.file(differentialCases)
    argumentProviders += CommandLineArgumentProvider { listOf(differentialCases.get().asFile.absolutePath) }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    inputs.files(generateDifferentialCases)
    environment("KT_MATH_DIFFERENTIAL_CASES", differentialCases.get().asFile.absolutePath)
}

kotlin.js().nodejs {
    testTask {
        useMocha {
            timeout = "600s" // the differential cases run in a single test
        }
    }
}

// Run the JVM tests on another JDK (e.g. -PtestJvmVersion=8, as CI does), provisioned via the foojay resolver.
// Azul Zulu, since it ships every Java version for every OS and architecture (e.g. Java 8 for macOS on ARM).
providers.gradleProperty("testJvmVersion").orNull?.let { version ->
    tasks.named<Test>("jvmTest") {
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(version))
                vendor.set(JvmVendorSpec.AZUL)
            },
        )
    }
}
