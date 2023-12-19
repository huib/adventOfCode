package day25

import day1.day1p1
import day1.day1p2
import day10.day10p1
import day10.day10p2
import day11.day11p1
import day11.day11p2
import day12.day12p1
import day12.day12p2
import day13.day13p1
import day13.day13p2
import day14.day14p1
import day14.day14p2
import day15.day15p1
import day15.day15p2
import day16.day16p1
import day16.day16p2
import day17.day17p1
import day17.day17p2
import day18.day18p1
import day18.day18p2
import day19.day19p1
import day19.day19p2
import day2.day2p1
import day2.day2p2
import day20.day20p1
import day20.day20p2
import day21.day21p1
import day21.day21p2
import day22.day22p1
import day22.day22p2
import day23.day23p1
import day23.day23p2
import day24.day24p1
import day24.day24p2
import day3.day3p1
import day3.day3p2
import day4.day4p1
import day4.day4p2
import day5.day5p1
import day5.day5p2
import day6.day6p1
import day6.day6p2
import day7.day7p1
import day7.day7p2
import day8.day8p1
import day8.day8p2
import day9.day9p1
import day9.day9p2
import getInput
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.measureTime

fun main() {
    val input = getInput("/input_day25")
    println(day25p2(input))
}

fun day25p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val runs = listOf(
        ::day1p1 to getInput("/input_day1"),
        ::day1p2 to getInput("/input_day1"),
        ::day2p1 to getInput("/input_day2"),
        ::day2p2 to getInput("/input_day2"),
        ::day3p1 to getInput("/input_day3"),
        ::day3p2 to getInput("/input_day3"),
        ::day4p1 to getInput("/input_day4"),
        ::day4p2 to getInput("/input_day4"),
        ::day5p1 to getInput("/input_day5"),
        ::day5p2 to getInput("/input_day5"),
        ::day6p1 to getInput("/input_day6"),
        ::day6p2 to getInput("/input_day6"),
        ::day7p1 to getInput("/input_day7"),
        ::day7p2 to getInput("/input_day7"),
        ::day8p1 to getInput("/input_day8"),
        ::day8p2 to getInput("/input_day8"),
        ::day9p1 to getInput("/input_day9"),
        ::day9p2 to getInput("/input_day9"),
        ::day10p1 to getInput("/input_day10"),
        ::day10p2 to getInput("/input_day10"),
        ::day11p1 to getInput("/input_day11"),
        ::day11p2 to getInput("/input_day11"),
        ::day12p1 to getInput("/input_day12"),
        ::day12p2 to getInput("/input_day12"),
        ::day13p1 to getInput("/input_day13"),
        ::day13p2 to getInput("/input_day13"),
        ::day14p1 to getInput("/input_day14"),
        ::day14p2 to getInput("/input_day14"),
        ::day15p1 to getInput("/input_day15"),
        ::day15p2 to getInput("/input_day15"),
        ::day16p1 to getInput("/input_day16"),
        ::day16p2 to getInput("/input_day16"),
        ::day17p1 to getInput("/input_day17"),
        ::day17p2 to getInput("/input_day17"),
        ::day18p1 to getInput("/input_day18"),
        ::day18p2 to getInput("/input_day18"),
        ::day19p1 to getInput("/input_day19"),
        ::day19p2 to getInput("/input_day19"),
        ::day20p1 to getInput("/input_day20"),
        ::day20p2 to getInput("/input_day20"),
        ::day21p1 to getInput("/input_day21"),
        ::day21p2 to getInput("/input_day21"),
        ::day22p1 to getInput("/input_day22"),
        ::day22p2 to getInput("/input_day22"),
        ::day23p1 to getInput("/input_day23"),
        ::day23p2 to getInput("/input_day23"),
        ::day24p1 to getInput("/input_day24"),
        ::day24p2 to getInput("/input_day24"),
        ::day25p1 to getInput("/input_day25"),
    )
    val durations = runs.mapIndexed { index, pair ->
        measureTime { pair.first(pair.second) }
    }
    val total = durations.reduce(Duration::plus)
    val proportions = durations.map { it / total }
    val returnValue = durations.zip(proportions).mapIndexed { index, (duration, proportion) ->
        "day ${index / 2 + 1} part ${index % 2 + 1}: $duration (${(proportion * 10_000).roundToInt() / 100.0}%)"
    }.joinToString("\n") + "\ntotal: $total"

    return returnValue.toString()
}
