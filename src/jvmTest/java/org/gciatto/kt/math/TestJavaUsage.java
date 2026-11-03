package org.gciatto.kt.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import org.gciatto.kt.math.big.BigComplex;
import org.gciatto.kt.math.big.BigDecimalMath;
import org.gciatto.kt.math.big.BigDecimalStream;
import org.junit.Test;

/**
 * kt-math as Java code sees it: static factories, conversions, operators as methods, exceptions. The documentation
 * website quotes the sections between the {@code --8<--} markers (getting-started-java.md). Each {@code // api:<Class>}
 * section must mention every public member of that class: {@link TestJavaUsageCoverage} checks it.
 */
public class TestJavaUsage {
    private static final MathContext MC = new MathContext(10);

    private static List<String> strings(Object... values) {
        List<String> result = new ArrayList<>();
        for (Object value : values) {
            result.add(String.valueOf(value));
        }
        return result;
    }

    private static <T> List<String> strings(Iterator<T> values) {
        List<String> result = new ArrayList<>();
        values.forEachRemaining(value -> result.add(String.valueOf(value)));
        return result;
    }

    @Test
    public void bigIntegers() {
        // api:BigInteger
        // --8<-- [start:bigIntegers]
        BigInteger x = BigInteger.of("123456789012345678901234567890");
        BigInteger y = BigInteger.of(42L);
        assertEquals("123456789012345678901234567932", x.plus(y).toString());
        assertEquals("5185185138518518513851851851380", x.times(y).toString());
        assertEquals("2939447357436801878600823045", x.div(y).toString());
        assertEquals("7", x.rem(BigInteger.of(11)).toString());
        assertEquals("1764", y.pow(2).toString());
        assertEquals("ff", BigInteger.of("255").toString(16));
        assertEquals(BigInteger.ONE, BigInteger.TEN.modPow(BigInteger.of(100), BigInteger.of(9)));
        assertTrue(x.compareTo(y) > 0);
        assertEquals(42, y.toInt());
        assertThrows(ArithmeticException.class, () -> x.div(BigInteger.ZERO));
        assertThrows(NumberFormatException.class, () -> BigInteger.of("12x"));
        // --8<-- [end:bigIntegers]

        // factories and constants
        assertEquals(strings("-255", "4294967296", "-256"), strings(
            BigInteger.of("-ff", 16), BigInteger.of(new int[] {1, 0}), BigInteger.of(-1, new byte[] {9, 1, 0, 9}, 1, 2)));
        assertEquals(strings("0", "1", "2", "-1"),
            strings(BigInteger.ZERO, BigInteger.ONE, BigInteger.TWO, BigInteger.NEGATIVE_ONE));
        assertEquals(strings("255", "-256"), strings(BigInteger.of(new byte[] {0, -1}), BigInteger.of(-1, new byte[] {1, 0})));
        assertEquals(64, BigInteger.probablePrime(64, Random.Default).getBitLength());

        // properties
        BigInteger n = BigInteger.of(-12);
        assertEquals("12", n.getAbsoluteValue().toString());
        assertEquals(-1, n.getSignum());
        assertEquals(4, n.getBitLength());
        assertEquals(3, BigInteger.of(7).getBitCount());

        // arithmetic, with BigInteger, int and long operands
        assertEquals(strings("43", "43", "40", "40", "40"), strings(y.plus(1), y.plus(1L), y.minus(BigInteger.TWO), y.minus(2), y.minus(2L)));
        assertEquals(strings("84", "84", "8", "8"), strings(y.times(2), y.times(2L), y.div(5), y.div(5L)));
        assertEquals(strings("8", "2"), strings((Object[]) y.divideAndRemainder(BigInteger.of(5))));
        assertEquals(strings("8", "2", "8", "2"), strings(y.divideAndRemainder(5)[0], y.divideAndRemainder(5)[1], y.divideAndRemainder(5L)[0], y.divideAndRemainder(5L)[1]));
        BigInteger m = BigInteger.of(-7); // remainders take the sign of the dividend
        assertEquals(strings("-1", "-1", "-1", "-1", "-1"), strings(m.remainder(BigInteger.of(3)), m.remainder(3), m.remainder(3L), m.rem(3), m.rem(3L)));
        assertEquals(strings("7", "-7"), strings(m.unaryMinus(), m.unaryPlus()));
        assertEquals("4", BigInteger.of(17).sqrt().toString());
        assertEquals(strings("4", "1"), strings((Object[]) BigInteger.of(17).sqrtAndRemainder()));
        assertEquals(strings("6", "6", "6"), strings(y.gcd(BigInteger.of(30)), y.gcd(30), y.gcd(30L)));
        assertEquals(strings("42", x), strings(y.min(x), y.max(x)));

        // modular arithmetic and primes
        assertEquals(strings("4", "4", "4"), strings(BigInteger.of(3).modInverse(BigInteger.of(11)), BigInteger.of(3).modInverse(11), BigInteger.of(3).modInverse(11L)));
        assertTrue(BigInteger.of(97).isProbablePrime(50));
        assertEquals("101", BigInteger.of(97).nextProbablePrime().toString());

        // bits (two's complement)
        BigInteger b = BigInteger.of(12);
        assertEquals(strings("48", "3", "-13"), strings(b.shl(2), b.shr(2), b.not()));
        assertEquals(strings("8", "8", "8"), strings(b.and(BigInteger.of(10)), b.and(10), b.and(10L)));
        assertEquals(strings("15", "15", "15"), strings(b.or(BigInteger.of(3)), b.or(3), b.or(3L)));
        assertEquals(strings("6", "6", "6"), strings(b.xor(BigInteger.of(10)), b.xor(10), b.xor(10L)));
        assertEquals(strings("8", "8", "8"), strings(b.andNot(BigInteger.of(4)), b.andNot(4), b.andNot(4L)));
        assertTrue(b.testBit(2) && b.get(3) && !b.get(0));
        assertEquals(strings(3, -1, 6L), strings(b.getIntBits(2, 2), BigInteger.NEGATIVE_ONE.getIntBits(0, 32), b.getLongBits(1, 64)));
        assertEquals(strings("13", "14", "8", "4"), strings(b.set(0, true), b.setBit(1), b.clearBit(2), b.flipBit(3)));

        // comparisons and conversions
        assertEquals(0, y.compare(BigInteger.of(42)));
        assertTrue(y.equals(BigInteger.of(42)) && y.hashCode() == BigInteger.of(42).hashCode());
        assertArrayEquals(new byte[] {0, -1}, BigInteger.of(255).toByteArray());
        assertEquals(strings("42", "4.2", "42"), strings(y.toBigDecimal(), y.toBigDecimal(1), y.toBigRational()));
        assertEquals(strings(42, 42L, (byte) 42, '*', (short) 42, 42f, 42d), strings(y.toInt(), y.toLong(), y.toByte(), y.toChar(), y.toShort(), y.toFloat(), y.toDouble()));
        assertEquals(strings(42L, 42, (short) 42, (byte) 42), strings(y.toLongExact(), y.toIntExact(), y.toShortExact(), y.toByteExact()));
        assertThrows(ArithmeticException.class, () -> BigInteger.of(300).toByteExact());

        // ranges
        assertEquals(strings("1", "2", "3"), strings(BigInteger.ONE.rangeTo(BigInteger.of(3)).iterator()));
        assertEquals(strings("1", "2"), strings(BigInteger.ONE.rangeTo(2).iterator()));
        assertEquals(strings(), strings(BigInteger.ONE.rangeTo(0L).iterator()));
    }

