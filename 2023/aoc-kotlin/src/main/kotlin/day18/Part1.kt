package day18

import getInput

fun main() {
    val input = getInput("/input_day18")
    println(day18p1(input))
}

fun day18p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
