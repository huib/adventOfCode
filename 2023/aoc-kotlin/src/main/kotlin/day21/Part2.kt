package day21

import day10.nbrs_not_diag
import getInput

fun main() {
    val input = getInput("/input_day21")
    println(day21p2(input))
}

fun day21p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val field = InfiniteField(lines)

    val numSteps = 26501365L // 26501365L

    val centerCycle = findCycle(field.startState, field::propagateCenter)
    centerCycle.printInfo(numSteps, "center")

//    val NECycle = findCycle(mapOf((0 to field.height - 1) to true), field::propagateCenter)
//    println(NECycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
//    val NWCycle = findCycle(mapOf((field.width - 1 to field.height - 1) to true), field::propagateCenter)
//    println(NWCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
//    val SWCycle = findCycle(mapOf((field.width - 1 to 0) to true), field::propagateCenter)
//    println(SWCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
//    val SECycle = findCycle(mapOf((0 to 0) to true), field::propagateCenter)
//    println(SECycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
//
    val westCycle = findCycle(field.startState, field::propagateLeft)
    westCycle.printInfo(numSteps, "west")
    val northCycle = findCycle(field.startState, field::propagateUp)
    northCycle.printInfo(numSteps, "north")
    val eastCycle = findCycle(field.startState, field::propagateRight)
    eastCycle.printInfo(numSteps, "east")
    val southCycle = findCycle(field.startState, field::propagateDown)
    southCycle.printInfo(numSteps, "south")

    val returnValue = 0

    return returnValue.toString()
}

fun CycleData<Triple<Pointset, Pointset, Pointset>>.printInfo(numSteps: Long, label: String) {
    println("cycle $label: $cycleStart + $cycleLength * k")
    println("   after $numSteps: ${getState(numSteps).let { it.first + it.third }.size}")
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
        val rightMostField = rightMostX / width

        fun Pointset.shift() = if (rightMostField == 2) {
            mapTo(mutableSetOf()) { (x, y) -> x - width to y }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            y in 0..height && x in 0..(2 * width)
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
        val leftMostField = (leftMostX - width) / width

        fun Pointset.shift() = if (leftMostField == -2) {
            mapTo(mutableSetOf()) { (x, y) -> x + width to y }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            y in 0..height && x in (-1 * width)..width
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
        val upMostField = (upMostY - height) / height

        fun Pointset.shift() = if (upMostField == -2) {
            mapTo(mutableSetOf()) { (x, y) -> x to y + height }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            x in 0..width && y in (-1 * height)..height
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
        val downMostField = downMostY / height

        fun Pointset.shift() = if (downMostField == 2) {
            mapTo(mutableSetOf()) { (x, y) -> x to y - height }
        } else {
            this
        }

        fun Pointset.truncate() = filterTo(mutableSetOf()) { (x, y) ->
            x in 0..width && y in 0..(2 * height)
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
            x in 0..width && y in 0..height
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
