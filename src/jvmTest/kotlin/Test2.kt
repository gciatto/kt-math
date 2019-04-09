import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger.Companion.bigInteger

fun main() {
    var x = bigInteger(Long.MAX_VALUE)
    println(x)
    x *= bigInteger(Long.MAX_VALUE)
    println(x)

    var y = BigDecimal(kotlin.math.PI)
    println(y)
    y *= y
    println(y)
}