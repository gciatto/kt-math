import io.github.gciatto.kt.mpp.utils.log
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
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

// Shared by every multiplatform project of the build: kt-math and its modules (e.g. big-math)
allprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
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
    }

    // kt-mpp's detektAll only covers the JVM and JS source sets: also analyse the native ones (commonMain, nonJvmMain
    // and nativeMain, through one native target)
    tasks.matching { it.name == "detektAll" }.configureEach {
        dependsOn("detektLinuxX64Main", "detektLinuxX64Test")
    }
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

// The C API (the shared library and header Kotlin/Native generates) as C code sees it: src/nativeUsageTest/c, compiled
// with the host's `cc` against the library of the host's target, then run
val usageLibraryTargets = setOf("linuxX64", "macosArm64")
kotlin.targets
    .withType<KotlinNativeTarget>()
    .matching { it.name in usageLibraryTargets }
    .configureEach { binaries.sharedLib("ktmath", listOf(NativeBuildType.DEBUG)) }
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
        val run =
            tasks.register<Exec>("nativeUsageTest") {
                group = "verification"
                description = "Runs the C usage test against the shared library of the host's native target"
                dependsOn(compile)
                commandLine(executable.get().asFile.absolutePath)
            }
        tasks.named("check") { dependsOn(run) }
    }

// The JS API as plain JavaScript sees it: src/jsUsageTest runs on Node's test runner against the npm package as
// published (assembleJsPackage)
// The TypeScript definitions as TypeScript sees them: src/jsUsageTest/usage.ts, type-checked (not run) with the
// TypeScript compiler of Kotlin's own npm dependencies
val tsUsageTest =
    tasks.register<Exec>("tsUsageTest") {
        group = "verification"
        description = "Type-checks the TypeScript usage test against the definitions of the npm package"
        dependsOn("assembleJsPackage", "kotlinNpmInstall", "kotlinNodeJsSetup")
        val tests = layout.projectDirectory.dir("src/jsUsageTest")
        inputs.dir(tests)
        val tsc = rootProject.layout.buildDirectory.file("js/node_modules/typescript/bin/tsc")
        val node = project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().executable
        doFirst {
            commandLine(
                node.get(),
                tsc.get().asFile.absolutePath,
                "-p",
                tests.file("tsconfig.json").asFile.absolutePath,
            )
        }
    }

val jsUsageTest =
    tasks.register<Exec>("jsUsageTest") {
        dependsOn(tsUsageTest)
        group = "verification"
        description = "Runs the plain-JavaScript usage tests against the npm package"
        dependsOn("assembleJsPackage", "kotlinNodeJsSetup")
        val tests = layout.projectDirectory.dir("src/jsUsageTest")
        val jsPackage = layout.buildDirectory.file("packages/js/${project.name}.js")
        inputs.dir(tests)
        val node = project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().executable
        doFirst {
            environment("KT_MATH_JS_PACKAGE", jsPackage.get().asFile.absolutePath)
            commandLine(
                listOf(node.get(), "--test") +
                    tests.asFileTree.matching { include("**/*.test.cjs") }.map { it.absolutePath },
            )
        }
    }
tasks.named("check") { dependsOn(jsUsageTest) }

// TypeScript definitions (.d.ts) of the @JsExport-ed API, shipped in the npm package
kotlin.js().generateTypeScriptDefinitions()

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
