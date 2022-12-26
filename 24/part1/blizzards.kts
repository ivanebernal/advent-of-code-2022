import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>
typealias BlizzardState = HashMap<Coord, HashSet<Char>>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val initialState = BlizzardState()
val barriers = hashSetOf<Coord>()

val lines = File(file).readLines()

val rows = lines.size
val cols = lines[0].length

var start = Coord(0, 1)
var end = Coord(rows - 1, cols - 2)
val current = start

for ((row, line) in lines.withIndex()) {
    for ((col, char) in line.withIndex()) {
        if (char == '#') {
            barriers.add(row to col)
        } else if (char.isBlizzard()) {
            val blizzardCoord = row to col 
            if (blizzardCoord !in initialState) {
                initialState[blizzardCoord] = hashSetOf()
            }
            initialState[blizzardCoord]!!.add(char)
        }
    }
}

fun Char.isBlizzard(): Boolean {
    return this == '<' || this == '^' || this == 'v' || this == '>'
}

fun printState(state: BlizzardState) {
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (state.contains(r to c)) {
                if (state[r to c]!!.size > 1) {
                    print(state[r to c]!!.size)
                } else {
                    print(state[r to c]!!.first())
                }
            } else if (barriers.contains(r to c)) {
                print('#')
            } else {
                print('.')
            }
        }
        println()
    }
}

fun getNextPoint(point: Coord, dir: Char): Coord {
    val (r, c) = point
    return when (dir) {
        '>' -> {
            if (c == cols - 2) {
                Coord(r, 1)
            } else {
                Coord(r, c + 1)
            }
        }
        'v' -> {
            if (r == rows - 2) {
                Coord(1, c)
            } else {
                Coord(r + 1, c)
            }
        }
        '<' -> {
            if (c == 1) {
                Coord(r, cols - 2)
            } else {
                Coord(r, c - 1)
            }
        }
        else -> {
            if (r == 1) {
                Coord(rows - 2, c)
            } else {
                Coord(r - 1, c)
            }
        }
    }
}

fun getNextState(previous: BlizzardState): BlizzardState {
    val new = BlizzardState()
    for (point in previous.keys) {
        val blizzardsInPoints = previous[point] ?: continue
        for (b in blizzardsInPoints) {
            val nextPoint = getNextPoint(point, b)
            if (!new.contains(nextPoint)) {
                new[nextPoint] = hashSetOf<Char>()
            }
            new[nextPoint]!!.add(b)
        }
    }
    return new
}

val directions = listOf(
    Coord(0, 1),
    Coord(1, 0),
    Coord(-1, 0),
    Coord(0, -1),
    Coord(0, 0)
)

val queue = ArrayDeque<Coord>()
queue.add(start)

var steps = 0

val cache = hashSetOf<Pair<Int, Coord>>()
var currentState = initialState
var stateNumber = 0

while(true) {
    val nextState = getNextState(currentState)
    if (nextState == initialState) {
        stateNumber = 0
    } else {
        stateNumber++
    }
    var exitFound = false
    val nextSteps = ArrayDeque<Coord>()
    while(!queue.isEmpty()) {
        val current = queue.removeFirst()
        if (current == end) {
            exitFound = true
            break
        }
        for (dir in directions) {
            val next = current + dir
            if(next.isValid(stateNumber, nextState)) {
                cache.add(stateNumber to next)
                nextSteps.add(next)
            }
        }
    }
    queue.addAll(nextSteps)
    if(exitFound) {
        break
    }
    currentState = nextState
    steps++
}

println("Steps to find the extit: $steps")

fun Coord.isValid(step: Int, state: BlizzardState): Boolean =
    this.first >= 0 && this.first < rows && this.second >= 0 && this.second < cols && 
    (state[this]?.isEmpty() ?: true) && (this !in barriers) && ((step to this) !in cache)

operator fun Coord.plus(other: Coord) = Coord(this.first + other.first, this.second + other.second)