    @Test
    public void bigIntegerRanges() {
        // api:BigIntegerRange
        BigIntegerRange range = new BigIntegerRange(BigInteger.of(1), BigInteger.of(3));
        assertEquals(strings("1", "3", "1", "3"), strings(range.getStart(), range.getEndInclusive(), range.getFirst(), range.getLast()));
        assertEquals(strings("1", "3"), strings(range.component1(), range.component2()));
        assertEquals(strings("1", "2", "3"), strings((Object[]) range.toArray()));
        assertEquals(strings("1", "2", "3"), strings(range.iterator()));
        assertEquals("BigIntegerRange(start=1, endInclusive=3)", range.toString());
        assertTrue(range.equals(BigInteger.ONE.rangeTo(3)) && range.hashCode() == BigInteger.ONE.rangeTo(3).hashCode());
        assertEquals("2", range.copy(BigInteger.TWO, BigInteger.TEN).getFirst().toString());
    }

    @Test
    public void bigDecimals() {
        // api:BigDecimal
        // --8<-- [start:bigDecimals]
        BigDecimal a = BigDecimal.of("1.10");
        BigDecimal b = BigDecimal.of(3);
        assertEquals("4.10", a.plus(b).toString());
        assertEquals("0.3666666667", a.div(b, new MathContext(10, RoundingMode.HALF_EVEN)).toString());
        assertEquals("0.37", a.div(b, 2, RoundingMode.HALF_UP).toString());
        assertEquals("1.1", a.stripTrailingZeros().toString());
        assertEquals("1.414213562", BigDecimal.of(2).sqrt(new MathContext(10)).toString());
        assertEquals("0.1", BigDecimal.valueOf(0.1).toString());
        assertEquals("0.1000000000000000055511151231257827021181583404541015625", BigDecimal.of(0.1).toString());
        assertThrows(ArithmeticException.class, () -> BigDecimal.ONE.div(b));
        // --8<-- [end:bigDecimals]

        // factories and constants
        MathContext two = new MathContext(2);
        assertEquals(strings("12", "12.34", "1.2E+3", "1.2E+3"), strings(BigDecimal.of(12L), BigDecimal.of(1234L, 2), BigDecimal.of(1234, two), BigDecimal.of(1234L, two)));
        assertEquals(strings("0.10", "0.25", "0.25", "1.6", "10", "10"), strings(BigDecimal.of(0.1, two), BigDecimal.of(0.25f), BigDecimal.of(0.25f, two), BigDecimal.of("1.55", two), BigDecimal.of(BigInteger.TEN), BigDecimal.of(BigInteger.TEN, MC)));
        assertEquals(strings("0", "2", "10", "0.1", "0.5"), strings(BigDecimal.ZERO, BigDecimal.TWO, BigDecimal.TEN, BigDecimal.ONE_TENTH, BigDecimal.ONE_HALF));
        assertTrue(BigDecimal.PI.toString().startsWith("3.14159") && BigDecimal.E.toString().startsWith("2.71828"));

        // properties
        BigDecimal x = BigDecimal.of("-12.340");
        assertEquals(strings("12.340", -1, 3, 5, "-12340"), strings(x.getAbsoluteValue(), x.getSignum(), x.getScale(), x.getPrecision(), x.getUnscaledValue()));

        // arithmetic
        MathContext three = new MathContext(3);
        assertEquals(strings("4.10", "-1.90", "-1.90", "3.30", "3.30"), strings(a.plus(b, three), a.minus(b), a.minus(b, three), a.times(b), a.times(b, three)));
        BigDecimal d = BigDecimal.of("7.5");
        assertEquals(strings("3", "1", "3.0", "3.0", "1.5", "1.5"), strings(d.div(BigDecimal.of("2.5")), BigDecimal.ONE.div(b, RoundingMode.UP), d.divideToIntegralValue(BigDecimal.TWO), d.divideToIntegralValue(BigDecimal.TWO, three), d.rem(BigDecimal.TWO), d.rem(BigDecimal.TWO, three)));
        assertEquals(strings("3.0", "1.5", "1.5"), strings(d.divideAndRemainder(BigDecimal.TWO).getFirst(), d.divideAndRemainder(BigDecimal.TWO).getSecond(), d.divideAndRemainder(BigDecimal.TWO, three).getSecond()));
        assertEquals(strings("1.2100", "1.3"), strings(a.pow(2), a.pow(3, two)));
        assertEquals(strings("12.3", "12.340", "12.3", "-12.340", "-12.3"), strings(x.absoluteValue(three), x.unaryMinus(), x.unaryMinus(three), x.unaryPlus(), x.unaryPlus(three)));
        assertEquals(strings("-12.3", "-12.340", "1.10", "0.001"), strings(x.round(three), x.min(a), x.max(a), x.ulp()));

        // scales
        assertEquals(strings("1.100", "1", "1"), strings(a.setScale(3), a.setScale(0, RoundingMode.HALF_UP), a.setScale(0, 4)));
        assertEquals(strings("0.110", "11.0", "1.10E+3"), strings(a.movePointLeft(1), a.movePointRight(1), a.scaleByPowerOfTen(3)));

        // comparisons and conversions
        assertEquals(0, BigDecimal.of("1.0").compare(BigDecimal.of("1.00")));
        assertEquals(0, BigDecimal.of("1.0").compareTo(BigDecimal.of("1.00")));
        BigDecimal big = BigDecimal.of("1.5E+4");
        assertEquals(strings("15E+3", "15000", "15000", "15000", "15000"), strings(big.toEngineeringString(), big.toPlainString(), big.toBigInteger(), big.toBigIntegerExact(), big.toBigRational()));
        assertEquals(strings(15000L, 15000L, 15000, 15000, (short) 15000, (short) 15000), strings(big.toLong(), big.toLongExact(), big.toInt(), big.toIntExact(), big.toShort(), big.toShortExact()));
        assertEquals(strings((byte) 100, (byte) 100, 'd', 1.1f, 1.1d), strings(BigDecimal.of(100).toByte(), BigDecimal.of(100).toByteExact(), BigDecimal.of(100).toChar(), a.toFloat(), a.toDouble()));
    }

