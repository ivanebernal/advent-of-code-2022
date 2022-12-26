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

fun minutesToCoord(s: Coord, e: Coord, state: BlizzardState): Pair<Int, BlizzardState> {
    val cache = hashSetOf<Pair<Int, Coord>>()
    var currentState = state
    var stateNumber = 0
    var steps = 0
    val queue = ArrayDeque<Coord>()
    queue.add(s)
    while(true) {
        val nextState = getNextState(currentState)
        if (nextState == state) {
            stateNumber = 0
        } else {
            stateNumber++
        }
        val nextSteps = ArrayDeque<Coord>()
        while(!queue.isEmpty()) {
            val current = queue.removeFirst()
            if (current == e) {
                return steps to currentState
            }
            for (dir in directions) {
                val next = current + dir
                if(next.isValid(stateNumber, nextState, cache)) {
                    cache.add(stateNumber to next)
                    nextSteps.add(next)
                }
            }
        }
        queue.addAll(nextSteps)
        currentState = nextState
        steps++
    }   
    return -1 to state
}

var minsToExit = 0
var state = initialState
for (i in 0 until 3) {
    val s = if (i % 2 == 0) start else end
    val e = if (i % 2 == 0) end else start
    val (mins, finalState) = minutesToCoord(s, e, state)
    minsToExit += mins
    state = finalState
    println("Mins from $s to $e: $mins")
}

println("Steps to find the extit and get snacks back: $minsToExit")

fun Coord.isValid(step: Int, state: BlizzardState, cache: HashSet<Pair<Int, Coord>>): Boolean =
    this.first >= 0 && this.first < rows && this.second >= 0 && this.second < cols && 
    (state[this]?.isEmpty() ?: true) && (this !in barriers) && ((step to this) !in cache)

operator fun Coord.plus(other: Coord) = Coord(this.first + other.first, this.second + other.second)