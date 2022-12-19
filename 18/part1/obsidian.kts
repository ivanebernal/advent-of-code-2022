import java.io.File
import java.util.ArrayDeque

typealias Coord = Triple<Int, Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val cubes = hashSetOf<Coord>()
val visited = hashSetOf<Coord>()

var surfaceArea = 0

File(file).forEachLine { line ->
    with(line.split(",")) {
        cubes.add(Coord(this[0].toInt(), this[1].toInt(), this[2].toInt()))
    }
}

val adjDirs = listOf(
    Coord(1, 0, 0),
    Coord(-1, 0, 0),
    Coord(0, 1, 0),
    Coord(0, -1, 0),
    Coord(0, 0, 1),
    Coord(0, 0, -1),
)

var queue = ArrayDeque<Coord>()
queue.addAll(cubes)

while (!queue.isEmpty()) {
    val currentCube = queue.removeFirst()!!
    if (currentCube !in cubes && currentCube !in visited) {
        surfaceArea++
        continue
    }
    visited.add(currentCube)
    queue.addAll(adjDirs.map { it + currentCube }.filter { it !in visited && it !in cubes })
}

println("Surface area: $surfaceArea")

operator fun Coord.plus(other: Coord) =
    Coord(
        first + other.first,
        second + other.second,
        third + other.third,
    )