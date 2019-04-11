import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger.Companion.of

fun main() {
    var x = of(1)
    println(x)
    x = x shl 640
    println(x)

    var y = BigDecimal(kotlin.math.PI)
    println(y)
    y *= y
    println(y)
}