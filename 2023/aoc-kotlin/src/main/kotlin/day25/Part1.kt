package day25

import getInput

fun main() {
    val input = getInput("/input_day25")
    println(day25p1(input))
}

fun day25p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