    @Test
    public void bigRationals() {
        // api:BigRational
        // --8<-- [start:bigRationals]
        BigRational third = BigRational.valueOf(1, 3);
        BigRational sum = third.plus(BigRational.valueOf("1/6"));
        assertEquals("1/2", sum.reduce().toRationalString());
        assertEquals("0.5", sum.toBigDecimal().stripTrailingZeros().toString());
        assertEquals(0, third.times(3).compareTo(BigRational.ONE));
        assertThrows(ArithmeticException.class, () -> BigRational.valueOf(1, 0));
        // --8<-- [end:bigRationals]

        // factories and constants
        assertEquals(strings("2", "3/2", "1/2", "10", "1/4", "1/2"), strings(BigRational.valueOf(2), BigRational.valueOf(1, 1, 2).toRationalString(), BigRational.valueOf(BigInteger.of(1), BigInteger.of(2)).toRationalString(), BigRational.valueOf(BigInteger.TEN), BigRational.valueOf(0.25).reduce().toRationalString(), BigRational.valueOf(BigDecimal.of("0.5")).reduce().toRationalString()));
        assertEquals(strings("3/2", "1/4"), strings(BigRational.valueOf(true, "1", "5", null, null).reduce().toRationalString(), BigRational.valueOf(BigDecimal.ONE, BigDecimal.of(4)).reduce().toRationalString()));
        assertEquals(strings("0", "2", "10"), strings(BigRational.ZERO, BigRational.TWO, BigRational.TEN));
        assertEquals(strings("0.3333333333333333333333333333333333", "0.5", "0.1666666666666666666666666666666667"), strings(BigRational.min(third, sum), BigRational.max(third, sum), BigRational.bernoulli(2)));

        // properties
        BigRational x = BigRational.valueOf(-6, 4);
        assertEquals(strings("-6", "4", "-6", "4"), strings(x.getNumerator(), x.getDenominator(), x.getNumeratorBigInteger(), x.getDenominatorBigInteger()));
        assertEquals(strings("6/4", -1, false, false), strings(x.getAbsoluteValue().toRationalString(), x.getSignum(), x.isZero(), x.isInteger()));

        // arithmetic
        assertEquals(strings("-4/4", "-2/4", "6/4", "-4/6", "-2/4", "-10/4"), strings(x.integerPart().toRationalString(), x.fractionPart().toRationalString(), x.unaryMinus().toRationalString(), x.reciprocal().toRationalString(), x.increment().toRationalString(), x.decrement().toRationalString()));
        assertEquals(strings("-2/4", "-2/4", "-22/12", "-10/4", "-10/4"), strings(x.plus(BigInteger.ONE).toRationalString(), x.plus(1).toRationalString(), x.minus(third).toRationalString(), x.minus(BigInteger.ONE).toRationalString(), x.minus(1).toRationalString()));
        assertEquals(strings("-1/2", "-3", "-9/2", "-3/4", "-1/2", "9/4"), strings(x.times(third).reduce().toRationalString(), x.times(BigInteger.TWO).reduce().toRationalString(), x.div(third).reduce().toRationalString(), x.div(BigInteger.TWO).reduce().toRationalString(), x.div(3).reduce().toRationalString(), x.pow(2).reduce().toRationalString()));
        assertEquals(strings("-2", "-2"), strings(x.withPrecision(1), x.withScale(0)));

        // comparisons and conversions
        assertEquals(strings(-1.5d, -1.5f, -1, -1L, "-1", "-1.5", "-1.5"), strings(x.toDouble(), x.toFloat(), x.toInt(), x.toLong(), x.toBigInteger(), x.toBigDecimal(), x.toBigDecimal(MC)));
        assertEquals(strings("-1.5", "-1.5", "-6/4", "-1 2/4"), strings(x.toString(), x.toPlainString(), x.toRationalString(), x.toIntegerRationalString()));
        assertTrue(x.equals(BigRational.valueOf(-6, 4)) && x.hashCode() == BigRational.valueOf(-6, 4).hashCode());
        assertEquals(0, x.compare(BigRational.valueOf("-3/2")));
    }

