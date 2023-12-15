package day4

import getInput

fun main() {
    val input = getInput("/input_day4")
    println(day4p2(input))
}

fun day4p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val cards = lines.map { it.split(":")[1].trim() }.map(::strToCard)
    val pile = Pile(cards)

    val returnValue = cards.indices.sumOf { index ->
        pile.numCards(index)
    }

    return returnValue.toString()
}

class Pile(
    val cardList: List<Card>,
) {
    private val cache = mutableMapOf<Int, Long>()
    fun numCards(index: Int): Long {
        return cache.getOrPut(index) {
            val card = cardList[index]
            1 + (index + 1..index + card.score).sumOf { copyIndex ->
                numCards(copyIndex)
            }
        }
    }
}

data class Card(
    val winning: Set<Long>,
    val actual: Set<Long>,
) {
    val score = (winning intersect actual).size
}

fun strToCard(string: String): Card {
    val (winning, actual) = string
        .split("|")
        .map(String::trim)
        .map(::parseNumbers)
        .map(List<Long>::toSet)

    return Card(winning, actual)
}
