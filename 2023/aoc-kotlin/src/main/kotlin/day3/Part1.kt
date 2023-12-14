package day3

import getInput

fun main() {
    val input = getInput("/input_day3")
    println(day3p1(input))
}

fun day3p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
