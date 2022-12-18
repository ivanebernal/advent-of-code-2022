import java.io.File
import java.util.HashSet

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

// relative coordinates of rocks
val pieces = arrayOf(
    arrayOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0)), // horizontal line
    arrayOf(Coord(1, 0), Coord(0, 1), Coord(1, 1), Coord(2, 1), Coord(1, 2)), // cross
    arrayOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(2, 1), Coord(2, 2)), // backwards L
    arrayOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(0, 3)), // vertical line
    arrayOf(Coord(0, 0), Coord(0, 1), Coord(1, 0), Coord(1, 1)) // square
)

val width = 7
val occupiedSpaces = hashSetOf<Coord>()
var top = -1
var currentPiece = absoluteCoord(pieces[0], Coord(2, top + 4))
var currentRock = 0
var currentStep = 0

File(file).forEachLine { line ->
    while(currentRock < 2022) {
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
                occupiedSpaces.add(space)
            }
            currentRock++
            currentPiece = absoluteCoord(pieces[currentRock % pieces.size], Coord(2, top + 4))
            // printRocks()
            // Thread.sleep(2000)
        }
        currentStep = (currentStep + 1) % line.length
    }
}

println("The tower is ${top + 1} units tall")

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
        if(!spaces.contains(oneDown) && (r == 0 || occupiedSpaces.contains(oneDown))) {
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