import java.io.File

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val elves = arrayListOf<Coord>()
val positions = hashSetOf<Coord>()


File(file).readLines().forEachIndexed { row, line ->
    line.forEachIndexed { col, char ->
        if (char == '#') {
            elves.add(row to col)
            positions.add(row to col)
        }
    }
}
val n = -1 to 0; val ne = -1 to 1; val nw = -1 to -1; 
val s = 1 to 0; val se = 1 to 1; val sw = 1 to -1
val e = 0 to 1; val w = 0 to -1
val allDirs = listOf(n, ne, e, se, s, sw, w, nw)
val proposals = listOf(
    n to listOf(n, ne, nw),
    s to listOf(s, se, sw),
    w to listOf(w, nw, sw),
    e to listOf(e, ne, se),
)

for (round in 0 until 10) {
    val proposedSpaces = hashMapOf<Coord, ArrayList<Int>>()

    for ((idx, elf) in elves.withIndex()) {
        // don't do anything no one nearby
        if (allDirs.all { (it + elf) !in positions }) continue
        // propose next move
        for (p in round .. round + proposals.size) {
            val (dir, check)  = proposals[p % proposals.size]
            if (check.any { it + elf in positions }) continue
            val proposedSpace = dir + elf
            proposedSpaces[proposedSpace] = (proposedSpaces[proposedSpace] ?: arrayListOf()).apply { add(idx)} 
            break
        }
    }

    // finally, move
    proposedSpaces.forEach { (newSpace, proposingElves) ->
        // if two or more elves propose the same space, no one moves
        if(proposingElves.size < 2) {
            val oldSpace = elves[proposingElves[0]]
            positions.remove(oldSpace)
            positions.add(newSpace)
            elves[proposingElves[0]] = newSpace
        }
    }
    println("finish round $round")
}

var minCol = Int.MAX_VALUE; var minRow = Int.MAX_VALUE; var maxRow = 0; var maxCol = 0

elves.forEach { (row, col) ->
    minRow = Math.min(minRow, row)
    minCol = Math.min(minCol, col)
    maxRow = Math.max(maxRow, row)
    maxCol = Math.max(maxCol, col)
}

val width = maxRow - minRow + 1
val height = maxCol - minCol + 1

for(row in minRow - 1 .. maxRow + 1) {
    for(col in minCol - 1 .. maxCol + 1) {
        if (row to col !in positions) {
            print(".")
        } else {
            print("#")
        }
    }
    println()
}

val freeSpaces = width * height - elves.size

println("Free spaces: $freeSpaces")

operator fun Coord.plus(other: Coord) = (first + other.first) to (second + other.second)