package day20

import getInput
import lcm

fun main() {
    val input = getInput("/input_day20")
    println(day20p2(input))
}

fun day20p2(input: String): String {
    val nameOfFinalModule = "rx"
    val moduleDefinitions = input.split("\n").map(::parseModule)
    val modules = moduleDefinitions.associate { (name, _, type) ->
        name to type.constructor(name)
    }.toMutableMap()
    moduleDefinitions.forEach { (name, destinations, _) ->
        val destinationModules = destinations.map { destinationName ->
            modules.getOrPut(destinationName) { UntypedModule(destinationName) }
        }
        val currentModule = modules.getValue(name)
        currentModule.output.addAll(destinationModules)
        destinationModules.onEach { it.input.add(currentModule) }
    }

    val finalModule = modules[nameOfFinalModule]!!
    val finalConjMod = finalModule.input.single()
    val counterConjMods = finalConjMod.input.map { it.input.single() }
    val countersBits = modules["broadcaster"]!!.output.map {
        var flipFlop: Module? = it
        buildList {
            while (flipFlop != null) {
                add(flipFlop!!)
                flipFlop = (flipFlop!!.output - counterConjMods).singleOrNull()
            }
        }
    }
    val cycleLengths = countersBits.map { counterBits ->
        check(counterBits.size <= Long.SIZE_BITS)
        var bitMask = 1
        counterBits.fold(0L) { cycleLength, module ->
            val cycleLength = if ((module.output intersect counterConjMods).isNotEmpty()) {
                cycleLength + bitMask
            } else {
                cycleLength
            }
            bitMask = bitMask shl 1
            cycleLength
        }
    }

    val returnValue = lcm(cycleLengths)

    return returnValue.toString()
}
