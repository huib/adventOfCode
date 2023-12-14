package day12

import getInput

fun main() {
    val input = getInput("/input_day12")
    println(day12p1(input))
}

fun day12p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
