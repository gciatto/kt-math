plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// https://github.com/gradle/gradle/issues/20847
configurations.configureEach {
    if (isCanBeResolved) {
        attributes {
            attribute(
                GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE,
                project.objects.named(GradleVersion.current().version)
            )
        }
    }
}

//kotlin {
//    jvmToolchain(11)
//}

dependencies {
    implementation(libs.kotlin.bom)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
    implementation(libs.dokka)
    implementation(libs.ktlint)
    implementation(libs.detekt)
}
