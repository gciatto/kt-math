// The kt-mpp plugins are already on the classpath (applied by the root project): no versions here
plugins {
    id(
        libs.plugins.ktMpp.multiplatform
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.linter
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.bugFinder
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.documentation
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.npmPublish
            .get()
            .pluginId,
    )
}

// What kt-mpp's versions plugin does, which can't be applied after the root project is evaluated
group = rootProject.group
version = rootProject.version

repositories {
    // Google mirror of Maven Central, used for resolution only (avoids Maven Central rate-limits)
    maven("https://maven-central.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":"))
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.big.math)
            }
        }
    }
}

// Maven artifacts are named after the project (big-math, big-math-jvm, ...): prefix them with kt-math, as the npm
// package already is (kt-mpp names it rootProject-project)
publishing {
    publications.withType<MavenPublication>().configureEach {
        afterEvaluate {
            artifactId = "${rootProject.name}-$artifactId"
        }
    }
}
