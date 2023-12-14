package day13

import getInput

fun main() {
    val input = getInput("/input_day13")
    println(day13p2(input))
}

fun day13p2(input: String): String {
    val inputs = input.split("\n\n").map { it.split("\n").map(String::trim) }

    val returnValue = inputs.sumOf { lines ->
        val horizontalPosition = findHorizontalSmudgedMirrorPosition(lines)
        val verticalPosition = findHorizontalSmudgedMirrorPosition(transpose(lines))

        check(horizontalPosition * verticalPosition == 0) { "both hor and ver possible" }
        check(horizontalPosition + verticalPosition > 0) { "neither hor and ver possible for\n${lines.joinToString("\n")}" }
        100 * horizontalPosition + verticalPosition
    }

    return returnValue.toString()
}

fun findHorizontalSmudgedMirrorPosition(lines: List<String>): Int {
    val candidates = 1 until lines.size

    val validPositions = candidates.filter { candidateMirrorPosition ->
        singleSmudge(lines, candidateMirrorPosition)
    }

    if (validPositions.isEmpty()) {
        return 0
    }

    return validPositions.single()
}

fun singleSmudge(lines: List<String>, mirrorPosition: Int): Boolean {
    val max = minOf(mirrorPosition - 1, lines.size - mirrorPosition - 1)
    return (0..max).sumOf {
        lines[mirrorPosition + it].zip(lines[mirrorPosition - 1 - it]).count { (a, b) ->
            a != b
        }
    } == 1
}
