package day5

import getInput

fun main() {
    val input = getInput("/input_day5")
    println(day5p1(input))
}

fun day5p1(input: String): String {
    val tables = input.split("\n\n").map { it.trim() }

    val seeds = tables.first().split(" ").drop(1).map(String::trim).map(String::toLong)
    val mappings = tables.drop(1).map(Mapping::fromString)

    val returnValue = seeds.map { seed ->
        mappings.fold(seed) { acc, map ->
            map.map(acc)
        }
    }.min()

    return returnValue.toString()
}

class Mapping(val ranges: List<Triple<Long, Long, Long>>) {

    fun map(value: Long): Long {
        ranges.forEach {
            if (value in it.second..(it.second+it.third)) {
                return value - it.second + it.first
            }
        }
        return value
    }

    companion object {
        fun fromString(string: String) = Mapping(
            string
                .split("\n")
                .drop(1)
                .map { it.split(" ").map(String::toLong) }
                .map { Triple(it[0], it[1], it[2]) }
        )
    }
}
