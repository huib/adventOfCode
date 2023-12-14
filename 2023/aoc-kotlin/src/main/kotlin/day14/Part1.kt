package day14

import day13.transpose
import getInput
import kotlin.time.measureTime

fun main() {
    val input = getInput("/input_day14")
    measureTime {
        repeat(5000) {
            day14p1(input)
        }
    }.also(::println)
    println(day14p1(input))
}

fun day14p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val returnValue = transpose(lines).sumOf { line ->
        val posCount = buildMap {
            var count = 0
            line.reversed().forEachIndexed { index, char ->
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

        posCount.entries.sumOf { (pos, count) ->
            (0 until count).sumOf { pos - it }
        }
    }

    return returnValue.toString()
}
