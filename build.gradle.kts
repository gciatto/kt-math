import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.npmPublish)
    alias(libs.plugins.dokka)
    `publish-on-maven`
}

group = "io.github.gciatto"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    assignGitSemanticVersion()
}

println("${rootProject.name} version: $version")

repositories {
    mavenCentral()
}

val mochaTimeout: String by project
val jvmTarget = JavaVersion.VERSION_1_8

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = jvmTarget.toString()
                freeCompilerArgs = listOf("-Xjvm-default=all")
            }
        }
    }

    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = mochaTimeout
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }
}

java {
    sourceCompatibility = jvmTarget
    targetCompatibility = jvmTarget
}

tasks.withType<DokkaTask>().matching { "Html" in it.name }.all {
    val dokkaHtml = this
    tasks.create<Jar>("dokkaHtmlJar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        dependsOn(dokkaHtml)
    }
}
