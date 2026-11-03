// kt-math as JavaScript code sees it: the npm package as published (`./gradlew jsUsageTest` assembles it into
// node_modules/kt-math, next to a copy of this file), run by Node's built-in test runner. The documentation website
// quotes the sections between the `--8<--` markers (getting-started-js.md). Each `// api:<Type>` section must mention
// every member of <Type> (see api-coverage.mjs): the last test checks it.
import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { uncovered } from "./api-coverage.mjs";
// --8<-- [start:import]
import {
  BigInteger, BigDecimal, BigRational, BigComplex, BigFactory, BigIntegerRange, MathContext, RoundingMode,
  bigInteger, bigDecimal, bigRational, pi, log,
} from "kt-math";
// --8<-- [end:import]
import * as kt from "kt-math";

const str = (x) => x.toString();
const mc = new MathContext(10);

test("BigInteger", () => {
  // api:BigInteger
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
  assert.equal(BigInteger.parseWithRadix("-ff", 16).toString(), "-255");
  assert.ok(x.compareTo(y) > 0);
  assert.ok(bigInteger(12).equals(BigInteger.of(12)));
  assert.equal(BigInteger.TEN.pow(3).toString(), "1000");
  // --8<-- [end:big-integers]

  // factories and constants
  assert.equal(str(BigInteger.ofLong(-5n)), "-5");
  assert.equal(str(BigInteger.parse("0x1f")), "31");
  assert.equal(str(BigInteger.ofIntArray(new Int32Array([1, 0]))), "4294967296"); // big-endian 32-bit words
  assert.equal(str(BigInteger.ofSignMagnitudeSlice(-1, new Int8Array([9, 1, 0, 9]), 1, 2)), "-256");
  assert.equal(str(BigInteger.ofBytes(new Int8Array([0, -1]))), "255"); // big-endian two's complement
  assert.equal(str(BigInteger.ofSignMagnitude(-1, new Int8Array([1, 0]))), "-256");
  assert.deepEqual([BigInteger.ZERO, BigInteger.ONE, BigInteger.TWO, BigInteger.NEGATIVE_ONE].map(str), ["0", "1", "2", "-1"]);

  // properties
  const n = BigInteger.of(-12);
  assert.equal(str(n.absoluteValue), "12");
  assert.equal(n.signum, -1);
  assert.equal(n.bitLength, 4);
  assert.equal(BigInteger.of(7).bitCount, 3);

  // arithmetic, with BigInteger, number (Int) and bigint (Long) operands
  assert.deepEqual([y.plusLong(1n), y.minus(BigInteger.TWO), y.minusInt(2), y.minusLong(2n)].map(str), ["43", "40", "40", "40"]);
  assert.deepEqual([y.timesInt(2), y.timesLong(2n), y.divInt(5), y.divLong(5n)].map(str), ["84", "84", "8", "8"]);
  assert.deepEqual(y.divideAndRemainder(BigInteger.of(5)).map(str), ["8", "2"]);
  assert.deepEqual(y.divideAndRemainderInt(5).map(str), ["8", "2"]);
  assert.deepEqual(y.divideAndRemainderLong(5n).map(str), ["8", "2"]);
  const m = BigInteger.of(-7); // remainders take the sign of the dividend
  assert.deepEqual([m.remainder(BigInteger.of(3)), m.remainderInt(3), m.remainderLong(3n)].map(str), ["-1", "-1", "-1"]);
  assert.deepEqual([m.remInt(3), m.remLong(3n)].map(str), ["-1", "-1"]);
  assert.deepEqual([m.unaryMinus(), m.unaryPlus()].map(str), ["7", "-7"]);
  assert.equal(str(BigInteger.of(17).sqrt()), "4");
  assert.deepEqual(BigInteger.of(17).sqrtAndRemainder().map(str), ["4", "1"]);
  assert.deepEqual([y.gcd(BigInteger.of(30)), y.gcdInt(30), y.gcdLong(30n)].map(str), ["6", "6", "6"]);
  assert.deepEqual([y.min(x), y.max(x)].map(str), ["42", str(x)]);

  // modular arithmetic and primes
  assert.equal(str(BigInteger.of(4).modPow(BigInteger.of(13), BigInteger.of(497))), "445");
  assert.deepEqual([BigInteger.of(3).modInverse(BigInteger.of(11)), BigInteger.of(3).modInverseInt(11), BigInteger.of(3).modInverseLong(11n)].map(str), ["4", "4", "4"]);
  assert.ok(BigInteger.of(97).isProbablePrime(50));
  assert.equal(str(BigInteger.of(97).nextProbablePrime()), "101");

  // bits (two's complement)
  const b = BigInteger.of(0b1100);
  assert.deepEqual([b.shl(2), b.shr(2), b.not()].map(str), ["48", "3", "-13"]);
  assert.deepEqual([b.and(BigInteger.of(10)), b.andInt(10), b.andLong(10n)].map(str), ["8", "8", "8"]);
  assert.deepEqual([b.or(BigInteger.of(3)), b.orInt(3), b.orLong(3n)].map(str), ["15", "15", "15"]);
  assert.deepEqual([b.xor(BigInteger.of(10)), b.xorInt(10), b.xorLong(10n)].map(str), ["6", "6", "6"]);
  assert.deepEqual([b.andNot(BigInteger.of(4)), b.andNotInt(4), b.andNotLong(4n)].map(str), ["8", "8", "8"]);
  assert.ok(b.testBit(2) && b.get(3) && !b.get(0));
  assert.deepEqual([b.set(0, true), b.setBit(1), b.clearBit(2), b.flipBit(3)].map(str), ["13", "14", "8", "4"]);

  // conversions
  assert.equal(BigInteger.of(255).toStringWithRadix(16), "ff");
  assert.deepEqual([...BigInteger.of(255).toByteArray()], [0, -1]);
  assert.equal(str(y.toBigDecimal()), "42");
  assert.equal(str(y.toBigDecimalWithScale(1)), "4.2");
  assert.equal(y.toBigRational().toRationalString(), "42");
  assert.deepEqual([y.toInt(), y.toByte(), y.toShort(), y.toFloat(), y.toDouble()], [42, 42, 42, 42, 42]);
  assert.deepEqual([y.toLongExact(), y.toIntExact(), y.toShortExact(), y.toByteExact()], [42n, 42, 42, 42]);
  assert.throws(() => BigInteger.of(300).toByteExact(), { name: "ArithmeticException" });
  assert.equal(BigInteger.of(5).hashCode(), bigInteger(5).hashCode());

  // ranges
  assert.deepEqual(BigInteger.ONE.rangeTo(BigInteger.of(3)).toArray().map(str), ["1", "2", "3"]);
  assert.deepEqual(BigInteger.ONE.rangeToInt(2).toArray().map(str), ["1", "2"]);
  assert.deepEqual(BigInteger.ONE.rangeToLong(0n).toArray(), []);
});

