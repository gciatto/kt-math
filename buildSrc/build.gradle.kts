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
        jvmTarget = libs.versions.jvm.get()
    }
}

java {
    targetCompatibility = libs.versions.jvm.map(JavaVersion::toVersion).get()
    sourceCompatibility = libs.versions.jvm.map(JavaVersion::toVersion).get()
}

dependencies {
    implementation(libs.versions.kotlin.map { "org.jetbrains.kotlin:kotlin-gradle-plugin:$it" })
}
