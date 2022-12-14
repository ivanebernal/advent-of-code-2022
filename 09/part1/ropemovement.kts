import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var head = Pair(0,0)
var tail = Pair(0,0)

val visited = hashSetOf<Pair<Int, Int>>(tail)

File(file).forEachLine { line: String ->
    val vector = line.split(" ")
    val direction = vector[0]
    val magnitude = vector[1].toInt()

    val movement = when (direction) {
        "U" -> Pair(0, 1)
        "D" -> Pair(0, -1)
        "L" -> Pair(-1, 0)
        "R" -> Pair(1, 0)
        else -> Pair(0, 0)
    }

    for (i in 0 until magnitude) {
        head = head + movement
        if (tail notNextTo head) {
            val diff = head - tail
            val xMov = if (Math.abs(diff.first) > 1) diff.first/Math.abs(diff.first) else diff.first
            val yMov = if (Math.abs(diff.second) > 1) diff.second/Math.abs(diff.second) else diff.second
            tail = tail + Pair(xMov, yMov)
            visited.add(tail)
        }
    }
    

}

operator fun Pair<Int, Int>.plus(b: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first + b.first, second + b.second)
}

operator fun Pair<Int, Int>.minus(b: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first - b.first, second - b.second)
}

infix fun Pair<Int, Int>.notNextTo(other: Pair<Int, Int>): Boolean {
    val diff = this - other
    return Math.abs(diff.first) > 1 || Math.abs(diff.second) > 1
}

println("Spaces visited: ${visited.size}")