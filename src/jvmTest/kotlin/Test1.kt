import java.math.*

fun main() {
    var x = BigInteger.valueOf(Long.MIN_VALUE)
    println(x)
    x = x.add(BigInteger.ONE)
    println(x)
}