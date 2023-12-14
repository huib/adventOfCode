package day16

import getInput

fun main() {
    val input = getInput("/input_day16")
    println(day16p1(input))
}

fun day16p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
