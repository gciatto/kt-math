import java.math.BigDecimal

fun main() {
    var x = BigDecimal.valueOf(Double.MIN_VALUE).subtract(BigDecimal.ONE)
    println(x)
}