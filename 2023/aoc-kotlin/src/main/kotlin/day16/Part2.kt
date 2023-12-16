package day16

import getInput

fun main() {
    val input = getInput("/input_day16")
    println(day16p2(input))
}

fun day16p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val width = lines.first().length
    val height = lines.size
    val network = linesToNetwork(lines)

    val possibleStarts = network.filterIsInstance<Wall>().map { wall ->
        wall to Direction.entries.mapNotNull { direction ->
            wall.visible(direction).singleOrNull()
        }.single().second
    }

    val returnValue = possibleStarts.maxOf { start ->
        countEnergizedTiles(start, width, height)
    }

    return returnValue.toString()
}
