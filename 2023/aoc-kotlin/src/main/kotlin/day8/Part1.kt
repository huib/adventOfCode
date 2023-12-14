package day8

import getInput

fun main() {
    val input = getInput("/input_day8")
    println(day8p1(input))
}

fun day8p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
