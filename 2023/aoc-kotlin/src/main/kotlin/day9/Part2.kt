package day9

import getInput

fun main() {
    val input = getInput("/input_day9")
    println(day9p2(input))
}

fun day9p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
