package day23

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day23")
    println(day23p1(input))
}

fun day23p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val vertices = createGraph(lines)

    return findLongestPath(vertices).toString()
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

fun <T> List<Vertex<T>>.reduce(): Pair<() -> Unit, List<Vertex<T>>> {
    val undoStack = mutableListOf<() -> Unit>()
    var list = this.filter { it.inNbrs.isNotEmpty() || it.outNbrs.isNotEmpty() }

    do {
        val previousSize = list.size
        list = list
            .onEach { contract(it).also(undoStack::add) }
            .filter { it.inNbrs.isNotEmpty() || it.outNbrs.isNotEmpty() }
    } while (list.isNotEmpty() && list.size < previousSize)

    return { undoStack.reversed().forEach { it() } } to list
}

fun <T> contract(vertex: Vertex<T>): () -> Unit {
    val undoStack = mutableListOf<() -> Unit>()
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
                    .also(undoStack::add)
            }
        }
        vertex.removeAllNeighbors()
            .also(undoStack::add)
    }

    if (vertex.outNbrs.size == 1) {
        val (outNbr, weight1) = vertex.outNbrs.entries.single()
        vertex.inNbrs.forEach { (inNbr, weight2) ->
            if (inNbr != outNbr) {
                outNbr.addInNeighbor(inNbr, weight1 + weight2)
                    .also(undoStack::add)
            }
        }
        vertex.removeAllNeighbors()
            .also(undoStack::add)
    }

    if (vertex.inNbrs.size == 2 && vertex.inNbrs.keys == vertex.outNbrs.keys) {
        val (nbr1, weight1) = vertex.inNbrs.entries.first()
        val (nbr2, weight2) = vertex.inNbrs.entries.last()

        if (nbr1 !in nbr2.outNbrs) {
            nbr1.addNeighbor(nbr2, weight1 + weight2)
                .also(undoStack::add)
            vertex.removeNeighbor(nbr1)
                .also(undoStack::add)
            vertex.removeNeighbor(nbr2)
                .also(undoStack::add)
        }
    }

    return { undoStack.reversed().forEach { it() } }
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

    fun reachable(from: Vertex<T>, visited: MutableSet<Vertex<T>> = mutableSetOf()): Boolean {
        return this == from || inNbrs.keys.filterNot(visited::contains).any {
            visited.add(it)
            it.reachable(from = from, visited)
        }
    }

    fun addNeighbor(nbr: Vertex<T>, weight: Int = 1): () -> Unit {
        val undo1 = addOutNeighbor(nbr, weight)
        val undo2 = addInNeighbor(nbr, weight)
        return { undo2(); undo1() }
    }

    fun addOutNeighbor(nbr: Vertex<T>, weight: Int = 1): () -> Unit {
        val oldWeight = mutableOutNbrs[nbr]
        val newWeight = maxOf(weight, oldWeight ?: 0)
        return if (mutableOutNbrs.put(nbr, newWeight) != newWeight) {
            val undo = nbr.addInNeighbor(this, newWeight);
            {
                undo()
                if (oldWeight == null) {
                    mutableOutNbrs.remove(nbr)
                } else {
                    mutableOutNbrs[nbr] = oldWeight
                }
            }
        } else {
            {}
        }
    }

    fun addInNeighbor(nbr: Vertex<T>, weight: Int = 1): () -> Unit {
        val oldWeight = mutableInNbrs[nbr]
        val newWeight = maxOf(weight, mutableInNbrs[nbr] ?: 0)
        return if (mutableInNbrs.put(nbr, newWeight) != newWeight) {
            val undo = nbr.addOutNeighbor(this, newWeight);
            {
                undo()
                if (oldWeight == null) {
                    mutableInNbrs.remove(nbr)
                } else {
                    mutableInNbrs[nbr] = oldWeight
                }
            }
        } else {
            {}
        }
    }

    fun removeNeighbor(nbr: Vertex<T>): () -> Unit {
        val undo1 = removeInNeighbor(nbr)
        val undo2 = removeOutNeighbor(nbr)
        return { undo2(); undo1() }
    }

    fun removeOutNeighbor(nbr: Vertex<T>): () -> Unit {
        val oldWeight = mutableOutNbrs.remove(nbr)
        return if (oldWeight != null) {
            nbr.removeInNeighbor(this);
            { addOutNeighbor(nbr, oldWeight) }
        } else {
            {}
        }
    }

    fun removeInNeighbor(nbr: Vertex<T>): () -> Unit {
        val oldWeight = mutableInNbrs.remove(nbr)
        return if (oldWeight != null) {
            nbr.removeOutNeighbor(this);
            { addInNeighbor(nbr, oldWeight) }
        } else {
            {}
        }
    }

    fun removeAllNeighbors(exceptIn: Set<Vertex<T>> = emptySet(), exceptOut: Set<Vertex<T>> = emptySet()): () -> Unit {
        val undo1 = removeAllInNeighbors(exceptIn)
        val undo2 = removeAllOutNeighbors(exceptOut)
        return { undo2(); undo1() }
    }

    fun removeAllOutNeighbors(except: Set<Vertex<T>> = emptySet()): () -> Unit {
        val oldOutNeighbors = mutableOutNbrs.toMap()

        mutableOutNbrs.keys.filterNot(except::contains).forEach {
            it.removeInNeighbor(this)
        }

        return {
            oldOutNeighbors.entries.forEach { (nbr, weight) ->
                addOutNeighbor(nbr, weight)
            }
        }
    }

    fun removeAllInNeighbors(except: Set<Vertex<T>> = emptySet()): () -> Unit {
        val oldInNeighbors = mutableInNbrs.toMap()

        mutableInNbrs.keys.filterNot(except::contains).forEach {
            it.removeOutNeighbor(this)
        }

        return {
            oldInNeighbors.entries.forEach { (nbr, weight) ->
                addInNeighbor(nbr, weight)
            }
        }
    }

    override fun toString() = "vertex $type ($x, $y) ->${outNbrs.size} <-${inNbrs.size}"
}
