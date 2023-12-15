package day20

import getInput

fun main() {
    val input = getInput("/input_day20")
    println(day20p1(input))
}

fun day20p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
