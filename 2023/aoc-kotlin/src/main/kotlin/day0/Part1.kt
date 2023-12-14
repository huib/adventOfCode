package day0

import getInput

fun main() {
    val input = getInput("/input_day0")
    println(run(input))
}

fun run(input: String): String {
    return "hello world: $input"
}
