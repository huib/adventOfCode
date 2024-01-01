package day23

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day23")
    println(day23p1(input))
}

fun day23p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val vertices = createGraph(lines).also { println(it.size) }
    val startVertex = vertices.first()
    val endVertex = vertices.last()

    val internalVertices = vertices
        .drop(1)
        .dropLast(1)
        .reduce()

    val returnValue = startVertex.outNbrs[endVertex]

    return returnValue.toString()
}

fun createGraph(lines: List<String>): List<Vertex<Char>> {
    val posToVertex = lines
        .flatMapIndexed { y, line -> line.mapIndexed { x, char -> (x to y) to Vertex(char, x, y) } }
        .filter { (pos, vertex) -> vertex.type != '#' }
        .toMap()

    posToVertex.forEach { (pos, vertex) ->
        when (vertex.type) {
            '>' -> posToVertex[pos.first + 1 to pos.second]?.let(vertex::addOutNeighbor)
            '<' -> posToVertex[pos.first - 1 to pos.second]?.let(vertex::addOutNeighbor)
            'v' -> posToVertex[pos.first to pos.second + 1]?.let(vertex::addOutNeighbor)
            '^' -> posToVertex[pos.first to pos.second - 1]?.let(vertex::addOutNeighbor)
            else -> nbrs_not_diag(pos)
                .mapNotNull(posToVertex::get)
                .forEach(vertex::addOutNeighbor)
        }
    }

    return posToVertex.values.toList()
}

fun <T> List<Vertex<T>>.reduce() {
    var list = this

    do {
        val previousSize = list.size
        list = list.filter { it.inNbrs.isNotEmpty() && it.outNbrs.isNotEmpty() }
            .onEach(::contract)
            .filter { (it.inNbrs.keys + it.outNbrs.keys).size > 1 }
            .also { println(it.size) }
    } while (list.isNotEmpty() && list.size < previousSize)
}

fun <T> contract(vertex: Vertex<T>) {
//    if (vertex.inNbrs.size == 1 && vertex.outNbrs.size == 1) {
//        val (inNbr, weight1) = vertex.inNbrs.entries.single()
//        val (outNbr, weight2) = vertex.outNbrs.entries.single()
//        if (inNbr != outNbr && outNbr !in inNbr.outNbrs) {
//            inNbr.addOutNeighbor(outNbr, weight1 + weight2)
//            vertex.removeAllNeighbors()
//        }
//    }

    if (vertex.inNbrs.size == 1) {
        val (inNbr, weight1) = vertex.inNbrs.entries.single()
        vertex.outNbrs.forEach { (outNbr, weight2) ->
            if (inNbr != outNbr) {
                inNbr.addOutNeighbor(outNbr, weight1 + weight2)
            }
        }
        vertex.removeAllNeighbors()
    }

    if (vertex.outNbrs.size == 1) {
        val (outNbr, weight1) = vertex.outNbrs.entries.single()
        vertex.inNbrs.forEach { (inNbr, weight2) ->
            if (inNbr != outNbr) {
                outNbr.addInNeighbor(inNbr, weight1 + weight2)
            }
        }
        vertex.removeAllNeighbors()
    }

    if (vertex.inNbrs.size == 2 && vertex.inNbrs.keys == vertex.outNbrs.keys) {
        val (nbr1, weight1) = vertex.inNbrs.entries.first()
        val (nbr2, weight2) = vertex.inNbrs.entries.last()

        if (nbr1 !in nbr2.outNbrs) {
            nbr1.addNeighbor(nbr2, weight1 + weight2)
            vertex.removeNeighbor(nbr1)
            vertex.removeNeighbor(nbr2)
        }
    }
}

class Vertex<T>(
    val type: T,
    val x: Int,
    val y: Int,
    outNbrs: Map<Vertex<T>, Int> = emptyMap(),
    inNbrs: Map<Vertex<T>, Int> = emptyMap(),
) {
    val outNbrs: Map<Vertex<T>, Int>
        get() = mutableOutNbrs

    private val mutableOutNbrs: MutableMap<Vertex<T>, Int> = outNbrs.toMutableMap()

    val inNbrs: Map<Vertex<T>, Int>
        get() = mutableInNbrs

    private val mutableInNbrs: MutableMap<Vertex<T>, Int> = inNbrs.toMutableMap()

    fun addNeighbor(nbr: Vertex<T>, weight: Int = 1) {
        addOutNeighbor(nbr, weight)
        addInNeighbor(nbr, weight)
    }

    fun addOutNeighbor(nbr: Vertex<T>, weight: Int = 1) {
        val newWeight = maxOf(weight, mutableOutNbrs[nbr] ?: 0)
        if (mutableOutNbrs.put(nbr, newWeight) != newWeight) {
            nbr.addInNeighbor(this, newWeight)
        }
    }

    fun addInNeighbor(nbr: Vertex<T>, weight: Int = 1) {
        val newWeight = maxOf(weight, mutableInNbrs[nbr] ?: 0)
        if (mutableInNbrs.put(nbr, newWeight) != newWeight) {
            nbr.addOutNeighbor(this, newWeight)
        }
    }

    fun removeNeighbor(nbr: Vertex<T>) {
        removeInNeighbor(nbr)
        removeOutNeighbor(nbr)
    }

    fun removeOutNeighbor(nbr: Vertex<T>) {
        if (mutableOutNbrs.remove(nbr) != null) {
            nbr.removeInNeighbor(this)
        }
    }

    fun removeInNeighbor(nbr: Vertex<T>) {
        if (mutableInNbrs.remove(nbr) != null) {
            nbr.removeOutNeighbor(this)
        }
    }

    fun removeAllNeighbors() {
        removeAllInNeighbors()
        removeAllOutNeighbors()
    }

    fun removeAllOutNeighbors() {
        mutableOutNbrs.keys.toList().forEach {
            it.removeInNeighbor(this)
        }
    }

    fun removeAllInNeighbors() {
        mutableInNbrs.keys.toList().forEach {
            it.removeOutNeighbor(this)
        }
    }

    override fun toString() = "vertex $type ($x, $y) ->${outNbrs.size} <-${inNbrs.size}"
}
