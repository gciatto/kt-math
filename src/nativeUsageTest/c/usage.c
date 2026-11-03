// kt-math as C code sees it, through the shared library and header Kotlin/Native generates (`./gradlew
// nativeUsageTest` builds them for the host, compiles this file against them and runs it). The documentation website
// quotes the sections between the `--8<--` markers (call-kt-math-from-c.md). The api_* functions call every function
// of the C API (api_coverage.py checks it, as part of nativeUsageTest), and don't dispose of what they get, for brevity.
// --8<-- [start:example]
// Every Kotlin object is a handle (a struct holding a pointer) to dispose of with DisposeStablePointer, every returned
// string is to dispose of with DisposeString; overloads get trailing underscores, in declaration order. Kotlin
// exceptions abort the process: the parse*OrNull functions return null handles instead.
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
    libktmath_kref_org_gciatto_kt_math_BigDecimal log = KT_MATH.big.log(b, ten_digits); // big-math's functions
    expect("log(3)", KT_MATH.BigDecimal.toPlainString(log), "1.098612289");
    DISPOSE(log);
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
// --8<-- [end:example]

// The whole API, without disposing of anything
typedef libktmath_kref_org_gciatto_kt_math_BigInteger BI;
typedef libktmath_kref_org_gciatto_kt_math_BigDecimal BD;
typedef libktmath_kref_org_gciatto_kt_math_BigRational BR;
typedef libktmath_kref_org_gciatto_kt_math_big_BigComplex BC;
typedef libktmath_kref_org_gciatto_kt_math_MathContext MC;
typedef libktmath_kref_org_gciatto_kt_math_RoundingMode RM;
typedef libktmath_kref_org_gciatto_kt_math_BigIntegerRange BIR;

#define ANY(handle) ((libktmath_kref_kotlin_Any){(handle).pinned})
#define BI_S(x) KT_MATH.BigInteger.toString_(x)
#define BD_S(x) KT_MATH.BigDecimal.toPlainString(x)
#define BR_S(x) KT_MATH.BigRational.toRationalString(x)
#define BC_S(x) KT_MATH.big.BigComplex.toString(x)
#define CHECK(what, condition) check(what, condition)

static void check(const char *what, int condition) {
    if (!condition) {
        printf("FAILED %s\n", what);
        failures++;
    }
}

static MC mc;   // 10 digits, HALF_UP
static MC mc3;  // 3 digits, HALF_UP
static MC none; // a null MathContext

