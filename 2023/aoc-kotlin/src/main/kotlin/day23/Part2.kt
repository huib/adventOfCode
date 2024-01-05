package day23

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day23")
    println(day23p2(input))
}

fun day23p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val vertices = createGraphPart2(lines)

    val returnValue = findLongestPath(vertices)

    check(returnValue > 4042) { "answer $returnValue to low, should be > 6270" }
    return returnValue.toString()
}

fun findLongestPath(vertices: List<Vertex<Char>>, lb: Int = 0): Int {
    val startVertex = vertices.first().apply { removeAllInNeighbors() }
    val endVertex = vertices.last().apply { removeAllOutNeighbors() }
    val otherVertices = vertices.drop(1).dropLast(1)

    return findLongestPath(startVertex, endVertex, otherVertices, lb)
}

fun findLongestPath(
    startVertex: Vertex<Char>,
    endVertex: Vertex<Char>,
    otherVertices: List<Vertex<Char>>,
    lb: Int,
): Int {
    val (undoReduce, reducedVertices) = otherVertices.reduce()

    if (!endVertex.reachable(from = startVertex)) {
        undoReduce()
        return lb
    }
    val ub = reducedVertices.sumOf { it.outNbrs.values.sum() } + startVertex.outNbrs.values.sum()
    if (ub <= lb) {
        undoReduce()
        return lb
    }

    val longestPath = if (endVertex in startVertex.outNbrs) {
        startVertex.outNbrs.getValue(endVertex)
    } else {
        val firstSplitVertex = startVertex.outNbrs.keys.singleOrNull() ?: startVertex
        val allNeighbors = firstSplitVertex.outNbrs.keys.toList()
        allNeighbors.fold(lb) { lb, nbr ->
            val addAllNeighborsBack = firstSplitVertex.removeAllNeighbors(
                exceptIn = setOf(startVertex),
                exceptOut = setOf(nbr),
            )
            val longestPath = findLongestPath(startVertex, endVertex, reducedVertices, lb)
            addAllNeighborsBack()

            maxOf(lb, longestPath)
        }
    }

    undoReduce()
    return longestPath
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