    @Test
    public void bigComplexes() {
        // api:BigComplex
        // --8<-- [start:bigComplexes]
        BigComplex z = BigComplex.valueOf(3, 4); // 3 + 4i
        BigComplex w = BigComplex.valueOf(BigDecimal.of(1), BigDecimal.of(2)); // 1 + 2i
        assertEquals("(-5.0 + 10.0 i)", z.times(w).toString());
        assertEquals("5.0", z.abs(MC).toString());
        assertEquals("(0 + 3.141592654 i)", BigComplex.valueOf(-1).log(MC).toString());
        // --8<-- [end:bigComplexes]
        BigDecimal two = BigDecimal.TWO;
        assertEquals(strings("3.0", "4.0", "(3.0 + 0 i)", "(0 + 4.0 i)"), strings(z.re, z.im, z.re(), z.im()));
        assertEquals(strings("(0 + 0 i)", "(1 + 0 i)", "(0 + 1 i)", "(2 + 0 i)", "(1.5 + 0 i)"), strings(BigComplex.ZERO, BigComplex.ONE, BigComplex.I, BigComplex.valueOf(two), BigComplex.valueOf(1.5)));
        assertEquals(strings("(2 + 0 i)", "(2.0000000 + 0.0000 i)"), strings(BigComplex.valueOfPolar(two, BigDecimal.ZERO, MC), BigComplex.valueOfPolar(2, 0, MC)));

        // arithmetic: exact, or rounded with a MathContext
        assertEquals(strings("(4.0 + 6.0 i)", "(4.0 + 6.0 i)", "(5.0 + 4.0 i)", "(5.0 + 4.0 i)", "(4.0 + 4.0 i)"), strings(z.plus(w), z.plus(w, MC), z.plus(two), z.plus(two, MC), z.plus(1.0)));
        assertEquals(strings("(2.0 + 2.0 i)", "(2.0 + 2.0 i)", "(1.0 + 4.0 i)", "(1.0 + 4.0 i)", "(2.0 + 4.0 i)"), strings(z.minus(w), z.minus(w, MC), z.minus(two), z.minus(two, MC), z.minus(1.0)));
        assertEquals(strings("(-5.0 + 10.0 i)", "(6.0 + 8.0 i)", "(6.0 + 8.0 i)", "(6.00 + 8.00 i)"), strings(z.times(w, MC), z.times(two), z.times(two, MC), z.times(2.0)));
        assertEquals(strings("(2.20 - 0.40 i)", "(1.5 + 2.0 i)", "(1.5 + 2 i)"), strings(z.div(w, MC), z.div(two, MC), z.div(2.0, MC)));
        assertEquals(strings("(0.12 - 0.16 i)", "(3.0 - 4.0 i)", "(-3.0 - 4.0 i)", "(3 + 4 i)"), strings(z.reciprocal(MC), z.conjugate(), z.unaryMinus(), z.round(new MathContext(1))));
        assertEquals(strings("25.00", "0.9272952180"), strings(z.absSquare(MC), z.angle(MC)));
        assertTrue(!z.isReal() && BigComplex.ONE.isReal());
        BigComplex exact = BigComplex.valueOf(BigDecimal.of(3), BigDecimal.of(4));
        assertTrue(z.equals(exact) && z.hashCode() == exact.hashCode() && !z.strictEquals(exact));

        // functions: big-math's BigComplexMath
        BigComplex i = BigComplex.I;
        assertEquals(strings("(0.5403023059 + 0.8414709848 i)", "(0 + 2.0 i)"), strings(i.exp(MC), BigComplex.valueOf(-4).sqrt(MC)));
        assertEquals(strings("(0E-13 + 1.175201194 i)", "(1.543080635 + 0E-13 i)", "(0E-31 + 0.7615941560 i)"), strings(i.sin(MC), i.cos(MC), i.tan(MC)));
        assertEquals(strings("(1.448306995 + 0.1589971917 i)", "(0.1224893316 - 0.1589971917 i)", "(0.6339838656 + 2.305509031 i)", "(0.9368124612 - 2.305509031 i)"), strings(z.atan(MC), z.acot(MC), z.asin(MC), z.acos(MC)));
        assertEquals(strings("(120 + 0 i)", "(24 + 0 i)"), strings(BigComplex.valueOf(5).factorial(MC), BigComplex.valueOf(5).gamma(MC)));
        assertEquals(strings("(-7.00 + 24.00 i)", "(-7.000000000 + 24.00000000 i)", "(-3.000000000 + 4.000000000 i)"), strings(z.pow(2L, MC), z.pow(two, MC), w.pow(BigComplex.valueOf(2), MC)));
        assertEquals(strings("(2.000000000 + 1.000000000 i)", "(3.000000000 + 4.000000000 i)", "(-1.000000000 + 1.732050808 i)"), strings(z.root(two, MC), BigComplex.valueOf(-7, 24).root(BigComplex.valueOf(2), MC), BigComplex.valueOf(8).root(3, 1, MC)));
    }