static void api_BigInteger(void) {
    libktmath_kref_org_gciatto_kt_math_BigInteger_Companion c = KT_MATH.BigInteger.Companion._instance();
    expect("BigInteger constants", BI_S(KT_MATH.BigInteger.Companion.get_NEGATIVE_ONE(c)), "-1");
    expect("ONE", BI_S(KT_MATH.BigInteger.Companion.get_ONE(c)), "1");
    expect("TWO", BI_S(KT_MATH.BigInteger.Companion.get_TWO(c)), "2");
    expect("TEN", BI_S(KT_MATH.BigInteger.Companion.get_TEN(c)), "10");
    expect("ZERO", BI_S(KT_MATH.BigInteger.Companion.get_ZERO(c)), "0");
    BI y = KT_MATH.BigInteger.Companion.of(c, 42LL);
    BI seven = KT_MATH.BigInteger.Companion.of_(c, 7);
    expect("of(String)", BI_S(KT_MATH.BigInteger.Companion.of__(c, "0x1f")), "31");
    expect("of(String, radix)", BI_S(KT_MATH.BigInteger.Companion.of___(c, "-ff", 16)), "-255");
    // byte arrays come from toByteArray: C can't create Kotlin arrays
    BI ff = KT_MATH.BigInteger.Companion.of__(c, "65535");
    expect("of(signum, bytes, off, len)", BI_S(KT_MATH.BigInteger.Companion.of_____(c, -1, KT_MATH.BigInteger.toByteArray(ff), 1, 2)), "-65535");

    BI n = KT_MATH.BigInteger.Companion.of_(c, -12);
    expect("absoluteValue", BI_S(KT_MATH.BigInteger.get_absoluteValue(n)), "12");
    CHECK("bitCount", KT_MATH.BigInteger.get_bitCount(seven) == 3);
    CHECK("bitLength", KT_MATH.BigInteger.get_bitLength(n) == 4);
    CHECK("signum", KT_MATH.BigInteger.get_signum(n) == -1);

    BI b = KT_MATH.BigInteger.Companion.of_(c, 12);
    BI ten = KT_MATH.BigInteger.Companion.of_(c, 10);
    expect("and", BI_S(KT_MATH.BigInteger.and_(b, ten)), "8");
    expect("and(Int)", BI_S(KT_MATH.BigInteger.and__(b, 10)), "8");
    expect("and(Long)", BI_S(KT_MATH.BigInteger.and___(b, 10LL)), "8");
    BI four = KT_MATH.BigInteger.Companion.of_(c, 4);
    expect("andNot", BI_S(KT_MATH.BigInteger.andNot(b, four)), "8");
    expect("andNot(Int)", BI_S(KT_MATH.BigInteger.andNot_(b, 4)), "8");
    expect("andNot(Long)", BI_S(KT_MATH.BigInteger.andNot__(b, 4LL)), "8");
    expect("clearBit", BI_S(KT_MATH.BigInteger.clearBit(b, 2)), "8");
    expect("flipBit", BI_S(KT_MATH.BigInteger.flipBit(b, 3)), "4");
    expect("setBit", BI_S(KT_MATH.BigInteger.setBit(b, 1)), "14");
    expect("set", BI_S(KT_MATH.BigInteger.set(b, 0, 1)), "13");
    CHECK("get, testBit", KT_MATH.BigInteger.get(b, 3) && KT_MATH.BigInteger.testBit(b, 2) && !KT_MATH.BigInteger.get(b, 0));
    expect("not", BI_S(KT_MATH.BigInteger.not_(b)), "-13");
    BI three = KT_MATH.BigInteger.Companion.of_(c, 3);
    expect("or", BI_S(KT_MATH.BigInteger.or_(b, three)), "15");
    expect("or(Int)", BI_S(KT_MATH.BigInteger.or__(b, 3)), "15");
    expect("or(Long)", BI_S(KT_MATH.BigInteger.or___(b, 3LL)), "15");
    expect("xor", BI_S(KT_MATH.BigInteger.xor_(b, ten)), "6");
    expect("xor(Int)", BI_S(KT_MATH.BigInteger.xor__(b, 10)), "6");
    expect("xor(Long)", BI_S(KT_MATH.BigInteger.xor___(b, 10LL)), "6");
    expect("shl", BI_S(KT_MATH.BigInteger.shl(b, 2)), "48");
    expect("shr", BI_S(KT_MATH.BigInteger.shr(b, 2)), "3");

    CHECK("compare", KT_MATH.BigInteger.compare(y, b) > 0 && KT_MATH.BigInteger.compareTo(b, y) < 0);
    CHECK("equals, hashCode", KT_MATH.BigInteger.equals(seven, ANY(KT_MATH.BigInteger.Companion.of(c, 7LL))) &&
        KT_MATH.BigInteger.hashCode(seven) == KT_MATH.BigInteger.hashCode(KT_MATH.BigInteger.Companion.of(c, 7LL)));
    expect("min", BI_S(KT_MATH.BigInteger.min(y, b)), "12");
    expect("max", BI_S(KT_MATH.BigInteger.max(y, b)), "42");

    expect("plus", BI_S(KT_MATH.BigInteger.plus(y, b)), "54");
    expect("plus(Int)", BI_S(KT_MATH.BigInteger.plus_(y, 1)), "43");
    expect("plus(Long)", BI_S(KT_MATH.BigInteger.plus__(y, 1LL)), "43");
    expect("minus", BI_S(KT_MATH.BigInteger.minus(y, b)), "30");
    expect("minus(Int)", BI_S(KT_MATH.BigInteger.minus_(y, 2)), "40");
    expect("minus(Long)", BI_S(KT_MATH.BigInteger.minus__(y, 2LL)), "40");
    expect("times", BI_S(KT_MATH.BigInteger.times(y, b)), "504");
    expect("times(Int)", BI_S(KT_MATH.BigInteger.times_(y, 2)), "84");
    expect("times(Long)", BI_S(KT_MATH.BigInteger.times__(y, 2LL)), "84");
    expect("div(Int)", BI_S(KT_MATH.BigInteger.div_(y, 5)), "8");
    expect("div(Long)", BI_S(KT_MATH.BigInteger.div__(y, 5LL)), "8");
    // Kotlin arrays are opaque to C: they can only be passed on (and disposed of)
    BI five = KT_MATH.BigInteger.Companion.of_(c, 5);
    CHECK("divideAndRemainder", KT_MATH.BigInteger.divideAndRemainder(y, five).pinned != NULL);
    CHECK("divideAndRemainder(Int)", KT_MATH.BigInteger.divideAndRemainder_(y, 5).pinned != NULL);
    CHECK("divideAndRemainder(Long)", KT_MATH.BigInteger.divideAndRemainder__(y, 5LL).pinned != NULL);
    BI m = KT_MATH.BigInteger.Companion.of_(c, -7); // remainders take the sign of the dividend
    expect("rem", BI_S(KT_MATH.BigInteger.rem(m, three)), "-1");
    expect("rem(Int)", BI_S(KT_MATH.BigInteger.rem_(m, 3)), "-1");
    expect("rem(Long)", BI_S(KT_MATH.BigInteger.rem__(m, 3LL)), "-1");
    expect("remainder", BI_S(KT_MATH.BigInteger.remainder(m, three)), "-1");
    expect("remainder(Int)", BI_S(KT_MATH.BigInteger.remainder_(m, 3)), "-1");
    expect("remainder(Long)", BI_S(KT_MATH.BigInteger.remainder__(m, 3LL)), "-1");
    expect("unaryMinus", BI_S(KT_MATH.BigInteger.unaryMinus(m)), "7");
    expect("unaryPlus", BI_S(KT_MATH.BigInteger.unaryPlus(m)), "-7");
    expect("pow", BI_S(KT_MATH.BigInteger.pow(y, 2)), "1764");
    BI seventeen = KT_MATH.BigInteger.Companion.of_(c, 17);
    expect("sqrt", BI_S(KT_MATH.BigInteger.sqrt(seventeen)), "4");
    CHECK("sqrtAndRemainder", KT_MATH.BigInteger.sqrtAndRemainder(seventeen).pinned != NULL);
    BI thirty = KT_MATH.BigInteger.Companion.of_(c, 30);
    expect("gcd", BI_S(KT_MATH.BigInteger.gcd(y, thirty)), "6");
    expect("gcd(Int)", BI_S(KT_MATH.BigInteger.gcd_(y, 30)), "6");
    expect("gcd(Long)", BI_S(KT_MATH.BigInteger.gcd__(y, 30LL)), "6");

    BI eleven = KT_MATH.BigInteger.Companion.of_(c, 11);
    expect("modInverse", BI_S(KT_MATH.BigInteger.modInverse(three, eleven)), "4");
    expect("modInverse(Int)", BI_S(KT_MATH.BigInteger.modInverse_(three, 11)), "4");
    expect("modInverse(Long)", BI_S(KT_MATH.BigInteger.modInverse__(three, 11LL)), "4");
    expect("modPow", BI_S(KT_MATH.BigInteger.modPow(four, KT_MATH.BigInteger.Companion.of_(c, 13), KT_MATH.BigInteger.Companion.of_(c, 497))), "445");
    BI ninety_seven = KT_MATH.BigInteger.Companion.of_(c, 97);
    CHECK("isProbablePrime", KT_MATH.BigInteger.isProbablePrime(ninety_seven, 50));
    expect("nextProbablePrime", BI_S(KT_MATH.BigInteger.nextProbablePrime(ninety_seven)), "101");

    expect("toBigDecimal", BD_S(KT_MATH.BigInteger.toBigDecimal(y)), "42");
    expect("toBigDecimal(scale)", BD_S(KT_MATH.BigInteger.toBigDecimal_(y, 1)), "4.2");
    expect("toBigRational", BR_S(KT_MATH.BigInteger.toBigRational(y)), "42");
    CHECK("toByte, toByteExact", KT_MATH.BigInteger.toByte(y) == 42 && KT_MATH.BigInteger.toByteExact(y) == 42);
    CHECK("toShort, toShortExact", KT_MATH.BigInteger.toShort(y) == 42 && KT_MATH.BigInteger.toShortExact(y) == 42);
    CHECK("toInt, toIntExact", KT_MATH.BigInteger.toInt(y) == 42 && KT_MATH.BigInteger.toIntExact(y) == 42);
    CHECK("toLong, toLongExact", KT_MATH.BigInteger.toLong(y) == 42 && KT_MATH.BigInteger.toLongExact(y) == 42);
    CHECK("toChar", KT_MATH.BigInteger.toChar(y) == '*');
    CHECK("toFloat, toDouble", KT_MATH.BigInteger.toFloat(y) == 42.0f && KT_MATH.BigInteger.toDouble(y) == 42.0);
    expect("toString(radix)", KT_MATH.BigInteger.toString(KT_MATH.BigInteger.Companion.of_(c, 255), 16), "ff");

    BI one = KT_MATH.BigInteger.Companion.get_ONE(c);
    expect("rangeTo", KT_MATH.BigIntegerRange.toString(KT_MATH.BigInteger.rangeTo(one, three)), "BigIntegerRange(start=1, endInclusive=3)");
    expect("rangeTo(Int)", BI_S(KT_MATH.BigIntegerRange.get_last(KT_MATH.BigInteger.rangeTo_(one, 2))), "2");
    expect("rangeTo(Long)", BI_S(KT_MATH.BigIntegerRange.get_last(KT_MATH.BigInteger.rangeTo__(one, 5LL))), "5");
}

