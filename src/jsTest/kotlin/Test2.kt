import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger.Companion.bigInteger

fun main() {
    var x = bigInteger(Long.MAX_VALUE)
    console.log(x)
    x *= bigInteger(Long.MAX_VALUE)
    console.log(x)

    var y = BigDecimal(kotlin.math.PI)
    console.log(y)
    y *= y
    console.log(y)
}