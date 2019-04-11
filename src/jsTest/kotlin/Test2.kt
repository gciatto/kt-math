import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger.Companion.of

fun main() {
    var x = of(Long.MAX_VALUE)
    console.log(x.toString())
    x *= of(Long.MAX_VALUE)
    console.log(x.toString())

    var y = BigDecimal(kotlin.math.PI)
    console.log(y.toString())
    y *= y
    console.log(y.toString())
}