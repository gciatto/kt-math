# Getting started in C

Kt-Math's Kotlin/Native build is also a C library: every [GitHub release](https://github.com/gciatto/kt-math/releases)
ships it, prebuilt, for the desktop platforms. No Kotlin or Gradle is needed to use it.

## Download

Pick the zip of your platform among the assets of the [latest release](https://github.com/gciatto/kt-math/releases/latest):

| Platform | Asset | Library | Header |
|---|---|---|---|
| Linux x64 | `kt-math-VERSION-linuxX64.zip` | `libktmath.so` | `libktmath_api.h` |
| Linux ARM64 | `kt-math-VERSION-linuxArm64.zip` | `libktmath.so` | `libktmath_api.h` |
| macOS (Apple Silicon) | `kt-math-VERSION-macosArm64.zip` | `libktmath.dylib` | `libktmath_api.h` |
| Windows x64 | `kt-math-VERSION-mingwX64.zip` | `ktmath.dll` (plus `ktmath.def`) | `ktmath_api.h` |

Each library is self-contained: the Kotlin runtime is linked into it. On Windows, every `libktmath_` prefix below is
`ktmath_` instead (`ktmath_symbols()`, `ktmath_kref_...`), as in the header's name: include `ktmath_api.h`.

```bash
curl -LO https://github.com/gciatto/kt-math/releases/download/VERSION/kt-math-VERSION-linuxX64.zip
unzip kt-math-VERSION-linuxX64.zip -d ktmath
```

## Write a program

The header declares a single function, `libktmath_symbols()`, returning a structure that mirrors Kotlin's packages and
classes: `lib->kotlin.root.org.gciatto.kt.math.BigInteger.times(x, y)` is Kotlin's `x * y`. Kotlin objects are
handles, to release when no longer needed:

```c
--8<-- "src/nativeUsageTest/c/usage.c:example"

int main(void) {
--8<-- "src/nativeUsageTest/c/usage.c:main"
    return failures == 0 ? 0 : 1;
}
```

This is the beginning of Kt-Math's own C usage test, compiled and run by CI: `src/nativeUsageTest/c/usage.c` goes on to
call every other function of the C API, a reference for each of them.

## Compile and run

=== "Linux"

    ```bash
    cc usage.c -I ktmath -L ktmath -lktmath -Wl,-rpath,"$PWD/ktmath" -o usage
    ./usage
    ```

=== "macOS"

    ```bash
    cc usage.c -I ktmath -L ktmath -lktmath -Wl,-rpath,"$PWD/ktmath" -o usage
    ./usage
    ```

    A library downloaded with a browser is quarantined by macOS: `xattr -d com.apple.quarantine ktmath/libktmath.dylib`.

=== "Windows (MinGW)"

    ```bash
    gcc usage.c -I ktmath -L ktmath -lktmath -o usage.exe
    cp ktmath/ktmath.dll .
    ./usage.exe
    ```

    With MSVC, create an import library from the definitions file first: `lib /def:ktmath.def /out:ktmath.lib /machine:x64`.

## Going further

- [Call Kt-Math from C](../how-to/call-kt-math-from-c.md): the conventions of the C API (handles, strings,
  overloads, exceptions), and how to build the library yourself, e.g. for other Kotlin/Native targets.
- [Getting started in Kotlin](getting-started-kotlin.md): the same API from Kotlin/Native code, through Gradle.
- [API reference](https://gciatto.github.io/kt-math/api/): every class and function, under the same names in C.