static void api_BigIntegerRange(void) {
    libktmath_kref_org_gciatto_kt_math_BigInteger_Companion c = KT_MATH.BigInteger.Companion._instance();
    BI one = KT_MATH.BigInteger.Companion.get_ONE(c);
    BI three = KT_MATH.BigInteger.Companion.of_(c, 3);
    BIR range = KT_MATH.BigIntegerRange.BigIntegerRange(one, three);
    expect("start", BI_S(KT_MATH.BigIntegerRange.get_start(range)), "1");
    expect("endInclusive", BI_S(KT_MATH.BigIntegerRange.get_endInclusive(range)), "3");
    expect("first", BI_S(KT_MATH.BigIntegerRange.get_first(range)), "1");
    expect("component1", BI_S(KT_MATH.BigIntegerRange.component1(range)), "1");
    expect("component2", BI_S(KT_MATH.BigIntegerRange.component2(range)), "3");
    expect("copy", BI_S(KT_MATH.BigIntegerRange.get_first(KT_MATH.BigIntegerRange.copy(range, three, three))), "3");
    BIR same = KT_MATH.BigInteger.rangeTo_(one, 3);
    CHECK("equals, hashCode", KT_MATH.BigIntegerRange.equals(range, ANY(same)) &&
        KT_MATH.BigIntegerRange.hashCode(range) == KT_MATH.BigIntegerRange.hashCode(same));
    CHECK("iterator, toArray", KT_MATH.BigIntegerRange.iterator(range).pinned != NULL && KT_MATH.BigIntegerRange.toArray(range).pinned != NULL);
}

static void api_BigDecimal(void) {
    libktmath_kref_org_gciatto_kt_math_BigDecimal_Companion c = KT_MATH.BigDecimal.Companion._instance();
    expect("E", BD_S(KT_MATH.BigDecimal.round(KT_MATH.BigDecimal.Companion.get_E(c), mc)), "2.718281828");
    expect("PI", BD_S(KT_MATH.BigDecimal.round(KT_MATH.BigDecimal.Companion.get_PI(c), mc)), "3.141592654");
    expect("ONE", BD_S(KT_MATH.BigDecimal.Companion.get_ONE(c)), "1");
    expect("ONE_HALF", BD_S(KT_MATH.BigDecimal.Companion.get_ONE_HALF(c)), "0.5");
    expect("ONE_TENTH", BD_S(KT_MATH.BigDecimal.Companion.get_ONE_TENTH(c)), "0.1");
    expect("TEN", BD_S(KT_MATH.BigDecimal.Companion.get_TEN(c)), "10");
    expect("TWO", BD_S(KT_MATH.BigDecimal.Companion.get_TWO(c)), "2");
    expect("ZERO", BD_S(KT_MATH.BigDecimal.Companion.get_ZERO(c)), "0");
    expect("of(unscaled, scale)", BD_S(KT_MATH.BigDecimal.Companion.of(c, 1234LL, 2)), "12.34");
    expect("of(Int)", BD_S(KT_MATH.BigDecimal.Companion.of_(c, 12)), "12");
    expect("of(Long)", BD_S(KT_MATH.BigDecimal.Companion.of__(c, 12LL)), "12");
    expect("of(Double, mc)", BD_S(KT_MATH.BigDecimal.Companion.of___(c, 0.1, mc3)), "0.100");
    expect("of(Float, mc)", BD_S(KT_MATH.BigDecimal.Companion.of____(c, 0.25f, none)), "0.25");
    expect("of(String, mc)", BD_S(KT_MATH.BigDecimal.Companion.of_____(c, "1.55", mc3)), "1.55");
    expect("of(BigInteger, mc)", BD_S(KT_MATH.BigDecimal.Companion.of______(c, KT_MATH.BigInteger.Companion.of_(KT_MATH.BigInteger.Companion._instance(), 10), none)), "10");
    expect("of(Int, mc)", BD_S(KT_MATH.BigDecimal.Companion.of_______(c, 1234, mc3)), "1230");
    expect("of(Long, mc)", BD_S(KT_MATH.BigDecimal.Companion.of________(c, 1234LL, mc3)), "1230");
    expect("valueOf", BD_S(KT_MATH.BigDecimal.Companion.valueOf(c, 0.1)), "0.1");

    BD x = KT_MATH.BigDecimal.Companion.of_____(c, "-12.340", none);
    expect("absoluteValue", BD_S(KT_MATH.BigDecimal.get_absoluteValue(x)), "12.340");
    CHECK("precision, scale, signum", KT_MATH.BigDecimal.get_precision(x) == 5 && KT_MATH.BigDecimal.get_scale(x) == 3 && KT_MATH.BigDecimal.get_signum(x) == -1);
    expect("unscaledValue", BI_S(KT_MATH.BigDecimal.get_unscaledValue(x)), "-12340");
    expect("absoluteValue(mc)", BD_S(KT_MATH.BigDecimal.absoluteValue(x, mc3)), "12.3");
    expect("unaryMinus", BD_S(KT_MATH.BigDecimal.unaryMinus(x)), "12.340");
    expect("unaryMinus(mc)", BD_S(KT_MATH.BigDecimal.unaryMinus_(x, mc3)), "12.3");
    expect("unaryPlus", BD_S(KT_MATH.BigDecimal.unaryPlus(x)), "-12.340");
    expect("unaryPlus(mc)", BD_S(KT_MATH.BigDecimal.unaryPlus_(x, mc3)), "-12.3");
    expect("round", BD_S(KT_MATH.BigDecimal.round(x, mc3)), "-12.3");
    expect("ulp", BD_S(KT_MATH.BigDecimal.ulp(x)), "0.001");

    BD a = KT_MATH.BigDecimal.Companion.of_____(c, "1.10", none);
    BD b = KT_MATH.BigDecimal.Companion.of_(c, 3);
    BD two = KT_MATH.BigDecimal.Companion.get_TWO(c);
    BD d = KT_MATH.BigDecimal.Companion.of_____(c, "7.5", none);
    CHECK("compare", KT_MATH.BigDecimal.compare(a, b) < 0 && KT_MATH.BigDecimal.compareTo(b, a) > 0);
    expect("min", BD_S(KT_MATH.BigDecimal.min(a, b)), "1.10");
    expect("max", BD_S(KT_MATH.BigDecimal.max(a, b)), "3");
    expect("plus", BD_S(KT_MATH.BigDecimal.plus(a, b)), "4.10");
    expect("plus(mc)", BD_S(KT_MATH.BigDecimal.plus_(a, b, mc3)), "4.10");
    expect("minus", BD_S(KT_MATH.BigDecimal.minus(a, b)), "-1.90");
    expect("minus(mc)", BD_S(KT_MATH.BigDecimal.minus_(a, b, mc3)), "-1.90");
    expect("times", BD_S(KT_MATH.BigDecimal.times(a, b)), "3.30");
    expect("times(mc)", BD_S(KT_MATH.BigDecimal.times_(a, b, mc3)), "3.30");
    expect("div", BD_S(KT_MATH.BigDecimal.div(d, KT_MATH.BigDecimal.Companion.of_____(c, "2.5", none))), "3");
    expect("div(scale, rounding)", BD_S(KT_MATH.BigDecimal.div__(a, b, 2, KT_MATH.RoundingMode.HALF_UP.get())), "0.37");
    expect("div(rounding)", BD_S(KT_MATH.BigDecimal.div___(KT_MATH.BigDecimal.Companion.get_ONE(c), b, KT_MATH.RoundingMode.UP.get())), "1");
    expect("divideToIntegralValue", BD_S(KT_MATH.BigDecimal.divideToIntegralValue(d, two)), "3.0");
    expect("divideToIntegralValue(mc)", BD_S(KT_MATH.BigDecimal.divideToIntegralValue_(d, two, mc3)), "3.0");
    expect("rem", BD_S(KT_MATH.BigDecimal.rem(d, two)), "1.5");
    expect("rem(mc)", BD_S(KT_MATH.BigDecimal.rem_(d, two, mc3)), "1.5");
    CHECK("divideAndRemainder (Kotlin Pairs)", KT_MATH.BigDecimal.divideAndRemainder(d, two).pinned != NULL && KT_MATH.BigDecimal.divideAndRemainder_(d, two, mc3).pinned != NULL);
    expect("pow", BD_S(KT_MATH.BigDecimal.pow(a, 2)), "1.2100");
    expect("pow(mc)", BD_S(KT_MATH.BigDecimal.pow_(a, 3, mc3)), "1.33");
    expect("setScale(rounding)", BD_S(KT_MATH.BigDecimal.setScale(a, 0, KT_MATH.RoundingMode.HALF_UP.get())), "1");
    expect("setScale(Int)", BD_S(KT_MATH.BigDecimal.setScale_(a, 0, 4)), "1");
    expect("setScale", BD_S(KT_MATH.BigDecimal.setScale__(a, 3)), "1.100");
    expect("movePointLeft", BD_S(KT_MATH.BigDecimal.movePointLeft(a, 1)), "0.110");
    expect("movePointRight", BD_S(KT_MATH.BigDecimal.movePointRight(a, 1)), "11.0");
    expect("scaleByPowerOfTen", KT_MATH.BigDecimal.toEngineeringString(KT_MATH.BigDecimal.scaleByPowerOfTen(a, 3)), "1.10E+3");
    expect("stripTrailingZeros", BD_S(KT_MATH.BigDecimal.stripTrailingZeros(a)), "1.1");

    BD big = KT_MATH.BigDecimal.Companion.of_____(c, "1.5E+4", none);
    expect("toEngineeringString", KT_MATH.BigDecimal.toEngineeringString(big), "15E+3");
    expect("toBigInteger", BI_S(KT_MATH.BigDecimal.toBigInteger(big)), "15000");
    expect("toBigIntegerExact", BI_S(KT_MATH.BigDecimal.toBigIntegerExact(big)), "15000");
    expect("toBigRational", BR_S(KT_MATH.BigDecimal.toBigRational(big)), "15000");
    BD hundred = KT_MATH.BigDecimal.Companion.of_(c, 100);
    CHECK("toByte, toByteExact", KT_MATH.BigDecimal.toByte(hundred) == 100 && KT_MATH.BigDecimal.toByteExact(hundred) == 100);
    CHECK("toShort, toShortExact", KT_MATH.BigDecimal.toShort(big) == 15000 && KT_MATH.BigDecimal.toShortExact(big) == 15000);
    CHECK("toInt, toIntExact", KT_MATH.BigDecimal.toInt(big) == 15000 && KT_MATH.BigDecimal.toIntExact(big) == 15000);
    CHECK("toLong, toLongExact", KT_MATH.BigDecimal.toLong(big) == 15000 && KT_MATH.BigDecimal.toLongExact(big) == 15000);
    CHECK("toChar", KT_MATH.BigDecimal.toChar(hundred) == 'd');
    CHECK("toFloat, toDouble", KT_MATH.BigDecimal.toFloat(a) == 1.1f && KT_MATH.BigDecimal.toDouble(a) == 1.1);
    expect("sqrt", BD_S(KT_MATH.BigDecimal.sqrt(two, mc)), "1.414213562");
}

