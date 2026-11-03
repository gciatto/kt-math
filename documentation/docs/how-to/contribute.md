# Contribute

Kt-Math is developed on [GitHub](https://github.com/gciatto/kt-math): issues and pull requests are welcome. Commit
messages follow [Conventional Commits](https://www.conventionalcommits.org/) (they drive the automatic releases).
The repository's `AGENTS.md` collects the details below and more.

## Build and test

Gradle needs a JDK 17 or later. The tasks run by CI:

| Task | What it does |
|---|---|
| `./gradlew ktlintFormat` | formats the code (`ktlintCheck` only checks) |
| `./gradlew detektAll` | static analysis (not plain `detekt`: it checks nothing in a multiplatform project) |
| `./gradlew jvmTest`, `jsTest` | JVM and JS tests; `jvmTest -PtestJvmVersion=8` runs them on Java 8 |
| `./gradlew linuxX64Test`, `macosArm64Test`, `mingwX64Test`, ... | native tests, on matching hosts |
| `./gradlew jsUsageTest`, `nativeUsageTest` | the usage tests in plain JavaScript and C |
| `./gradlew check` | all of the above that the host can run |
| `./gradlew assemble` | every artifact, as publishing builds them |
| `./gradlew :documentation:assembleSite` | this website (needs `pip install mkdocs-material`) |

## Port fidelity

The Kotlin implementation (`CommonBigInteger`, `CommonBigDecimal`, `MutableBigInteger`) follows the JDK sources line
by line, with the JDK's names: keep it that way, so that it can be diffed against them. Any change in behaviour is
caught by the [differential tests](../explanation/testing.md): extend them (inputs or operations) rather than
writing hand-picked expectations.

## Documentation

This website is built with [MkDocs Material](https://squidfunk.github.io/mkdocs-material/) from
`documentation/docs/`, plus the [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) API reference of every
module, under `/api/`. The code examples are quoted from the usage tests (`--8<--` markers), so they are always
tested: change the tests, not the pages, to change an example.
