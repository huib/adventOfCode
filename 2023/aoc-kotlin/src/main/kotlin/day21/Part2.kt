package day21

import day10.nbrs_not_diag
import getInput
import java.lang.Math.floorDiv

fun main() {
    val input = getInput("/input_day21")
    println(day21p2(input))
}

val width = 131
val height = 131

fun day21p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val field = InfiniteField(lines)

    val numSteps = 500L // 26501365L

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

//    (262L..655L).forEach { numSteps ->
//        val counts = buildList {
//            repeat(((numSteps - 197) / 131).toInt()) {
//                add(centerCycle.getState(numSteps + it % 2).size)
//            }
//            add(eastCycle.getState(numSteps).size)
//        }
//
//        println("$numSteps :  ${counts.sum()}    $counts")
//    }
//    (262L..655L).forEach { numSteps ->
//        val counts = buildList {
//            repeat(((numSteps - 197) / 131).toInt()) {
//                add(centerCycle.getState(numSteps + it % 2).size)
//            }
//            add(westCycle.getState(numSteps).size)
//        }
//
//        println("$numSteps :  ${counts.sum()}    $counts")
//    }
//    (262L..655L).forEach { numSteps ->
//        val counts = buildList {
//            repeat(((numSteps - 197) / 131).toInt()) {
//                add(centerCycle.getState(numSteps + it % 2).size)
//            }
//            add(northCycle.getState(numSteps).size)
//        }
//
//        println("$numSteps :  ${counts.sum()}    $counts")
//    }
//    (197L..655L).forEach { numSteps ->
//        val numCenterChunks = (numSteps - 197) / 131
//        val total = ((numCenterChunks + 1) / 2) * centerCycle.getState(numSteps).size +
//                (numCenterChunks / 2) * centerCycle.getState(numSteps + 1).size +
//                southCycle.getState(numSteps).size
//
//        println("$numSteps :  $total")
//    }
//    (197L..655L).forEach { numSteps ->
//        val total = centerCycle.getState(numSteps).size * ((numSteps - NECycle.cycleStart) / NECycle.cycleLength) +
//                NECycle.getState(numSteps).size
//        println("$numSteps :  $total")
//    }

    (197L..655L).forEach { numSteps ->
        val r = (numSteps - NECycle.cycleStart) / (NECycle.cycleLength / 2)
        val r1 = r / 2
        val r2 = (r + 1) / 2
        val total = centerCycle.getState(numSteps).size * r1 * r1 +
                centerCycle.getState(numSteps + 1).size * (r2 - 1) * r2 +
                r * NECycle.getState(numSteps).size +
                (r - 1).coerceAtLeast(0) * NECycle.getState(numSteps + NECycle.cycleLength / 2).size
        println("$numSteps :  $total")
    }

    val returnValue = 0

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

        return counter.toString()
    }

val Triple<Pointset, Pointset, Pointset>.size: Long
    get() {
        check((first intersect third).isEmpty())

        return first.size.toLong() + third.size
    }

val InfiniteField.startState
    get() = Triple<Pointset, Pointset, Pointset>(setOf(), setOf(), setOf(startPosition))

typealias Pointset = Set<Pair<Int, Int>>
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
