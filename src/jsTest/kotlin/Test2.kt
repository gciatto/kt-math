import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger.Companion.bigInteger

fun main() {
    var x = bigInteger(Long.MAX_VALUE)
    console.log(x.toString())
    x *= bigInteger(Long.MAX_VALUE)
    console.log(x.toString())

    var y = BigDecimal(kotlin.math.PI)
    console.log(y.toString())
    y *= y
    console.log(y.toString())
}