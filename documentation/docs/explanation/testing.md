# Testing

Kt-Math aims at giving the same results as `java.math` on every platform, exceptions included. Rather than
hand-picked expectations, most of its tests are **differential**: they run the same operations on Kt-Math and on
`java.math`, and compare the outcomes.

## On the JVM

`TestAgainstJavaMath` runs every operation of the interfaces, on many inputs (small and huge numbers, edge cases,
every rounding mode), both on the Kotlin port and on `java.math`, and compares results and exception types. CI runs it
on Java 8, 17, 21 and 25.

## On JavaScript and Kotlin/Native

`java.math` isn't available there, so the JVM computes the expected outcomes once: `generateDifferentialCases` runs the
named operations of `DifferentialOps` on `java.math` and writes cases and outcomes to a file, which the JS and native
tests replay on Kt-Math. CI runs them on Node.js, Linux, Windows, macOS and the iOS, watchOS and tvOS simulators.

The big-math module is compared with big-math itself in the same way.

## Usage tests

The API is also exercised as users of each language see it: in Java (JUnit), plain JavaScript (Node's test runner, on
the npm package as published) and C (against the Kotlin/Native shared library). The examples of this website are
quoted from those tests.
