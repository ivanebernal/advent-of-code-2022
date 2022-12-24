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
var round = 0
while (true) {
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

    if (!proposedSpaces.any { (c, e) -> e.size == 1 }) {
        println("First round where no one moves: ${round + 1}")
        break
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
    round++
}

operator fun Coord.plus(other: Coord) = (first + other.first) to (second + other.second)