    @Test
    public void factory() {
        // api:BigFactory
        // --8<-- [start:factory]
        BigFactory factory = BigFactory.getDefault();
        assertEquals("-255", factory.parseBigInteger("-ff", 16).toString());
        assertNull(factory.parseBigIntegerOrNull("ff", 10));
        assertEquals("1.2E+3", factory.parseBigDecimal("1234", new MathContext(2)).toString());
        assertNull(factory.parseBigDecimalOrNull("1..2", null));
        assertEquals("1/3", factory.bigRationalOf(1, 3).toRationalString());
        assertEquals(64, factory.probablePrime(64).getBitLength());
        // --8<-- [end:factory]
        assertEquals(10, BigFactory.DEFAULT_RADIX);
        assertEquals(64, factory.probablePrime(64, Random.Default).getBitLength());
        assertEquals(strings("1", "2", "4", "-256"), strings(factory.bigIntegerOf(1), factory.bigIntegerOf(2L), factory.bigIntegerOf(new int[] {4}), factory.bigIntegerOf(-1, new byte[] {1, 0}, 0, 2)));
        assertEquals("-1", factory.bigIntegerOf(new byte[] {-1}).toString());
        assertEquals(strings("1", "2", "0.5", "0.1", "10", "12.34", "1.234"), strings(factory.bigDecimalOf(1, null), factory.bigDecimalOf(2L, MC), factory.bigDecimalOf(0.5, null), factory.bigDecimalValueOf(0.1), factory.bigDecimalOf(BigInteger.TEN, null), factory.bigDecimalOf(BigInteger.of(1234), 2, null), factory.bigDecimalOf(1234L, 3)));
        assertEquals(strings("1", "2", "10", "5/10", "15/10", "1/2", "1/4"), strings(factory.bigRationalOf(1), factory.bigRationalOf(2L), factory.bigRationalOf(BigInteger.TEN), factory.bigRationalOf(0.5).toRationalString(), factory.bigRationalOf(BigDecimal.of("1.5")).toRationalString(), factory.bigRationalOf(BigInteger.ONE, BigInteger.TWO).toRationalString(), factory.bigRationalOf(BigDecimal.ONE, BigDecimal.of(4)).reduce().toRationalString()));
        assertEquals(strings("2/3", "2/3", "1.5"), strings(factory.parseBigRational("2/3").toRationalString(), factory.parseBigRationalOrNull("2/3").toRationalString(), factory.parseBigDecimalOrNull("1.5", null)));
    }

