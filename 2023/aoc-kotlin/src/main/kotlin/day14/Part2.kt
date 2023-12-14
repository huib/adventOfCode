package day14

import getInput

fun main() {
    val input = getInput("/input_day14")
    println(day14p2(input))
}

val iterations = 1000000000

fun day14p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    var grid = lines
    val visited = mutableMapOf<List<String>, Int>()
    repeat(iterations) { iteration ->
        if (visited.containsKey(grid)) {
            val byIteration = visited.entries.associate { (a, b) -> b to a }
            val cycleToIteration = visited[grid]!!
            val cycleLength = (iteration - cycleToIteration)
            val offset = (iterations - cycleToIteration) % cycleLength
            val finalGrid = byIteration[cycleToIteration + offset]!!
            val finalLoad = calculateLoad(rotateClockwise(finalGrid))
            return finalLoad.toString()
        }

        visited[grid] = iteration

        repeat(4) { grid = slideRight(rotateClockwise(grid)) }
    }

    return calculateLoad(rotateClockwise(grid)).toString()
}
fun rotateClockwise(lines: List<String>): List<String> {
    return List(lines.first().length) { col ->
        lines.reversed().map { it[col] }.joinToString("")
    }
}

fun slideRight(lines: List<String>): List<String> {
    val lineLength = lines.first().length
    return lines
        .map { line ->
            buildMap {
                var count = 0
                line.forEachIndexed { index, char ->
                    when (char) {
                        '.' -> Unit
                        '#' -> {
                            put(index, count)
                            count = 0
                        }

                        'O' -> count++
                    }
                }
                put(line.length, count)
            }
        }
        .map { map ->
            val chars = CharArray(lineLength) { '.' }
            map.forEach { (position, count) ->
                (position - count until position).forEach { chars[it] = 'O' }
                if (position < lineLength) {
                    chars[position] = '#'
                }
            }
            chars.joinToString("")
        }
}

fun calculateLoad(lines: List<String>): Long {
    return lines.sumOf { line ->
        line
            .mapIndexed { pos, char ->
                if (char == 'O')
                    pos + 1L
                else
                    0L
            }
            .sum()
    }
}
