package day1

import getInput

fun main() {
    val input = getInput("/input_day1")
    println(day1p2(input))
}

val nums = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
) + (0..9).associateBy { it.toString() }

fun String.parse() =
    10 * nums.getValue(findAnyOf(nums.keys)!!.second) + nums.getValue(findLastAnyOf(nums.keys)!!.second)

fun day1p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val cal_values = lines.map { it.parse() }

    return cal_values.sum().toString()
}
