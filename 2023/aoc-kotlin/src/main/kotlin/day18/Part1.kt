package day18

import getInput
import kotlin.math.abs

fun main() {
    val input = getInput("/input_day18")
    println(day18p1(input))
}

fun day18p1(input: String): String {
    val instructions = input.split("\n").map { it.trim() }.map(::part1Instruction)

    val areaTwice = (
        listOf(0 to 0) + instructions.runningFold(0 to 0) { oldPos, instruction ->
            instruction.nextPosition(oldPos)
        } + (0 to 0)
        )
        .zipWithNext { a, b ->
            a.first.toLong() * b.second.toLong() - a.second.toLong() * b.first.toLong() +
                abs(a.first.toLong() - b.first.toLong()) + abs(a.second.toLong() - b.second.toLong())
        }.sum() + 2
    check(areaTwice % 2 == 0L)
    val returnValue = areaTwice / 2

    return returnValue.toString()
}

fun part1Instruction(str: String): Instruction {
    val (d, l, c) = str.split(" ")
    return Instruction(
        direction = d,
        length = l.toInt(),
        color = c.substring(2, 8),
    )
}
data class Instruction(
    val direction: String,
    val length: Int,
    val color: String,
) {
    fun nextPosition(from: Pair<Int, Int>) = when (direction) {
        "U" -> from.first to from.second - length
        "D" -> from.first to from.second + length
        "L" -> from.first - length to from.second
        "R" -> from.first + length to from.second
        else -> error("odd direction: $direction")
    }
}
