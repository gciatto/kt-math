import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

fun main() {
    var x = BigInteger.of(1)
    println(x)
    x = x shl 640
    println(x)

    var y = BigDecimal.of(kotlin.math.PI)
    println(y)
    y *= y
    println(y)
}