test("BigIntegerRange", () => {
  // api:BigIntegerRange
  const range = new BigIntegerRange(BigInteger.of(1), BigInteger.of(3));
  assert.deepEqual([range.first, range.last].map(str), ["1", "3"]);
  assert.deepEqual(range.toArray().map(str), ["1", "2", "3"]);
  assert.equal(range.toString(), "BigIntegerRange(start=1, endInclusive=3)");
  assert.ok(range.equals(BigInteger.ONE.rangeToInt(3)) && range.hashCode() === BigInteger.ONE.rangeToInt(3).hashCode());
  assert.equal(str(range.copy(BigInteger.TWO).first), "2");
});

test("errors are JS Errors, named after the Kotlin exceptions", () => {
  // --8<-- [start:errors]
  assert.throws(() => bigInteger(1).div(BigInteger.ZERO), { name: "ArithmeticException" });
  assert.throws(() => bigInteger("12x"), (e) => e instanceof Error && e.constructor.name === "NumberFormatException");
  // --8<-- [end:errors]
});

test("BigDecimal", () => {
  // api:BigDecimal
  // --8<-- [start:big-decimals]
  const a = bigDecimal("1.10");
  const b = bigDecimal(3);
  assert.equal(a.plus(b).toString(), "4.10");
  assert.equal(a.divWithContext(b, new MathContext(10, RoundingMode.HALF_EVEN)).toString(), "0.3666666667");
  assert.equal(a.divWithScale(b, 2, RoundingMode.HALF_UP).toString(), "0.37");
  assert.equal(bigDecimal(["1234.5", new MathContext(2)]).toString(), "1.2E+3");
  assert.equal(bigDecimal(0.1).toString(), "0.1"); // JS numbers go through their string representation
  assert.equal(BigDecimal.valueOfDouble(0.1).toString(), "0.1");
  assert.equal(bigDecimal(2).sqrt(new MathContext(10)).toString(), "1.414213562");
  assert.equal(bigDecimal("1.0").compareTo(bigDecimal("1.00")), 0); // equal values, while equals checks scales too
  assert.ok(!bigDecimal("1.0").equals(bigDecimal("1.00")));
  assert.throws(() => BigDecimal.ONE.div(b), { name: "ArithmeticException" }); // 1/3 isn't exact
  // --8<-- [end:big-decimals]

  // factories and constants
  assert.deepEqual([BigDecimal.of(12), BigDecimal.ofLong(12n), BigDecimal.ofScaledLong(1234n, 2)].map(str), ["12", "12", "12.34"]);
  assert.deepEqual([BigDecimal.ofWithContext(1234, new MathContext(2)), BigDecimal.ofLongWithContext(1234n, new MathContext(2))].map(str), ["1.2E+3", "1.2E+3"]);
  assert.equal(str(BigDecimal.ofDouble(0.5)), "0.5");
  assert.equal(str(BigDecimal.ofDouble(0.1, new MathContext(3))), "0.100"); // the exact binary value, rounded
  assert.equal(str(BigDecimal.ofFloat(0.25)), "0.25");
  assert.deepEqual([BigDecimal.parse("1.50"), BigDecimal.parse("1.55", new MathContext(2))].map(str), ["1.50", "1.6"]);
  assert.equal(str(BigDecimal.ofBigInteger(BigInteger.TEN, mc)), "10");
  assert.deepEqual([BigDecimal.ZERO, BigDecimal.TWO, BigDecimal.TEN, BigDecimal.ONE_TENTH, BigDecimal.ONE_HALF].map(str), ["0", "2", "10", "0.1", "0.5"]);
  assert.ok(BigDecimal.PI.toString().startsWith("3.14159") && BigDecimal.E.toString().startsWith("2.71828"));

  // properties
  const x = bigDecimal("-12.340");
  assert.deepEqual([str(x.absoluteValue), x.signum, x.scale, x.precision, str(x.unscaledValue)], ["12.340", -1, 3, 5, "-12340"]);

  // arithmetic
  const three = new MathContext(3);
  assert.deepEqual([a.plusWithContext(b, three), a.minus(b), a.minusWithContext(b, three), a.times(b), a.timesWithContext(b, three)].map(str), ["4.10", "-1.90", "-1.90", "3.30", "3.30"]);
  assert.deepEqual([bigDecimal("7.5").div(bigDecimal("2.5")), bigDecimal(1).divWithRounding(b, RoundingMode.UP)].map(str), ["3", "1"]);
  assert.deepEqual([bigDecimal("7.5").divideToIntegralValue(bigDecimal(2)), bigDecimal("7.5").divideToIntegralValueWithContext(bigDecimal(2), three)].map(str), ["3.0", "3.0"]);
  assert.deepEqual([bigDecimal("7.5").rem(bigDecimal(2)), bigDecimal("7.5").remWithContext(bigDecimal(2), three)].map(str), ["1.5", "1.5"]);
  const qr = bigDecimal("7.5").divideAndRemainder(bigDecimal(2)); // a Kotlin Pair
  assert.deepEqual([qr.first, qr.second].map(str), ["3.0", "1.5"]);
  assert.equal(str(bigDecimal("7.5").divideAndRemainderWithContext(bigDecimal(2), three).second), "1.5");
  assert.deepEqual([a.pow(2), a.powWithContext(3, new MathContext(2))].map(str), ["1.2100", "1.3"]);
  assert.deepEqual([x.absoluteValueWithContext(three), x.unaryMinus(), x.unaryMinusWithContext(three), x.unaryPlus(), x.unaryPlusWithContext(three)].map(str), ["12.3", "12.340", "12.3", "-12.340", "-12.3"]);
  assert.deepEqual([x.round(three), x.min(a), x.max(a), x.ulp()].map(str), ["-12.3", "-12.340", "1.10", "0.001"]);

  // scales
  assert.deepEqual([a.setScale(3), a.setScaleRounding(0, RoundingMode.HALF_UP), a.setScaleInt(0, 4)].map(str), ["1.100", "1", "1"]);
  assert.deepEqual([a.movePointLeft(1), a.movePointRight(1), a.scaleByPowerOfTen(3), a.stripTrailingZeros()].map(str), ["0.110", "11.0", "1.10E+3", "1.1"]);

  // conversions
  const big = bigDecimal("1.5E+4");
  assert.deepEqual([big.toEngineeringString(), big.toPlainString(), str(big.toBigInteger()), str(big.toBigIntegerExact())], ["15E+3", "15000", "15000", "15000"]);
  assert.equal(bigDecimal("0.25").toBigRational().reduce().toRationalString(), "1/4");
  assert.deepEqual([big.toLong(), big.toLongExact(), big.toInt(), big.toIntExact(), big.toShort(), big.toShortExact()], [15000n, 15000n, 15000, 15000, 15000, 15000]);
  assert.deepEqual([bigDecimal(100).toByte(), bigDecimal(100).toByteExact(), a.toFloat(), a.toDouble()], [100, 100, 1.1, 1.1]); // Kotlin Floats are JS numbers
});

