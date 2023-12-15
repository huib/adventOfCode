package day20

import getInput

fun main() {
    val input = getInput("/input_day20")
    println(day20p2(input))
}

fun day20p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
