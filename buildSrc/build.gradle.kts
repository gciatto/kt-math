plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val javaVersion = JavaVersion.current()

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = javaVersion.toString()
    }
}

java {
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
}
