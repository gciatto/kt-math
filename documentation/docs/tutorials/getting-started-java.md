# Getting started in Java

Kt-Math's JVM artifact is a plain Java 8+ library: on the JVM, its numbers delegate to `java.math`, so they are as
fast as Java's own. What Kt-Math adds on the JVM is the same API as on the other platforms, `BigRational`, and the
factories and conversions.

## Add the dependency

With Gradle:

```kotlin
dependencies {
    implementation("io.github.gciatto:kt-math-jvm:VERSION")
}
```

With Maven:

```xml
<dependency>
    <groupId>io.github.gciatto</groupId>
    <artifactId>kt-math-jvm</artifactId>
    <version>VERSION</version>
</dependency>
```

## Integers

Kotlin's operators are methods in Java (`plus`, `minus`, `times`, `div`, `rem`), and the factories are static methods
of the interfaces:

```java
--8<-- "src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java:bigIntegers"
```

## Decimals

```java
--8<-- "src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java:bigDecimals"
```

## Rationals

```java
--8<-- "src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java:bigRationals"
```

## Factories and conversions

The factory is `BigFactory.getDefault()`; the conversions of Java numbers and strings are static methods of the
`Conversions` class:

```java
--8<-- "src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java:factory"
```

```java
--8<-- "src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java:conversions"
```

## With the Java standard library

`BigInteger`s, `BigDecimal`s and `BigRational`s are `Comparable`, and integer ranges are `Iterable`:

```java
--8<-- "src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java:javaInterop"
```
