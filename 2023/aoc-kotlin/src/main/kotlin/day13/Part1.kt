package day13

import getInput

fun main() {
    val input = getInput("/input_day13")
    println(day13p1(input))
}

fun day13p1(input: String): String {
    val inputs = input.split("\n\n").map { it.split("\n").map(String::trim) }

    val returnValue = inputs.sumOf { lines ->
        val horizontalPosition = findHorizontalMirrorPosition(lines)
        val verticalPosition = findHorizontalMirrorPosition(transpose(lines))

        check(horizontalPosition * verticalPosition == 0) { "both hor and ver possible" }
        100 * horizontalPosition + verticalPosition
    }

    return returnValue.toString()
}

fun findHorizontalMirrorPosition(lines: List<String>): Int {
    val candidates = lines.zipWithNext().mapIndexedNotNull { index, (line1, line2) ->
        if (line1 == line2) index + 1 else null
    }

    val validPositions = candidates.filter { candidateMirrorPosition ->
        val max = minOf(candidateMirrorPosition - 1, lines.size - candidateMirrorPosition - 1)
        (1..max).all {
            lines[candidateMirrorPosition + it] == lines[candidateMirrorPosition - 1 - it]
        }
    }

    if (validPositions.isEmpty()) {
        return 0
    }

    return validPositions.single()
}

fun transpose(lines: List<String>): List<String> {
    return List(lines.first().length) { col ->
        lines.map { it[col] }.toString()
    }
}
