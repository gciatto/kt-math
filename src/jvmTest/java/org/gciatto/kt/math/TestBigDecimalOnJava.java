package org.gciatto.kt.math;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.gciatto.kt.math.JvmUtils.toJava;
import static org.gciatto.kt.math.JvmUtils.toKotlin;
import static org.junit.Assert.assertEquals;

public class TestBigDecimalOnJava {
    @Test
    public void testFactoryMethods() {
        assertEquals(BigDecimal.ONE, BigDecimal.of(1));
        assertEquals(BigDecimal.ONE, BigDecimal.of(1.0).stripTrailingZeros());
        assertEquals(BigDecimal.ONE, BigDecimal.of(1.0f).stripTrailingZeros());
        assertEquals(BigDecimal.ONE, BigDecimal.of(CommonBigInteger.of(1)));
        assertEquals(BigDecimal.ONE, BigDecimal.of("1"));
        assertEquals(BigDecimal.ONE, BigDecimal.of("1.0").stripTrailingZeros());
        assertEquals(BigDecimal.ONE, BigDecimal.of("1.00").stripTrailingZeros());

        assertEquals(BigDecimal.ZERO, BigDecimal.of(0));
        assertEquals(BigDecimal.ZERO, BigDecimal.of(0.0).stripTrailingZeros());
        assertEquals(BigDecimal.ZERO, BigDecimal.of(0.0f).stripTrailingZeros());
        assertEquals(BigDecimal.ZERO, BigDecimal.of(CommonBigInteger.of(0)));
        assertEquals(BigDecimal.ZERO, BigDecimal.of("0"));
        assertEquals(BigDecimal.ZERO, BigDecimal.of("0.0").stripTrailingZeros());
        assertEquals(BigDecimal.ZERO, BigDecimal.of("0.00").stripTrailingZeros());

        assertEquals(BigDecimal.ONE_HALF, BigDecimal.of(0.5));
        assertEquals(BigDecimal.ONE_HALF, BigDecimal.of(0.50).stripTrailingZeros());
        assertEquals(BigDecimal.ONE_HALF, BigDecimal.of(0.5f));
        assertEquals(BigDecimal.ONE_HALF, BigDecimal.of("0.5"));
        assertEquals(BigDecimal.ONE_HALF, BigDecimal.of("0.50").stripTrailingZeros());

        assertEquals(BigDecimal.ONE_TENTH, BigDecimal.of(0.1));
        assertEquals(BigDecimal.ONE_TENTH, BigDecimal.of(0.10).stripTrailingZeros());
        assertEquals(BigDecimal.of("0.10000000149011612"), BigDecimal.of(0.1f));
        assertEquals(BigDecimal.ONE_TENTH, BigDecimal.of("0.1"));
        assertEquals(BigDecimal.ONE_TENTH, BigDecimal.of("0.10").stripTrailingZeros());

        assertEquals(
                BigDecimal.of("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648"),
                BigDecimal.PI
        );

        assertEquals(
                BigDecimal.of("2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427427466391932003059921817413596629043572900334295260595630738132328627943490763233829880753195251019011573834187930702154089149934884167509244761460668082264"),
                BigDecimal.E
        );
    }

    @Test
    public void testConversions() {
        List<BigDecimal> toTest = Arrays.asList(
                BigDecimal.ZERO,
                BigDecimal.ONE,
                BigDecimal.TWO,
                BigDecimal.E,
                BigDecimal.PI,
                BigDecimal.ONE_HALF,
                BigDecimal.ONE_TENTH
        );

        for (BigDecimal x : toTest) {
            assertEquals(x, toKotlin(toJava(x)));
        }
    }
}
