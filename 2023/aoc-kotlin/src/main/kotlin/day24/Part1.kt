package day24

import getInput
import java.math.BigDecimal

fun main() {
    val input = getInput("/input_day24")
    println(day24p1(input))
}

fun day24p1(input: String): String {
    val hailstones = input.split("\n").map(::parseHailstone)
    val range = (200000000000000.toBigDecimal())..(400000000000000.toBigDecimal())

    var count = 0
    hailstones.indices.forEach { index1 ->
        (0..<index1).forEach { index2 ->
            val stone1 = hailstones[index1]
            val stone2 = hailstones[index2]

            (stone1 intersection2d stone2)?.let { intersection ->
                if (intersection.first in range && intersection.second in range) {
                    count++
                }
            }
        }
    }
    val returnValue = count

    return returnValue.toString()
}

data class Hailstone(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal,
    val dx: BigDecimal,
    val dy: BigDecimal,
    val dz: BigDecimal,
) {
    init {
        require(dx.compareTo(BigDecimal.ZERO) != 0)
        require(dy.compareTo(BigDecimal.ZERO) != 0)
        require(dz.compareTo(BigDecimal.ZERO) != 0)
    }
    infix fun intersects2d(other: Hailstone) = intersection2d(other) != null

    infix fun intersection2d(other: Hailstone): Pair<BigDecimal, BigDecimal>? {
        // (x0 + t*dx, y0 + t*dy) == (x0' + t'*dx', y0' + t'*dy')
        // x = x0 + t*dx <=> t = (x - x0)/dx
        // y = y0 * t*dy -> y = y0 + (x - x0) * dy/dx

        // y == y`
        // y0 + (x - x0) * dy/dx == y0' + (x - x0') * dy'/dx'
        // y0 + x * dy/dx - x0 * dy/dx == y0' + x * dy'/dx' - x0' * dy'/dx'
        // x * (dy/dx - dy'/dx') == y0' - y0 - x0' * dy'/dx' + x0 * dy/dx
        // x == (y0' - y0 - x0' * dy'/dx' + x0 * dy/dx) / (dy/dx - dy'/dx')

        // x == (y0' * dx * dx' - y0 * dx * dx' - x0' * dy' * dx + x0 * dy * dx') / (dy/dx - dy'/dx') / dx' / dx
        // x == (y0' - y0) * (dx * dx' - x0' * dy' * dx + x0 * dy * dx') / ( dx * dx' * (dy * dx' - dy' * dx)/(dx' * dx) )
        // x == (y0' - y0) * (dx^2 * dx'^2 - x0' * dy' * dx^2 * dx' + x0 * dy * dx'^2 * dx) / ( dx * dx' * (dy * dx' - dy' * dx) )

        if (dx == 0.0.toBigDecimal() || other.dx == 0.0.toBigDecimal()) {
            error("this doesn't happen in my input, so I don't try to deal with it :)")
        }

        if ((dx * other.dy).compareTo(other.dx * dy) == 0) {
            // parallel, assume they don't intersect, otherwise I'm not sure if
            // it counts as a single intersection or infinitely many.
            return null
        }

        val px = (other.y - y - other.x * other.dy / other.dx + x * dy / dx) / (dy / dx - other.dy / other.dx)
        val t1 = (px - x) / dx
        val t2 = (px - other.x) / other.dx
        val py = y + t1 * dy

        if (t1.compareTo(BigDecimal.ZERO) == -1 || t2.compareTo(BigDecimal.ZERO) == -1) {
            return null
        }

        return px to py
    }
}

fun parseHailstone(str: String): Hailstone {
    val (x, y, z, dx, dy, dz) = str
        .split('@')
        .flatMap { it.split(',') }
        .map(String::trim)
        .map { it.toBigDecimal().setScale(16) }

    return (Hailstone(x, y, z, dx, dy, dz))
}

private operator fun <E> List<E>.component6() = get(5)
