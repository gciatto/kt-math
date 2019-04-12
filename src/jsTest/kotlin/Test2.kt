import org.gciatto.kt.math.bigDecimal
import org.gciatto.kt.math.bigInteger

fun main() {
    var x = bigInteger(1)
    println(x)
    x = x shl 640
    println(x)

    var y = bigDecimal(kotlin.math.PI)
    println(y)
    y *= y
    println(y)
}