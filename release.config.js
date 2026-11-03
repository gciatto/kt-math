var forceVersion = "-PforceVersion=${nextRelease.version}"

// kt-math is a single Maven Central Portal deployment: if zip/release fails (e.g. a flaky upload), the
// deployment is dead and nothing of it got published, so retrying exactly those two tasks once is safe.
var mavenCentralTasks = "zipMavenCentralPortalPublication releaseMavenCentralPortalPublication"
var publishCmd = `
./gradlew ${forceVersion} publishAllPublicationsToProjectLocalRepository || exit 3
./gradlew ${forceVersion} ${mavenCentralTasks} || ./gradlew ${forceVersion} ${mavenCentralTasks} || exit 3
./gradlew ${forceVersion} publishAllPublicationsToGithubRepository || true
./gradlew ${forceVersion} publishJsPackageToNpmjsRegistry || true
`
var prepareCmd = `
./gradlew ${forceVersion} dokkaGenerateHtml || true
`

var config = require('semantic-release-preconfigured-conventional-commits');
config.plugins.push(
    [
        "@semantic-release/exec",
        {
            "prepareCmd": prepareCmd,
            "publishCmd": publishCmd,
        }
    ],
    "@semantic-release/github",
    "@semantic-release/git",
)
module.exports = config
