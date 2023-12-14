package day11

import getInput

fun main() {
    val input = getInput("/input_day11")
    println(day11p1(input))
}

fun day11p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
