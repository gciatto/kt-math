import io.github.gciatto.kt.mpp.utils.log
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.HostManager

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
    // Every Kotlin/Native target but the deprecated ones (macosX64, watchosX64, tvosX64, see
    // https://kotl.in/native-targets-tiers): they share with JS the actuals using the Kotlin implementation (nonJvmMain)
    macosArm64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()
    linuxX64()
    linuxArm64()
    mingwX64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            // Flat on purpose: nesting the "native" group in here makes the native metadata compilation see the
            // stdlib twice (a KLIB loader warning, hence an error with allWarningsAsErrors)
            group("nonJvm") {
                withJs()
                withNative()
            }
        }
    }

    // The C kernels of CommonBigInteger (src/nativeInterop/cinterop/kernels.def): cinterop for Apple targets needs
    // their SDKs, hence a macOS host with Xcode, also to publish
    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops.create("kernels")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlin.reflect)
            }
        }
    }
}

// Benchmarks (src/commonBenchmark, src/{jvm,js,native}Benchmark): a "benchmark" compilation per target, on top of main,
// run manually with `./gradlew benchmark` (JVM, JS and the native target of the host, e.g. to update the README), which
// prints one Markdown table per platform. Not part of `check`.
val commonBenchmark = kotlin.sourceSets.create("commonBenchmark")
val nativeBenchmark = kotlin.sourceSets.create("nativeBenchmark") { dependsOn(commonBenchmark) }
val benchmarkRuns = mutableListOf<Any>()

kotlin.targets.matching { it.platformType != KotlinPlatformType.common }.all {
    val main = compilations.getByName("main")
    val benchmark =
        compilations.create("benchmark") {
            associateWith(main)
            defaultSourceSet.dependsOn(if (this@all is KotlinNativeTarget) nativeBenchmark else commonBenchmark)
        }
    when (this) {
        is KotlinNativeTarget -> {
            binaries.executable("benchmark", listOf(NativeBuildType.RELEASE)) {
                compilation = compilations.getByName("benchmark")
                entryPoint = "org.gciatto.kt.math.main"
            }
            if (konanTarget == HostManager.host) {
                benchmarkRuns += "runBenchmarkReleaseExecutable${targetName.replaceFirstChar { it.uppercase() }}"
            }
        }
        is KotlinJsIrTarget -> {
            binaries.executable(compilations.getByName("benchmark"))
            benchmarkRuns +=
                tasks.register<Exec>("jsBenchmarkRun") {
                    dependsOn("jsBenchmarkBenchmarkProductionExecutableCompileSync", "kotlinNodeJsSetup")
                    val script =
                        layout.buildDirectory.file(
                            "compileSync/js/benchmark/benchmarkProductionExecutable/kotlin/${project.name}-benchmark.js",
                        )
                    val node = project.the<NodeJsEnvSpec>().executable
                    doFirst { commandLine(node.get(), script.get().asFile.absolutePath) }
                }
        }
        is KotlinJvmTarget -> {
            benchmarkRuns +=
                tasks.register<JavaExec>("jvmBenchmarkRun") {
                    classpath(benchmark.output.allOutputs, benchmark.runtimeDependencyFiles)
                    mainClass.set("org.gciatto.kt.math.BenchmarksKt")
                }
        }
    }
}

tasks.register("benchmark") {
    group = "verification"
    description = "Runs the benchmarks on the JVM, JS and the native target of the host"
    dependsOn(benchmarkRuns)
}

// Differential tests on JS and native: the JVM computes the expected outcomes of the DifferentialOps cases with
// java.math (GenerateDifferentialCases, in jvmTest), the JS and native tests replay them on the Kotlin implementation.
val differentialCases = layout.buildDirectory.file("differential/cases.tsv")

val generateDifferentialCases =
    tasks.register<JavaExec>("generateDifferentialCases") {
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

tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest>().configureEach {
    inputs.files(generateDifferentialCases)
    environment("KT_MATH_DIFFERENTIAL_CASES", differentialCases.get().asFile.absolutePath)
    // Apple simulators: `xcrun simctl` only passes on the variables prefixed with SIMCTL_CHILD_ (prefix stripped)
    environment("SIMCTL_CHILD_KT_MATH_DIFFERENTIAL_CASES", differentialCases.get().asFile.absolutePath)
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

// kt-mpp's detektAll only covers the JVM and JS source sets: also analyse the native ones (commonMain, nonJvmMain and
// nativeMain, through one native target)
tasks.named("detektAll") {
    dependsOn("detektLinuxX64Main", "detektLinuxX64Test")
}
