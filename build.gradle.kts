import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform") version "1.3.21"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

group = "org.gciatto"
version = "0.0.1"

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("stdlib-jdk8"))
                }
            }

            mavenPublication {
                artifactId = rootProject.name + "-jvm"
                groupId = rootProject.group.toString()
                version = rootProject.version.toString()
            }

            // JVM-specific tests and their dependencies:
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
        }

        js {
            sequenceOf("", "Test").forEach {
                tasks.getByName<KotlinJsCompile>("compile${it}KotlinJs") {
                    kotlinOptions {
                        moduleKind = "umd"
                        noStdlib = true
                        metaInfo = true
                    }
                }
            }
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                }
            }
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }

            mavenPublication {
                artifactId = rootProject.name + "-js"
                groupId = rootProject.group.toString()
                version = rootProject.version.toString()
            }
        }
    }
}