    @Test
    public void mathContextsAndRoundingModes() {
        // api:MathContext
        MathContext context = new MathContext(5, RoundingMode.DOWN);
        assertEquals(strings(5, RoundingMode.DOWN, 5, RoundingMode.DOWN), strings(context.getPrecision(), context.getRoundingMode(), context.component1(), context.component2()));
        assertEquals("MathContext(precision=5, roundingMode=DOWN)", context.toString());
        assertEquals(MathContext.DECIMAL64, context.copy(16, RoundingMode.HALF_EVEN));
        assertTrue(context.equals(new MathContext(5, RoundingMode.DOWN)) && context.hashCode() == new MathContext(5, RoundingMode.DOWN).hashCode());
        assertEquals(strings(0, 7, 34, 7, 9), strings(MathContext.UNLIMITED.getPrecision(), MathContext.DECIMAL32.getPrecision(), MathContext.DECIMAL128.getPrecision(), new MathContext(7).getPrecision(), new MathContext().getPrecision()));

        // api:RoundingMode
        assertEquals(strings("UP", "DOWN", "CEILING", "FLOOR", "HALF_UP", "HALF_DOWN", "HALF_EVEN", "UNNECESSARY"), strings((Object[]) RoundingMode.values()));
        assertEquals(Arrays.asList(RoundingMode.values()), RoundingMode.getEntries());
        assertEquals(RoundingMode.HALF_UP, RoundingMode.valueOf("HALF_UP"));
        assertEquals(RoundingMode.HALF_UP, RoundingMode.valueOf(4)); // java.math's int constant...
        assertEquals(3, RoundingMode.FLOOR.getValue()); // ...here
        assertEquals(strings("3", "2", "3", "2"), strings(BigDecimal.of("2.5").setScale(0, RoundingMode.UP), BigDecimal.of("2.5").setScale(0, RoundingMode.HALF_DOWN), BigDecimal.of("2.5").setScale(0, RoundingMode.CEILING), BigDecimal.of("2.5").setScale(0, RoundingMode.HALF_EVEN)));
        assertThrows(ArithmeticException.class, () -> BigDecimal.of("2.5").setScale(0, RoundingMode.UNNECESSARY));
    }

