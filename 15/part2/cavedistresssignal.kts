import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val readingRegex = "Sensor at x=(-?\\d*), y=(-?\\d*): closest beacon is at x=(-?\\d*), y=(-?\\d*)".toRegex()

val sensorMap = hashMapOf<Coord, Coord>()

File(file).forEachLine { reading ->
    val (sx, sy, bx, by) = readingRegex.find(reading)!!.destructured
    val sensor = Coord(sx.toInt(), sy.toInt()) 
    val beacon = Coord(bx.toInt(), by.toInt())
    sensorMap[sensor] = beacon
}


val top = 4_000_000
var found = false
val stack = ArrayDeque<Coord>()
val xProgress = arrayOf(1, 1, -1, -1)
val yProgress = arrayOf(1, -1, -1, 1)
for ((s, b) in sensorMap) {
    val distance = getManhathanDistance(s, b)
    val checkedSpaces = hashSetOf<Coord>()
    val startX = s.first
    val startY = s.second + distance + 1
    var x = startX
    var y = startY
    var orientation = 0

    do {
        if (isValidCoord(x, y)) {
            var occupied = false
            for((sensor, beacon) in sensorMap) {
                val spaceDistance = getManhathanDistance(sensor.first, sensor.second, x, y)
                val beaconDistance = getManhathanDistance(sensor, beacon)
                if (spaceDistance <= beaconDistance) {
                    occupied = true
                    break
                }
            }
            if(!occupied) {
                found = true
                val frequency = x.toLong() * top.toLong() + y.toLong()
                println("Beacon located at x:${x}, y:${y}")
                println("Tuning frequency: $frequency")
                break
            }
        }
        if(getManhathanDistance(s.first, s.second, x + xProgress[orientation], y + yProgress[orientation]) != distance + 1) {
            orientation+= (orientation + 1) % xProgress.size
        }
        x += xProgress[orientation]
        y += yProgress[orientation]

    } while (!(x == startX && y == startY))
    if(found) {
        break
    }
}

fun isValidCoord(x: Int, y: Int): Boolean {
    return x >= 0 && x <= top && y >= 0 && y <= top
}

fun getManhathanDistance(a: Coord, b: Coord): Int {
    return Math.abs(a.first - b.first) + Math.abs(a.second - b.second)
}

fun getManhathanDistance(x1: Int, x2: Int, y1: Int, y2: Int): Int {
    return Math.abs(x1 - y1) + Math.abs(x2 - y2)
}