static void api_BigRational(void) {
    libktmath_kref_org_gciatto_kt_math_BigRational_Companion c = KT_MATH.BigRational.Companion._instance();
    libktmath_kref_org_gciatto_kt_math_BigInteger_Companion ic = KT_MATH.BigInteger.Companion._instance();
    libktmath_kref_org_gciatto_kt_math_BigDecimal_Companion dc = KT_MATH.BigDecimal.Companion._instance();
    expect("ONE", BR_S(KT_MATH.BigRational.Companion.get_ONE(c)), "1");
    expect("TEN", BR_S(KT_MATH.BigRational.Companion.get_TEN(c)), "10");
    expect("TWO", BR_S(KT_MATH.BigRational.Companion.get_TWO(c)), "2");
    expect("ZERO", BR_S(KT_MATH.BigRational.Companion.get_ZERO(c)), "0");
    expect("bernoulli", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.Companion.bernoulli(c, 2))), "1/6");
    expect("valueOf(Int)", BR_S(KT_MATH.BigRational.Companion.valueOf(c, 2)), "2");
    expect("valueOf(n, d)", BR_S(KT_MATH.BigRational.Companion.valueOf_(c, 1, 3)), "1/3");
    expect("valueOf(i, n, d)", BR_S(KT_MATH.BigRational.Companion.valueOf__(c, 1, 1, 2)), "3/2");
    expect("valueOf(BigInteger, BigInteger)", BR_S(KT_MATH.BigRational.Companion.valueOf___(c, KT_MATH.BigInteger.Companion.of_(ic, 1), KT_MATH.BigInteger.Companion.of_(ic, 2))), "1/2");
    expect("valueOf(BigInteger)", BR_S(KT_MATH.BigRational.Companion.valueOf____(c, KT_MATH.BigInteger.Companion.of_(ic, 10))), "10");
    expect("valueOf(Double)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.Companion.valueOf_____(c, 0.25))), "1/4");
    expect("valueOf(BigDecimal)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.Companion.valueOf______(c, KT_MATH.BigDecimal.Companion.valueOf(dc, 0.5)))), "1/2");
    expect("valueOf(String)", BR_S(KT_MATH.BigRational.Companion.valueOf_______(c, "-3/6")), "-3/6");
    expect("valueOf(parts)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.Companion.valueOf________(c, 1, "1", "5", NULL, NULL))), "3/2");
    expect("valueOf(BigDecimal, BigDecimal)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.Companion.valueOf_________(c, KT_MATH.BigDecimal.Companion.of_(dc, 1), KT_MATH.BigDecimal.Companion.of_(dc, 4)))), "1/4");

    BR x = KT_MATH.BigRational.Companion.valueOf_(c, -6, 4);
    BR third = KT_MATH.BigRational.Companion.valueOf_(c, 1, 3);
    BI one = KT_MATH.BigInteger.Companion.get_ONE(ic);
    BI two = KT_MATH.BigInteger.Companion.get_TWO(ic);
    expect("absoluteValue", BR_S(KT_MATH.BigRational.get_absoluteValue(x)), "6/4");
    expect("numerator", BD_S(KT_MATH.BigRational.get_numerator(x)), "-6");
    expect("denominator", BD_S(KT_MATH.BigRational.get_denominator(x)), "4");
    expect("numeratorBigInteger", BI_S(KT_MATH.BigRational.get_numeratorBigInteger(x)), "-6");
    expect("denominatorBigInteger", BI_S(KT_MATH.BigRational.get_denominatorBigInteger(x)), "4");
    CHECK("isInteger, isZero, signum", !KT_MATH.BigRational.get_isInteger(x) && !KT_MATH.BigRational.get_isZero(x) && KT_MATH.BigRational.get_signum(x) == -1);
    CHECK("compare", KT_MATH.BigRational.compare(x, third) < 0 && KT_MATH.BigRational.compareTo(third, x) > 0);
    CHECK("equals, hashCode", KT_MATH.BigRational.equals(x, ANY(KT_MATH.BigRational.Companion.valueOf_(c, -6, 4))) &&
        KT_MATH.BigRational.hashCode(x) == KT_MATH.BigRational.hashCode(KT_MATH.BigRational.Companion.valueOf_(c, -6, 4)));
    expect("increment", BR_S(KT_MATH.BigRational.increment(x)), "-2/4");
    expect("decrement", BR_S(KT_MATH.BigRational.decrement(x)), "-10/4");
    expect("integerPart", BR_S(KT_MATH.BigRational.integerPart(x)), "-4/4");
    expect("fractionPart", BR_S(KT_MATH.BigRational.fractionPart(x)), "-2/4");
    expect("reciprocal", BR_S(KT_MATH.BigRational.reciprocal(x)), "-4/6");
    expect("unaryMinus", BR_S(KT_MATH.BigRational.unaryMinus(x)), "6/4");
    expect("plus", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.plus(x, third))), "-7/6");
    expect("plus(BigInteger)", BR_S(KT_MATH.BigRational.plus_(x, one)), "-2/4");
    expect("plus(Int)", BR_S(KT_MATH.BigRational.plus__(x, 1)), "-2/4");
    expect("minus", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.minus(x, third))), "-11/6");
    expect("minus(BigInteger)", BR_S(KT_MATH.BigRational.minus_(x, one)), "-10/4");
    expect("minus(Int)", BR_S(KT_MATH.BigRational.minus__(x, 1)), "-10/4");
    expect("times", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.times(x, third))), "-1/2");
    expect("times(BigInteger)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.times_(x, two))), "-3");
    expect("times(Int)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.times__(x, 2))), "-3");
    expect("div", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.div(x, third))), "-9/2");
    expect("div(BigInteger)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.div_(x, two))), "-3/4");
    expect("div(Int)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.div__(x, 3))), "-1/2");
    expect("pow", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigRational.pow(x, 2))), "9/4");
    expect("withPrecision", BR_S(KT_MATH.BigRational.withPrecision(x, 1)), "-2");
    expect("withScale", BR_S(KT_MATH.BigRational.withScale(x, 0)), "-2");
    expect("toBigDecimal", BD_S(KT_MATH.BigRational.toBigDecimal(x)), "-1.5");
    expect("toBigDecimal(mc)", BD_S(KT_MATH.BigRational.toBigDecimal_(third, mc3)), "0.333");
    expect("toBigInteger", BI_S(KT_MATH.BigRational.toBigInteger(x)), "-1");
    CHECK("toDouble, toFloat", KT_MATH.BigRational.toDouble(x) == -1.5 && KT_MATH.BigRational.toFloat(x) == -1.5f);
    CHECK("toInt, toLong", KT_MATH.BigRational.toInt(x) == -1 && KT_MATH.BigRational.toLong(x) == -1);
    expect("toIntegerRationalString", KT_MATH.BigRational.toIntegerRationalString(x), "-1 2/4");
    expect("toPlainString", KT_MATH.BigRational.toPlainString(x), "-1.5");
    expect("toString", KT_MATH.BigRational.toString(x), "-1.5");
}

