# Architecture

## One API, several implementations

`BigInteger` and `BigDecimal` are Kotlin **interfaces**, mirroring the public API of their `java.math` namesakes.
Their companion objects (`BigInteger.of`, ...) and `BigFactory.default` return the most efficient implementation of
the platform at hand:

- on the **JVM**, adapters delegating to `java.math`: HotSpot replaces its hot loops with hand-written assembly
  (intrinsics), and nothing written in Kotlin can beat it;
- on **JavaScript**, `BigInteger` wraps a native `BigInt`, whose arithmetic is implemented by the JS engine; `BigDecimal`
  is the Kotlin port of `java.math.BigDecimal`, written against the `BigInteger` interface, so it runs on `BigInt`s;
- on **Kotlin/Native**, both are the Kotlin port of `java.math`; the routines that HotSpot intrinsifies (schoolbook
  multiplication and squaring, Montgomery multiplication for `modPow`) run in C, through cinterop, on 64-bit limbs.

Code written against the interfaces, in common code, is unaware of which implementation it gets.

## A faithful port

The Kotlin port of `java.math` (`CommonBigInteger`, `CommonBigDecimal`, `MutableBigInteger`) follows the JDK sources
line by line, including the names of variables and the comments: diffing it against the JDK is how bugs get found
and fixed. The port follows JDK 17's algorithms; on the JVM, the results are the ones of the JDK running the code.

## Rationals and big-math

`BigRational` (ported from big-math) is a class built on the `BigDecimal` and `BigInteger` interfaces, so it uses the
platform's implementation too. Package `org.gciatto.kt.math.big` ports big-math's `BigDecimalMath` in the same way, as extension functions.

## Why interfaces

Interfaces let each platform use its fastest arithmetic, without the users' code changing: the price is that
`BigInteger` and `BigDecimal` can't be instantiated directly (`BigInteger.of(...)` rather than a constructor), and
that, on JavaScript, members inherited from Kotlin's `Comparable` aren't exported (hence the `compare` members, called
`compareTo` in JavaScript).
