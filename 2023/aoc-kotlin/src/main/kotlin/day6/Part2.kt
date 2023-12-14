package day6

import getInput
import kotlin.math.sqrt

fun main() {
    val input = getInput("/input_day6")
    println(day6p2(input))
}

fun day6p2(input: String): String {
    val (time, record) = input.split("\n").map { it.trim() }.map { it.replace(Regex("\\D"), "").toLong() }

    val widthFrac = sqrt(time * time - 4.0 * (record + 1))
    val rounded = widthFrac.toLong()
    val returnValue = sqrt(time * time - 4.0 * (record + 1)).toLong().let { it + (time + it + 1) % 2 }

    return returnValue.toString()
}
