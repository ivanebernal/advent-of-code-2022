import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var knots = Array(10) { Pair(0,0) } // first knot is head

val visited = hashSetOf<Pair<Int, Int>>(knots[0])

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
        knots[0] = knots[0] + movement
        for (n in 1 until knots.size) {
            if (knots[n] notNextTo knots[n - 1]) {
                val diff = knots[n - 1] - knots[n]
                val xMov = if (Math.abs(diff.first) > 1) diff.first/Math.abs(diff.first) else diff.first
                val yMov = if (Math.abs(diff.second) > 1) diff.second/Math.abs(diff.second) else diff.second
                knots[n] = knots[n] + Pair(xMov, yMov)
                if(n == knots.size - 1) visited.add(knots[n])
            }
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