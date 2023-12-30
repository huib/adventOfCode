package day21

import day10.nbrs_not_diag
import getInput
import java.lang.Math.floorDiv
import kotlin.time.measureTime

fun main() {
    val input = getInput("/input_day21")
    measureTime { println(day21p2(input)) }.also(::println)
}

val width = 131
val height = 131

fun day21p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val field = InfiniteField(lines)

    // val numSteps = 4 * 131L + 26501365 % 131
    val numSteps = 26501365L
    println(numSteps)

    val centerCycle = findCycle(field.startState, field::propagateCenter)
    centerCycle.printInfo(numSteps, "center")
    centerCycle.printInfo(numSteps + 1, "offcenter")

    val NECycle = diagonalCycle(centerCycle, field, 0 to field.height - 1)
    NECycle.printInfo(numSteps, "NE")
    val NWCycle = diagonalCycle(centerCycle, field, field.width - 1 to field.height - 1)
    NWCycle.printInfo(numSteps, "NW")
    val SWCycle = diagonalCycle(centerCycle, field, field.width - 1 to 0)
    SWCycle.printInfo(numSteps, "SW")
    val SECycle = diagonalCycle(centerCycle, field, 0 to 0)
    SECycle.printInfo(numSteps, "SE")

    val westCycle = findCycle(field.startState, field::propagateLeft)
    westCycle.printInfo(numSteps, "west")
    val northCycle = findCycle(field.startState, field::propagateUp)
    northCycle.printInfo(numSteps, "north")
    val eastCycle = findCycle(field.startState, field::propagateRight)
    eastCycle.printInfo(numSteps, "east")
    val southCycle = findCycle(field.startState, field::propagateDown)
    southCycle.printInfo(numSteps, "south")

//    buildList {
//        repeat(((numSteps - 66) / 131).toInt()) {
//            add(centerCycle.getState(numSteps + it % 2).size)
//        }
//        add(eastCycle.getState(numSteps).size)
//    }.also {
//        println("east $numSteps :  ${it.sum()}    $it")
//    }
//
//    buildList {
//        repeat(((numSteps - 66) / 131).toInt()) {
//            add(centerCycle.getState(numSteps + it % 2).size)
//        }
//        add(westCycle.getState(numSteps).size)
//    }.also {
//        println("west $numSteps :  ${it.sum()}    $it")
//    }
//
//    buildList {
//        repeat(((numSteps - 66) / 131).toInt()) {
//            add(centerCycle.getState(numSteps + it % 2).size)
//        }
//        add(northCycle.getState(numSteps).size)
//    }.also {
//        println("north $numSteps :  ${it.sum()}    $it")
//    }
//
//    buildList {
//        repeat(((numSteps - 66) / 131).toInt()) {
//            add(centerCycle.getState(numSteps + it % 2).size)
//        }
//        add(southCycle.getState(numSteps).size)
//    }.also {
//        println("south $numSteps :  ${it.sum()}    $it")
//    }
//
//    diagList(numSteps, NECycle, centerCycle).also {
//        println("NE $numSteps :  ${it.sum()}    $it")
//    }
//
//    diagList(numSteps, SECycle, centerCycle).also {
//        println("SE $numSteps :  ${it.sum()}    $it")
//    }
//
//    diagList(numSteps, SWCycle, centerCycle).also {
//        println("SW $numSteps :  ${it.sum()}    $it")
//    }
//
//    diagList(numSteps, NWCycle, centerCycle).also {
//        println("NW $numSteps :  ${it.sum()}    $it")
//    }

    val returnValue = quadrantCount(numSteps, NECycle, centerCycle) +
        quadrantCount(numSteps, SECycle, centerCycle) +
        quadrantCount(numSteps, SWCycle, centerCycle) +
        quadrantCount(numSteps, NWCycle, centerCycle) +
        buildList {
            val r = (numSteps - 66) / 131
            add(centerCycle.getState(numSteps + 1).size * 4 * (r / 2))
            add(centerCycle.getState(numSteps).size * 4 * ((r + 1) / 2))
            add(northCycle.getState(numSteps).size)
            add(southCycle.getState(numSteps).size)
            add(eastCycle.getState(numSteps).size)
            add(westCycle.getState(numSteps).size)
        }.sum() -
        3 * centerCycle.getState(numSteps).size

    return returnValue.toString()
}

fun diagonalCycle(
    centerCycle: CycleData<Triple<Pointset, Pointset, Pointset>>,
    field: InfiniteField,
    startPosition: Pair<Int, Int>,
): CycleData<Triple<Pointset, Pointset, Pointset>> {
    val SWCorner = Triple<Pointset, Pointset, Pointset>(setOf(), setOf(), setOf(startPosition))
    val stepsToCorner = centerCycle.states.indexOfFirst { (step1, step2, step3) ->
        0 to field.height - 1 in step3
    } + 2
    return findCycle(SWCorner, field::propagateCenter).states.loop(offset = stepsToCorner, length = field.width + field.height) // TODO: calc offset
}

