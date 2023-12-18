package day17

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day17")
    println(day17p1(input))
}

fun day17p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val grid = lines.map { line -> line.map { it.digitToInt() } }
    val width = grid[0].size
    val height = grid.size
    val pathingGraph = PathingGraph(grid)

    val start = pathingGraph.getNode(0, 0, 0, 0)
    val queue = KindOfPriorityQueue<Node>(numQueues = 10)
    val distances = mutableMapOf(start to 0)
    queue.enqueue(start, 0)

    while (queue.isNotEmpty()) {
        val (node, distance) = queue.dequeue()
        pathingGraph
            .getNeighbours(node)
            .filterNot { neighbor -> distances.containsKey(neighbor) }
            .forEach { neighbor ->
                distances[neighbor] = distance + neighbor.heatLoss
                queue.enqueue(neighbor, distance + neighbor.heatLoss)
            }
    }

    val returnValue = distances
        .filter { (node, _) -> node.x == width - 1 && node.y == height - 1 }
        .minOf { (_, distance) -> distance }

    return returnValue.toString()
}

class KindOfPriorityQueue<E>(private val numQueues: Int) {
    private val queues = List(numQueues) { mutableListOf<E>() }
    private var minDistance = 0
    private val currentQueue: MutableList<E>
        get() {
            var i = 0
            while (queues[minDistance % numQueues].isEmpty() && ++i < numQueues) {
                minDistance++
            }
            return queues[minDistance % numQueues]
        }

    fun enqueue(element: E, priority: Int) {
        require(priority in minDistance until minDistance + numQueues) { "$priority !in [$minDistance + $numQueues]"}

        queues[priority % numQueues].add(element)
    }

    fun dequeue(): Pair<E, Int> {
        val result = currentQueue.removeFirst() to minDistance

        return result
    }

    fun isEmpty() = currentQueue.isEmpty()
    fun isNotEmpty() = currentQueue.isNotEmpty()
}

class PathingGraph(
    private val grid: List<List<Int>>,
    private val min: Int = 1,
    private val max: Int = 3,
) {
    fun getNode(x: Int, y: Int, stepsX: Int = 0, stepsY: Int = 0) = Node(
        x = x,
        y = y,
        heatLoss = grid[y][x],
        stepsX = stepsX,
        stepsY = stepsY,
    )

    fun getNeighbours(node: Node) = nbrs_not_diag(node.x to node.y)
        .filter { (x, y) ->
            x in 0 until grid[0].size && y in 0 until grid.size
        }
        .filter { (x, y) ->
            when {
                node.stepsX <= -max -> node.x == x
                node.stepsX >= max -> node.x == x
                node.stepsX <= -min -> node.x >= x
                node.stepsX >= min -> node.x <= x
                node.stepsX in -min + 1 until 0 -> node.x > x
                node.stepsX in 1 until min -> node.x < x

                node.stepsY <= -max -> node.y == y
                node.stepsY >= max -> node.y == y
                node.stepsY <= -min -> node.y >= y
                node.stepsY >= min -> node.y <= y
                node.stepsY in -min + 1 until 0 -> node.y > y
                node.stepsY in 1 until min -> node.y < y
                else -> true
            }
        }
        .map { (x, y) ->
            if (x == node.x) {
                if (y > node.y)
                    getNode(x, y, stepsY = node.stepsY + 1)
                else
                    getNode(x, y, stepsY = node.stepsY - 1)
            } else {
                if (x > node.x)
                    getNode(x, y, stepsX = node.stepsX + 1)
                else
                    getNode(x, y, stepsX = node.stepsX - 1)
            }
        }
}

data class Node(
    val x: Int,
    val y: Int,
    val heatLoss: Int,
    val stepsX: Int,
    val stepsY: Int,
) {
    init {
        require(x >= 0)
        require(y >= 0)
        require(heatLoss in 0..9)
        // require(stepsX in -3..3)
        // require(stepsY in -3..3)
        require((stepsX == 0) xor (stepsY == 0) || (x == 0 && y == 0))
    }
}
