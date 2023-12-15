package day15

import getInput

fun main() {
    val input = getInput("/input_day15")
    println(day15p2(input))
}

fun day15p2(input: String): String {
    val instructions = input.split("\n").map { it.trim() }.single().split(",")

    val boxes = List(256) { mutableListOf<String>() }
    val lenses = mutableMapOf<String, Int>()
    instructions.forEach { lensInstruction ->
        val (lensStr) = lensInstruction.split(Regex("\\W"))
        val instruction = lensInstruction.substring(lensStr.length)
        val lensHash = hash(lensStr)

        if (instruction == "-") {
            boxes[lensHash].remove(lensStr)
        } else {
            check(instruction.startsWith("="))
            val focalLength = instruction.substring(1).toInt()
            if (lensStr !in boxes[lensHash]) {
                boxes[lensHash].add(lensStr)
            }

            lenses[lensStr] = focalLength
        }
    }
    val returnValue = boxes.mapIndexed { index, boxedLenses ->
        (index + 1) * boxedLenses.mapIndexed { lensSlot, lensStr -> (lensSlot + 1) * lenses[lensStr]!! }.sum()
    }.sum()

    return returnValue.toString()
}

