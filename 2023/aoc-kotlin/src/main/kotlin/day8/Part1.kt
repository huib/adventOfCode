package day8

import getInput

fun main() {
    val input = getInput("/input_day8")
    println(day8p1(input))
}

fun day8p1(input: String): String {
    val lines = input.split("\n").map { it.trim() }
    val route = lines.first()
    val nodeMap = lines.drop(2).map(::Node).associateBy(Node::code)

    var steps = 0
    var currentNode = nodeMap["AAA"]!!
    while (currentNode.code != "ZZZ") {
        val direction = route[steps % route.length]

        currentNode = when (direction) {
            'R' -> nodeMap[currentNode.right]!!
            'L' -> nodeMap[currentNode.left]!!
            else -> error("")
        }
        steps++
    }
    val returnValue = steps

    return returnValue.toString()
}

class Node(str: String) {
    val code = str.substring(0..2)
    val left = str.substring(7..9)
    val right = str.substring(12..14)

    override fun toString() = "node $code = ($left, $right)"
}
