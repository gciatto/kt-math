# Getting started in JavaScript

The `kt-math` npm package brings Kt-Math to JavaScript (Node.js and browsers). Its `BigInteger`s are thin wrappers of
native `BigInt`s; its `BigDecimal`s and `BigRational`s are the Kotlin implementation, on top of them.

## Install

```bash
npm install kt-math
```

## Import

`kt-math` is an ES module (`require("kt-math")` works too, on Node.js 20.19+ and 22.12+), exporting every class and
function under its own name. The `bigInteger`, `bigDecimal` and `bigRational` functions accept any JS value (strings,
numbers, `BigInt`s, arrays of arguments); the classes have static factories (`BigInteger.of`, `BigDecimal.parse`, ...)
and constants (`BigInteger.TEN`, `MathContext.DECIMAL64`, ...). The package ships TypeScript definitions.

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:import"
```

## Integers

Kotlin's operators are methods (`plus`, `times`, ...); overloads have distinct names (`plusInt`, `timesLong`, ...).
`Long`s are native `BigInt`s:

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:big-integers"
```

## Errors

Kotlin exceptions are JS `Error`s, whose class name is the Kotlin one:

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:errors"
```

## Decimals

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:big-decimals"
```

## Rationals

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:big-rationals"
```

## Functions

big-math's functions take the `BigDecimal` as their first argument (they are extension functions in Kotlin):

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:functions"
```

## The factory

```javascript
--8<-- "src/jsUsageTest/usage.test.mjs:factory"
```
