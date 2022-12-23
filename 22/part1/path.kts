import java.io.File

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

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

val linesIterator = File(file).readLines().iterator()

var currentLine = linesIterator.next()
var currentRow = 1

var currentCoord = -1 to -1

while (currentLine.isNotEmpty()) {
    maxCol = Math.max(currentLine.length, maxCol)
    currentLine.forEachIndexed { idx, char ->
        if (char != empty) {
            if (currentCoord == -1 to -1 && char == free) {
                currentCoord = currentRow to idx + 1
            }
            coordMap[currentRow to idx + 1] = char
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

val password = 1000 * currentCoord.first + 4 * currentCoord.second + currentDir

println("The password is: $password")

fun move(steps: Int, orientation: String) {
    for (i in 0 until steps) {
        var nextCoord = currentCoord
        if (currentCoord + directions[currentDir] !in coordMap) {
            var stepBack = nextCoord
            while (stepBack.first > 0 && stepBack.second > 0 && stepBack.first <= maxRow && stepBack.second <= maxCol) {
                stepBack = stepBack + directions[(currentDir + 2) % 4]
                if (stepBack in coordMap) nextCoord = stepBack
            }
        } else {
            nextCoord = currentCoord + directions[currentDir]
        }
        if (coordMap[nextCoord] == wall) break
        currentCoord = nextCoord
    }
    if(orientation == "R") {
        currentDir = (currentDir + 1) % 4
    } else if(orientation == "L") {
        currentDir = (currentDir + 3) % 4
    }
}

operator fun Coord.plus(other: Coord) = (first + other.first) to (second + other.second)