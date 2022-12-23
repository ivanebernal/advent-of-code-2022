import java.io.File

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

// transformations[face][dir]
val transformations = listOf(
    // face 0
    listOf(
        { p: Coord -> p },
        { p: Coord -> p },
        { p: Coord -> p + directions[(currentDir + 3) % 4].times(3 * w - 2 * (p.first % w) - 1) + directions[currentDir].times(w) }, // a
        { p: Coord -> p + directions[(currentDir + 2) % 4].times(3 * w + p.second % w) + directions[(currentDir + 3) % 4].times(w + p.second % w) }, //b
    ),
    // face 1
    listOf(
        { p: Coord -> p + directions[(currentDir + 1) % 4].times(3 * w - 2 * (p.first % w) - 1) + directions[(currentDir + 2) % 4].times(w) }, // gp
        { p: Coord -> p + (directions[currentDir] + directions[(currentDir + 1) % 4]).times(p.second % w + 1) }, // fp
        { p: Coord -> p },
        { p: Coord -> p + directions[(currentDir + 2) % 4].times(4 * w - 1) + directions[(currentDir + 3) % 4].times(2 * w) }, // c
    ),
    // face 2
    listOf(
        { p: Coord -> p + directions[currentDir].times(p.first % w + 1) + directions[(currentDir + 3) % 4].times(p.first % w + 1) }, // f
        { p: Coord -> p },
        { p: Coord -> p + directions[currentDir].times(w - (p.first % w)) + directions[(currentDir + 3) % 4].times(w - (p.first % w)) }, // e
        { p: Coord -> p },
    ),
    // face 3
    listOf(
        { p: Coord -> p },
        { p: Coord -> p },
        { p: Coord -> p + directions[(currentDir + 1) % 4].times(w + 2 * (p.first % w) + 1) + directions[(currentDir + 2) % 4].times(w) }, // ap
        { p: Coord -> p + (directions[currentDir] + directions[(currentDir + 1) % 4]).times(w - p.second % w) }, // ep
    ),
    // face 4
    listOf(
        { p: Coord -> p + directions[(currentDir + 3) % 4].times(w + 2 * (p.first % w) + 1) + directions[currentDir].times(w) }, // g
        { p: Coord -> p + (directions[currentDir] + directions[(currentDir + 1) % 4]).times(p.second % w + 1) }, // dp
        { p: Coord -> p },
        { p: Coord -> p },
    ),
    // face 5
    listOf(
        { p: Coord -> p + directions[currentDir].times(p.first % w + 1) + directions[(currentDir + 3) % 4].times(p.first % w + 1) }, // d
        { p: Coord -> p + directions[(currentDir + 2) % 4].times(4 * w - 1) + directions[(currentDir + 3) % 4].times(2 * w) }, // cp
        { p: Coord -> p + directions[(currentDir + 1) % 4].times(3 * w + (p.first % w)) + directions[(currentDir + 2) % 4].times(w + p.first % w) }, //bp
        { p: Coord -> p },
    ),
)

val wall = '#'
val free = '.'
val empty = ' '
val instructionRegex = "(\\d*)(.?)".toRegex()
val directions = arrayOf(
    0 to 1,
    1 to 0,
    0 to -1,
    -1 to 0
)
var maxRow = 0; var maxCol = 0
var currentDir = 0
val coordMap = hashMapOf<Coord, Char>()
val pathMap = hashMapOf<Coord, Char>()
val turnMap = hashMapOf<Coord, String>()

val linesIterator = File(file).readLines().iterator()

var currentLine = linesIterator.next()
var currentRow = 0

var currentCoord = -1 to -1

val referenceFace = -1 to -1
val isTest = "test_input.txt" in file
val w = if (isTest) 4 else 50

while (currentLine.isNotEmpty()) {
    maxCol = Math.max(currentLine.length, maxCol)
    currentLine.forEachIndexed { idx, char ->
        if (char != empty) {
            if (currentCoord == -1 to -1 && char == free) {
                currentCoord = currentRow to idx
            }
            coordMap[currentRow to idx] = char
        }
    }
    currentRow++
    currentLine = linesIterator.next()
}

maxRow = currentRow

val instructions = arrayListOf<Pair<Int, String>>()
val instructionIterator = instructionRegex.findAll(linesIterator.next()).iterator()
while (instructionIterator.hasNext()) {
    val (tiles, orientation) = instructionIterator.next().destructured
    if (tiles.isNotEmpty()) instructions.add(tiles.toInt() to orientation)
}
instructions.forEach { (steps, orientation) ->
    move(steps, orientation)
}

val password = 1000 * (currentCoord.first + 1) + 4 * (currentCoord.second + 1) + (currentDir % 4)

println("The password is: $password")

fun move(steps: Int, orientation: String) {
    for (i in 0 until steps) {
        addPath(currentCoord, currentDir)
        var nextCoord = currentCoord
        var nextDir = currentDir
        if (currentCoord + directions[currentDir] !in coordMap) {
            val (c, d) = warp(currentCoord)
            nextCoord = c
            nextDir = d
            // println("Warp from $currentCoord to $nextCoord")
        } else {
            nextCoord = currentCoord + directions[currentDir]
        }
        if (coordMap[nextCoord] == wall) break
        currentCoord = nextCoord
        currentDir = nextDir
    }
    turnMap[currentCoord] = orientation
    if(orientation == "R") {
        currentDir = (currentDir + 1) % 4
    } else if(orientation == "L") {
        currentDir = (currentDir + 3) % 4
    }
}

fun warp(coord: Coord): Pair<Coord, Int> {
    val currentFace = getFace(coord)
    val nextCoord = transformations[currentFace][currentDir](coord)
    val deltaDir = getChangeDir(currentFace, getFace(nextCoord))
    return nextCoord to (currentDir + deltaDir) % 4
}

fun getFace(coord: Coord): Int {
    val (row, col) = coord
    return when {
        row / w == 0 && col / w == 1 -> 0
        row / w == 0 && col / w == 2 -> 1
        row / w == 1 && col / w == 1 -> 2
        row / w == 2 && col / w == 0 -> 3
        row / w == 2 && col / w == 1 -> 4
        row / w == 3 && col / w == 0 -> 5
        else -> -1
    }
}

fun getChangeDir(face1: Int, face2: Int): Int {
    return when (face1 to face2) {
        0 to 3, 3 to 0, 1 to 4, 4 to 1 -> 2 // a, ap, g, gp
        0 to 5, 4 to 5, 3 to 2, 1 to 2 -> 1 // b, dp, ep, fp
        5 to 0, 5 to 4, 2 to 3, 2 to 1 -> 3 // bp, d, e, f
        else -> 0 // c, cp   
    }
}

fun addPath(coord: Coord, dir: Int) {
    pathMap[coord] = when(dir) {
        0 -> '>'
        1 -> 'v'
        2 -> '<'
        else -> '^'
    }
}

if(isTest) {
    for (row in 0 until maxRow) {
        for (col in 0 until maxCol) {
            if (row to col in turnMap) {
                val turn = turnMap[row to col]!!
                print(if(turn.isEmpty()) "F" else turn)
            } else if (row to col in pathMap) {
                print(pathMap[row to col])
            } else {
                print(coordMap[row to col] ?: ' ')
            }
        }
        println()
    }
}

operator fun Coord.plus(other: Coord) = (first + other.first) to (second + other.second)
fun Coord.times(other: Int) = (first * other) to (second * other)