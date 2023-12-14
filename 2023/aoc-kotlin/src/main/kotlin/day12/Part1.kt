package day12

import getInput

fun main() {
    val input = getInput("/input_day12")
    println(day12p1(input))
}

fun day12p1(input: String): String {
    check(Puzzle.IMPOSSIBLE.isInfeasible)

    val lines = input.split("\n").map { it.trim() }

    val puzzles = lines.map(Puzzle::fromString)
    val nums = puzzles.map {
        val n = numberOfSolutions(it)
        println("$it --> $n")
        n
    }
    val returnValue = nums.sum()

    return returnValue.toString()
}

class Puzzle(
    str: String,
    val clues: List<Int>,
) {
    val cells = str.trim('.')
    val isInfeasible = cells.length < clues.size + clues.sum() - 1 || cells.count { it == '#' } > clues.sum()
    val isSolved = (!cells.contains('?') && cells.split(Regex("\\.+")).map(String::length) == clues) ||
        (!cells.contains('#') && clues.isEmpty()) ||
        (clues.size == 1 && cells.length == clues.first() && !cells.contains('.'))

    fun reversed() = Puzzle(cells.reversed(), clues.reversed())

    override fun toString() = "$cells $clues"

    companion object {
        fun fromString(str: String): Puzzle {
            val (cells, clues) = str.split(" ")
            return Puzzle(cells, clues.split(",").map { it.toInt() })
        }

        val IMPOSSIBLE = Puzzle("#", emptyList())
    }
}

fun numberOfSolutions(puzzle: Puzzle): Long {
    val simplified = greedySolve(puzzle)

    if (simplified.isInfeasible) {
        return 0L
    }

    if (simplified.isSolved) {
        return 1L
    }

    check(simplified.cells[0] == '?') { "simplified starts with ${simplified.cells[0]}" }

    return numberOfSolutions(Puzzle(simplified.cells.drop(1), simplified.clues)) +
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
