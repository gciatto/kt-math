import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

fun main() {
    var x = BigInteger.invoke(Long.MAX_VALUE)
    println(x)
    x = x.multiply(BigInteger(2))
    println(x)

    var y = BigDecimal("3.14")
    println(y)
    y = y.multiply(y)
    println(y)
}