import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun main() {
    var x = BigDecimal("4").sqrt(MathContext(9, RoundingMode.HALF_UP))
    println(x)
}