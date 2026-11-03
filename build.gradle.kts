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
    // https://kotl.in/native-targets-tiers): they share with JS the actuals using the Kotlin implementation
    // (nonJvmMain)
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
            // Flat on purpose: nesting the "native" group in here makes the native metadata compilation see
            // the stdlib twice (a KLIB loader warning, hence an error with allWarningsAsErrors)
            group("nonJvm") {
                withJs()
                withNative()
            }
        }
    }
}

// kt-mpp's detektAll only covers the JVM and JS source sets: also analyse the native ones (commonMain, nonJvmMain and
// nativeMain, through one native target)
tasks.named("detektAll") {
    dependsOn("detektLinuxX64Main", "detektLinuxX64Test")
}

kotlin {
    // The C kernels of CommonBigInteger (src/nativeInterop/cinterop/kernels.def): cinterop for Apple targets needs
    // their SDKs, hence a macOS host with Xcode, also to publish
    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops.create("kernels")
    }

    sourceSets {
        jvmTest {
            dependencies {
                // the original Java library ported in org.gciatto.kt.math.big: the oracle of TestAgainstBigMath
                implementation(libs.big.math)
                // TestJavaUsageCoverage tells public declarations from internal ones
                implementation(libs.kotlin.reflect)
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

// Everything attached to the GitHub release (see release.config.js): the JVM jars, the npm tarball, and a zip of the
// C shared library and header of each desktop native target
val releaseAssets =
    tasks.register("releaseAssets") {
        group = "distribution"
        description = "Builds the files attached to the GitHub release"
        dependsOn("jvmJar", "jvmSourcesJar", "packJsPackage")
    }

// The C API (the shared library and header Kotlin/Native generates) as C code sees it: src/nativeUsageTest/c, compiled
// with the host's `cc` against the library of the host's target, then run
val usageLibraryTargets = setOf("linuxX64", "macosArm64")
val sharedLibraryTargets = usageLibraryTargets + setOf("linuxArm64", "mingwX64")
kotlin.targets
    .withType<KotlinNativeTarget>()
    .matching { it.name in sharedLibraryTargets }
    .configureEach {
        val buildTypes =
            listOf(NativeBuildType.RELEASE) + listOf(NativeBuildType.DEBUG).filter { name in usageLibraryTargets }
        binaries.sharedLib("ktmath", buildTypes)
        val library = binaries.getSharedLib("ktmath", NativeBuildType.RELEASE)
        val zip =
            tasks.register<Zip>("zipKtmathReleaseShared${targetName.replaceFirstChar { it.uppercase() }}") {
                group = "distribution"
                description = "Zips the C shared library and header of $targetName"
                from(library.linkTaskProvider.flatMap { it.destinationDirectory })
                archiveClassifier.set(targetName)
            }
        releaseAssets { dependsOn(zip) }
    }
kotlin.targets
    .withType<KotlinNativeTarget>()
    .firstOrNull { it.name in usageLibraryTargets && it.konanTarget == HostManager.host }
    ?.let { target ->
        val library = target.binaries.getSharedLib("ktmath", NativeBuildType.DEBUG)
        val source = layout.projectDirectory.file("src/nativeUsageTest/c/usage.c")
        val executable = layout.buildDirectory.file("nativeUsageTest/usage")
        val compile =
            tasks.register<Exec>("compileNativeUsageTest") {
                dependsOn(library.linkTaskProvider)
                inputs.file(source)
                outputs.file(executable)
                doFirst {
                    val dir = library.outputDirectory.absolutePath
                    executable
                        .get()
                        .asFile.parentFile
                        .mkdirs()
                    commandLine(
                        listOf("cc", "-Wall", "-Werror", "-I", dir, "-o", executable.get().asFile.absolutePath) +
                            listOf(source.asFile.absolutePath, "-L", dir, "-lktmath", "-Wl,-rpath,$dir"),
                    )
                }
            }
        // usage.c must call every function of the header that C code can call: api_coverage.py checks it
        val coverage =
            tasks.register<Exec>("nativeUsageCoverage") {
                dependsOn(library.linkTaskProvider)
                val script = layout.projectDirectory.file("src/nativeUsageTest/api_coverage.py")
                inputs.files(script, source)
                doFirst {
                    val header = library.outputDirectory.resolve("libktmath_api.h")
                    commandLine("python3", script.asFile.absolutePath, header.absolutePath, source.asFile.absolutePath)
                }
            }
        val run =
            tasks.register<Exec>("nativeUsageTest") {
                group = "verification"
                description = "Runs the C usage test against the shared library of the host's native target"
                dependsOn(compile, coverage)
                commandLine(executable.get().asFile.absolutePath)
            }
        tasks.named("check") { dependsOn(run) }
    }

// The JS API as JavaScript and TypeScript see it: src/jsUsageTest, next to the npm package as published
// (assembleJsPackage) in node_modules/kt-math, so that the tests `import ... from "kt-math"` as users do
val jsUsageTestDir = layout.buildDirectory.dir("jsUsageTest")
val prepareJsUsageTest =
    tasks.register<Sync>("prepareJsUsageTest") {
        dependsOn("assembleJsPackage")
        from(layout.projectDirectory.dir("src/jsUsageTest"))
        from(layout.buildDirectory.dir("packages/js")) { into("node_modules/${project.name}") }
        into(jsUsageTestDir)
    }

// usage.mts type-checked (not run) with the TypeScript compiler of Kotlin's own npm dependencies
val tsUsageTest =
    tasks.register<Exec>("tsUsageTest") {
        group = "verification"
        description = "Type-checks the TypeScript usage test against the definitions of the npm package"
        dependsOn(prepareJsUsageTest, "kotlinNpmInstall", "kotlinNodeJsSetup")
        val tsc = rootProject.layout.buildDirectory.file("js/node_modules/typescript/bin/tsc")
        val node = project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().executable
        doFirst {
            commandLine(node.get(), tsc.get().asFile.absolutePath, "-p", jsUsageTestDir.get().file("tsconfig.json"))
        }
    }

// *.test.mjs run on Node's test runner
val jsUsageTest =
    tasks.register<Exec>("jsUsageTest") {
        group = "verification"
        description = "Runs the JavaScript usage tests against the npm package"
        dependsOn(tsUsageTest, "kotlinNodeJsSetup")
        val node = project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().executable
        doFirst {
            val tests = jsUsageTestDir.get().asFileTree.matching { include("*.test.mjs") }
            commandLine(listOf(node.get(), "--test") + tests.map { it.absolutePath })
        }
    }
tasks.named("check") { dependsOn(jsUsageTest) }

// TypeScript definitions (.d.ts) of the @JsExport-ed API, shipped in the npm package
kotlin.js().generateTypeScriptDefinitions()
kotlin.js().useEsModules()

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
