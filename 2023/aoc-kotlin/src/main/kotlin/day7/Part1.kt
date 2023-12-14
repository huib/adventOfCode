package day7

import getInput

fun main() {
    val input = getInput("/input_day7")
    println(day7p1(input))
}

fun day7p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
