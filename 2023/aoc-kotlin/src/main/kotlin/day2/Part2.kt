package day2

import getInput

fun main() {
    val input = getInput("/input_day2")
    println(day2p2(input))
}

fun day2p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val games = lines.map(Game::fromString)

    return games
        .sumOf { it.power }.toString()
}
