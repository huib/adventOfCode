package day16

import getInput

fun main() {
    val input = getInput("/input_day16")
    println(day16p1(input))
}

fun day16p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val width = lines.first().length
    val height = lines.size
    val network = linesToNetwork(lines)

    val start = Direction.entries.mapNotNull { direction ->
        network.first { it.position == -1 to 0 }.visible(direction).singleOrNull()
    }.single()

    val returnValue = countEnergizedTiles(start, width, height)

    return returnValue.toString()
}

fun countEnergizedTiles(start: Pair<OpticalInstrument, Direction>, width: Int, height: Int): Int {
    val beams = mutableListOf(start)
    val processedBeams = mutableSetOf<Pair<OpticalInstrument, Direction>>()
    val tileCounter = TileCounter(width, height)

    while (beams.isNotEmpty()) {
        val beam = beams.removeLast()
        if (!processedBeams.add(beam)) {
            continue
        }

        beam.first.visible(beam.second).forEach { (neighbor, direction) ->
            tileCounter.registerTiles(beam.first.position, neighbor.position)
            beams.add(neighbor to direction)
        }
    }

    return tileCounter.getTotal()
}

fun linesToNetwork(lines: List<String>): List<OpticalInstrument> {
    val northNeighbors = MutableList<OpticalInstrument>(lines.first().length) { Wall(it, -1) }
    return northNeighbors.toList() + lines.flatMapIndexed { y, line ->
        var westNeighbor: OpticalInstrument = Wall(-1, y)
        listOf(westNeighbor) + line.mapIndexedNotNull { x, char ->
            when (char) {
                '.' -> null
                '/' -> Mirror(x, y, leftDown = false)
                '\\' -> Mirror(x, y, leftDown = true)
                '-' -> Splitter(x, y, horizontal = true)
                '|' -> Splitter(x, y, horizontal = false)
                else -> null
            }?.also { currentOpticalInstrument ->
                westNeighbor.setNeighbor(currentOpticalInstrument)
                currentOpticalInstrument.setNeighbor(westNeighbor)
                westNeighbor = currentOpticalInstrument

                northNeighbors[x].setNeighbor(currentOpticalInstrument)
                currentOpticalInstrument.setNeighbor(northNeighbors[x])
                northNeighbors[x] = currentOpticalInstrument
            }
        }.let {
            val wall = Wall(line.length, y)
            westNeighbor.setNeighbor(wall)
            wall.setNeighbor(westNeighbor)
            it + wall
        }
    }.let {
        it + northNeighbors.mapIndexed { x, neighbor ->
            val wall = Wall(x, lines.size)
            neighbor.setNeighbor(wall)
            wall.setNeighbor(neighbor)
            wall
        }
    }
}

class TileCounter(val width: Int, val height: Int) {
    private val grid = List(width) { MutableList(height) { false } }

    fun registerTiles(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        if (from.first == to.first) {
            val x = from.first
            val ys = minOf(from.second, to.second).coerceAtLeast(0)..maxOf(from.second, to.second).coerceAtMost(height - 1)
            ys.forEach { y -> grid[x][y] = true }
        } else if (from.second == to.second) {
            val y = from.second
            val xs = minOf(from.first, to.first).coerceAtLeast(0)..maxOf(from.first, to.first).coerceAtMost(width - 1)
            xs.forEach { x -> grid[x][y] = true }
        } else {
            error("can't register diagonally from $from to $to")
        }
    }

    fun getTotal() = grid.sumOf { it.count { it } }

    override fun toString() = buildString {
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                if (grid[x][y]) {
                    append('#')
                } else {
                    append('.')
                }
            }
            appendLine()
        }
    }
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

abstract class OpticalInstrument(
    val x: Int,
    val y: Int,
) {
    val position = x to y
    protected val neighbors = mutableMapOf<Direction, OpticalInstrument>()
    abstract fun visible(direction: Direction): Iterable<Pair<OpticalInstrument, Direction>>
    fun setNeighbor(neighbor: OpticalInstrument) {
        if (neighbor.x == x) {
            if (neighbor.y > y) {
                neighbors[Direction.SOUTH] = neighbor
            } else {
                neighbors[Direction.NORTH] = neighbor
            }
        } else if (neighbor.y == y) {
            if (neighbor.x > x) {
                neighbors[Direction.EAST] = neighbor
            } else {
                neighbors[Direction.WEST] = neighbor
            }
        } else {
            error("$this: invalid neighbor $neighbor")
        }
    }
}
class Mirror(x: Int, y: Int, val leftDown: Boolean) : OpticalInstrument(x, y) {
    override fun visible(direction: Direction): Iterable<Pair<OpticalInstrument, Direction>> {
        val neighborDirection = when (direction) {
            Direction.NORTH -> if (leftDown) Direction.WEST else Direction.EAST
            Direction.EAST -> if (leftDown) Direction.SOUTH else Direction.NORTH
            Direction.SOUTH -> if (leftDown) Direction.EAST else Direction.WEST
            Direction.WEST -> if (leftDown) Direction.NORTH else Direction.SOUTH
        }

        val neighbor = neighbors[neighborDirection]

        return if (neighbor == null) {
            emptyList()
        } else {
            listOf(neighbor to neighborDirection)
        }
    }
}

class Splitter(x: Int, y: Int, val horizontal: Boolean) : OpticalInstrument(x, y) {
    override fun visible(direction: Direction): Iterable<Pair<OpticalInstrument, Direction>> {
        val neighborDirections = when (direction) {
            Direction.NORTH, Direction.SOUTH -> if (horizontal) listOf(Direction.WEST, Direction.EAST) else listOf(direction)
            Direction.EAST, Direction.WEST -> if (!horizontal) listOf(Direction.NORTH, Direction.SOUTH) else listOf(direction)
        }

        return neighborDirections.mapNotNull { neighborDirection ->
            val neighbor = neighbors[neighborDirection]
            if (neighbor == null) {
                null
            } else {
                neighbor to neighborDirection
            }
        }
    }
}

class Wall(x: Int, y: Int) : OpticalInstrument(x, y) {
    override fun visible(direction: Direction): Iterable<Pair<OpticalInstrument, Direction>> {
        return if (neighbors.containsKey(direction)) {
            listOf(neighbors[direction]!! to direction)
        } else {
            emptyList()
        }
    }
}
