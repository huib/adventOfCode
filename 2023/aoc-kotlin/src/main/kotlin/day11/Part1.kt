package day11

import getInput
import kotlin.math.abs

fun main() {
    val input = getInput("/input_day11")
    println(day11p1(input))
}

fun day11p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val imageHeight = lines.size
    val imageWidth = lines.first().length
    val emptyRows = MutableList(imageHeight) { true }
    val emptyCols = MutableList(imageWidth) { true }

    val galaxyImagePositions = lines.flatMapIndexed { lineNr, line ->
        line.mapIndexedNotNull { colNr, char ->
            if (char == '#') {
                emptyCols[colNr] = false
                emptyRows[lineNr] = false
                colNr to lineNr
            } else {
                null
            }
        }
    }

    val col2x = emptyCols.runningFold(0L) { x, empty ->
        if (empty) x + 2L else x + 1L
    }
    val row2y = emptyRows.runningFold(0L) { y, empty ->
        if (empty) y + 2L else y + 1L
    }

    val galaxyRealPositions = galaxyImagePositions.map { (imageX, imageY) ->
        col2x[imageX] to row2y[imageY]
    }

    val returnValue = galaxyRealPositions
        .unorderedPairs()
        .sumOf { (a, b) ->
            taxicabDistance(a, b)
        }

    return returnValue.toString()
}

fun <E> List<E>.unorderedPairs() = flatMapIndexed { index, e ->
    subList(fromIndex = index, toIndex = size).map { e to it }
}

fun taxicabDistance(a: Pair<Long, Long>, b: Pair<Long, Long>) = abs(a.first - b.first) + abs(a.second - b.second)

