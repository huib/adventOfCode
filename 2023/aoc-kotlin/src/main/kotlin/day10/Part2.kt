package day10

import getInput

fun main() {
    val input = getInput("/input_day10")
    println(day10p2(input))
}

fun day10p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val grid = lines.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->
            Cell(char, rowIndex to colIndex)
        }
    }

    val startPosition = grid.flatten().single { it.c == 'S' }.pos

    val longestLoop = longestLoop(grid, startPosition)

    val returnValue = enclosedArea(longestLoop, grid)

    return returnValue.toString()
}

fun longestLoop(grid: List<List<Cell>>, startPosition: Pair<Int, Int>): List<Pair<Int, Int>> {
    return nbrs_not_diag(startPosition).map { pos ->
        val path = mutableListOf(startPosition)
        var nextPos: Pair<Int, Int>? = pos
        while (nextPos != null) {
            path.add(nextPos)
            nextPos = grid.getOrNull(path.last().first)?.getOrNull(path.last().second)?.nextPosition(path.beforeLast())
        }

        if (path.last() == startPosition)
            path
        else
            emptyList()
    }.maxBy { it.size }
}

fun enclosedArea(loop: List<Pair<Int, Int>>, grid: List<List<Cell>>): Int {
    check(loop.first() == loop.last())

    val startIsVertical = loop[1].first < loop[0].first || loop.beforeLast().first < loop[0].first

    val verticalPathParts = loop.filter { (rowIndex, colIndex) ->
        grid[rowIndex][colIndex].c == '|' ||
            (startIsVertical && grid[rowIndex][colIndex].c == 'S') ||
            grid[rowIndex][colIndex].c == 'L' ||
            grid[rowIndex][colIndex].c == 'J'
    }

    val boundingBox = Box(
        fromRow = loop.minOf { it.first },
        toRow = loop.maxOf { it.first },
        fromCol = loop.minOf { it.second },
        toCol = loop.maxOf { it.second },
    )

    val pathTile = loop.toSet()
    val enclosedCells = boundingBox.filterNot { it in pathTile }.filter { (r, c) ->
        verticalPathParts.count { it.first == r && it.second > c } % 2 == 1
    }

    return enclosedCells.size
}

class Box(
    val fromRow: Int,
    val toRow: Int,
    val fromCol: Int,
    val toCol: Int,
) : Iterable<Pair<Int, Int>> {
    override fun iterator() = sequence {
        (fromRow..toRow).forEach { row ->
            (fromCol..toCol).map { col ->
                yield(row to col)
            }
        }
    }.iterator()
}

fun <E> List<E>.beforeLast() = this[size - 2]