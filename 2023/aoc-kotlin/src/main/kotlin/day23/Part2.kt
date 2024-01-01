package day23

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day23")
    println(day23p2(input))
}

fun day23p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val vertices = createGraphPart2(lines).also { println(it.size) }
    val startVertex = vertices.first().apply { removeAllInNeighbors() }
    val endVertex = vertices.last().apply { removeAllOutNeighbors() }

    val internalVertices = vertices
        .drop(1)
        .dropLast(1)
        .reduce()

    val returnValue = startVertex.outNbrs[endVertex]

    return returnValue.toString()
}

fun createGraphPart2(lines: List<String>): List<Vertex<Char>> {
    val posToVertex = lines
        .flatMapIndexed { y, line -> line.mapIndexed { x, char -> (x to y) to Vertex(char, x, y) } }
        .filter { (pos, vertex) -> vertex.type != '#' }
        .toMap()

    posToVertex.forEach { (pos, vertex) ->
        nbrs_not_diag(pos)
            .mapNotNull(posToVertex::get)
            .forEach(vertex::addOutNeighbor)
    }

    return posToVertex.values.toList()
}
