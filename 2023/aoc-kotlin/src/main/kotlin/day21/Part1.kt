package day21

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day21")
    println(day21p1(input))
}

fun day21p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val numSteps = 64

    val width = lines.first().length
    val height = lines.size
    val inBounds = { pos: Pair<Int, Int> -> pos.first in 0..<width && pos.second in 0..<height }

    val startY = lines.indexOfFirst { it.contains('S') }
    val startX = lines[startY].indexOf('S')
    val startPosition = startX to startY

    val (reachable, _, lastPositions) = (1..numSteps)
        .fold(
            Triple(
                setOf<Pair<Int, Int>>(),
                setOf<Pair<Int, Int>>(),
                setOf(startPosition),
            ),
        ) { (step1, step2, step3), _ ->
            val step4 = step3.flatMapTo(mutableSetOf()) {
                nbrs_not_diag(it)
                    .filterNot(step2::contains)
                    .filter(inBounds)
                    .filter { (x, y) -> lines[y][x] != '#' }
            }
            Triple(step2, step1 + step3, step4)
        }

    val returnValue = (reachable + lastPositions).size

    return returnValue.toString()
}
