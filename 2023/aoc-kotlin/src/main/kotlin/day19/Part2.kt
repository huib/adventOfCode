package day19

import getInput

fun main() {
    val input = getInput("/input_day19")
    println(day19p2(input))
}

fun day19p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.size

    return returnValue.toString()
}
