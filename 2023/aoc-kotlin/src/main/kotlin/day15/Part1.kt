package day15

import getInput

fun main() {
    val input = getInput("/input_day15")
    println(day15p1(input))
}

fun day15p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = lines.single().split(",").sumOf(::hash)

    return returnValue.toString()
}

fun hash(str: String) =
    str.fold(0) { currentValue, char ->
        (17 * (currentValue + char.code)) and 255
    }