fun diagList(
    numSteps: Long,
    cycleData: CycleData<Triple<Pointset, Pointset, Pointset>>,
    centerCycle: CycleData<Triple<Pointset, Pointset, Pointset>>,
) = buildList {
    repeat(((numSteps - cycleData.cycleStart) / (cycleData.cycleLength / 2)).toInt()) {
        add(centerCycle.getState(numSteps + 1 + it % 2).size)
    }
    add(cycleData.getState(numSteps).size)
    add(cycleData.getState(numSteps + cycleData.cycleLength / 2).size)
}

fun quadrantCount(
    numSteps: Long,
    cycleData: CycleData<Triple<Pointset, Pointset, Pointset>>,
    centerCycle: CycleData<Triple<Pointset, Pointset, Pointset>>,
): Long {
    val x = (numSteps - cycleData.cycleStart) / (cycleData.cycleLength / 2) - 1

    val halfXUp = (x + 1) / 2
    val halfXDn = x / 2

    return halfXUp * halfXUp * centerCycle.getState(numSteps).size +
            halfXDn * (halfXDn + 1) * centerCycle.getState(numSteps + 1).size +
            (x + 1) * cycleData.getState(numSteps).size +
            (x + 2) * cycleData.getState(numSteps + cycleData.cycleLength / 2).size
}

fun CycleData<Triple<Pointset, Pointset, Pointset>>.printInfo(numSteps: Long, label: String) {
    println("cycle $label: $cycleStart + $cycleLength * k")
    println("after $numSteps:")
    println(getState(numSteps).chunkSummary)
    println()
}

val Triple<Pointset, Pointset, Pointset>.chunkSummary: String
    get() {
        val counter = mutableMapOf<Pair<Int, Int>, Int>()
        (first + third).forEach { pos ->
            val chunk = floorDiv(pos.first, width) to floorDiv(pos.second, height)
            counter[chunk] = counter[chunk]?.let { it + 1 } ?: 1
        }

        val minX = counter.keys.minOf { (x, y) -> x }
        val maxX = counter.keys.maxOf { (x, y) -> x }
        val minY = counter.keys.minOf { (x, y) -> y }
        val maxY = counter.keys.maxOf { (x, y) -> y }



        return buildString {
            (minY..maxY).forEach { y ->
                (minX..maxX).forEach { x ->
                    append(counter[x to y])
                    append("    ")
                }
                appendLine()
            }
        }
    }

val Triple<Pointset, Pointset, Pointset>.size: Long
    get() {
        check((first intersect third).isEmpty())

        return first.size.toLong() + third.size
    }

val InfiniteField.startState
    get() = Triple<Pointset, Pointset, Pointset>(setOf(), setOf(), setOf(startPosition))

typealias Pointset = Set<Pair<Int, Int>>
typealias MutablePointSet = MutableSet<Pair<Int, Int>>
class InfiniteField(val lines: List<String>) {
    val width = lines.first().length
    val height = lines.size
    val startY = lines.indexOfFirst { it.contains('S') }
    val startX = lines[startY].indexOf('S')
    val startPosition = startX to startY

    init {
        check(this.width == day21.width) { "hardcoded width for debug-prints doesn't match input width" }
        check(this.height == day21.height) { "hardcoded height for debug-prints doesn't match input height" }
    }

    val cache = mutableMapOf<
        Triple<Pointset, Pointset, Pointset>,
        Triple<Pointset, Pointset, Pointset>,
        >()

    fun propagate(steps123: Triple<Pointset, Pointset, Pointset>) =
        propagate(steps123.first, steps123.second, steps123.third)

    fun propagate(step1: Pointset, step2: Pointset, step3: Pointset): Triple<Pointset, Pointset, Pointset> {
        return cache.getOrPut(Triple(step1, step2, step3)) {
            val step4 = step3.flatMapTo(mutableSetOf()) { pos ->
                nbrs_not_diag(pos)
                    .filterNot(step2::contains)
                    .filter(::isPassable)
            }
            Triple(step2, step1 + step3, step4)
        }
    }

    fun fastPropagate(steps123: Triple<MutablePointSet, MutablePointSet, Pointset>) = fastPropagate(steps123.first, steps123.second, steps123.third)
    fun fastPropagate(step1: MutablePointSet, step2: MutablePointSet, step3: Pointset): Triple<MutablePointSet, MutablePointSet, Pointset> {
        val step4 = step3.flatMapTo(mutableSetOf()) { pos ->
            nbrs_not_diag(pos)
                .filterNot(step2::contains)
                .filter(::isPassable)
        }
        step1.addAll(step3)
        return Triple(step2, step1, step4)
    }

    var lastState = Triple<MutablePointSet, MutablePointSet, Pointset>(mutableSetOf(), mutableSetOf(), setOf(startPosition))
    val countCache = mutableListOf(lastState.size)
    fun getCount(numSteps: Long): Long {
        while (numSteps >= countCache.size) {
            lastState = fastPropagate(lastState)
            countCache.add(lastState.size)
        }

        return countCache[numSteps.toInt()]
    }

