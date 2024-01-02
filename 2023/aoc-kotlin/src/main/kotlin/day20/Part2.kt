package day20

import getInput
import lcm

fun main() {
    val input = getInput("/input_day20")
    println(day20p2(input))
}

fun day20p2(input: String): String {
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
    val button: Module = UntypedModule("button")

    val bitMods = listOf("cd", "sq", "mj", "pq", "tt", "zq", "zg", "bk", "fz", "bn", "fx", "gb")
    val inputs = modules["th"]!!.input.toSet()
    val state = (modules["th"] as ConjunctionModule).lastReceived
    println("start")
    repeat(8200) { numPresses ->
        val pulseQueue = mutableListOf(Triple(Pulse.LOW, button, modules.getValue("broadcaster")))
        while (pulseQueue.isNotEmpty()) {
            val (pulse, sender, receiver) = pulseQueue.removeFirst()
            pulseQueue.addAll(receiver.receive(sender, pulse))
        }

        bitMods
            .map(modules::getValue)
            .map { module ->
                if (module in inputs) {
                    state.getValue(module)
                } else {
                    null
                } to (module as FlipFlopModule).isOn
            }
            .joinToString("") { when(it) {
                Pulse.HIGH to true -> "H"
                Pulse.HIGH to false -> "h"
                Pulse.LOW to true -> "L"
                Pulse.LOW to false -> "l"
                null to true -> "_"
                null to false -> " "
                else -> "#"
            } }
            .also { println("$numPresses : $it") }
    }

    val returnValue = lcm(3793, 4019, 4003, 3947)

    return returnValue.toString()
}
