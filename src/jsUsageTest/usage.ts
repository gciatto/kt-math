// kt-math as TypeScript code sees it, through the definitions (kt-math.d.ts) shipped in the npm package:
// `./gradlew jsUsageTest` type-checks this file (tsc --noEmit, strict), it isn't run.
import { org } from "kt-math";
import BigInteger = org.gciatto.kt.math.BigInteger;
import BigDecimal = org.gciatto.kt.math.BigDecimal;
import BigRational = org.gciatto.kt.math.BigRational;
import MathContext = org.gciatto.kt.math.MathContext;
import RoundingMode = org.gciatto.kt.math.RoundingMode;

const { bigInteger, bigDecimal, bigRational, BigFactory } = org.gciatto.kt.math;

const x: BigInteger = bigInteger("123456789012345678901234567890");
const product: BigInteger = x.times(bigInteger(42)).plusInt(1);
const asLong: bigint = product.toLong();
const comparison: number = x.compareTo(product);

// div with a MathContext is declared nullable in Kotlin, though it never returns null
const quotient: BigDecimal | null | undefined = bigDecimal("1.10").divWithContext(bigDecimal(3), new MathContext(10, RoundingMode.HALF_EVEN));
const half: string = bigRational("1/3").plus(bigRational([1, 6])).reduce().toRationalString();
const parsed: BigRational | null | undefined = BigFactory.Companion.default.parseBigRationalOrNull("1/0");

// @ts-expect-error: plusInt takes a number, not a string
x.plusInt("1");
// @ts-expect-error: no such member
x.plusString("1");

export { asLong, comparison, half, parsed, quotient };