test("BigRational", () => {
  // api:BigRational
  // --8<-- [start:big-rationals]
  const third = bigRational("1/3");
  const sum = third.plus(bigRational([1, 6]));
  assert.equal(sum.reduce().toRationalString(), "1/2");
  assert.equal(sum.toBigDecimalWithContext(new MathContext(3)).toString(), "0.5"); // exact: no trailing zeros
  assert.equal(BigRational.ofFraction(37, 3).toBigInteger().toString(), "12");
  assert.equal(third.timesInt(3).compareTo(BigRational.ONE), 0);
  // --8<-- [end:big-rationals]

  const r = (x) => x.reduce().toRationalString();
  // factories and constants
  assert.deepEqual([BigRational.ofInt(2), BigRational.ofMixedFraction(1, 1, 2), BigRational.ofBigIntegerFraction(BigInteger.of(2), BigInteger.of(4))].map(r), ["2", "3/2", "1/2"]);
  assert.deepEqual([BigRational.ofBigInteger(BigInteger.TEN), BigRational.ofDouble(0.25), BigRational.ofBigDecimal(bigDecimal("0.5"))].map(r), ["10", "1/4", "1/2"]);
  assert.deepEqual([BigRational.parse("-3/6"), BigRational.ofParts(true, "1", "5", null, null), BigRational.ofBigDecimalFraction(bigDecimal(1), bigDecimal(4))].map(r), ["-1/2", "3/2", "1/4"]);
  assert.deepEqual([BigRational.ZERO, BigRational.TWO, BigRational.TEN].map(r), ["0", "2", "10"]);
  assert.deepEqual([BigRational.min([third, sum]), BigRational.max([third, sum]), BigRational.bernoulli(2)].map(r), ["1/3", "1/2", "1/6"]);

  // properties
  const x = BigRational.ofFraction(-6, 4);
  assert.deepEqual([x.numerator, x.denominator].map(str), ["-6", "4"]); // BigDecimals
  assert.deepEqual([x.numeratorBigInteger, x.denominatorBigInteger].map(str), ["-6", "4"]);
  assert.deepEqual([r(x.absoluteValue), x.signum, x.isZero, x.isInteger], ["3/2", -1, false, false]);

  // arithmetic
  assert.deepEqual([x.integerPart(), x.fractionPart(), x.unaryMinus(), x.reciprocal(), x.increment(), x.decrement()].map(r), ["-1", "-1/2", "3/2", "-2/3", "-1/2", "-5/2"]);
  assert.deepEqual([x.plusBigInteger(BigInteger.ONE), x.plusInt(1), x.minus(third), x.minusBigInteger(BigInteger.ONE), x.minusInt(1)].map(r), ["-1/2", "-1/2", "-11/6", "-5/2", "-5/2"]);
  assert.deepEqual([x.times(third), x.timesBigInteger(BigInteger.TWO), x.div(third), x.divBigInteger(BigInteger.TWO), x.divInt(3), x.pow(2)].map(r), ["-1/2", "-3", "-9/2", "-3/4", "-1/2", "9/4"]);
  assert.deepEqual([x.withPrecision(1), x.withScale(0)].map(r), ["-2", "-2"]); // rounded half up

  // conversions
  assert.deepEqual([x.toDouble(), x.toFloat(), x.toInt(), x.toLong(), str(x.toBigDecimal())], [-1.5, -1.5, -1, -1n, "-1.5"]);
  assert.deepEqual([x.toString(), x.toPlainString(), x.toRationalString(), x.toIntegerRationalString()], ["-1.5", "-1.5", "-6/4", "-1 2/4"]);
  assert.ok(x.equals(BigRational.ofFraction(-6, 4)) && x.hashCode() === BigRational.ofFraction(-6, 4).hashCode());
  assert.ok(!x.equals(BigRational.parse("-3/2"))); // not reduced: compare with compareTo
});

