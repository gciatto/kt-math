import java.time.Duration

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.ktMppPP)
    alias(libs.plugins.nexusPublish)
}

group = "io.github.gciatto"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    fullHash.set(false) // set to true if you want to use the full git hash
    maxVersionLength.set(Int.MAX_VALUE) // Useful to limit the maximum version length, e.g. Gradle Plugins have a limit on 20
    developmentCounterLength.set(2) // How many digits after `dev`
    enforceSemanticVersioning.set(true) // Whether the plugin should stop if the resulting version is not a valid SemVer, or just warn
    // The separator for the pre-release block.
    // Changing it to something else than "+" may result in non-SemVer compatible versions
    preReleaseSeparator.set("-")
    // The separator for the build metadata block.
    // Some systems (notably, the Gradle plugin portal) do not support versions with a "+" symbol.
    // In these cases, changing it to "-" is appropriate.
    buildMetadataSeparator.set("+")
    distanceCounterRadix.set(36) // The radix for the commit-distance counter. Must be in the 2-36 range.
    // A prefix on tags that should be ignored when computing the Semantic Version.
    // Many project are versioned with tags named "vX.Y.Z", de-facto building valid SemVer versions but for the leading "v".
    // If it is the case for some project, setting this property to "v" would make these tags readable as SemVer tags.
    versionPrefix.set("")
    assignGitSemanticVersion()
}

println("${rootProject.name} version: $version")

kotlinMultiplatform {
    developer("Giovanni Ciatto", "giovanni.ciatto@gmail.com", "http://about.me/gciatto")
}

val mavenRepo: String by project
val mavenUsername: String by project
val mavenPassword: String by project

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri(mavenRepo))
            username.set(mavenUsername)
            password.set(mavenPassword)
        }
    }
    clientTimeout.set(Duration.ofMinutes(10))
}
