package day0

import getInput

fun main() {
    val input = getInput("/input_day0")
    println(run2(input))
}

fun run2(input: String): String {
    return "hello world: ${input.split(" ").reversed().joinToString(" ")}"
}