    fun propagateRight(steps123: Triple<Pointset, Pointset, Pointset>): Triple<Pointset, Pointset, Pointset> {
        val (step2, step3, step4) = propagate(steps123)
        val rightMostX = step4.maxOf { (x, y) -> x }
        val rightMostField = floorDiv(rightMostX, width)

        fun Pointset.shift() = if (rightMostField == 2) {
            mapTo(mutableSetOf()) { (x, y) -> x - width to y }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            y in 0..<height && x in 0..<(2 * width)
        }

        return Triple(
            step2.shift().truncate(),
            step3.shift().truncate(),
            step4.shift().truncate(),
        )
    }

    fun propagateLeft(steps123: Triple<Pointset, Pointset, Pointset>): Triple<Pointset, Pointset, Pointset> {
        val (step2, step3, step4) = propagate(steps123)
        val leftMostX = step4.minOf { (x, y) -> x }
        val leftMostField = floorDiv(leftMostX, width)

        fun Pointset.shift() = if (leftMostField == -2) {
            mapTo(mutableSetOf()) { (x, y) -> x + width to y }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            y in 0..<height && x in (-1 * width)..<width
        }

        return Triple(
            step2.shift().truncate(),
            step3.shift().truncate(),
            step4.shift().truncate(),
        )
    }
    fun propagateUp(steps123: Triple<Pointset, Pointset, Pointset>): Triple<Pointset, Pointset, Pointset> {
        val (step2, step3, step4) = propagate(steps123)
        val upMostY = step4.minOf { (x, y) -> y }
        val upMostField = floorDiv(upMostY, height)

        fun Pointset.shift() = if (upMostField == -2) {
            mapTo(mutableSetOf()) { (x, y) -> x to y + height }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            x in 0..<width && y in (-1 * height)..<height
        }

        return Triple(
            step2.shift().truncate(),
            step3.shift().truncate(),
            step4.shift().truncate(),
        )
    }

    fun propagateDown(steps123: Triple<Pointset, Pointset, Pointset>): Triple<Pointset, Pointset, Pointset> {
        val (step2, step3, step4) = propagate(steps123)
        val downMostY = step4.maxOf { (x, y) -> y }
        val downMostField = floorDiv(downMostY, height)

        fun Pointset.shift() = if (downMostField == 2) {
            mapTo(mutableSetOf()) { (x, y) -> x to y - height }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            x in 0..<width && y in 0..<(2 * height)
        }

        return Triple(
            step2.shift().truncate(),
            step3.shift().truncate(),
            step4.shift().truncate(),
        )
    }

    fun propagateCenter(steps123: Triple<Pointset, Pointset, Pointset>): Triple<Pointset, Pointset, Pointset> {
        val (step2, step3, step4) = propagate(steps123)

        val step4InBounds = step4.filterTo(mutableSetOf()) { (x, y) ->
            x in 0..<width && y in 0..<height
        }

        return Triple(step2, step3, step4InBounds)
    }

    fun isPassable(x: Int, y: Int) = lines[(y % height + height) % height][(x % width + width) % width] != '#'
    fun isPassable(pos: Pair<Int, Int>) = isPassable(pos.first, pos.second)
}

fun <T> findCycle(start: T, transform: (T) -> T): CycleData<T> {
    val stateToIteration = mutableMapOf<T, Int>()
    val states = mutableListOf(start)

    while (true) {
        val state = states.last()
        val repeatedIteration = stateToIteration.getOrPut(state) { states.size - 1 }

        if (repeatedIteration == states.size - 1) {
            states.add(transform(state))
        } else {
            return CycleData(
                cycleStart = repeatedIteration,
                cycleLength = states.size - 1 - repeatedIteration,
                states = states,
            )
        }
    }
}

data class CycleData<T>(
    val cycleStart: Int,
    val cycleLength: Int,
    val states: List<T>,
) {
    fun getState(steps: Long): T {
        if (steps < cycleStart + cycleLength) {
            return states[steps.toInt()]
        }

        val phase = ((steps - cycleStart) % cycleLength).toInt()
        return states[cycleStart + phase]
    }
}

fun <T> List<T>.loop(offset: Int = 0, length: Int = size) = CycleData(
    cycleStart = offset,
    cycleLength = length,
    states = PaddedList(offset, this),
)

class PaddedList<T>(
    private val prefixLength: Int,
    private val list: List<T>,
) : List<T> by list {
    override val size: Int
        get() = list.size + prefixLength

    override fun get(index: Int): T {
        require(index >= prefixLength) {
            "Cannot access padding (index $index maps to ${index - prefixLength} in original list)"
        }

        return list[index - prefixLength]
    }

    override fun indexOf(element: T) = prefixLength + list.indexOf(element)

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): Iterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(): ListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): ListIterator<T> {
        require(index >= prefixLength) {
            "Cannot access padding"
        }

        return list.listIterator(index - prefixLength)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        require(fromIndex >= prefixLength) {
            "Cannot access padding"
        }

        return list.subList(fromIndex - prefixLength, toIndex - prefixLength)
    }
}
