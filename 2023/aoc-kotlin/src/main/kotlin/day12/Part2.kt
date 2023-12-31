package day12

import getInput

fun main() {
    val input = getInput("/input_day12")
    println(day12p2(input))
}

fun day12p2(input: String): String {
    cache.clear() // fair runtime calculation for repeated trails
    check(Puzzle.IMPOSSIBLE.isInfeasible)

    val lines = input.split("\n").map { it.trim() }

    val puzzles = lines.map(::unfold).map(Puzzle::fromString)
    val nums = puzzles.map(::numberOfSolutions)
    val returnValue = nums.sum()

    return returnValue.toString()
}

fun unfold(str: String): String {
    val (a, b) = str.split(" ")
    return List(5) { a }.joinToString("?") + " " + List(5) { b }.joinToString(",")
}
