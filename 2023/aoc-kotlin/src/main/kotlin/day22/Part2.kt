package day22

import getInput

fun main() {
    val input = getInput("/input_day22")
    println(day22p2(input))
}

fun day22p2(input: String): String {
    val bricks = input.split("\n").map { it.trim() }.map(::parseBrick)

    val (changed, settledBricks) = settle(bricks)

    val returnValue = settledBricks.sumOf { brick ->
        settle(settledBricks - setOf(brick)).first
    }

    return returnValue.toString()
}