    @Test
    public void conversions() {
        // api:Conversions
        // --8<-- [start:conversions]
        BigInteger x = BigInteger.of(1234);
        assertEquals("12.34", x.toBigDecimal(2).toString());
        assertEquals("617/50", x.toBigDecimal(2).toBigRational().reduce().toRationalString());
        assertEquals("12", BigRational.valueOf(37, 3).toBigInteger().toString());
        assertEquals("255", Conversions.toBigInteger("ff", 16).toString());
        assertEquals("0.1", Conversions.toBigDecimal(0.1).toString());
        assertEquals("1/4", Conversions.toBigRational("2/8").reduce().toRationalString());
        // --8<-- [end:conversions]
        assertEquals(strings("1", "2", "3", "1.0", "2", "1.5"), strings(Conversions.toBigInteger(1), Conversions.toBigInteger(2L), Conversions.toBigIntegerOrNull("3", 10), Conversions.toBigDecimal("1.0", null), Conversions.toBigDecimal(2, null), Conversions.toBigDecimalOrNull("1.5", null)));
        assertEquals(strings("3", "4", "1/2", "1/2"), strings(Conversions.toBigDecimal(3L, null), Conversions.toBigRational(4), Conversions.toBigRational(0.5).reduce().toRationalString(), Conversions.toBigRational(2L).reciprocal().toRationalString()));
        assertNull(Conversions.toBigRationalOrNull("1/0"));
    }

    @Test
    public void bigDecimalMath() {
        // api:BigDecimalMath
        // --8<-- [start:bigDecimalMath]
        // big-math's static methods, as in big-math
        assertEquals("3.141592654", BigDecimalMath.pi(MC).toString());
        assertEquals("0.6931471806", BigDecimalMath.log(BigDecimal.TWO, MC).toString());
        // --8<-- [end:bigDecimalMath]
        BigDecimal x = BigDecimal.of("2.5");
        BigDecimal half = BigDecimal.ONE_HALF;
        assertEquals(strings("12.50", "12.5"), strings(BigDecimalMath.toBigDecimal("12.50"), BigDecimalMath.toBigDecimal("12.54", new MathContext(3))));
        assertEquals(strings(false, true, true, "1.234", 3, 2), strings(BigDecimalMath.isIntValue(x), BigDecimalMath.isLongValue(BigDecimal.of("2.0")), BigDecimalMath.isDoubleValue(x), BigDecimalMath.mantissa(BigDecimal.of(1234)), BigDecimalMath.exponent(BigDecimal.of(1234)), BigDecimalMath.significantDigits(BigDecimal.of("1.200"))));
        assertEquals(strings("2", "0.5", "2.500", "0.4"), strings(BigDecimalMath.integralPart(x), BigDecimalMath.fractionalPart(x), BigDecimalMath.roundWithTrailingZeroes(x, new MathContext(4)), BigDecimalMath.reciprocal(x, MC)));
        assertEquals(strings("120", "3.323350970", "1.329340388", "0.1666666667"), strings(BigDecimalMath.factorial(5), BigDecimalMath.factorial(x, MC), BigDecimalMath.gamma(x, MC), BigDecimalMath.bernoulli(2, MC)));
        assertEquals(strings("9.882117688", "6.25", "2.0"), strings(BigDecimalMath.pow(x, x, MC), BigDecimalMath.pow(x, 2L, MC), BigDecimalMath.root(BigDecimal.of(8), BigDecimal.of(3), MC)));
        assertEquals(strings("3.000000000", "3.000000000", "2.718281828", "2.718281828"), strings(BigDecimalMath.log2(BigDecimal.of(8), MC), BigDecimalMath.log10(BigDecimal.of(1000), MC), BigDecimalMath.e(MC), BigDecimalMath.exp(BigDecimal.ONE, MC)));
        assertEquals(strings("0.4794255386", "0.5235987756", "0.8775825619", "1.047197551"), strings(BigDecimalMath.sin(half, MC), BigDecimalMath.asin(half, MC), BigDecimalMath.cos(half, MC), BigDecimalMath.acos(half, MC)));
        assertEquals(strings("0.5463024898", "0.4636476090", "0.7853981634", "1.830487722", "1.107148718"), strings(BigDecimalMath.tan(half, MC), BigDecimalMath.atan(half, MC), BigDecimalMath.atan2(BigDecimal.ONE, BigDecimal.ONE, MC), BigDecimalMath.cot(half, MC), BigDecimalMath.acot(half, MC)));
        assertEquals(strings("0.5210953055", "1.127625965", "0.4621171573", "2.163953414"), strings(BigDecimalMath.sinh(half, MC), BigDecimalMath.cosh(half, MC), BigDecimalMath.tanh(half, MC), BigDecimalMath.coth(half, MC)));
        assertEquals(strings("0.4812118251", "1.566799237", "0.5493061443", "0.4236489302"), strings(BigDecimalMath.asinh(half, MC), BigDecimalMath.acosh(x, MC), BigDecimalMath.atanh(half, MC), BigDecimalMath.acoth(x, MC)));
        assertEquals(strings("180.0000000", "3.141592654"), strings(BigDecimalMath.toDegrees(BigDecimalMath.pi(MC), MC), BigDecimalMath.toRadians(BigDecimal.of(180), MC)));
    }