test("BigComplex", () => {
  // api:BigComplex
  const z = BigComplex.valueOfDoubles(3, 4); // 3 + 4i
  const w = BigComplex.valueOf(bigDecimal(1), bigDecimal(2)); // 1 + 2i
  const two = bigDecimal(2);
  assert.deepEqual([z.toString(), str(z.re), str(z.im)], ["(3.0 + 4.0 i)", "3.0", "4.0"]);
  assert.deepEqual([BigComplex.ZERO, BigComplex.ONE, BigComplex.I, BigComplex.valueOf(two), BigComplex.valueOfDouble(1.5)].map(str), ["(0 + 0 i)", "(1 + 0 i)", "(0 + 1 i)", "(2 + 0 i)", "(1.5 + 0 i)"]);
  assert.equal(str(BigComplex.valueOfPolar(two, BigDecimal.ZERO, mc)), "(2 + 0 i)");
  assert.equal(str(BigComplex.valueOfPolarDouble(2, 0, mc)), "(2.0000000 + 0.0000 i)");

  // arithmetic: exact, or rounded with a MathContext
  assert.deepEqual([z.plus(w), z.plusWithContext(w, mc), z.plusBigDecimal(two), z.plusBigDecimalWithContext(two, mc), z.plusDouble(1)].map(str), ["(4.0 + 6.0 i)", "(4.0 + 6.0 i)", "(5.0 + 4.0 i)", "(5.0 + 4.0 i)", "(4.0 + 4.0 i)"]);
  assert.deepEqual([z.minus(w), z.minusWithContext(w, mc), z.minusBigDecimal(two), z.minusBigDecimalWithContext(two, mc), z.minusDouble(1)].map(str), ["(2.0 + 2.0 i)", "(2.0 + 2.0 i)", "(1.0 + 4.0 i)", "(1.0 + 4.0 i)", "(2.0 + 4.0 i)"]);
  assert.deepEqual([z.times(w), z.timesWithContext(w, mc), z.timesBigDecimal(two), z.timesBigDecimalWithContext(two, mc), z.timesDouble(2)].map(str), ["(-5.0 + 10.0 i)", "(-5.0 + 10.0 i)", "(6.0 + 8.0 i)", "(6.0 + 8.0 i)", "(6.00 + 8.00 i)"]);
  assert.deepEqual([z.div(w, mc), z.divBigDecimal(two, mc), z.divDouble(2, mc)].map(str), ["(2.20 - 0.40 i)", "(1.5 + 2.0 i)", "(1.5 + 2 i)"]);
  assert.deepEqual([z.reciprocal(mc), z.conjugate(), z.unaryMinus(), z.realPart(), z.imaginaryPart(), z.round(new MathContext(1))].map(str), ["(0.12 - 0.16 i)", "(3.0 - 4.0 i)", "(-3.0 - 4.0 i)", "(3.0 + 0 i)", "(0 + 4.0 i)", "(3 + 4 i)"]);
  assert.deepEqual([z.abs(mc), z.absSquare(mc), z.angle(mc)].map(str), ["5.0", "25.00", "0.9272952180"]);
  assert.ok(!z.isReal() && BigComplex.ONE.isReal());
  assert.ok(z.equals(BigComplex.valueOfDoubles(3, 4)) && z.hashCode() === BigComplex.valueOf(bigDecimal(3), bigDecimal(4)).hashCode());
  assert.ok(z.equals(BigComplex.valueOf(bigDecimal(3), bigDecimal(4))) && !z.strictEquals(BigComplex.valueOf(bigDecimal(3), bigDecimal(4))));

  // functions: big-math's BigComplexMath
  const i = BigComplex.I;
  assert.equal(str(i.exp(mc)), "(0.5403023059 + 0.8414709848 i)");
  assert.equal(str(BigComplex.valueOfDouble(-1).log(mc)), "(0 + 3.141592654 i)");
  assert.equal(str(BigComplex.valueOfDouble(-4).sqrt(mc)), "(0 + 2.0 i)");
  assert.deepEqual([i.sin(mc), i.cos(mc), i.tan(mc)].map(str), ["(0E-13 + 1.175201194 i)", "(1.543080635 + 0E-13 i)", "(0E-31 + 0.7615941560 i)"]);
  assert.deepEqual([z.atan(mc), z.acot(mc), z.asin(mc), z.acos(mc)].map(str), ["(1.448306995 + 0.1589971917 i)", "(0.1224893316 - 0.1589971917 i)", "(0.6339838656 + 2.305509031 i)", "(0.9368124612 - 2.305509031 i)"]);
  assert.deepEqual([BigComplex.valueOfDouble(5).factorial(mc), BigComplex.valueOfDouble(5).gamma(mc)].map(str), ["(120 + 0 i)", "(24 + 0 i)"]);
  assert.deepEqual([z.powLong(2n, mc), z.powBigDecimal(two, mc), w.pow(BigComplex.valueOfDouble(2), mc)].map(str), ["(-7.00 + 24.00 i)", "(-7.000000000 + 24.00000000 i)", "(-3.000000000 + 4.000000000 i)"]);
  assert.deepEqual([z.rootBigDecimal(two, mc), BigComplex.valueOfDouble(-7).plus(BigComplex.valueOfDoubles(0, 24)).root(BigComplex.valueOfDouble(2), mc)].map(str), ["(2.000000000 + 1.000000000 i)", "(3.000000000 + 4.000000000 i)"]);
  assert.equal(str(BigComplex.valueOfDouble(8).rootInt(3, 1, mc)), "(-1.000000000 + 1.732050808 i)"); // the 2nd cube root of 8
});

