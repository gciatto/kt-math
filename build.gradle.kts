import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform") version "1.3.21"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

group = "com.github.gciatto"
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
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        groupId = rootProject.group.toString()
        version = rootProject.version.toString()

        pom {
            name.set("Kotlin Math")
            description.set("Pure Kotlin porting of the java.math package")
            url.set("https://github.com/gciatto/kt-math")
            licenses {
                license {
                    name.set("GNU General Public License, version 2, with the Classpath Exception")
                    url.set("https://openjdk.java.net/legal/gplv2+ce.html")
                }
            }
        }

    }
}