static void api_BigComplex(void) {
    libktmath_kref_org_gciatto_kt_math_big_BigComplex_Companion c = KT_MATH.big.BigComplex.Companion._instance();
    libktmath_kref_org_gciatto_kt_math_BigDecimal_Companion dc = KT_MATH.BigDecimal.Companion._instance();
    BD two = KT_MATH.BigDecimal.Companion.get_TWO(dc);
    BD zero = KT_MATH.BigDecimal.Companion.get_ZERO(dc);
    BC i = KT_MATH.big.BigComplex.Companion.get_I(c);
    expect("I", BC_S(i), "(0 + 1 i)");
    expect("ONE", BC_S(KT_MATH.big.BigComplex.Companion.get_ONE(c)), "(1 + 0 i)");
    expect("ZERO", BC_S(KT_MATH.big.BigComplex.Companion.get_ZERO(c)), "(0 + 0 i)");
    BC w = KT_MATH.big.BigComplex.Companion.valueOf(c, KT_MATH.BigDecimal.Companion.of_(dc, 1), two);
    expect("valueOf", BC_S(w), "(1 + 2 i)");
    expect("valueOf(Double)", BC_S(KT_MATH.big.BigComplex.Companion.valueOf_(c, 1.5)), "(1.5 + 0 i)");
    BC z = KT_MATH.big.BigComplex.Companion.valueOf__(c, 3.0, 4.0);
    expect("valueOf(Double, Double)", BC_S(z), "(3.0 + 4.0 i)");
    expect("valueOfPolar", BC_S(KT_MATH.big.BigComplex.Companion.valueOfPolar(c, two, zero, mc)), "(2 + 0 i)");
    expect("valueOfPolar(Double)", BC_S(KT_MATH.big.BigComplex.Companion.valueOfPolar_(c, 2.0, 0.0, mc)), "(2.0000000 + 0.0000 i)");
    expect("re", BD_S(KT_MATH.big.BigComplex.get_re(z)), "3.0");
    expect("im", BD_S(KT_MATH.big.BigComplex.get_im(z)), "4.0");
    expect("re()", BC_S(KT_MATH.big.BigComplex.re(z)), "(3.0 + 0 i)");
    expect("im()", BC_S(KT_MATH.big.BigComplex.im(z)), "(0 + 4.0 i)");
    CHECK("isReal", !KT_MATH.big.BigComplex.isReal(z));
    BC exact = KT_MATH.big.BigComplex.Companion.valueOf(c, KT_MATH.BigDecimal.Companion.of_(dc, 3), KT_MATH.BigDecimal.Companion.of_(dc, 4));
    CHECK("equals, strictEquals, hashCode", KT_MATH.big.BigComplex.equals(z, ANY(exact)) && !KT_MATH.big.BigComplex.strictEquals(z, ANY(exact)) &&
        KT_MATH.big.BigComplex.hashCode(z) == KT_MATH.big.BigComplex.hashCode(exact));

    expect("plus", BC_S(KT_MATH.big.BigComplex.plus(z, w)), "(4.0 + 6.0 i)");
    expect("plus(mc)", BC_S(KT_MATH.big.BigComplex.plus_(z, w, mc)), "(4.0 + 6.0 i)");
    expect("plus(BigDecimal, mc)", BC_S(KT_MATH.big.BigComplex.plus__(z, two, mc)), "(5.0 + 4.0 i)");
    expect("plus(BigDecimal)", BC_S(KT_MATH.big.BigComplex.plus___(z, two)), "(5.0 + 4.0 i)");
    expect("plus(Double)", BC_S(KT_MATH.big.BigComplex.plus____(z, 1.0)), "(4.0 + 4.0 i)");
    expect("minus", BC_S(KT_MATH.big.BigComplex.minus(z, w)), "(2.0 + 2.0 i)");
    expect("minus(mc)", BC_S(KT_MATH.big.BigComplex.minus_(z, w, mc)), "(2.0 + 2.0 i)");
    expect("minus(BigDecimal, mc)", BC_S(KT_MATH.big.BigComplex.minus__(z, two, mc)), "(1.0 + 4.0 i)");
    expect("minus(BigDecimal)", BC_S(KT_MATH.big.BigComplex.minus___(z, two)), "(1.0 + 4.0 i)");
    expect("minus(Double)", BC_S(KT_MATH.big.BigComplex.minus____(z, 1.0)), "(2.0 + 4.0 i)");
    expect("times", BC_S(KT_MATH.big.BigComplex.times(z, w)), "(-5.0 + 10.0 i)");
    expect("times(mc)", BC_S(KT_MATH.big.BigComplex.times_(z, w, mc)), "(-5.0 + 10.0 i)");
    expect("times(BigDecimal, mc)", BC_S(KT_MATH.big.BigComplex.times__(z, two, mc)), "(6.0 + 8.0 i)");
    expect("times(BigDecimal)", BC_S(KT_MATH.big.BigComplex.times___(z, two)), "(6.0 + 8.0 i)");
    expect("times(Double)", BC_S(KT_MATH.big.BigComplex.times____(z, 2.0)), "(6.00 + 8.00 i)");
    expect("div", BC_S(KT_MATH.big.BigComplex.div(z, w, mc)), "(2.20 - 0.40 i)");
    expect("div(BigDecimal)", BC_S(KT_MATH.big.BigComplex.div_(z, two, mc)), "(1.5 + 2.0 i)");
    expect("div(Double)", BC_S(KT_MATH.big.BigComplex.div__(z, 2.0, mc)), "(1.5 + 2 i)");
    expect("reciprocal", BC_S(KT_MATH.big.BigComplex.reciprocal(z, mc)), "(0.12 - 0.16 i)");
    expect("conjugate", BC_S(KT_MATH.big.BigComplex.conjugate(z)), "(3.0 - 4.0 i)");
    expect("unaryMinus", BC_S(KT_MATH.big.BigComplex.unaryMinus(z)), "(-3.0 - 4.0 i)");
    expect("round", BC_S(KT_MATH.big.BigComplex.round(z, KT_MATH.MathContext.MathContext(1, KT_MATH.RoundingMode.HALF_UP.get()))), "(3 + 4 i)");
    expect("abs", BD_S(KT_MATH.big.BigComplex.abs(z, mc)), "5.0");
    expect("absSquare", BD_S(KT_MATH.big.BigComplex.absSquare(z, mc)), "25.00");
    expect("angle", BD_S(KT_MATH.big.BigComplex.angle(z, mc)), "0.9272952180");

    expect("exp", BC_S(KT_MATH.big.BigComplex.exp(i, mc)), "(0.5403023059 + 0.8414709848 i)");
    expect("log", BC_S(KT_MATH.big.BigComplex.log(KT_MATH.big.BigComplex.Companion.valueOf_(c, -1.0), mc)), "(0 + 3.141592654 i)");
    expect("sqrt", BC_S(KT_MATH.big.BigComplex.sqrt(KT_MATH.big.BigComplex.Companion.valueOf_(c, -4.0), mc)), "(0 + 2.0 i)");
    expect("sin", BC_S(KT_MATH.big.BigComplex.sin(i, mc)), "(0E-13 + 1.175201194 i)");
    expect("cos", BC_S(KT_MATH.big.BigComplex.cos(i, mc)), "(1.543080635 + 0E-13 i)");
    expect("tan", BC_S(KT_MATH.big.BigComplex.tan(i, mc)), "(0E-31 + 0.7615941560 i)");
    expect("atan", BC_S(KT_MATH.big.BigComplex.atan(z, mc)), "(1.448306995 + 0.1589971917 i)");
    expect("acot", BC_S(KT_MATH.big.BigComplex.acot(z, mc)), "(0.1224893316 - 0.1589971917 i)");
    expect("asin", BC_S(KT_MATH.big.BigComplex.asin(z, mc)), "(0.6339838656 + 2.305509031 i)");
    expect("acos", BC_S(KT_MATH.big.BigComplex.acos(z, mc)), "(0.9368124612 - 2.305509031 i)");
    BC five = KT_MATH.big.BigComplex.Companion.valueOf_(c, 5.0);
    expect("factorial", BC_S(KT_MATH.big.BigComplex.factorial(five, mc)), "(120 + 0 i)");
    expect("gamma", BC_S(KT_MATH.big.BigComplex.gamma(five, mc)), "(24 + 0 i)");
    expect("pow(Long)", BC_S(KT_MATH.big.BigComplex.pow(z, 2LL, mc)), "(-7.00 + 24.00 i)");
    expect("pow(BigDecimal)", BC_S(KT_MATH.big.BigComplex.pow_(z, two, mc)), "(-7.000000000 + 24.00000000 i)");
    expect("pow", BC_S(KT_MATH.big.BigComplex.pow__(w, KT_MATH.big.BigComplex.Companion.valueOf_(c, 2.0), mc)), "(-3.000000000 + 4.000000000 i)");
    expect("root(BigDecimal)", BC_S(KT_MATH.big.BigComplex.root(z, two, mc)), "(2.000000000 + 1.000000000 i)");
    expect("root", BC_S(KT_MATH.big.BigComplex.root_(KT_MATH.big.BigComplex.Companion.valueOf__(c, -7.0, 24.0), KT_MATH.big.BigComplex.Companion.valueOf_(c, 2.0), mc)), "(3.000000000 + 4.000000000 i)");
    expect("root(n, k)", BC_S(KT_MATH.big.BigComplex.root__(KT_MATH.big.BigComplex.Companion.valueOf_(c, 8.0), 3, 1, mc)), "(-1.000000000 + 1.732050808 i)");
}