test("BigFactory", () => {
  // api:BigFactory
  // --8<-- [start:factory]
  const factory = BigFactory.DEFAULT;
  assert.equal(factory.parseBigInteger("-ff", 16).toString(), "-255");
  assert.equal(factory.parseBigIntegerOrNull("ff", 10), null);
  assert.equal(factory.parseBigDecimal("1234", new MathContext(2)).toString(), "1.2E+3");
  assert.equal(factory.parseBigRationalOrNull("1/0"), null);
  assert.equal(factory.bigRationalOfInts(2, 6).reduce().toRationalString(), "1/3");
  assert.equal(factory.probablePrime(64).bitLength, 64);
  // --8<-- [end:factory]

  assert.deepEqual([factory.bigIntegerOfInt(1), factory.bigIntegerOfLong(2n), factory.parseBigInteger("3"), factory.bigIntegerOfIntArray(new Int32Array([4]))].map(str), ["1", "2", "3", "4"]);
  assert.equal(str(factory.bigIntegerOfSignMagnitude(-1, new Int8Array([1, 0]))), "-256");
  assert.equal(str(factory.bigIntegerOfBytes(new Int8Array([-1]))), "-1");
  assert.deepEqual([factory.bigDecimalOfInt(1), factory.bigDecimalOfLong(2n, mc), factory.bigDecimalOfDouble(0.5), factory.bigDecimalValueOf(0.1)].map(str), ["1", "2", "0.5", "0.1"]);
  assert.deepEqual([factory.bigDecimalOfBigInteger(BigInteger.TEN), factory.bigDecimalOfScaled(BigInteger.of(1234), 2), factory.bigDecimalOfScaledLong(1234n, 3)].map(str), ["10", "12.34", "1.234"]);
  assert.deepEqual([str(factory.parseBigDecimalOrNull("1.5")), factory.parseBigDecimalOrNull("1..5")], ["1.5", null]);
  const r = (x) => x.toRationalString();
  assert.deepEqual([factory.bigRationalOfInt(1), factory.bigRationalOfLong(2n), factory.bigRationalOfBigInteger(BigInteger.TEN), factory.bigRationalOfDouble(0.5)].map(r), ["1", "2", "10", "5/10"]); // not reduced
  assert.deepEqual([factory.bigRationalOfBigDecimal(bigDecimal("1.5")), factory.bigRationalOfBigIntegers(BigInteger.ONE, BigInteger.TWO), factory.bigRationalOfBigDecimals(bigDecimal(1), bigDecimal(4))].map((x) => x.reduce().toRationalString()), ["3/2", "1/2", "1/4"]);
  assert.deepEqual([r(factory.parseBigRational("2/3")), r(factory.parseBigRationalOrNull("2/3"))], ["2/3", "2/3"]);
});

