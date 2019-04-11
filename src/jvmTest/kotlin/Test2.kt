import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger.Companion.of

fun main() {
    var x = of(Long.MAX_VALUE)
    println(x)
    x *= of(Long.MAX_VALUE)
    println(x)

    var y = BigDecimal(kotlin.math.PI)
    println(y)
    y *= y
    println(y)
}