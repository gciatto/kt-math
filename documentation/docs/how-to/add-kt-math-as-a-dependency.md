# Add Kt-Math as a dependency

Kt-Math is published on Maven Central (group `io.github.gciatto`) and on npm, and mirrored on GitHub Packages.
Replace `VERSION` with the [latest release](https://central.sonatype.com/artifact/io.github.gciatto/kt-math).

| Maven Central | npm | GitHub Packages |
|---|---|---|
| `io.github.gciatto:kt-math` | `kt-math` | [`io.github.gciatto:kt-math`](https://github.com/gciatto/kt-math/packages), `@gciatto/kt-math` |

Each [GitHub release](https://github.com/gciatto/kt-math/releases) also attaches the JVM jars, the npm tarball and,
for Linux, macOS and Windows, a zip of the C shared library and header (see
[Getting started in C](../tutorials/getting-started-c.md)).

A single artifact holds everything: `BigInteger`, `BigDecimal`, `BigRational`, `BigFactory`, and
[`BigDecimalMath`](use-big-math.md).

## Kotlin Multiplatform (Gradle)

Add the dependency to `commonMain`: Gradle picks the artifact of each target.

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.gciatto:kt-math:VERSION")
            }
        }
    }
}
```

## Kotlin/Native (Gradle)

A Kotlin/Native-only project declares its targets and depends on Kt-Math from `nativeMain` (or from the source set of
a single target): Gradle picks the klib of each target, with the C kernels already compiled in.

```kotlin
kotlin {
    linuxX64()
    macosArm64()
    mingwX64()
    sourceSets {
        nativeMain {
            dependencies {
                implementation("io.github.gciatto:kt-math:VERSION")
            }
        }
    }
}
```

Every Kotlin/Native target but the deprecated ones is available (see the [platforms](../reference/platforms.md)).

## Kotlin/JVM or Java (Gradle)

```kotlin
dependencies {
    implementation("io.github.gciatto:kt-math:VERSION")
}
```

## Java (Maven)

Maven doesn't read Gradle's module metadata: depend on the JVM artifact, suffixed with `-jvm`.

```xml
<dependency>
    <groupId>io.github.gciatto</groupId>
    <artifactId>kt-math-jvm</artifactId>
    <version>VERSION</version>
</dependency>
```

The JVM artifact requires Java 8 or later.

## JavaScript (npm)

```bash
npm install kt-math
```

From GitHub Packages, the package is scoped by the repository owner, and needs a GitHub token with the
`read:packages` scope (in `.npmrc`: `@gciatto:registry=https://npm.pkg.github.com` and
`//npm.pkg.github.com/:_authToken=TOKEN`):

```bash
npm install @gciatto/kt-math
```