test("MathContext and RoundingMode", () => {
  // api:MathContext
  const context = new MathContext(5, RoundingMode.DOWN);
  assert.deepEqual([context.precision, context.roundingMode.name], [5, "DOWN"]);
  assert.equal(context.toString(), "MathContext(precision=5, roundingMode=DOWN)");
  assert.ok(context.copy(16, RoundingMode.HALF_EVEN).equals(MathContext.DECIMAL64));
  assert.equal(context.hashCode(), new MathContext(5, RoundingMode.DOWN).hashCode());
  assert.deepEqual([MathContext.UNLIMITED, MathContext.DECIMAL32, MathContext.DECIMAL128].map((c) => c.precision), [0, 7, 34]);

  // api:RoundingMode
  assert.deepEqual(RoundingMode.values().map((mode) => mode.name), ["UP", "DOWN", "CEILING", "FLOOR", "HALF_UP", "HALF_DOWN", "HALF_EVEN", "UNNECESSARY"]);
  assert.equal(RoundingMode.valueOf("HALF_UP"), RoundingMode.HALF_UP);
  assert.deepEqual([RoundingMode.CEILING.ordinal, RoundingMode.FLOOR.value], [2, 3]); // value: java.math's int constant
  assert.equal(RoundingMode.valueOfInt(4), RoundingMode.HALF_UP);
  assert.equal(bigDecimal("2.5").setScaleRounding(0, RoundingMode.HALF_DOWN).toString(), "2");
  assert.throws(() => bigDecimal("2.5").setScaleRounding(0, RoundingMode.UNNECESSARY), { name: "ArithmeticException" });
});