static void api_big(void) {
    libktmath_kref_org_gciatto_kt_math_BigDecimal_Companion dc = KT_MATH.BigDecimal.Companion._instance();
    BD x = KT_MATH.BigDecimal.Companion.of_____(dc, "2.5", none);
    BD half = KT_MATH.BigDecimal.Companion.get_ONE_HALF(dc);
    BD one = KT_MATH.BigDecimal.Companion.get_ONE(dc);
    BD eight = KT_MATH.BigDecimal.Companion.of_(dc, 8);
    expect("toBigDecimal", BD_S(KT_MATH.big.toBigDecimal("12.54", mc3)), "12.5");
    CHECK("isIntValue, isLongValue, isDoubleValue", !KT_MATH.big.isIntValue(x) && KT_MATH.big.isLongValue(KT_MATH.BigDecimal.Companion.of_____(dc, "2.0", none)) && KT_MATH.big.isDoubleValue(x));
    expect("mantissa", BD_S(KT_MATH.big.mantissa(KT_MATH.BigDecimal.Companion.of_(dc, 1234))), "1.234");
    CHECK("exponent", KT_MATH.big.exponent(KT_MATH.BigDecimal.Companion.of_(dc, 1234)) == 3);
    CHECK("significantDigits", KT_MATH.big.significantDigits(KT_MATH.BigDecimal.Companion.of_____(dc, "1.200", none)) == 2);
    expect("integralPart", BD_S(KT_MATH.big.integralPart(x)), "2");
    expect("fractionalPart", BD_S(KT_MATH.big.fractionalPart(x)), "0.5");
    expect("roundWithTrailingZeroes", BD_S(KT_MATH.big.roundWithTrailingZeroes(x, KT_MATH.MathContext.MathContext(4, KT_MATH.RoundingMode.HALF_UP.get()))), "2.500");
    expect("reciprocal", BD_S(KT_MATH.big.reciprocal(x, mc)), "0.4");
    expect("factorial(Int)", BD_S(KT_MATH.big.factorial(5)), "120");
    expect("factorial", BD_S(KT_MATH.big.factorial_(x, mc)), "3.323350970");
    expect("gamma", BD_S(KT_MATH.big.gamma(x, mc)), "1.329340388");
    expect("bernoulli", BD_S(KT_MATH.big.bernoulli(2, mc)), "0.1666666667");
    expect("pow", BD_S(KT_MATH.big.pow(x, x, mc)), "9.882117688");
    expect("pow(Long)", BD_S(KT_MATH.big.pow_(x, 2LL, mc)), "6.25");
    expect("root", BD_S(KT_MATH.big.root(eight, KT_MATH.BigDecimal.Companion.of_(dc, 3), mc)), "2.0");
    expect("log2", BD_S(KT_MATH.big.log2(eight, mc)), "3.000000000");
    expect("log10", BD_S(KT_MATH.big.log10(KT_MATH.BigDecimal.Companion.of_(dc, 1000), mc)), "3.000000000");
    expect("pi", BD_S(KT_MATH.big.pi(mc)), "3.141592654");
    expect("e", BD_S(KT_MATH.big.e(mc)), "2.718281828");
    expect("exp", BD_S(KT_MATH.big.exp(one, mc)), "2.718281828");
    expect("sin", BD_S(KT_MATH.big.sin(half, mc)), "0.4794255386");
    expect("asin", BD_S(KT_MATH.big.asin(half, mc)), "0.5235987756");
    expect("cos", BD_S(KT_MATH.big.cos(half, mc)), "0.8775825619");
    expect("acos", BD_S(KT_MATH.big.acos(half, mc)), "1.047197551");
    expect("tan", BD_S(KT_MATH.big.tan(half, mc)), "0.5463024898");
    expect("atan", BD_S(KT_MATH.big.atan(half, mc)), "0.4636476090");
    expect("atan2", BD_S(KT_MATH.big.atan2(one, one, mc)), "0.7853981634");
    expect("cot", BD_S(KT_MATH.big.cot(half, mc)), "1.830487722");
    expect("acot", BD_S(KT_MATH.big.acot(half, mc)), "1.107148718");
    expect("sinh", BD_S(KT_MATH.big.sinh(half, mc)), "0.5210953055");
    expect("cosh", BD_S(KT_MATH.big.cosh(half, mc)), "1.127625965");
    expect("tanh", BD_S(KT_MATH.big.tanh(half, mc)), "0.4621171573");
    expect("coth", BD_S(KT_MATH.big.coth(half, mc)), "2.163953414");
    expect("asinh", BD_S(KT_MATH.big.asinh(half, mc)), "0.4812118251");
    expect("acosh", BD_S(KT_MATH.big.acosh(x, mc)), "1.566799237");
    expect("atanh", BD_S(KT_MATH.big.atanh(half, mc)), "0.5493061443");
    expect("acoth", BD_S(KT_MATH.big.acoth(x, mc)), "0.4236489302");
    expect("toDegrees", BD_S(KT_MATH.big.toDegrees(KT_MATH.big.pi(mc), mc)), "180.0000000");
    expect("toRadians", BD_S(KT_MATH.big.toRadians(KT_MATH.BigDecimal.Companion.of_(dc, 180), mc)), "3.141592654");
    // Kotlin sequences are opaque to C
    CHECK("range, rangeClosed", KT_MATH.big.range(KT_MATH.BigDecimal.Companion.get_ZERO(dc), one, half, mc).pinned != NULL &&
        KT_MATH.big.rangeClosed(KT_MATH.BigDecimal.Companion.get_ZERO(dc), one, half, mc).pinned != NULL);
}

