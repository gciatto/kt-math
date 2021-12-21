import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.npmPublish)
    alias(libs.plugins.dokka)
    `publish-on-maven`
}

group = "io.github.gciatto"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    assignGitSemanticVersion()
}

println("${rootProject.name} version: $version")

repositories {
    mavenCentral()
}

jvmVersion(libs.versions.jvm)
nodeVersion(libs.versions.node)


tasks.withType<DokkaTask>().matching { "Html" in it.name }.all {
    val dokkaHtml = this
    tasks.create<Jar>("dokkaHtmlJar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        dependsOn(dokkaHtml)
    }
}
