// kt-math as plain JavaScript code sees it: the npm package as published (`./gradlew jsUsageTest` assembles it and
// passes its path), run by Node's built-in test runner. The documentation website quotes the sections between the
// `--8<--` markers (getting-started-js.md).
const { test } = require("node:test");
const assert = require("node:assert/strict");
// --8<-- [start:import]
const { org } = require(process.env.KT_MATH_JS_PACKAGE); // require("kt-math") in your project

const {
  BigInteger, BigDecimal, BigRational, BigFactory, MathContext, RoundingMode, bigInteger, bigDecimal, bigRational,
} = org.gciatto.kt.math;
// --8<-- [end:import]

test("BigIntegers from JS values, operations as methods", () => {
  // --8<-- [start:big-integers]
  const x = bigInteger("123456789012345678901234567890");
  const y = bigInteger(42);
  assert.equal(x.plus(y).toString(), "123456789012345678901234567932");
  assert.equal(x.times(y).toString(), "5185185138518518513851851851380");
  assert.equal(x.div(y).toString(), "2939447357436801878600823045");
  assert.equal(x.rem(bigInteger(11)).toString(), "7");
  assert.equal(x.plusInt(1).toString(), "123456789012345678901234567891");
  assert.equal(bigInteger(["ff", 16]).toString(), "255");
  assert.equal(bigInteger(12n).toString(), "12"); // native BigInts are accepted...
  assert.equal(typeof y.toLong(), "bigint"); // ...and Kotlin Longs are BigInts
  assert.equal(y.toLong(), 42n);
  assert.equal(BigInteger.Companion.parseWithRadix("-ff", 16).toString(), "-255");
  assert.ok(x.compareTo(y) > 0);
  assert.ok(bigInteger(12).equals(BigInteger.Companion.of(12)));
  assert.equal(BigInteger.Companion.TEN.pow(3).toString(), "1000");
  // --8<-- [end:big-integers]
});

test("errors are JS Errors, named after the Kotlin exceptions", () => {
  // --8<-- [start:errors]
  assert.throws(() => bigInteger(1).div(BigInteger.Companion.ZERO), { name: "ArithmeticException" });
  assert.throws(() => bigInteger("12x"), (e) => e instanceof Error && e.constructor.name === "NumberFormatException");
  // --8<-- [end:errors]
});

test("BigDecimals with MathContexts and RoundingModes", () => {
  // --8<-- [start:big-decimals]
  const a = bigDecimal("1.10");
  const b = bigDecimal(3);
  assert.equal(a.plus(b).toString(), "4.10");
  assert.equal(a.divWithContext(b, new MathContext(10, RoundingMode.HALF_EVEN)).toString(), "0.3666666667");
  assert.equal(a.divWithScale(b, 2, RoundingMode.HALF_UP).toString(), "0.37");
  assert.equal(bigDecimal(["1234.5", new MathContext(2)]).toString(), "1.2E+3");
  assert.equal(bigDecimal(0.1).toString(), "0.1"); // JS numbers go through their string representation
  assert.equal(BigDecimal.Companion.valueOfDouble(0.1).toString(), "0.1");
  assert.equal(bigDecimal(2).sqrt(new MathContext(10)).toString(), "1.414213562");
  assert.equal(bigDecimal("1.0").compareTo(bigDecimal("1.00")), 0); // equal values, while equals checks scales too
  assert.ok(!bigDecimal("1.0").equals(bigDecimal("1.00")));
  assert.throws(() => BigDecimal.Companion.ONE.div(b), { name: "ArithmeticException" }); // 1/3 isn't exact
  // --8<-- [end:big-decimals]
});

test("BigRationals", () => {
  // --8<-- [start:big-rationals]
  const third = bigRational("1/3");
  const sum = third.plus(bigRational([1, 6]));
  assert.equal(sum.reduce().toRationalString(), "1/2");
  assert.equal(sum.toBigDecimalWithContext(new MathContext(3)).toString(), "0.5"); // exact: no trailing zeros
  assert.equal(BigRational.Companion.ofFraction(37, 3).toBigInteger().toString(), "12");
  assert.equal(third.timesInt(3).compareTo(BigRational.Companion.ONE), 0);
  // --8<-- [end:big-rationals]
});

test("the default BigFactory", () => {
  // --8<-- [start:factory]
  const factory = BigFactory.Companion.default;
  assert.equal(factory.parseBigInteger("-ff", 16).toString(), "-255");
  assert.equal(factory.parseBigIntegerOrNull("ff", 10), null);
  assert.equal(factory.parseBigDecimal("1234", new MathContext(2)).toString(), "1.2E+3");
  assert.equal(factory.parseBigRationalOrNull("1/0"), null);
  assert.equal(factory.bigRationalOfInts(2, 6).reduce().toRationalString(), "1/3");
  assert.equal(factory.probablePrime(64).bitLength, 64);
  // --8<-- [end:factory]
});

test("conversions", () => {
  const x = bigInteger(1234);
  assert.equal(x.toBigDecimalWithScale(2).toString(), "12.34");
  assert.equal(x.toBigDecimalWithScale(2).toBigRational().reduce().toRationalString(), "617/50");
  assert.equal(bigDecimal("12.99").toBigInteger().toString(), "12");
  assert.equal(x.toBigRational().toBigDecimal().toString(), "1234");
});
