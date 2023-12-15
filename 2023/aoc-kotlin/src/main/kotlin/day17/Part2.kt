package day17

import getInput

fun main() {
    val input = getInput("/input_day17")
    println(day17p2(input))
}

fun day17p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
