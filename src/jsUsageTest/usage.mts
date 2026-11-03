// kt-math as TypeScript code sees it, through the definitions (kt-math.d.mts) shipped in the npm package:
// `./gradlew jsUsageTest` type-checks this file (tsc --noEmit, strict), it isn't run (usage.test.mjs runs the API).
import {
  BigComplex, BigDecimal, BigFactory, BigInteger, BigIntegerRange, BigRational, MathContext, RoundingMode,
  bigDecimal, bigInteger, bigRational, log, pi, range,
} from "kt-math";

const x: BigInteger = bigInteger("123456789012345678901234567890");
const product: BigInteger = x.times(BigInteger.of(42)).plusInt(1);
const asLong: bigint = product.toLong();
const comparison: number = x.compareTo(product);
const bounds: BigIntegerRange = BigInteger.ONE.rangeToInt(3);
const values: BigInteger[] = bounds.toArray();

const quotient: BigDecimal = bigDecimal("1.10").divWithContext(BigDecimal.of(3), new MathContext(10, RoundingMode.HALF_EVEN));
const half: string = bigRational("1/3").plus(BigRational.ofFraction(1, 6)).reduce().toRationalString();
const parsed: BigRational | null | undefined = BigFactory.DEFAULT.parseBigRationalOrNull("1/0"); // null on invalid input
const z: BigComplex = BigComplex.valueOfDoubles(3, 4).pow(BigComplex.I, MathContext.DECIMAL64);
const logarithm: BigDecimal = log(pi(MathContext.DECIMAL32), MathContext.DECIMAL32);
const steps: BigDecimal[] = range(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE_TENTH, MathContext.DECIMAL32);

// @ts-expect-error: plusInt takes a number, not a string
x.plusInt("1");
// @ts-expect-error: no such member
x.plusString("1");
// @ts-expect-error: Longs are BigInts
BigInteger.ofLong(1);

export { asLong, comparison, half, logarithm, parsed, quotient, steps, values, z };
