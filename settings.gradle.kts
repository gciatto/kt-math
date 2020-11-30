import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

rootProject.name = "kt-math"

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("de.fayard:dependencies:0.+")
    }
}

bootstrapRefreshVersionsAndDependencies()