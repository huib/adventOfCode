package day6

import getInput
import kotlin.math.sqrt

fun main() {
    val input = getInput("/input_day6")
    println(day6p1(input))
}

// distance = (time - x)*x = -x^2 + time*x
// distance >= record, for how many integer t?
// max distance = time^2 / 4 at x=time/2
// -x^2 + time*x - record > 0
// -x^2 + time*x - (record + 1) >= 0
//  --> sqrt(time^2 - 4*(-1)*(-(record + 1))) = sqrt(time^2 - 4*(record + 1))
//  rounding!
//  if time is odd:
//     width is even:
//         [ , 1) -> 0
//         [1, 3) -> 2
//         ....
//  else
//     width is odd:
//         [0, 2) -> 1
//         [2, 4) -> 3
//         ....

fun day6p1(input: String): String {
    val (times, records) = input.split("\n").map { it.trim() }.map { it.split(Regex("\\s+")).drop(1).map(String::toLong) }

    val returnValue = times.zip(records).map { (time, record) ->
        sqrt(time * time - 4.0 * (record + 1)).toLong().let { it + (time + it + 1) % 2 }
    }.reduce(Long::times)

    return returnValue.toString()
}
