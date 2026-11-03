var forceVersion = "-PforceVersion=${nextRelease.version}"
// As in tuProlog/2p-kt: a plain string, so that @semantic-release/exec substitutes the version at release time. Read
// back by the release job (build-and-deploy.yml), which passes it on to the docs job and to the CI summary.
var recordVersionCmd = 'echo "${nextRelease.version}" > "$RUNNER_TEMP/last-release-version.txt"'

// The npm package again, on GitHub Packages, whose npm registry only takes packages scoped by the repository owner
// (@gciatto/kt-math): a copy of the package published on npmjs, renamed. GITHUB_PACKAGES_TOKEN comes from the release
// job. No `${...}` here: @semantic-release/exec would take it for a template variable.
var githubNpmCmd = `
rm -rf build/packages/github && cp -R build/packages/js build/packages/github &&
(cd build/packages/github &&
  npm pkg set "name=@$GITHUB_REPOSITORY_OWNER/$(npm pkg get name | tr -d '"')" &&
  npm publish --registry=https://npm.pkg.github.com "--//npm.pkg.github.com/:_authToken=$GITHUB_PACKAGES_TOKEN")
`

// kt-math is a single Maven Central Portal deployment: if zip/release fails (e.g. a flaky upload), the
// deployment is dead and nothing of it got published, so retrying exactly those two tasks once is safe.
var mavenCentralTasks = "zipMavenCentralPortalPublication releaseMavenCentralPortalPublication"
var publishCmd = `
./gradlew ${forceVersion} publishAllPublicationsToProjectLocalRepository || exit 3
./gradlew ${forceVersion} ${mavenCentralTasks} || ./gradlew ${forceVersion} ${mavenCentralTasks} || exit 3
./gradlew ${forceVersion} publishAllPublicationsToGithubRepository || true
./gradlew ${forceVersion} publishJsPackageToNpmjsRegistry || true
${githubNpmCmd} || true
${recordVersionCmd}
`
// releaseAssets: the files attached to the GitHub release below (see build.gradle.kts)
var prepareCmd = `
./gradlew ${forceVersion} dokkaGenerateHtml || true
./gradlew ${forceVersion} releaseAssets || exit 4
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
    [
        "@semantic-release/github",
        {
            "assets": [
                { "path": "build/libs/kt-math-jvm-*.jar" },
                { "path": "build/packages/kt-math-*.tgz" },
                { "path": "build/distributions/kt-math-*.zip" },
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
