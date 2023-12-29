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

    val numSteps = 6L

    val centerCycle = findCycle(mapOf(field.startPosition to true), field::propagateCenter)
    println(centerCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })

    val NECycle = findCycle(mapOf((0 to field.height - 1) to true), field::propagateCenter)
    println(NECycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
    val NWCycle = findCycle(mapOf((field.width - 1 to field.height - 1) to true), field::propagateCenter)
    println(NWCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
    val SWCycle = findCycle(mapOf((field.width - 1 to 0) to true), field::propagateCenter)
    println(SWCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
    val SECycle = findCycle(mapOf((0 to 0) to true), field::propagateCenter)
    println(SECycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })

    val westCycle = findCycle(mapOf(field.startPosition to true), field::propagateLeft)
    println(westCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
    val northCycle = findCycle(mapOf(field.startPosition to true), field::propagateUp)
    println(northCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
    val eastCycle = findCycle(mapOf(field.startPosition to true), field::propagateRight)
    println(eastCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })
    val southCycle = findCycle(mapOf(field.startPosition to true), field::propagateDown)
    println(southCycle.getState(numSteps).count { (pos, isPreceding) -> !isPreceding })

    val returnValue = 0

    return returnValue.toString()
}

class InfiniteField(val lines: List<String>) {
    val width = lines.first().length
    val height = lines.size
    val startY = lines.indexOfFirst { it.contains('S') }
    val startX = lines[startY].indexOf('S')
    val startPosition = startX to startY

    val cache = mutableMapOf<Map<Pair<Int, Int>, Boolean>, Map<Pair<Int, Int>, Boolean>>()
    fun propagate(previousPositions: Map<Pair<Int, Int>, Boolean>) = cache.getOrPut(previousPositions) {
        return previousPositions.flatMap { (pos, isPrecedingStep) ->
            if (isPrecedingStep) {
                nbrs_not_diag(pos)
                    .filter(::isPassable)
                    .filterNot(previousPositions::containsKey)
                    .map { it to true } + (pos to false)
            } else {
                listOf(pos to true)
            }
        }.toMap()
    }

    fun propagateRight(previousPositions: Map<Pair<Int, Int>, Boolean>): Map<Pair<Int, Int>, Boolean> {
        val newPositions = propagate(previousPositions)
        val rightMostX = newPositions.filterValues { it }.keys.maxOf { (x, y) -> x }
        val rightMostField = rightMostX / width

        val shiftedPositions = if (rightMostField == 2) {
            newPositions.mapKeys { (pos, _) ->
                val (x, y) = pos
                x - width to y
            }
        } else {
            newPositions
        }

        return shiftedPositions.filterKeys { (x, y) ->
            y in 0..height && x in 0..(2 * width)
        }
    }

    fun propagateLeft(previousPositions: Map<Pair<Int, Int>, Boolean>): Map<Pair<Int, Int>, Boolean> {
        val newPositions = propagate(previousPositions)
        val leftMostX = newPositions.filterValues { it }.keys.minOf { (x, y) -> x }
        val leftMostField = (leftMostX - width) / width

        val shiftedPositions = if (leftMostField == -2) {
            newPositions.mapKeys { (pos, _) ->
                val (x, y) = pos
                x + width to y
            }
        } else {
            newPositions
        }

        return shiftedPositions.filterKeys { (x, y) ->
            y in 0..height && x in (-1 * width)..width
        }
    }

    fun propagateUp(previousPositions: Map<Pair<Int, Int>, Boolean>): Map<Pair<Int, Int>, Boolean> {
        val newPositions = propagate(previousPositions)
        val upMostY = newPositions.filterValues { it }.keys.minOf { (x, y) -> y }
        val upMostField = (upMostY - height) / height

        val shiftedPositions = if (upMostField == -2) {
            newPositions.mapKeys { (pos, _) ->
                val (x, y) = pos
                x to y + height
            }
        } else {
            newPositions
        }

        return shiftedPositions.filterKeys { (x, y) ->
            x in 0..width && y in (-1 * height)..height
        }
    }

    fun propagateDown(previousPositions: Map<Pair<Int, Int>, Boolean>): Map<Pair<Int, Int>, Boolean> {
        val newPositions = propagate(previousPositions)
        val downMostY = newPositions.filterValues { it }.keys.maxOf { (x, y) -> y }
        val downMostField = downMostY / height

        val shiftedPositions = if (downMostField == 2) {
            newPositions.mapKeys { (pos, _) ->
                val (x, y) = pos
                x to y - height
            }
        } else {
            newPositions
        }

        return shiftedPositions.filterKeys { (x, y) ->
            x in 0..width && y in 0..(2 * height)
        }
    }

    fun propagateCenter(previousPositions: Map<Pair<Int, Int>, Boolean>): Map<Pair<Int, Int>, Boolean> {
        val newPositions = propagate(previousPositions)

        return newPositions.filterKeys { (x, y) ->
            x in 0..width && y in 0..height
        }
    }

    fun isPassable(x: Int, y: Int) = lines[(y % height + height) % height][(x % width + width) % width] != '#'
    fun isPassable(pos: Pair<Int, Int>) = isPassable(pos.first, pos.second)
}

fun <T> findCycle(start: T, transform: (T) -> T): CycleData<T> {
    val stateToIteration = mutableMapOf<T, Int>()
    val iterationToResult = mutableMapOf<Int, T>()

    var state = start
    var iteration = 0
    while (true) {
        val repeatedIteration = stateToIteration.getOrPut(state) { iteration }
        state = iterationToResult.getOrPut(repeatedIteration) { transform(state) }

        if (repeatedIteration != iteration) {
            return CycleData(
                cycleStart = repeatedIteration,
                cycleLength = iteration - repeatedIteration,
                states = (0..<iteration).map(iterationToResult::getValue),
            )
        }

        iteration++
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
