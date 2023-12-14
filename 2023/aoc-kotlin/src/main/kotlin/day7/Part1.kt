package day7

import getInput

fun main() {
    val input = getInput("/input_day7")
    println(day7p1(input))
}

fun day7p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val cards = lines.map(::Hand).sortedWith { hand1, hand2 ->
        (hand1.grouped + hand1.cardValues)
            .zip(hand2.grouped + hand2.cardValues)
            .firstOrNull { (a, b) -> a != b }
            ?.let { (a, b) -> compareValues(a, b) }
            ?: 0
    }
    val returnValue = cards.mapIndexed { index, hand -> hand.score * (index + 1) }.sum()

    return returnValue.toString()
}

class Hand(str: String) {
    val score = str.split(" ").last().toInt()
    val cardValues = str.split(" ").first().map { value[it]!! }
    val grouped = cardValues.groupBy { it }.map { it.value.size }.sorted().reversed()

    override fun toString(): String {
        return "Card: $cardValues, $grouped, $score"
    }

    companion object {
        val value = (('2'..'9') + 'T' + 'J' + 'Q' + 'K' + 'A').mapIndexed { index: Int, c: Char -> c to index }.toMap()
    }
}