static void api_BigFactory(libktmath_kref_org_gciatto_kt_math_BigFactory f) {
    libktmath_kref_org_gciatto_kt_math_BigFactory_Companion c = KT_MATH.BigFactory.Companion._instance();
    libktmath_kref_org_gciatto_kt_math_BigInteger_Companion ic = KT_MATH.BigInteger.Companion._instance();
    CHECK("DEFAULT_RADIX", KT_MATH.BigFactory.Companion.get_DEFAULT_RADIX(c) == 10);
    CHECK("default", KT_MATH.BigFactory.Companion.get_default(c).pinned != NULL);
    BI ten = KT_MATH.BigInteger.Companion.get_TEN(ic);
    expect("bigDecimalOf(Int)", BD_S(KT_MATH.BigFactory.bigDecimalOf(f, 1, none)), "1");
    expect("bigDecimalOf(Long)", BD_S(KT_MATH.BigFactory.bigDecimalOf_(f, 2LL, mc)), "2");
    expect("bigDecimalOf(Double)", BD_S(KT_MATH.BigFactory.bigDecimalOf__(f, 0.5, none)), "0.5");
    expect("bigDecimalOf(BigInteger)", BD_S(KT_MATH.BigFactory.bigDecimalOf___(f, ten, none)), "10");
    expect("bigDecimalOf(unscaled, scale)", BD_S(KT_MATH.BigFactory.bigDecimalOf____(f, KT_MATH.BigInteger.Companion.of_(ic, 1234), 2, none)), "12.34");
    expect("bigDecimalOf(Long unscaled, scale)", BD_S(KT_MATH.BigFactory.bigDecimalOf_____(f, 1234LL, 3)), "1.234");
    expect("bigDecimalValueOf", BD_S(KT_MATH.BigFactory.bigDecimalValueOf(f, 0.1)), "0.1");
    expect("bigIntegerOf(Long)", BI_S(KT_MATH.BigFactory.bigIntegerOf_(f, 2LL)), "2");
    expect("bigIntegerOf(signum, bytes)", BI_S(KT_MATH.BigFactory.bigIntegerOf___(f, -1, KT_MATH.BigInteger.toByteArray(KT_MATH.BigInteger.Companion.of_(ic, 256)), 0, 2)), "-256");
    expect("bigRationalOf(Int)", BR_S(KT_MATH.BigFactory.bigRationalOf(f, 1)), "1");
    expect("bigRationalOf(Long)", BR_S(KT_MATH.BigFactory.bigRationalOf_(f, 2LL)), "2");
    expect("bigRationalOf(BigInteger)", BR_S(KT_MATH.BigFactory.bigRationalOf__(f, ten)), "10");
    expect("bigRationalOf(BigDecimal)", BR_S(KT_MATH.BigFactory.bigRationalOf___(f, KT_MATH.BigFactory.parseBigDecimal(f, "1.5", none))), "15/10");
    expect("bigRationalOf(Double)", BR_S(KT_MATH.BigFactory.bigRationalOf____(f, 0.5)), "5/10");
    expect("bigRationalOf(BigInteger, BigInteger)", BR_S(KT_MATH.BigFactory.bigRationalOf______(f, KT_MATH.BigInteger.Companion.get_ONE(ic), KT_MATH.BigInteger.Companion.get_TWO(ic))), "1/2");
    expect("bigRationalOf(BigDecimal, BigDecimal)", BR_S(KT_MATH.BigRational.reduce(KT_MATH.BigFactory.bigRationalOf_______(f, KT_MATH.BigFactory.bigDecimalOf(f, 1, none), KT_MATH.BigFactory.bigDecimalOf(f, 4, none)))), "1/4");
    expect("parseBigDecimalOrNull", BD_S(KT_MATH.BigFactory.parseBigDecimalOrNull(f, "1.5", none)), "1.5");
    CHECK("parseBigDecimalOrNull(invalid)", KT_MATH.BigFactory.parseBigDecimalOrNull(f, "1..5", none).pinned == NULL);
    CHECK("probablePrime", KT_MATH.BigInteger.get_bitLength(KT_MATH.BigFactory.probablePrime_(f, 64)) == 64);
}

