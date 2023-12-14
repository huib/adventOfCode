package day13

import getInput

fun main() {
    val input = getInput("/input_day13")
    println(day13p1(input))
}

fun day13p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
