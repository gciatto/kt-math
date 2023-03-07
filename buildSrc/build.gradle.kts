plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.bom)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
    implementation(libs.dokka)
    implementation(libs.ktlint)
    implementation(libs.detekt)
}
