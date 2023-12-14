package day5

import getInput

fun main() {
    val input = getInput("/input_day5")
    println(day5p1(input))
}

fun day5p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
