import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.dokka)
    `publish-on-maven`
    `publish-on-npm`
    `print-versions`
}

group = "io.github.gciatto"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    assignGitSemanticVersion()
}

logger.log(LogLevel.LIFECYCLE, "${rootProject.name} version: $version")

repositories {
    mavenCentral()
}

jvmVersion(libs.versions.jvm)

nodeVersion(default = libs.versions.node, override = project.findProperty("nodeVersion"))

packageJson {
    version = project.npmCompliantVersion
    dependencies {
        "kotlin" to libs.versions.kotlin.get()
    }
}

tasks.withType<DokkaTask>().matching { "Html" in it.name }.all {
    val dokkaHtml = this
    tasks.create<Jar>("dokkaHtmlJar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        dependsOn(dokkaHtml)
    }
}

kotlin {
    js {
        nodejs {
//            binaries.library()
        }
    }
}
