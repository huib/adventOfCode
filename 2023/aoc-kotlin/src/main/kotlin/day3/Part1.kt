package day3

import getInput

fun main() {
    val input = getInput("/input_day3")
    println(day3p1(input))
}

fun day3p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val numberMap = mutableMapOf<Pair<Int, Int>, PartNumber>()
    val symbolMap = mutableMapOf<Pair<Int, Int>, Char>()
    lines.forEachIndexed { row, line ->
        var currentPart = PartNumber(0)
        line.forEachIndexed { col, char ->
            if (char.isDigit()) {
                currentPart.append(char.digitToInt())
                numberMap[row to col] = currentPart
            } else {
                currentPart = PartNumber(0)

                if (char != '.') {
                    symbolMap[row to col] = char
                }
            }
        }
    }

    val returnValue = symbolMap
        .entries
        .map { (pos, symbol) ->
            nbrs(pos).mapNotNullTo(mutableSetOf(), numberMap::get)
        }
        .sumOf { it.sumOf(PartNumber::value) }

    return returnValue.toString()
}

fun nbrs(pos: Pair<Int, Int>): Iterable<Pair<Int, Int>> {
    return sequence {
        (pos.first-1..pos.first+1).forEach { row ->
            (pos.second-1..pos.second+1).forEach { col ->
                yield(row to col)
            }
        }
    }.asIterable()
}

class PartNumber(private var initialValue: Long = 0) {
    val value: Long
        get() = initialValue

    fun append(digit: Int) {
        initialValue *= 10
        initialValue += digit
    }
}