    @Test
    public void bigDecimalStreams() {
        // api:BigDecimalStream
        BigDecimal quarter = BigDecimal.of("0.25");
        Sequence<BigDecimal> range = BigDecimalStream.range(BigDecimal.ZERO, BigDecimal.ONE, quarter, MC); // Kotlin sequences
        assertEquals(strings("0", "0.25", "0.50", "0.75"), strings(range.iterator()));
        assertEquals(strings("1", "0.5", "0.0"), strings(BigDecimalStream.rangeClosed(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.of("-0.5"), MC).iterator()));
        // Kotlin's a..b and a..<b, with a step
        assertEquals(strings("0", "0.25", "0.50", "0.75", "1.00"), strings(BigDecimalStream.step(RangesKt.rangeTo(BigDecimal.ZERO, BigDecimal.ONE), quarter).iterator()));
        assertEquals(strings("0", "0.25", "0.50", "0.75"), strings(BigDecimalStream.step(RangesKt.rangeUntil(BigDecimal.ZERO, BigDecimal.ONE), quarter).iterator()));
    }

    @Test
    public void javaInterop() {
        // api:JvmUtils
        // --8<-- [start:javaInterop]
        // BigIntegers are Comparable and ranges are Iterable, so the Java collections work with them
        List<BigInteger> values = Arrays.asList(BigInteger.TEN, BigInteger.ONE, BigInteger.TWO);
        values.sort(null);
        assertEquals(Arrays.asList(BigInteger.ONE, BigInteger.TWO, BigInteger.TEN), values);
        BigInteger total = BigInteger.ZERO;
        for (BigInteger i : BigInteger.ONE.rangeTo(BigInteger.TEN)) {
            total = total.plus(i);
        }
        assertEquals(BigInteger.of(55), total);
        // to and from java.math
        java.math.BigInteger javaInteger = JvmUtils.toJava(total);
        assertEquals(total, JvmUtils.toKotlin(javaInteger));
        assertEquals(total, JvmUtils.fromJava(javaInteger));
        // --8<-- [end:javaInterop]
        java.math.BigDecimal javaDecimal = JvmUtils.toJava(BigDecimal.of("1.5"));
        assertEquals(BigDecimal.of("1.5"), JvmUtils.toKotlin(javaDecimal));
        assertEquals(BigDecimal.of("1.5"), JvmUtils.fromJava(javaDecimal));
        assertEquals(java.math.RoundingMode.HALF_EVEN, JvmUtils.toJava(RoundingMode.HALF_EVEN));
        assertEquals(RoundingMode.HALF_EVEN, JvmUtils.toKotlin(java.math.RoundingMode.HALF_EVEN));
        assertEquals(java.math.MathContext.DECIMAL64, JvmUtils.toJava(MathContext.DECIMAL64));
        assertEquals(MathContext.DECIMAL64, JvmUtils.toKotlin(java.math.MathContext.DECIMAL64));
        assertNotEquals(javaDecimal, BigDecimal.of("1.5")); // different classes
        assertFalse(BigDecimal.of("1.5").equals(javaDecimal));
    }
}
