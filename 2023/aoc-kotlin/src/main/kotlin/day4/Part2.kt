package day4

import getInput

fun main() {
    val input = getInput("/input_day4")
    println(day4p2(input))
}

fun day4p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
