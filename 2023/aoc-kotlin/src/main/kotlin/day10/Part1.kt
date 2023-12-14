package day10

import getInput

fun main() {
    val input = getInput("/input_day10")
    println(day10p1(input))
}

fun day10p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val grid = lines.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->
            Cell(char, rowIndex to colIndex)
        }
    }

    val startPosition = grid.flatten().single { it.c == 'S' }.pos

    val returnValue = longestLoopLength(grid, startPosition) / 2

    return returnValue.toString()
}

class Cell(val c: Char, val pos: Pair<Int, Int>) {

    fun nextPosition(from: Pair<Int, Int>): Pair<Int, Int>? {
        val delta = from.first - pos.first to from.second - pos.second
        return when (delta) {
            0 to -1 -> when (c) { // came from west
                '-' -> pos.first to pos.second + 1 // continue east
                'J' -> pos.first - 1 to pos.second // turn left/north
                '7' -> pos.first + 1 to pos.second // turn right/south
                else -> null // could not have reached this cell
            }
            0 to 1 -> when (c) { // came from east
                '-' -> pos.first to pos.second - 1 // continue west
                'F' -> pos.first + 1 to pos.second // turn left/south
                'L' -> pos.first - 1 to pos.second // turn right/north
                else -> null // could not have reached this cell
            }
            1 to 0 -> when (c) { // come from south
                '|' -> pos.first - 1 to pos.second // continue north
                '7' -> pos.first to pos.second - 1 // turn left/west
                'F' -> pos.first to pos.second + 1 // turn right/east
                else -> null // could not have reached this cell
            }
            -1 to 0 -> when (c) { // came from north
                '|' -> pos.first + 1 to pos.second // continue south
                'L' -> pos.first to pos.second + 1 // turn left/east
                'J' -> pos.first to pos.second - 1 // turn right/west
                else -> null // could not have reached this cell
            }
            else -> error("Non-neighbouring position")
        }
    }
}

fun longestLoopLength(grid: List<List<Cell>>, startPosition: Pair<Int, Int>): Int {
    return nbrs_not_diag(startPosition).maxOf { pos ->
        var lastPos = startPosition
        var curPos: Pair<Int, Int>? = pos
        var counter = 0
        while (curPos != null) {
            val tmpPos = lastPos
            lastPos = curPos
            curPos = grid.getOrNull(curPos.first)?.getOrNull(curPos.second)?.nextPosition(tmpPos)
            counter++
        }

        if (lastPos == startPosition)
            counter
        else
            0
    }
}

fun nbrs_not_diag(pos: Pair<Int, Int>): Iterable<Pair<Int, Int>> {
    return listOf(
        pos.first - 1 to pos.second,
        pos.first to pos.second - 1,
        pos.first + 1 to pos.second,
        pos.first to pos.second + 1,
    )
}