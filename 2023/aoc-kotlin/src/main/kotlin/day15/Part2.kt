package day15

import getInput

fun main() {
    val input = getInput("/input_day15")
    println(day15p2(input))
}

fun day15p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
