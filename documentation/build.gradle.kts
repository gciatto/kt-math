// The documentation website: MkDocs Material pages (docs/) plus the Dokka API reference, mounted at
// /api/. `./gradlew :documentation:assembleSite` writes it to build/assembledSite; CI publishes it on GitHub Pages.
plugins {
    id("org.jetbrains.dokka")
}

repositories {
    // Google mirror of Maven Central, used for resolution only (avoids Maven Central rate-limits)
    maven("https://maven-central.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

dependencies {
    dokka(project(":"))
}

dokka {
    moduleName.set("Kt-Math")
}

val mkdocsSiteDir = layout.buildDirectory.dir("site")

val mkdocsBuild =
    tasks.register<Exec>("mkdocsBuild") {
        group = "documentation"
        description = "Builds the MkDocs pages (needs `pip install mkdocs-material`)"
        inputs.dir("docs")
        inputs.file("mkdocs.yml")
        inputs.files(rootProject.file("README.md"))
        outputs.dir(mkdocsSiteDir)
        workingDir = projectDir
        commandLine("mkdocs", "build", "--strict", "--site-dir", mkdocsSiteDir.get().asFile.absolutePath)
    }

tasks.register<Copy>("assembleSite") {
    group = "documentation"
    description = "Builds the documentation website: the MkDocs pages, plus the API reference under /api/"
    dependsOn(mkdocsBuild, "dokkaGenerateHtml")
    from(mkdocsSiteDir)
    from(layout.buildDirectory.dir("dokka/html")) { into("api") }
    into(layout.buildDirectory.dir("assembledSite"))
}
