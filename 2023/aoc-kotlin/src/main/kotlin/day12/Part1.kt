package day12

import getInput
import java.util.*

fun main() {
    val input = getInput("/input_day12")
    println(day12p1(input))
}

fun day12p1(input: String): String {
    cache.clear() // fair runtime calculation for repeated trails
    check(Puzzle.IMPOSSIBLE.isInfeasible)

    val lines = input.split("\n").map { it.trim() }

    val puzzles = lines.map(Puzzle::fromString)
    val nums = puzzles.map {
        val n = numberOfSolutions(it)
        val n2 = numberOfSolutions2(it)
        if (n != n2)
            println("$it --> $n / $n2")
        n to n2
    }
    val returnValue = nums.reduce { acc, pair ->
        acc.first + pair.first to acc.second + pair.second
    }

    return returnValue.toString()
}

class Puzzle(
    val str: String,
    val clues: List<Int>,
) {
    val cells = str.trim('.')
    val isInfeasible = cells.length < clues.size + clues.sum() - 1 ||
            cells.count { it == '#' } > clues.sum()
    val isSolved = (!cells.contains('?') && cells.split(Regex("\\.+")).map(String::length) == clues) ||
            (!cells.contains('#') && clues.isEmpty()) ||
            (clues.size == 1 && cells.length == clues.first() && !cells.contains('.'))

    fun reversed() = Puzzle(cells.reversed(), clues.reversed())

    override fun toString() = "$str ($cells) $clues"
    override fun hashCode() = Objects.hash(cells, clues)
    override fun equals(other: Any?): Boolean {
        return other is Puzzle && other.cells == cells && other.clues == clues
    }

    companion object {
        fun fromString(str: String): Puzzle {
            val (cells, clues) = str.split(" ")
            return Puzzle(cells, clues.split(",").map { it.toInt() })
        }

        val IMPOSSIBLE = Puzzle("#", emptyList())
    }
}

fun numberOfSolutions2(puzzle: Puzzle): Long {
    val puzzle = presolve(puzzle)

    if (puzzle.isInfeasible) {
        return 0L
    }

    if (puzzle.isSolved) {
        return 1L
    }

    check(puzzle.cells[0] == '?') { "simplified starts with ${puzzle.cells[0]}" }

    return numberOfSolutions2(Puzzle(puzzle.cells.drop(1), puzzle.clues)) +
            numberOfSolutions2(Puzzle("#" + puzzle.cells.drop(1), puzzle.clues))
}

fun presolve(puzzle: Puzzle): Puzzle {
    val simplified = presolveStart(puzzle)
    return presolveStart(simplified.reversed()).reversed()
}

fun presolveStart(puzzle: Puzzle): Puzzle {
    if (puzzle.isInfeasible || puzzle.isSolved) {
        return puzzle
    }

    val indexOfFirstDot = puzzle.cells.indexOf('.')
    if (indexOfFirstDot in 0 until puzzle.clues.first()) {
        if (puzzle.cells.indexOf('#') in 0..indexOfFirstDot) {
            return Puzzle.IMPOSSIBLE
        }
        return presolveStart(Puzzle(puzzle.cells.substring(indexOfFirstDot), puzzle.clues))
    }

    if (puzzle.cells[0] == '#') {
        if (puzzle.cells[puzzle.clues.first()] == '#') {
            return Puzzle.IMPOSSIBLE
        }
        return presolveStart(Puzzle(puzzle.cells.substring(puzzle.clues.first() + 1), puzzle.clues.drop(1)))
    }

    return puzzle
}

val cache = mutableMapOf<Puzzle, Long>()
fun numberOfSolutions(puzzle: Puzzle): Long = cache.getOrPut(puzzle) {
    val simplified = greedySolve(puzzle)

    if (simplified.isInfeasible) {
        return 0L
    }

    if (simplified.isSolved) {
        return 1L
    }

    check(simplified.cells[0] == '?') { "simplified starts with ${simplified.cells[0]}" }

    numberOfSolutions(Puzzle(simplified.cells.drop(1), simplified.clues)) +
        numberOfSolutions(Puzzle("#" + simplified.cells.drop(1), simplified.clues))
}

fun greedySolve(puzzle: Puzzle): Puzzle {
    var simplified = puzzle
    simplified = greedyFirstClue(simplified)
    simplified = greedyFirstClue(simplified.reversed())
    return simplified
}

fun greedyFirstClue(puzzle: Puzzle): Puzzle {
    if (puzzle.isInfeasible || puzzle.isSolved) {
        return puzzle
    }

    val indexOfFirstDot = puzzle.cells.indexOf('.')
    if (indexOfFirstDot in 0 until puzzle.clues.first()) {
        if (puzzle.cells.indexOf('#') in 0..indexOfFirstDot) {
            return Puzzle.IMPOSSIBLE
        }
        return greedyFirstClue(Puzzle(puzzle.cells.substring(indexOfFirstDot), puzzle.clues))
    }

    if (puzzle.cells[0] == '#') {
        return if (puzzle.cells[puzzle.clues.first()] == '#') {
            Puzzle.IMPOSSIBLE
        } else {
            greedyFirstClue(Puzzle(puzzle.cells.substring(puzzle.clues.first() + 1), puzzle.clues.drop(1)))
        }
    }

    return puzzle
}
