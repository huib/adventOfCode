package day11

import getInput

fun main() {
    val input = getInput("/input_day11")
    println(day11p2(input))
}

fun day11p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
