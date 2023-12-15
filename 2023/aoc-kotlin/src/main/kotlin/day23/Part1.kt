package day23

import getInput

fun main() {
    val input = getInput("/input_day23")
    println(day23p1(input))
}

fun day23p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
