plugins {
    kotlin("multiplatform")
}

val mochaTimeout: String by project

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
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

    @Suppress("UNUSED_VARIABLE")
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
