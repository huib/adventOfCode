package day2

import getInput

fun main() {
    val input = getInput("/input_day2")
    println(day2p1(input))
}

fun day2p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val games = lines.map(Game::fromString)

    return games
        .filter { game ->
            game.lb.red <= 12 && game.lb.green <= 13 && game.lb.blue <= 14
        }
        .sumOf { it.id }.toString()
}

data class Observation(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    companion object {
        fun fromString(string: String): Observation {
            val colorCounts = string.split(",").map { it.trim() }

            var r = 0
            var g = 0
            var b = 0

            colorCounts.map {
                val (countStr, color) = it.split(" ")
                val count = countStr.toInt()

                when (color) {
                    "red" -> r += count
                    "green" -> g += count
                    "blue" -> b += count
                }
            }

            return Observation(red = r, green = g, blue = b)
        }
    }
}

data class Game(
    val id: Int,
    val observations: List<Observation>,
) {
    val lb = Observation(
        red = observations.maxOf { it.red },
        green = observations.maxOf { it.green },
        blue = observations.maxOf { it.blue },
    )

    val power = lb.red * lb.green * lb.blue

    companion object {
        fun fromString(string: String): Game {
            val (gameStr, obsStr) = string.split(":").map { it.trim() }
            val gameId = gameStr.substring(5).toInt()
            val observations = obsStr.split(";").map { it.trim() }.map(Observation::fromString)
            return Game(gameId, observations)
        }
    }
}
