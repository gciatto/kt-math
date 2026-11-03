# Add Kt-Math as a dependency

Kt-Math is published on Maven Central (group `io.github.gciatto`) and on npm. Replace `VERSION` with the
[latest release](https://central.sonatype.com/artifact/io.github.gciatto/kt-math).

| Module | Maven Central | npm |
|---|---|---|
| Numbers: `BigInteger`, `BigDecimal`, `BigRational`, `BigFactory` | `io.github.gciatto:kt-math` | `kt-math` |
| [big-math](use-big-math.md): `BigDecimalMath` | `io.github.gciatto:kt-math-big-math` | `kt-math-big-math` |

## Kotlin Multiplatform (Gradle)

Add the dependency to `commonMain`: Gradle picks the artifact of each target.

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.gciatto:kt-math:VERSION")
                implementation("io.github.gciatto:kt-math-big-math:VERSION") // optional
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
npm install kt-math-big-math # optional
```