static void api_MathContext(void) {
    libktmath_kref_org_gciatto_kt_math_MathContext_Companion c = KT_MATH.MathContext.Companion._instance();
    CHECK("DECIMAL128", KT_MATH.MathContext.get_precision(KT_MATH.MathContext.Companion.get_DECIMAL128(c)) == 34);
    CHECK("DECIMAL32", KT_MATH.MathContext.get_precision(KT_MATH.MathContext.Companion.get_DECIMAL32(c)) == 7);
    CHECK("UNLIMITED", KT_MATH.MathContext.get_precision(KT_MATH.MathContext.Companion.get_UNLIMITED(c)) == 0);
    MC context = KT_MATH.MathContext.MathContext(5, KT_MATH.RoundingMode.DOWN.get());
    CHECK("component1", KT_MATH.MathContext.component1(context) == 5);
    CHECK("roundingMode, component2", KT_MATH.RoundingMode.get_value(KT_MATH.MathContext.get_roundingMode(context)) == 1 &&
        KT_MATH.RoundingMode.get_value(KT_MATH.MathContext.component2(context)) == 1);
    MC decimal64 = KT_MATH.MathContext.copy(context, 16, KT_MATH.RoundingMode.HALF_EVEN.get());
    CHECK("copy, equals, hashCode", KT_MATH.MathContext.equals(decimal64, ANY(KT_MATH.MathContext.Companion.get_DECIMAL64(c))) &&
        KT_MATH.MathContext.hashCode(decimal64) == KT_MATH.MathContext.hashCode(KT_MATH.MathContext.Companion.get_DECIMAL64(c)));
    expect("toString", KT_MATH.MathContext.toString(context), "MathContext(precision=5, roundingMode=DOWN)");
}

static void api_RoundingMode(void) {
    // value: java.math's int constant of each rounding mode
    CHECK("UP", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.UP.get()) == 0);
    CHECK("DOWN", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.DOWN.get()) == 1);
    CHECK("CEILING", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.CEILING.get()) == 2);
    CHECK("FLOOR", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.FLOOR.get()) == 3);
    CHECK("HALF_UP", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.HALF_UP.get()) == 4);
    CHECK("HALF_DOWN", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.HALF_DOWN.get()) == 5);
    CHECK("HALF_EVEN", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.HALF_EVEN.get()) == 6);
    CHECK("UNNECESSARY", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.UNNECESSARY.get()) == 7);
    libktmath_kref_org_gciatto_kt_math_RoundingMode_Companion c = KT_MATH.RoundingMode.Companion._instance();
    CHECK("valueOf", KT_MATH.RoundingMode.get_value(KT_MATH.RoundingMode.Companion.valueOf(c, 4)) == 4);
}

static void api_conversions(void) {
    expect("Int.toBigDecimal", BD_S(KT_MATH.toBigDecimal(2, none)), "2");
    expect("Long.toBigDecimal", BD_S(KT_MATH.toBigDecimal_(3LL, none)), "3");
    expect("Double.toBigDecimal", BD_S(KT_MATH.toBigDecimal__(0.1)), "0.1");
    expect("String.toBigDecimal", BD_S(KT_MATH.toBigDecimal___("1.0", none)), "1.0");
    CHECK("String.toBigDecimalOrNull", KT_MATH.toBigDecimalOrNull("1..0", none).pinned == NULL);
    expect("Int.toBigInteger", BI_S(KT_MATH.toBigInteger(1)), "1");
    expect("Long.toBigInteger", BI_S(KT_MATH.toBigInteger_(2LL)), "2");
    expect("String.toBigInteger", BI_S(KT_MATH.toBigInteger__("ff", 16)), "255");
    CHECK("String.toBigIntegerOrNull", KT_MATH.toBigIntegerOrNull("ff", 10).pinned == NULL);
    expect("Int.toBigRational", BR_S(KT_MATH.toBigRational(4)), "4");
    expect("Long.toBigRational", BR_S(KT_MATH.toBigRational_(5LL)), "5");
    expect("Double.toBigRational", BR_S(KT_MATH.toBigRational__(0.5)), "5/10");
    expect("String.toBigRational", BR_S(KT_MATH.toBigRational___("2/8")), "2/8");
    CHECK("String.toBigRationalOrNull", KT_MATH.toBigRationalOrNull("1/0").pinned == NULL);
}

int main(void) {
    // --8<-- [start:main]
    lib = libktmath_symbols();
    libktmath_kref_org_gciatto_kt_math_BigFactory_Companion companion = KT_MATH.BigFactory.Companion._instance();
    libktmath_kref_org_gciatto_kt_math_BigFactory factory = KT_MATH.BigFactory.Companion.get_default(companion);
    big_integers(factory);
    big_decimals(factory);
    big_rationals(factory);
    // --8<-- [end:main]
    mc = KT_MATH.MathContext.MathContext(10, KT_MATH.RoundingMode.HALF_UP.get());
    mc3 = KT_MATH.MathContext.MathContext(3, KT_MATH.RoundingMode.HALF_UP.get());
    none.pinned = NULL;
    api_BigInteger();
    api_BigIntegerRange();
    api_BigDecimal();
    api_BigRational();
    api_BigComplex();
    api_big();
    api_BigFactory(factory);
    api_MathContext();
    api_RoundingMode();
    api_conversions();
    DISPOSE(factory);
    DISPOSE(companion);
    printf("%d failures\n", failures);
    return failures == 0 ? 0 : 1;
}
