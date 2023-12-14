package day3

import getInput

fun main() {
    val input = getInput("/input_day3")
    println(day3p2(input))
}

fun day3p2(input: String): String {
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
        .filter { (_, symbol) -> symbol == '*' }
        .map { (pos, _) ->
            nbrs(pos).mapNotNullTo(mutableSetOf(), numberMap::get)
        }
        .filter { it.size == 2}
        .sumOf {
            it.fold(1L) { acc, part ->
                acc * part.value
            }
        }

    return returnValue.toString()
}
