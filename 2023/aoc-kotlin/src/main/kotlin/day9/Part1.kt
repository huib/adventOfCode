package day9

import getInput

fun main() {
    val input = getInput("/input_day9")
    println(day9p1(input))
}

fun day9p1(input: String): String {
    val sequences = input.split("\n").map { it.trim() }.map { it.split(" ").map(String::toInt) }

    val returnValue = sequences.sumOf(::next)

    return returnValue.toString()
}

fun next(sequence: List<Int>): Int {
    if (sequence.all { it == 0 }) {
        return 0
    }

    return sequence.last() + next(sequence.zipWithNext().map { (a, b) -> b - a })
}