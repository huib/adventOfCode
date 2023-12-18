package day17

import getInput
import kotlin.math.abs

fun main() {
    val input = getInput("/input_day17")
    println(day17p2(input))
}

fun day17p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val grid = lines.map { line -> line.map { it.digitToInt() } }
    val width = grid[0].size
    val height = grid.size
    val pathingGraph = PathingGraph(grid, min = 4, max = 10)

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
        .filter { (node, _) ->
            node.x == width - 1 && node.y == height - 1 &&
                (abs(node.stepsX) >= 4 || abs(node.stepsY) >= 4)
        }
        .minOf { (_, distance) -> distance }

    return returnValue.toString()
}
