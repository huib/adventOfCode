package day24

import getInput

fun main() {
    val input = getInput("/input_day24")
    println(day24p2(input))
}

fun day24p2(input: String): String {
    val hailstones = input.split("\n").map(::parseHailstone)

    val sageMathScript = buildString {
        appendLine("t1, t2, t0, sx, sy, sz, sdx, sdy, sdz = var('t1 t2 t0 sx sy sz sdx sdy sdz')")
        appendLine("solve([")
        hailstones.take(3).forEachIndexed { i, stone ->
            with(stone) {
                appendLine("    ${x.toBigInteger()} + ${dx.toBigInteger()} * t$i == sx + t$i * sdx,")
                appendLine("    ${y.toBigInteger()} + ${dy.toBigInteger()} * t$i == sy + t$i * sdy,")
                appendLine("    ${z.toBigInteger()} + ${dz.toBigInteger()} * t$i == sz + t$i * sdz,")
            }
        }
        appendLine("], t1, t2, t0, sx, sy, sz, sdx, sdy, sdz)")
    }
    println(sageMathScript)

    // sage math solution:
    val t1 = 994990845610
    val t2 = 792974158044
    val t0 = 137307190534
    val sx = 335849990884055
    val sy = 362494628861890
    val sz = 130073711567420
    val sdx = -110
    val sdy = -135
    val sdz = 299

    val returnValue = sx + sy + sz

    return returnValue.toString()
}
