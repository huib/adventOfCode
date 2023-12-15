package day24

import getInput

fun main() {
    val input = getInput("/input_day24")
    println(day24p1(input))
}

fun day24p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
