package day22

import getInput

fun main() {
    val input = getInput("/input_day22")
    println(day22p1(input))
}

fun day22p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
