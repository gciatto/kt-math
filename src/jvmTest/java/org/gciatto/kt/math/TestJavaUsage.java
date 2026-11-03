package org.gciatto.kt.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 * kt-math as Java code sees it: static factories, conversions, operators as methods, exceptions. The documentation
 * website quotes the sections between the {@code --8<--} markers (getting-started-java.md).
 */
public class TestJavaUsage {
    @Test
    public void bigIntegers() {
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
    }

    @Test
    public void bigDecimals() {
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
    }

    @Test
    public void bigRationals() {
        // --8<-- [start:bigRationals]
        BigRational third = BigRational.valueOf(1, 3);
        BigRational sum = third.plus(BigRational.valueOf("1/6"));
        assertEquals("1/2", sum.reduce().toRationalString());
        assertEquals("0.5", sum.toBigDecimal().stripTrailingZeros().toString());
        assertEquals(0, third.times(3).compareTo(BigRational.ONE));
        assertThrows(ArithmeticException.class, () -> BigRational.valueOf(1, 0));
        // --8<-- [end:bigRationals]
    }

    @Test
    public void factory() {
        // --8<-- [start:factory]
        BigFactory factory = BigFactory.getDefault();
        assertEquals("-255", factory.parseBigInteger("-ff", 16).toString());
        assertNull(factory.parseBigIntegerOrNull("ff", 10));
        assertEquals("1.2E+3", factory.parseBigDecimal("1234", new MathContext(2)).toString());
        assertNull(factory.parseBigDecimalOrNull("1..2", null));
        assertEquals("1/3", factory.bigRationalOf(1, 3).toRationalString());
        assertEquals(64, factory.probablePrime(64).getBitLength());
        // --8<-- [end:factory]
    }

    @Test
    public void conversions() {
        // --8<-- [start:conversions]
        BigInteger x = BigInteger.of(1234);
        assertEquals("12.34", x.toBigDecimal(2).toString());
        assertEquals("617/50", x.toBigDecimal(2).toBigRational().reduce().toRationalString());
        assertEquals("12", BigRational.valueOf(37, 3).toBigInteger().toString());
        assertEquals("255", Conversions.toBigInteger("ff", 16).toString());
        assertEquals("255", Conversions.toBigInteger("255").toString());
        assertEquals("0.1", Conversions.toBigDecimal(0.1).toString());
        assertEquals("1/4", Conversions.toBigRational("2/8").reduce().toRationalString());
        // --8<-- [end:conversions]
    }

    @Test
    public void javaInterop() {
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
        // --8<-- [end:javaInterop]
    }
}
