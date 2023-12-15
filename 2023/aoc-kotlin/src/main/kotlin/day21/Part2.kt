package day21

import getInput

fun main() {
    val input = getInput("/input_day21")
    println(day21p2(input))
}

fun day21p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
