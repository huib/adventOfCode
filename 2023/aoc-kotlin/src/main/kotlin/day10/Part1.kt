package day10

import getInput

fun main() {
    val input = getInput("/input_day10")
    println(day10p1(input))
}

fun day10p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
