package day9

import getInput

fun main() {
    val input = getInput("/input_day9")
    println(day9p2(input))
}

fun day9p2(input: String): String {
    val sequences = input.split("\n").map { it.trim() }.map { it.split(" ").map(String::toInt) }

    val returnValue = sequences.sumOf(::prev)

    return returnValue.toString()
}

fun prev(sequence: List<Int>): Int {
    if (sequence.all { it == 0 }) {
        return 0
    }

    return sequence.first() - prev(sequence.zipWithNext().map { (a, b) -> b - a })
}