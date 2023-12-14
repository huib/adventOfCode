package day4

import getInput

fun main() {
    val input = getInput("/input_day4")
    println(day4p1(input))
}

fun day4p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val cards = lines.map { it.split(":")[1].trim() }

    val returnValue = cards.sumOf { card ->
        val (winning, actual) = card
            .split("|")
            .map(String::trim)
            .map(::parseNumbers)
            .map(List<Long>::toSet)

        val matches = (winning intersect actual).size

        if (matches == 0) {
            0
        } else {
            1 shl (matches - 1)
        }
    }

    return returnValue.toString()
}

fun parseNumbers(string: String, separator: Regex = Regex("\\s+")): List<Long> {
    return string.split(separator).map(String::trim).map(String::toLong)
}
