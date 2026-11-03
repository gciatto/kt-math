# Add Kt-Math as a dependency

Kt-Math is published on Maven Central (group `io.github.gciatto`) and on npm. Replace `VERSION` with the
[latest release](https://central.sonatype.com/artifact/io.github.gciatto/kt-math).

| Maven Central | npm |
|---|---|
| `io.github.gciatto:kt-math` | `kt-math` |

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
