package day1

import getInput

fun main() {
    val input = getInput("/input_day1")
    println(day1p1(input))
}

fun day1p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val cal_values = lines.map {
        10 * it.first(Char::isDigit).digitToInt() + it.last(Char::isDigit).digitToInt()
    }

    return cal_values.sum().toString()
}
