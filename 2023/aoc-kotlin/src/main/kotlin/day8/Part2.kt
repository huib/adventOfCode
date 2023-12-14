package day8

import getInput
import lcm

fun main() {
    val input = getInput("/input_day8")
    println(day8p2v2(input))
}

fun day8p2v2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val route = lines.first()
    val nodeMap = lines.drop(2).map(::Node).associateBy(Node::code)

    val startNodes = nodeMap.values.filter { it.code.endsWith("A") }

    val cycleDefs = startNodes.map { startNode ->
        var steps = 0L
        var parity = 0
        var currentNode = startNode
        val visited = mutableMapOf<Pair<Node, Int>, Long>()
        val zNodeIndices = mutableListOf<Long>()
        while ((currentNode to parity) !in visited) {
            visited[currentNode to parity] = steps
            if (currentNode.code.endsWith("Z")) {
                zNodeIndices.add(steps)
            }

            val direction = route[parity]

            currentNode = when (direction) {
                'R' -> nodeMap[currentNode.right]!!
                'L' -> nodeMap[currentNode.left]!!
                else -> error("")
            }
            steps++
            parity = (steps % route.length).toInt()
        }

        val offset = visited[currentNode to parity]!!
        val cycleLength = steps - offset
        Triple(offset, cycleLength, zNodeIndices.single())
    }.onEach(::println)

    check(cycleDefs.all { it.second == it.third })

    val cycleLengths = cycleDefs.map { it.second }

    val returnValue = lcm(cycleLengths)

    return returnValue.toString()
}
