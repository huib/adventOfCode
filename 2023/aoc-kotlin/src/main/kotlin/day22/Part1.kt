package day22

import getInput

fun main() {
    val input = getInput("/input_day22")
    println(day22p1(input))
}

fun day22p1(input: String): String {
    val bricks = input.split("\n").map { it.trim() }.map(::parseBrick)

    val (changed, settledBricks) = settle(bricks)

    val returnValue = settledBricks.count { brick ->
        settle(settledBricks - setOf(brick)).first == 0
    }

    return returnValue.toString()
}

fun settle(bricks: Iterable<Brick>): Pair<Int, Iterable<Brick>> {
    val heights = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    var droppedBricks = 0

    val groundedBricks = bricks.sortedBy(Brick::height).map { brick ->
        val pileHeight = brick.map { it.first to it.second }.maxOf(heights::getValue)
        val dropDistance = brick.height - pileHeight - 1

        if (dropDistance > 0) {
            droppedBricks++
        }

        val droppedBrick = brick.descend(brick.height - pileHeight - 1)
        droppedBrick.forEach { position ->
            heights[position.first to position.second] = position.third
        }
        droppedBrick
    }

    return droppedBricks to groundedBricks
}

fun parseBrick(str: String): Brick {
    val (pos1, pos2) = str.split('~').map { it.split(',').map(String::toInt).toTriple() }
    return Brick(pos1, pos2)
}

class Brick(
    val pos1: Triple<Int, Int, Int>,
    val pos2: Triple<Int, Int, Int>,
) : Iterable<Triple<Int, Int, Int>> {
    val height = minOf(pos1.third, pos2.third)
    override fun iterator() = sequence {
        val (x1, y1, z1) = pos1
        val (x2, y2, z2) = pos2

        (x1..x2).forEach { x ->
            (y1..y2).forEach { y ->
                (z1..z2).forEach { z ->
                    yield(Triple(x, y, z))
                }
            }
        }
    }.iterator()

    fun descend(distance: Int): Brick {
        val (x1, y1, z1) = pos1
        val (x2, y2, z2) = pos2

        return Brick(Triple(x1, y1, z1 - distance), Triple(x2, y2, z2 - distance))
    }
}

fun <T> List<T>.toTriple(): Triple<T, T, T> {
    check(size == 3)
    return Triple(this[0], this[1], this[2])
}