package day20

import getInput

fun main() {
    val input = getInput("/input_day20")
    println(day20p1(input))
}

fun day20p1(input: String): String {
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

    var highPulses = 0
    var lowPulses = 0
    repeat(1000) {
        val pulseQueue = mutableListOf(Triple(Pulse.LOW, button, modules.getValue("broadcaster")))
        while (pulseQueue.isNotEmpty()) {
            val (pulse, sender, receiver) = pulseQueue.removeFirst()
            pulseQueue.addAll(receiver.receive(sender, pulse))

            if (pulse == Pulse.HIGH) {
                highPulses++
            } else {
                lowPulses++
            }
        }
    }

    val returnValue = lowPulses * highPulses

    return returnValue.toString()
}

fun parseModule(str: String): ModuleDefinition {
    val type = when (str.first()) {
        in 'a'..'z' -> ModuleType.BROADCASTER
        '%' -> ModuleType.FLIPFLOP
        '&' -> ModuleType.CONJUNCTION
        else -> error("odd module type")
    }

    val start = if (type == ModuleType.BROADCASTER) 0 else 1
    val names = str.substring(start).split(Regex("\\W+"))

    return ModuleDefinition(
        name = names.first(),
        destinations = names.drop(1),
        type = type,
    )
}

data class ModuleDefinition(
    val name: String,
    val destinations: List<String>,
    val type: ModuleType,
)

abstract class Module(
    val name: String
) {
    val output = mutableListOf<Module>()
    val input = mutableListOf<Module>()

    abstract fun receive(source: Module, pulse: Pulse): List<Triple<Pulse, Module, Module>>
}

class UntypedModule(name: String) : Module(name) {
    override fun receive(source: Module, pulse: Pulse) = emptyList<Triple<Pulse, Module, Module>>()
}

class Broadcaster(
    name: String,
) : Module(name) {
    override fun receive(source: Module, pulse: Pulse) = output.map { Triple(pulse, this, it) }
}

class FlipFlopDefinition(
    name: String,
) : Module(name) {
    var isOn = false

    override fun receive(source: Module, pulse: Pulse) =
        if (pulse == Pulse.HIGH) {
            emptyList()
        } else if (isOn) {
            isOn = false
            output.map { Triple(Pulse.LOW, this, it) }
        } else {
            isOn = true
            output.map { Triple(Pulse.HIGH, this, it) }
        }
}

class ConjunctionDefinition(
    name: String,
) : Module(name) {
    val lastReceived = mutableMapOf<Module, Pulse>().withDefault { Pulse.LOW }
    override fun receive(source: Module, pulse: Pulse): List<Triple<Pulse, Module, Module>> {
        lastReceived[source] = pulse

        return if (input.map(lastReceived::getValue).all { it == Pulse.HIGH }) {
            output.map { Triple(Pulse.LOW, this, it) }
        } else {
            output.map { Triple(Pulse.HIGH, this, it) }
        }
    }
}

enum class ModuleType(val constructor: (String) -> Module) {
    BROADCASTER(::Broadcaster),
    FLIPFLOP(::FlipFlopDefinition),
    CONJUNCTION(::ConjunctionDefinition),
}

enum class Pulse {
    HIGH,
    LOW,
}
