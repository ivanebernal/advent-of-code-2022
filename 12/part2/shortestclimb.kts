import java.io.File
import java.util.ArrayList
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val grid = arrayListOf<ArrayList<Char>>()

File(file).forEachLine { line: String ->
    grid.add(ArrayList(line.toList()))
}

var start = Pair(0,0)
var end = Pair(0,0)

val validMoves = listOf(
    Pair(0, 1),
    Pair(0, -1),
    Pair(1, 0),
    Pair(-1, 0),
)

grid.forEachIndexed { rowIdx, row ->
    row.forEachIndexed { colIdx, col ->
        if (grid[rowIdx][colIdx] == 'S') {
            start = Pair(rowIdx, colIdx)
        } else if (grid[rowIdx][colIdx] == 'E') {
            end = Pair(rowIdx, colIdx)
        }
    }
}

grid[start.first][start.second] = 'a'
grid[end.first][end.second] = 'z'

fun isValidCoord(row: Int, col: Int): Boolean {
    return row >= 0 && row < grid.size && col >= 0 && col < grid[0].size
}

val visited = hashSetOf<Coord>()
val queue: ArrayDeque<Pair<Coord, ArrayList<Coord>>> = ArrayDeque()
queue.add(Pair(end, arrayListOf(end)))

fun getShortestPath(): Int {
    while (!queue.isEmpty()) {
        val (coord, path) = queue.poll()!!

        if (visited.contains(coord)) {
            continue
        }

        visited.add(coord)
        validMoves.forEach { move ->
            val row = coord.first
            val col = coord.second
            val newRow = row + move.first
            val newCol = col + move.second
            val newCoord = Pair(newRow, newCol)
            if (isValidCoord(newRow, newCol) && 
                !visited.contains(newCoord) &&
                grid[newRow][newCol] - grid[row][col] >= -1) {
                    if (grid[newRow][newCol] == 'a') {
                        return path.size
                    }
                    val newPath = ArrayList(path)
                    newPath.add(newCoord)
                    queue.add(Pair(newCoord, newPath))
            }
        }
    }
    return 0
}

val minSteps = getShortestPath()

println("Min steps: $minSteps")