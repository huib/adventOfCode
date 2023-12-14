package day6

import getInput

fun main() {
    val input = getInput("/input_day6")
    println(day6p1(input))
}

fun day6p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
