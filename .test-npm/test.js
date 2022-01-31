const ktmath = require("kt-math").org.gciatto.kt.math
const assert = require('assert');

describe("kt-math", function () {
    describe('.bigInteger', function() {
        it('should create a BigInteger', function() {
            assert.equal(ktmath.bigInteger(1), ktmath.BigInteger.Companion.of(1));
        });
    });

    describe('.bigDecimal', function() {
        it('should create a BigDecimal', function() {
            assert.ok(ktmath.bigDecimal('1.5').equals(ktmath.BigDecimal.Companion.parse('1.5')));
        });
    });
});
