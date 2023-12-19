package day19

import getInput
import kotlin.math.abs

fun main() {
    val input = getInput("/input_day19")
    println(day19p2(input))
}

fun day19p2(input: String): String {
    val (strWorkflows, _) = input.split("\n\n").map { it.split("\n") }

    val workflows = strWorkflows.map(::WorkFlow).associateBy { it.name }
    val cuboids = mutableListOf(HyperCuboid(1..4000, 1..4000, 1..4000, 1..4000) to "in")
    val acceptedCuboids = mutableListOf<HyperCuboid>()

    while (cuboids.isNotEmpty()) {
        val (cuboid, workflowName) = cuboids.removeFirst()
        val workflow = workflows[workflowName]!!

        val passingCuboids = workflow.next(cuboid).filterNot { it.second == "R" }
        val (accepted, pending) = passingCuboids.partition { it.second == "A" }
        acceptedCuboids.addAll(accepted.map { it.first })
        cuboids.addAll(pending)
    }

    val returnValue = acceptedCuboids.sumOf { it.count() }

    return returnValue.toString()
}

data class HyperCuboid(
    val xRange: IntRange,
    val mRange: IntRange,
    val aRange: IntRange,
    val sRange: IntRange,
) {
    fun split(category: Char, value: Int): Pair<HyperCuboid, HyperCuboid> {
        return when (category) {
            'x' -> copy(xRange = xRange.first..< value) to copy(xRange = value..xRange.last)
            'm' -> copy(mRange = mRange.first..< value) to copy(mRange = value..mRange.last)
            'a' -> copy(aRange = aRange.first..< value) to copy(aRange = value..aRange.last)
            's' -> copy(sRange = sRange.first..< value) to copy(sRange = value..sRange.last)
            else -> error("odd category")
        }
    }

    fun count() = xRange.size * mRange.size * aRange.size * sRange.size

    fun isEmpty() = xRange.isEmpty() || mRange.isEmpty() || aRange.isEmpty() || sRange.isEmpty()
    fun isNotEmpty() = !isEmpty()
}

val IntRange.size get() = if (isEmpty()) 0L else last - first + 1L