test("functions", () => {
  // api:functions
  // --8<-- [start:functions]
  const digits = new MathContext(20);
  assert.equal(pi(digits).toString(), "3.1415926535897932385");
  assert.equal(log(bigDecimal(2), digits).toString(), "0.69314718055994530942"); // log(x, mc): x.log(mc) in Kotlin
  assert.equal(bigDecimal(2).sqrt(digits).toString(), "1.4142135623730950488"); // a method, as in Kotlin
  // --8<-- [end:functions]
  const x = bigDecimal("2.5");
  assert.equal(str(kt.toBigDecimal("12.50")), "12.50");
  assert.deepEqual([kt.isIntValue(x), kt.isLongValue(bigDecimal("2.0")), kt.isDoubleValue(x)], [false, true, true]);
  assert.deepEqual([str(kt.mantissa(bigDecimal(1234))), kt.exponent(bigDecimal(1234)), kt.significantDigits(bigDecimal("1.200"))], ["1.234", 3, 2]);
  assert.deepEqual([kt.integralPart(x), kt.fractionalPart(x), kt.roundWithTrailingZeroes(x, new MathContext(4)), kt.reciprocal(x, mc)].map(str), ["2", "0.5", "2.500", "0.4"]);
  assert.deepEqual([kt.factorial(5), kt.factorialDecimal(x, mc), kt.gamma(x, mc), kt.bernoulli(2, mc)].map(str), ["120", "3.323350970", "1.329340388", "0.1666666667"]);
  assert.deepEqual([kt.pow(x, x, mc), kt.powLong(x, 2n, mc), kt.root(bigDecimal(8), bigDecimal(3), mc)].map(str), ["9.882117688", "6.25", "2.0"]);
  assert.deepEqual([kt.log2(bigDecimal(8), mc), kt.log10(bigDecimal(1000), mc), kt.e(mc), kt.exp(BigDecimal.ONE, mc)].map(str), ["3.000000000", "3.000000000", "2.718281828", "2.718281828"]);
  const half = BigDecimal.ONE_HALF;
  assert.deepEqual([kt.sin(half, mc), kt.asin(half, mc), kt.cos(half, mc), kt.acos(half, mc)].map(str), ["0.4794255386", "0.5235987756", "0.8775825619", "1.047197551"]);
  assert.deepEqual([kt.tan(half, mc), kt.atan(half, mc), kt.atan2(BigDecimal.ONE, BigDecimal.ONE, mc), kt.cot(half, mc), kt.acot(half, mc)].map(str), ["0.5463024898", "0.4636476090", "0.7853981634", "1.830487722", "1.107148718"]);
  assert.deepEqual([kt.sinh(half, mc), kt.cosh(half, mc), kt.tanh(half, mc), kt.coth(half, mc)].map(str), ["0.5210953055", "1.127625965", "0.4621171573", "2.163953414"]);
  assert.deepEqual([kt.asinh(half, mc), kt.acosh(x, mc), kt.atanh(half, mc), kt.acoth(x, mc)].map(str), ["0.4812118251", "1.566799237", "0.5493061443", "0.4236489302"]);
  assert.deepEqual([kt.toDegrees(pi(mc), mc), kt.toRadians(bigDecimal(180), mc)].map(str), ["180.0000000", "3.141592654"]);
  // decimal ranges, as arrays
  assert.deepEqual(kt.range(BigDecimal.ZERO, BigDecimal.ONE, bigDecimal("0.25"), mc).map(str), ["0", "0.25", "0.50", "0.75"]);
  assert.deepEqual(kt.rangeClosed(BigDecimal.ONE, BigDecimal.ZERO, bigDecimal("-0.5"), mc).map(str), ["1", "0.5", "0.0"]);
  // bigInteger, bigDecimal and bigRational take any JS value: see the other tests
  assert.deepEqual([bigInteger(1.9), bigDecimal(1n), bigRational(0.5)].map(str), ["1", "1", "0.5"]);
});

test("every member of the API is mentioned in the section about its type", () => {
  const missing = uncovered(readFileSync(new URL(import.meta.url), "utf8"));
  assert.deepEqual(missing, [], `the usage test misses part of the API: ${missing.join(", ")}`);
});
