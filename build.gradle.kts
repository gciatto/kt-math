import java.time.Duration

plugins {
    id("io.github.gciatto.kt-mpp-pp")
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("de.marcphilipp.nexus-publish")
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

kotlinMultiplatform {
    developer("Giovanni Ciatto", "giovanni.ciatto@gmail.com", "http://about.me/gciatto")
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(project.property("mavenUsername").toString())
            password.set(project.property("mavenPassword").toString())
        }
    }
    clientTimeout.set(Duration.ofMinutes(10))
}