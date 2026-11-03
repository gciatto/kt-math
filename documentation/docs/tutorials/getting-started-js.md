# Getting started in JavaScript

The `kt-math` npm package brings Kt-Math to JavaScript (Node.js and browsers). Its `BigInteger`s are thin wrappers of
native `BigInt`s; its `BigDecimal`s and `BigRational`s are the Kotlin implementation, on top of them.

## Install

```bash
npm install kt-math
```

## Import

Everything is under the `org.gciatto.kt.math` namespace. The `bigInteger`, `bigDecimal` and `bigRational` functions
accept any JS value (strings, numbers, `BigInt`s, arrays of arguments):

```javascript
--8<-- "src/jsUsageTest/usage.test.cjs:import"
```

## Integers

Kotlin's operators are methods (`plus`, `times`, ...); overloads have distinct names (`plusInt`, `timesLong`, ...).
`Long`s are native `BigInt`s:

```javascript
--8<-- "src/jsUsageTest/usage.test.cjs:big-integers"
```

## Errors

Kotlin exceptions are JS `Error`s, whose class name is the Kotlin one:

```javascript
--8<-- "src/jsUsageTest/usage.test.cjs:errors"
```

## Decimals

```javascript
--8<-- "src/jsUsageTest/usage.test.cjs:big-decimals"
```

## Rationals

```javascript
--8<-- "src/jsUsageTest/usage.test.cjs:big-rationals"
```

## The factory

```javascript
--8<-- "src/jsUsageTest/usage.test.cjs:factory"
```
