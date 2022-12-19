import java.io.File
import java.util.ArrayDeque

typealias Coord = Triple<Int, Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val cubes = hashSetOf<Coord>()
val visited = hashSetOf<Coord>()

var surfaceAreas = arrayListOf<Int>()
var currentArea = 0

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
        val surfaceQueue = ArrayDeque<Coord>()
        surfaceQueue.add(currentCube)
        while (!surfaceQueue.isEmpty()) {
            val currentAir = surfaceQueue.removeFirst()
            if (currentAir in visited) continue
            val nextToSurface = adjDirs.any { (it + currentAir) in cubes } 
            if (nextToSurface) {
                currentArea += adjDirs.map { it + currentAir }.filter { it in cubes }.size
            }
            visited.add(currentAir)
            surfaceQueue.addAll(
                adjDirs.map { it + currentAir }.filter { adj ->
                    val externalLayer = !nextToSurface && adjDirs.any { (it + adj) in cubes }
                    adj !in visited && adj !in cubes && (nextToSurface || externalLayer)
                }
            )
        }
        surfaceAreas.add(currentArea)
        currentArea = 0
    }
    queue.addAll(adjDirs.map { it + currentCube }.filter { it !in visited && it !in cubes })
}

val exteriorArea = surfaceAreas.max()

println("Exterior area: $exteriorArea")

operator fun Coord.plus(other: Coord) =
    Coord(
        first + other.first,
        second + other.second,
        third + other.third,
    )