// kt-math as C code sees it, through the shared library and header Kotlin/Native generates (`./gradlew
// nativeUsageTest` builds them for the host, compiles this file against them and runs it). Every Kotlin object is a
// handle (a struct holding a pointer) to dispose of with DisposeStablePointer, every returned string is to dispose of
// with DisposeString; overloads get trailing underscores, in declaration order. Kotlin exceptions abort the process:
// the parse*OrNull functions return null handles instead.
#include <stdio.h>
#include <string.h>

#include "libktmath_api.h"

static libktmath_ExportedSymbols *lib;
static int failures = 0;

#define KT_MATH (lib->kotlin.root.org.gciatto.kt.math)
#define DISPOSE(handle) lib->DisposeStablePointer((handle).pinned)

// Checks that a string returned by Kotlin is the expected one, then frees it
static void expect(const char *what, const char *actual, const char *expected) {
    if (strcmp(actual, expected) != 0) {
        printf("FAILED %s: expected %s, got %s\n", what, expected, actual);
        failures++;
    } else {
        printf("ok %s = %s\n", what, actual);
    }
    lib->DisposeString(actual);
}

static void big_integers(libktmath_kref_org_gciatto_kt_math_BigFactory factory) {
    libktmath_kref_org_gciatto_kt_math_BigInteger x =
        KT_MATH.BigFactory.parseBigInteger(factory, "123456789012345678901234567890", 10);
    libktmath_kref_org_gciatto_kt_math_BigInteger y = KT_MATH.BigFactory.bigIntegerOf(factory, 42);
    libktmath_kref_org_gciatto_kt_math_BigInteger product = KT_MATH.BigInteger.times(x, y);
    expect("x * 42", KT_MATH.BigInteger.toString_(product), "5185185138518518513851851851380");
    libktmath_kref_org_gciatto_kt_math_BigInteger quotient = KT_MATH.BigInteger.div(x, y);
    expect("x / 42", KT_MATH.BigInteger.toString_(quotient), "2939447357436801878600823045");
    expect("x in hex", KT_MATH.BigInteger.toString(x, 16), "18ee90ff6c373e0ee4e3f0ad2");
    if (KT_MATH.BigInteger.compare(x, y) <= 0) {
        printf("FAILED compare(x, 42) > 0\n");
        failures++;
    }
    libktmath_kref_org_gciatto_kt_math_BigInteger invalid = KT_MATH.BigFactory.parseBigIntegerOrNull(factory, "12x", 10);
    if (invalid.pinned != NULL) {
        printf("FAILED parseBigIntegerOrNull(\"12x\") is null\n");
        failures++;
    }
    DISPOSE(quotient);
    DISPOSE(product);
    DISPOSE(y);
    DISPOSE(x);
}

static void big_decimals(libktmath_kref_org_gciatto_kt_math_BigFactory factory) {
    libktmath_kref_org_gciatto_kt_math_MathContext null_context = {NULL};
    libktmath_kref_org_gciatto_kt_math_BigDecimal a = KT_MATH.BigFactory.parseBigDecimal(factory, "1.10", null_context);
    libktmath_kref_org_gciatto_kt_math_BigDecimal b = KT_MATH.BigFactory.bigDecimalOf(factory, 3, null_context);
    libktmath_kref_org_gciatto_kt_math_RoundingMode half_even = KT_MATH.RoundingMode.HALF_EVEN.get();
    libktmath_kref_org_gciatto_kt_math_MathContext ten_digits = KT_MATH.MathContext.MathContext(10, half_even);
    libktmath_kref_org_gciatto_kt_math_BigDecimal quotient = KT_MATH.BigDecimal.div_(a, b, ten_digits);
    expect("1.10 / 3", KT_MATH.BigDecimal.toPlainString(quotient), "0.3666666667");
    libktmath_kref_org_gciatto_kt_math_BigDecimal root = KT_MATH.BigDecimal.sqrt(b, ten_digits);
    expect("sqrt(3)", KT_MATH.BigDecimal.toPlainString(root), "1.732050808");
    DISPOSE(root);
    DISPOSE(quotient);
    DISPOSE(ten_digits);
    DISPOSE(half_even);
    DISPOSE(b);
    DISPOSE(a);
}

static void big_rationals(libktmath_kref_org_gciatto_kt_math_BigFactory factory) {
    libktmath_kref_org_gciatto_kt_math_BigRational third = KT_MATH.BigFactory.parseBigRational(factory, "1/3");
    libktmath_kref_org_gciatto_kt_math_BigRational sixth = KT_MATH.BigFactory.bigRationalOf_____(factory, 1, 6);
    libktmath_kref_org_gciatto_kt_math_BigRational sum = KT_MATH.BigRational.plus(third, sixth);
    libktmath_kref_org_gciatto_kt_math_BigRational reduced = KT_MATH.BigRational.reduce(sum);
    expect("1/3 + 1/6", KT_MATH.BigRational.toRationalString(reduced), "1/2");
    libktmath_kref_org_gciatto_kt_math_BigRational invalid = KT_MATH.BigFactory.parseBigRationalOrNull(factory, "1/0");
    if (invalid.pinned != NULL) {
        printf("FAILED parseBigRationalOrNull(\"1/0\") is null\n");
        failures++;
    }
    DISPOSE(reduced);
    DISPOSE(sum);
    DISPOSE(sixth);
    DISPOSE(third);
}

int main(void) {
    lib = libktmath_symbols();
    libktmath_kref_org_gciatto_kt_math_BigFactory_Companion companion = KT_MATH.BigFactory.Companion._instance();
    libktmath_kref_org_gciatto_kt_math_BigFactory factory = KT_MATH.BigFactory.Companion.get_default(companion);
    big_integers(factory);
    big_decimals(factory);
    big_rationals(factory);
    DISPOSE(factory);
    DISPOSE(companion);
    printf("%d failures\n", failures);
    return failures == 0 ? 0 : 1;
}
