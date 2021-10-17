package org.gciatto.kt.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBigIntegerOnJava {
    @Test
    public void testFactoryMethods() {
        assertEquals(BigInteger.ONE, BigInteger.of(1));
        assertEquals(BigInteger.ONE, BigInteger.of(new int[] { 1 }));
        assertEquals(BigInteger.ONE, BigInteger.of("1"));
        assertEquals(BigInteger.ONE, BigInteger.of("1", 2));

        assertEquals(BigInteger.ZERO, BigInteger.of(0));
        assertEquals(BigInteger.ZERO, BigInteger.of(new int[] { 0 }));
        assertEquals(BigInteger.ZERO, BigInteger.of("0"));
        assertEquals(BigInteger.ZERO, BigInteger.of("0", 2));

        assertEquals(BigInteger.TEN, BigInteger.of(10));
        assertEquals(BigInteger.TEN, BigInteger.of(new int[] { 10 }));
        assertEquals(BigInteger.TEN, BigInteger.of("10"));
        assertEquals(BigInteger.TEN, BigInteger.of("1010", 2));

        assertEquals(BigInteger.TWO, BigInteger.of(2));
        assertEquals(BigInteger.TWO, BigInteger.of(new int[] { 2 }));
        assertEquals(BigInteger.TWO, BigInteger.of("2"));
        assertEquals(BigInteger.TWO, BigInteger.of("10", 2));

        assertEquals(BigInteger.NEGATIVE_ONE, BigInteger.of(-1));
        assertEquals(BigInteger.NEGATIVE_ONE, BigInteger.of(new int[] { -1 }));
        assertEquals(BigInteger.NEGATIVE_ONE, BigInteger.of("-1"));
        assertEquals(BigInteger.NEGATIVE_ONE, BigInteger.of("-1", 2));
    }
}
