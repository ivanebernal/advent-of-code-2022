import java.io.File

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val restingSand = hashSetOf<Coord>()
val rock = hashSetOf<Coord>()
var maxY = 0

File(file).forEachLine { line ->
    val points = line.split(" -> ").map { pair ->
        val coord = pair.split(",")
        val x = coord[0].toInt()
        val y = coord[1].toInt()
        maxY = Math.max(maxY, y)
        Coord(x, y)
    }

    for (i in 0 until points.size - 1) {
        val p1 = points[i]
        val p2 = points[i + 1]
        val isHorizontal = p1.first != p2.first
        val range = if (isHorizontal) {
            Math.min(p1.first, p2.first) .. Math.max(p1.first, p2.first)
        } else {
            Math.min(p1.second, p2.second) .. Math.max(p1.second, p2.second)
        }
        for(j in range) {
            if (isHorizontal) {
                rock.add(Coord(j, p1.second))
            } else {
                rock.add(Coord(p1.first, j))
            }
        }
    }
}

var isRested = false

while (!isRested) {
    var grain = Coord(500, 0)
    var next = getNextSpace(grain)
    while (next != grain) {
        grain = next
        next = getNextSpace(next)
        if (next.second > maxY) {
            isRested = true
            break
        }
    }
    if (isRested) {
        break
    }
    restingSand.add(grain)
}

fun getNextSpace(grain: Coord): Coord {
    val fallingPoints = arrayOf(
        Coord(grain.first, grain.second + 1),
        Coord(grain.first - 1, grain.second + 1),
        Coord(grain.first + 1, grain.second + 1)
    )
    for (point in fallingPoints) {
        if (!rock.contains(point) && !restingSand.contains(point)) {
            return point
        }
    }
    return grain
}

println("Grains of sand before overflow: ${restingSand.size}")