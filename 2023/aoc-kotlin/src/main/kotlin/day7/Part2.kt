package day7

import getInput

fun main() {
    val input = getInput("/input_day7")
    println(day7p2(input))
}

fun day7p2(input: String): String {
    val lines = input.split("\n").map { it.trim() }

    val cards = lines.map(::Hand2).sortedWith { hand1, hand2 ->
        (hand1.grouped + hand1.cardValues)
            .zip(hand2.grouped + hand2.cardValues)
            .firstOrNull { (a, b) -> a != b }
            ?.let { (a, b) -> compareValues(a, b) }
            ?: 0
    }.onEach(::println)
    val returnValue = cards.mapIndexed { index, hand -> hand.score * (index + 1) }.sum()

    return returnValue.toString()
}

class Hand2(val str: String) {
    val score = str.split(" ").last().toInt()
    val cardValues = str.split(" ").first().map { value[it]!! }
    val numJokers = cardValues.count { it == value['J'] }
    val grouped = cardValues.filterNot { it == value['J'] }.groupBy { it }.map { it.value.size }.sorted().reversed().mapIndexed { index, i ->
        if (index == 0) i + numJokers else i
    }.ifEmpty { listOf(5) }

    override fun toString(): String {
        return "Card $str: $grouped, $cardValues, $score"
    }

    companion object {
        val value = (listOf('J') + ('2'..'9') + 'T' + 'Q' + 'K' + 'A').mapIndexed { index: Int, c: Char -> c to index }.toMap()
    }
}
