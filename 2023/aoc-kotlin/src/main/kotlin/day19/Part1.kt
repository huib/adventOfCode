package day19

import getInput

fun main() {
    val input = getInput("/input_day19")
    println(day19p1(input))
}

fun day19p1(input: String): String {
    val (strWorkflows, strMachineParts) = input.split("\n\n").map { it.split("\n") }

    val workflows = strWorkflows.map(::WorkFlow).associateBy { it.name }
    val machineParts = strMachineParts.map(::machinePartOf)

    val acceptRejectParts = machineParts.groupBy { part ->
        var nextFlow = "in"
        while (nextFlow !in listOf("A", "R")) {
            nextFlow = workflows[nextFlow]!!.next(part)
        }

        nextFlow
    }

    val returnValue = acceptRejectParts["A"]!!.sumOf { part -> part.sum() }

    return returnValue.toString()
}

class WorkFlow(def: String) {
    val name = def.substring(0, def.indexOf('{'))
    val rules = def.substring(def.indexOf('{') + 1, def.length - 1).split(',').map(::ruleOf)

    fun next(part: MachinePart): String {
        return rules.firstNotNullOf { it.next(part) }
    }

    fun next(cube: HyperCuboid): List<Pair<HyperCuboid, String>> {
        return buildList {
            rules.fold(cube) { unmatchedCube, rule ->
                val (unmatched, matched) = rule.next(unmatchedCube).map { it.key to it.value }.partition { it.second == null }
                addAll(matched.map { it.first to it.second!! })

                if (unmatched.isEmpty())
                    return@buildList

                unmatched.single().first
            }
        }
    }
}

fun ruleOf(def: String): Rule {
    val p = def.split(':')
    return if (p.size == 1) {
        UnconditionalRule(destination = p.single())
    } else {
        ConditionalRule(conditionDef = p.first(), destination = p.last())
    }
}

interface Rule {
    fun next(part: MachinePart): String?
    fun next(cube: HyperCuboid): Map<HyperCuboid, String?>
}

class ConditionalRule(conditionDef: String, private val destination: String) : Rule {
    val catSelector: (MachinePart) -> Int
    val category: Char
    val split: Int // split to < and >=
    val less = conditionDef.contains('<')

    init {
        val (ratingCat, valueStr) = conditionDef.split('<', '>')
        category = ratingCat.first()
        catSelector = when (category) {
            'x' -> MachinePart::x
            'm' -> MachinePart::m
            'a' -> MachinePart::a
            's' -> MachinePart::s
            else -> error("odd category: $ratingCat")
        }
        val value = valueStr.toInt()
        split = if (less) value else value + 1
    }

    private fun condition(part: MachinePart): Boolean {
        return if (less) catSelector(part) < split else catSelector(part) >= split
    }

    override fun next(part: MachinePart): String? {
        return if (condition(part)) destination else null
    }

    override fun next(cube: HyperCuboid): Map<HyperCuboid, String?> {
        val (lessCube, geqCube) = cube.split(category, split)
        return if (less) {
            mapOf(lessCube to destination, geqCube to null)
        } else {
            mapOf(lessCube to null, geqCube to destination)
        }.filterKeys { it.isNotEmpty() }
    }
}

class UnconditionalRule(private val destination: String) : Rule {
    override fun next(part: MachinePart) = destination
    override fun next(cube: HyperCuboid) = mapOf(cube to destination)
}

fun machinePartOf(def: String): MachinePart {
    if (def.startsWith('{')) {
        return machinePartOf(def.substring(1, def.length - 1))
    }

    val chunks = def.split('=', ',')
    check(chunks[0].endsWith('x'))
    val x = chunks[1].toInt()
    check(chunks[2].endsWith('m'))
    val m = chunks[3].toInt()
    check(chunks[4].endsWith('a'))
    val a = chunks[5].toInt()
    check(chunks[6].endsWith('s'))
    val s = chunks[7].toInt()

    return MachinePart(x, m, a, s)
}
data class MachinePart(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    fun sum() = x + m + a + s
}
