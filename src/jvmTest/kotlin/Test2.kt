import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import org.gciatto.kt.math.MathContext

fun main() {
    var x = BigInteger.of(1)
    println(x)
    x = x shl 640
    println(x)

    var y = BigDecimal.PI
    println(y)
    y = y / BigDecimal.of(3)
    println(y)
}