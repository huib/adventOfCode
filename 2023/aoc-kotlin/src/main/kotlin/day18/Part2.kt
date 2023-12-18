package day18

import getInput
import kotlin.math.abs

fun main() {
    val input = getInput("/input_day18")
    println(day18p2(input))
}

fun day18p2(input: String): String {
    val instructions = input.split("\n").map { it.trim() }.map(::part2Instruction)

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

@OptIn(ExperimentalStdlibApi::class)
fun part2Instruction(str: String): Instruction {
    val s = str.split(" ")[2].substring(2, 8)
    return Instruction(
        direction = s.last().asDirection(),
        length = s.substring(0, 5).hexToInt(),
        color = "x",
    )
}

fun Char.asDirection() = when (this) {
    '0' -> "R"
    '1' -> "D"
    '2' -> "L"
    '3' -> "U"
    else -> error("odd direction")
}
