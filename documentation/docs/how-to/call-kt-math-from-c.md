# Call Kt-Math from C

Kotlin/Native can compile a Kotlin library into a C shared library, with a header declaring its whole API: this is
how Kt-Math can be used from C (or any language with a C foreign-function interface).

## Build the shared library

In a Kotlin Multiplatform project depending on Kt-Math, declare a shared library for your native target, exporting
Kt-Math's API:

```kotlin
kotlin {
    linuxX64 { // or macosArm64, mingwX64, ...
        binaries.sharedLib("ktmath") {
            export("io.github.gciatto:kt-math:VERSION")
        }
    }
    sourceSets {
        nativeMain {
            dependencies {
                api("io.github.gciatto:kt-math:VERSION")
            }
        }
    }
}
```

`./gradlew linkKtmathReleaseSharedLinuxX64` writes `libktmath.so` (`.dylib` on macOS, `.dll` on Windows) and
`libktmath_api.h` to `build/bin/linuxX64/ktmathReleaseShared/`.

## Use it from C

The header exposes a `libktmath_symbols()` function, returning a structure that mirrors Kotlin's packages and classes:

- every Kotlin object is a handle, a structure holding a pointer, to release with `DisposeStablePointer`;
- every string returned by Kotlin is to release with `DisposeString`;
- member functions take the receiver as first argument, companion objects are reached through `_instance()`;
- overloads are distinguished by trailing underscores, in declaration order;
- a Kotlin exception aborts the process: prefer the `parse*OrNull` functions of `BigFactory`, which return a null
  handle on invalid input.

This program is the beginning of Kt-Math's own C usage test, compiled and run by CI against Kt-Math's shared library
(the rest of `src/nativeUsageTest/c/usage.c` calls every other function of the C API):

```c
--8<-- "src/nativeUsageTest/c/usage.c:example"

int main(void) {
--8<-- "src/nativeUsageTest/c/usage.c:main"
    return failures == 0 ? 0 : 1;
}
```

Functions taking a Kotlin `IntArray`, `Array`, `Random` or range can't be called from C, which can't create those;
Kotlin arrays and sequences returned to C can only be passed on and disposed of.

Compile it against the library: `cc usage.c -I<dir> -L<dir> -lktmath -Wl,-rpath,<dir>`.
