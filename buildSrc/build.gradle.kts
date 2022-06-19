plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val javaVersion = JavaVersion.current()

java {
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = false
        languageVersion = "1.5"
        jvmTarget = javaVersion.toString()
    }
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
}
