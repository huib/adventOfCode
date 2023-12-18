package day18

import day3.nbrs
import getInput

fun main() {
    val input = getInput("/input_day18")
    println(day18p1(input))
}

fun day18p1(input: String): String {
    val instructions = input.split("\n").map { it.trim() }.map(::Instruction)

    var minX = 0
    var maxX = 0
    var minY = 0
    var maxY = 0
    var currentPosition = 0 to 0
    val visitedPositions = mutableSetOf(currentPosition)

    val clockwise = 0 < instructions.map { it.direction }.zipWithNext().sumOf { (a, b) ->
        if (a == "L" && b == "U" || a == "U" && b == "R" || a == "R" && b == "D" || a == "D" && b == "L")
            1L
        else
            -1
    }

    instructions.forEach { instruction ->
        val nextPosition = instruction.nextPosition(from = currentPosition)
        visitedPositions.addAll(positions(from = currentPosition, to = nextPosition))
        currentPosition = nextPosition
        minX = minOf(currentPosition.first, minX)
        maxX = maxOf(currentPosition.first, maxX)
        minY = minOf(currentPosition.second, minY)
        maxY = maxOf(currentPosition.second, maxY)
    }

    currentPosition = 0 to 0
    val firstInnerTile = instructions.firstNotNullOf { instruction ->
        val nextPosition = instruction.nextPosition(from = currentPosition)
        if (instruction.direction == "R") {
            positions(from = currentPosition, to = nextPosition).forEach { position ->
                val dy = if (clockwise) 1 else -1
                if (position.first to position.second + dy !in visitedPositions) {
                    return@firstNotNullOf position.first to position.second + dy
                }
            }
        }
        currentPosition = nextPosition
        null
    }

//    (minY..maxY).forEach { y ->
//        (minX..maxX).forEach { x ->
//            if (x to y in visitedPositions)
//                print("#")
//            else if (x to y == firstInnerTile)
//                print("*")
//            else
//                print(".")
//        }
//        println()
//    }

    val filledTiles = visitedPositions.floodFillCount(firstInnerTile, maxCount = (maxX - minX) * (maxY - minY))

//    (minY..maxY).forEach { y ->
//        (minX..maxX).forEach { x ->
//            if (x to y in returnValue)
//                print("#")
//            else if (x to y == firstInnerTile)
//                print("*")
//            else
//                print(".")
//        }
//        println()
//    }

    val returnValue = filledTiles.size

    return returnValue.toString()
}

fun Set<Pair<Int, Int>>.floodFillCount(start: Pair<Int, Int>, maxCount: Int): Set<Pair<Int, Int>> {
    val filled = mutableSetOf(start)
    val q = mutableSetOf(start)

    while (q.isNotEmpty() && filled.size < maxCount) {
        val tile = q.first()
        q.remove(tile)
        filled.add(tile)
        val unfilledNeighbors = nbrs(tile) - this - filled
        q.addAll(unfilledNeighbors)
    }

    return this + filled
}

fun positions(from: Pair<Int, Int>, to: Pair<Int, Int>) =
    if (from.first == to.first) {
        (minOf(from.second, to.second)..maxOf(from.second, to.second)).map {
            from.first to it
        }
    } else if (from.second == to.second) {
        (minOf(from.first, to.first)..maxOf(from.first, to.first)).map {
            it to from.second
        }
    } else {
        error("Cannot diagonal")
    }

class Instruction(str: String) {
    val direction: String
    val length: Int
    val color: String

    init {
        val (d,l,c) = str.split(" ")
        direction = d
        length = l.toInt()
        color = c.substring(2, 8)
    }

    fun nextPosition(from: Pair<Int, Int>) = when(direction) {
        "U" -> from.first to from.second - length
        "D" -> from.first to from.second + length
        "L" -> from.first - length to from.second
        "R" -> from.first + length to from.second
        else -> error("odd direction: $direction")
    }
}
