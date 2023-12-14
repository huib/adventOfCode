package day14

import getInput

fun main() {
    val input = getInput("/input_day14")
    println(day14p2(input))
}

fun day14p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
