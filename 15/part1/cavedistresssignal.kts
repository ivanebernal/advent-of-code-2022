import java.io.File

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val readingRegex = "Sensor at x=(-?\\d*), y=(-?\\d*): closest beacon is at x=(-?\\d*), y=(-?\\d*)".toRegex()

var minX = Int.MAX_VALUE
var maxX = Int.MIN_VALUE

val sensorMap = hashMapOf<Coord, Coord>()

File(file).forEachLine { reading ->
    val (sx, sy, bx, by) = readingRegex.find(reading)!!.destructured
    val sensor = Coord(sx.toInt(), sy.toInt()) 
    val beacon = Coord(bx.toInt(), by.toInt())
    val distance = getManhathanDistance(sensor, beacon)
    minX = Math.min(minX, sensor.first - distance)
    maxX = Math.max(maxX, sensor.first + distance)
    sensorMap[sensor] = beacon
}

fun getManhathanDistance(a: Coord, b: Coord): Int {
    return Math.abs(a.first - b.first) + Math.abs(a.second - b.second)
}

var occupiedSpaces = 0

println("x = $minX until $maxX")
println("y = 2,000,000")

for (x in minX .. maxX) {
    val currentCoord = Coord(x, 2_000_000)
    for((sensor, beacon) in sensorMap) {
        if(currentCoord == beacon) continue
        val coordDistance = getManhathanDistance(sensor, currentCoord)
        val beaconDistance = getManhathanDistance(sensor, beacon)
        if (coordDistance <= beaconDistance) {
            occupiedSpaces++
            break
        }
    }
}

println("Occupied spaces in y=2,000,000: ${occupiedSpaces}")