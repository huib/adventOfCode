package day25

import com.google.ortools.Loader
import com.google.ortools.graph.MaxFlow
import getInput

fun main() {
    val input = getInput("/input_day25")
    println(day25p1(input))
}

fun day25p1(input: String): String {
    val edges = input.split("\n").map { it.trim() }.flatMapTo(mutableSetOf(), ::parseEdges)
    val vertexNameToIndex = edges
        .flatMapTo(mutableSetOf()) { listOf(it.first, it.second) }
        .mapIndexed { index, vertexName -> vertexName to index }
        .toMap()
    val edgesIdx = edges.map { (a, b) -> vertexNameToIndex.getValue(a) to vertexNameToIndex.getValue(b) }

    Loader.loadNativeLibraries()
    val maxFlow = MaxFlow()

    edgesIdx.forEachIndexed { index, (a, b) ->
        val i = maxFlow.addArcWithCapacity(a, b, 1)
        val j = maxFlow.addArcWithCapacity(b, a, 1)
        check(i == 2 * index)
        check(j == i + 1)
    }

    val source = (0 until vertexNameToIndex.size - 1).indexOfFirst { source ->
        val result = maxFlow.solve(source, vertexNameToIndex.size - 1)
        check(result == MaxFlow.Status.OPTIMAL)

        maxFlow.optimalFlow == 3L
    }

    val nbrsReducedGraph = edgesIdx.filterIndexed { edgeIndex, (a, b) ->
        maxFlow.getFlow(2 * edgeIndex) == 0L && maxFlow.getFlow(2 * edgeIndex + 1) == 0L
    }
        .flatMap { (a, b) -> listOf(a to b, b to a) }
        .groupBy { it.first }
        .mapValues { (v1, e) -> e.map { it.second } }

    val cc = mutableSetOf(vertexNameToIndex.size - 1)
    val q = cc.toMutableList()
    while (q.isNotEmpty()) {
        val v = q.removeFirst()

        val nbrs = nbrsReducedGraph.getValue(v)

        nbrs.filter(cc::add).onEach(q::add)
    }

    val cc2 = mutableSetOf(source)
    val q2 = cc2.toMutableList()
    while (q2.isNotEmpty()) {
        val v = q2.removeFirst()

        val nbrs = nbrsReducedGraph.getValue(v)

        nbrs.filter(cc2::add).onEach(q2::add)
    }

    println("total: ${vertexNameToIndex.size}, cc1: ${cc.size}, cc2: ${cc2.size}, others? ${vertexNameToIndex.size - cc.size - cc2.size}")

    val returnValue = cc.size * cc2.size

    return returnValue.toString()
}

fun parseEdges(str: String): Iterable<Pair<String, String>> {
    val (thisEdge, others) = str.split(": ")
    val nbrs = others.split(' ')

    return nbrs.map { nbr ->
        Pair(minOf(thisEdge, nbr), maxOf(thisEdge, nbr))
    }
}
