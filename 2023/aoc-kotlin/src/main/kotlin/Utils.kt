// https://stackoverflow.com/a/53018129/2065212
fun getInput(path: String): String =
    object {}.javaClass.getResource(path)?.readText() ?: error("file not found: $path")

// source: https://www.baeldung.com/kotlin/lcm#:~:text=The%20findLCM()%20function%20computes,it's%20divisible%20by%20both%20numbers.
fun lcm(values: List<Long>): Long {
    var result = values[0]
    for (i in 1 until values.size) {
        result = lcm(result, values[i])
    }
    return result
}

// source: https://www.baeldung.com/kotlin/lcm#:~:text=The%20findLCM()%20function%20computes,it's%20divisible%20by%20both%20numbers.
fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}
