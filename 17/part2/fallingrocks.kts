import java.io.File
import java.util.HashSet
import java.util.ArrayDeque

typealias Coord = Pair<Int, Long>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

// relative coordinates of rocks
val pieces = arrayOf(
    arrayOf(Coord(0, 0L), Coord(1, 0L), Coord(2, 0L), Coord(3, 0L)), // horizontal line
    arrayOf(Coord(1, 0L), Coord(0, 1L), Coord(1, 1L), Coord(2, 1L), Coord(1, 2L)), // cross
    arrayOf(Coord(0, 0L), Coord(1, 0L), Coord(2, 0L), Coord(2, 1L), Coord(2, 2L)), // backwards L
    arrayOf(Coord(0, 0L), Coord(0, 1L), Coord(0, 2L), Coord(0, 3L)), // vertical line
    arrayOf(Coord(0, 0L), Coord(0, 1L), Coord(1, 0L), Coord(1, 1L)) // square
)

val width = 7
val occupiedSpaces = hashSetOf<Coord>()
var top = -1L
val ceiling = mutableListOf(0L,0L,0L,0L,0L,0L,0L)
var currentPiece = absoluteCoord(pieces[0], Coord(2, top + 4L))
var currentRock = 0L
var currentStep = 0
val maxRocks = 1000000000000
val seen = hashMapOf<State, Pair<Long, Long>>()
var loopDetected = false
var offset = 0L

File(file).forEachLine { line ->
    while(currentRock < maxRocks) {
        val dirChar = line[currentStep]
        val dir = if(dirChar == '<') -1 else 1
        if(canMoveSideways(dir)) {
            move(Coord(dir, 0))
        }
        if(canMoveDown()) {
            move(Coord(0, -1))
        } else {
            for (space in currentPiece) {
                top = Math.max(top, space.second)
                ceiling[space.first] = Math.max(ceiling[space.first], space.second)
                occupiedSpaces.add(space)
            }
            removeUnreachableSpaces()
            currentRock++
            val state = State(ceiling.normalize(), (currentRock % pieces.size.toLong()).toInt(), (currentStep + 1) % line.length)
            if(seen.contains(state) && !loopDetected) {
                loopDetected = true
                val (rocksAtStart, height) = seen[state]!!
                val cycleLength = currentRock - rocksAtStart
                val possibleCycles = (maxRocks - currentRock) / cycleLength
                val rocksLeft = (maxRocks - currentRock) % cycleLength
                val heightDelta = top - height
                offset = heightDelta * possibleCycles
                currentRock += possibleCycles * cycleLength
            }
            seen[state] = currentRock to top
            currentPiece = absoluteCoord(pieces[(currentRock % pieces.size.toLong()).toInt()], Coord(2, ceiling.max() + 4))
        }
        currentStep = (currentStep + 1) % line.length
    }
}
println("The tower is ${top + offset + 1} units tall")

fun printRocks() {
    for(row in top downTo top - 10) {
        for (col in 0 until 7) {
            if(occupiedSpaces.contains(Coord(col, row))) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
    println()
}

fun removeUnreachableSpaces() {
    for ((col, row) in currentPiece) {
        var isLine = true
        for (c in 0 until 7) {
            if (!occupiedSpaces.contains(Coord(c, row))) {
                isLine = false
            }
        }
        if(isLine) {
            occupiedSpaces.removeIf { (c, r) ->
                r < row
            }
        }
    }
}

fun move(dir: Coord) {
    val (col, row) = dir
    currentPiece = Array(currentPiece.size) { idx ->
        val (c, r) = currentPiece[idx]
        Coord(c + col, r + row)
    }
}

fun canMoveDown(): Boolean {
    val spaces = HashSet<Coord>().apply { addAll(currentPiece) }
    for((c, r) in currentPiece) {
        val oneDown = Coord(c, r - 1)
        if(!spaces.contains(oneDown) && (r == 0L || occupiedSpaces.contains(oneDown))) {
            return false
        }
    }
    return true
}

fun canMoveSideways(dir: Int): Boolean {
    val spaces = HashSet<Coord>().apply { addAll(currentPiece) }
    for((col, row) in currentPiece) {
        val oneSide = Coord(col + dir, row)
        if(!spaces.contains(oneSide) && 
            (occupiedSpaces.contains(oneSide) || col + dir >= width || col + dir < 0)
        ) {
            return false
        }
    }
    return true
}

fun absoluteCoord(piece: Array<Coord>, origin: Coord): Array<Coord> {
    return Array(piece.size) { idx ->
        val (x, y) = piece[idx]
        Coord(x + origin.first, y + origin.second)
    }
}

data class State(
    val ceiling: List<Long>,
    val shape: Int,
    val jet: Int
)

fun MutableList<Long>.normalize(): List<Long> {
    var min = Long.MAX_VALUE
    for (h in this) {
        min = Math.min(min, h)
    }
    return this.map { it - min }
}