import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import node.Bugs
import node.People
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradlePassConfigurationImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask

plugins {
    kotlin("multiplatform") version Versions.org_jetbrains_kotlin_multiplatform_gradle_plugin
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version Versions.org_jetbrains_dokka_gradle_plugin
    id("com.jfrog.bintray") version Versions.com_jfrog_bintray_gradle_plugin
    id("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
    id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/dokka")
    maven("https://jitpack.io")
}

group = "io.github.gciatto"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    developmentCounterLength.set(2) // How many digits after `dev`
    version = computeGitSemVer() // THIS IS MANDATORY, AND MUST BE LAST IN THIS BLOCK!
}

println("${rootProject.name} version: $version")


// env ORG_GRADLE_PROJECT_signingKey
val signingKey = getPropertyOrWarnForAbsence("signingKey")
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
// env ORG_GRADLE_PROJECT_bintrayUser
val bintrayUser = getPropertyOrWarnForAbsence("bintrayUser")
// env ORG_GRADLE_PROJECT_bintrayKey
val bintrayKey = getPropertyOrWarnForAbsence("bintrayKey")
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")
// env ORG_GRADLE_PROJECT_npmToken
val npmToken = getPropertyOrWarnForAbsence("npmToken")

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(kotlin("stdlib-jdk8"))
                }
            }

            mavenPublication {
                artifactId = rootProject.name + "-jvm"
            }

            // JVM-specific tests and their dependencies:
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
        }

        js {
            nodejs()

            sequenceOf("", "Test").forEach {
                tasks.getByName<KotlinJsCompile>("compile${it}KotlinJs") {
                    kotlinOptions {
                        moduleKind = "umd"
                        noStdlib = true
                        metaInfo = true
                    }
                }
            }
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(kotlin("stdlib-js"))
                }
            }
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }

            mavenPublication {
                artifactId = rootProject.name + "-js"
            }
        }
    }
}

val publishAllToBintrayTask = tasks.create<DefaultTask>("publishAllToBintray") {
    group = "publishing"
}

with(rootProject) {
    
    configureDokka()

    configureMavenPublications("packDokka")

    configureUploadToMavenCentral(
        if (version.toString().contains("SNAPSHOT")) {
            "https://oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
    )

    configureUploadToBintray("kotlinMultiplatform", "js", "jvm", "metadata")

    configureSigning()

    configureJsPackage()
}

fun Project.configureDokka() {
    tasks.withType<DokkaTask> {
        outputDirectory = docDir
        outputFormat = "html"

        multiplatform {
            registerPlatform("jvm")
            registerPlatform("js")
        }
    }

    val jarPlatform = tasks.withType<Jar>().map { it.name.replace("Jar", "") }

    task<DefaultTask>("packAllDokka") {
        group = "documentation"
    }

    jarPlatform.forEach {
        val packDokkaForPlatform = "packDokka${capitalize(it)}"

        task<Jar>(packDokkaForPlatform) {
            group = "documentation"
            dependsOn("dokka")
            from(docDir)
            archiveBaseName.set(project.name)
            archiveVersion.set(project.version.toString())
            archiveAppendix.set(it)
            archiveClassifier.set("javadoc")
        }

        tasks.getByName("${it}Jar").dependsOn(packDokkaForPlatform)
        tasks.getByName("packAllDokka").dependsOn(packDokkaForPlatform)
    }
}

fun Project.configureSigning() {
    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)

        sign(publishing.publications)
    }

    publishing {
        val pubs = publications.withType<MavenPublication>().map { "sign${capitalize(it.name)}Publication" }

        task<Sign>("signAllPublications") {
            dependsOn(*pubs.toTypedArray())
        }
    }
}

fun Project.configureUploadToBintray(vararg publicationNames: String) {
    publishing {
        bintray {
            user = bintrayUser
            key = bintrayKey
            setPublications(*publicationNames)
            override = true
            with(pkg) {
                repo = "kt-math"
                name = project.name
                user = bintrayUser
                vcsUrl = "https://github.com/gciatto/kt-math"
                setLicenses("GPL-2.0+CE")
                with(version) {
                    name = project.version.toString()
                }
            }
        }
    }
    this.tasks.withType<BintrayUploadTask> {
        publishAllToBintrayTask.dependsOn(this)
    }
}

fun Project.configureUploadToMavenCentral(mavenRepoUrl: String) {
    if (ossrhUsername != null && ossrhPassword != null) {
        publishing {
            repositories {
                maven(mavenRepoUrl) {
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }
        }
    }
}

fun Project.configureMavenPublications(docArtifactBaseName: String) {
    publishing {
        publications.withType<MavenPublication> {
            groupId = this@configureMavenPublications.group.toString()
            version = this@configureMavenPublications.version.toString()

            val docArtifact = "${docArtifactBaseName}${name.capitalize()}"

            if (docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            } else if (!docArtifact.endsWith("KotlinMultiplatform")) {
                println("w: no javadoc artifact for publication $name in " +
                        "project ${this@configureMavenPublications.name}: no such a task: $docArtifact")
            }

            pom {
                name.set("Kotlin Math")
                description.set("Pure Kotlin porting of the java.math package")
                url.set("https://github.com/gciatto/kt-math")
                licenses {
                    license {
                        name.set("GNU General Public License, version 2, with the Classpath Exception")
                        url.set("https://openjdk.java.net/legal/gplv2+ce.html")
                    }
                }

                developers {
                    developer {
                        email.set("giovanni.ciatto@gmail.com")
                        url.set("https://about.me/gciatto")
                        organization.set("GitHub")
                        organizationUrl.set("https://github.com")
                    }
                }

                scm {
                    connection.set("scm:git:git:///github.com/gciatto/kt-math.git")
                    developerConnection.set("scm:git:ssh://github.com:gciatto/kt-math.git")
                    url.set("https://github.com/gciatto/kt-math")
                }
            }

        }
    }
}

fun Set<String>.forEachProject(f: Project.() -> Unit) = subprojects.filter { it.name in this }.forEach(f)

fun NamedDomainObjectContainerScope<GradlePassConfigurationImpl>.registerPlatform(
    platform: String, configuration: Action<in GradlePassConfigurationImpl>
) {

    val low = platform.toLowerCase()
    val up = platform.toUpperCase()

    register(low) {
        targets = listOf(up)
        this@register.platform = low
        includeNonPublic = false
        reportUndocumented = false
        collectInheritedExtensionsFromLibraries = true
        skipEmptyPackages = true
        configuration(this@register)
        noStdlibLink = true
    }
}

fun NamedDomainObjectContainerScope<GradlePassConfigurationImpl>.registerPlatform(platform: String) =
    registerPlatform(platform) { }

fun Project.configureJsPackage() {
    apply<node.NpmPublishPlugin>()

    configure<node.NpmPublishExtension> {
        nodeRoot = tasks.withType<NodeJsSetupTask>().asSequence().map { it.destination }.first()
        token = npmToken!!
        packageJson = tasks.getByName<KotlinPackageJsonTask>("jsPackageJson").packageJson
        nodeSetupTask = "kotlinNodeJsSetup"
        jsCompileTask = "jsMainClasses"

        liftPackageJson {
            people = mutableListOf(
                People(
                    "Giovanni Ciatto",
                    "giovanni.ciatto@gmail.com",
                    "https://about.me/gciatto"
                )
            )
            homepage = "https://github.com/gciatto/kt-math"
            bugs = Bugs(
                "https://github.com/gciatto/kt-math/issues",
                "giovanni.ciatto@gmail.com"
            )
            license = "GPL-2.0+CE"
        }
    }
}