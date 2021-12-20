plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = false
        jvmTarget = JavaVersion.current().toString()
    }
}

java {
    targetCompatibility = JavaVersion.current()
    sourceCompatibility = JavaVersion.current()
}

//gradlePlugin {
//    plugins {
//        register("hello-plugin") {
//            id = "hello"
//            implementationClass = "com.github.daggerok.plugin.HelloPlugin"
//        }
//    }
//}
