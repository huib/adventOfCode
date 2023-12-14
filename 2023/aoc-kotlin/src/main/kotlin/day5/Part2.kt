package day5

import getInput

fun main() {
    val input = getInput("/input_day5")
    println(day5p2(input))
}

fun day5p2(input: String): String {
    val tables = input.split("\n\n").map { it.trim() }

    val seedRanges = tables.first()
        .split(" ")
        .drop(1)
        .map(String::trim)
        .map(String::toLong)
        .zipWithNext()
        .filterIndexed { index, _ -> index % 2 == 0 }
        .map { it.first until (it.first + it.second) }
    val mappings = tables.drop(1).map(Mapping::fromString)

    val returnValue = seedRanges
        .map { seedRange ->
            mappings.fold(listOf(seedRange)) { ranges, map ->
                ranges.flatMap { range ->
                    map.map(range)
                }
            }
        }
        .flatten()
        .minOf { it.first }

    return returnValue.toString()
}

fun Mapping.map(range: LongRange): List<LongRange> {
    val fixed = range except ranges.map { it.second until (it.second + it.third) }
    return (
        ranges.mapNotNull { mapDef ->
            val intersect = range intersect (mapDef.second until (mapDef.second + mapDef.third))
            if (intersect.isEmpty()) {
                null
            } else {
                val offset = mapDef.first - mapDef.second
                (intersect.first + offset)..(intersect.last + offset)
            }
        } + fixed
        )
        .merge()
}

infix fun LongRange.intersect(other: LongRange) = maxOf(first, other.first)..minOf(last, other.last)

infix fun LongRange.except(others: Iterable<LongRange>) = others.fold(listOf(this)) { result, other ->
    result.flatMap { range -> range.except(other) }
}

fun LongRange.except(other: LongRange) = listOf(
    first until minOf(last + 1, other.first),
    maxOf(first, other.last + 1)..last,
).filter { !it.isEmpty() }

fun List<LongRange>.merge(): List<LongRange> {
    return this
}
