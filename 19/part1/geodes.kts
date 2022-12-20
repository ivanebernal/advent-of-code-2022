import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val blueprintRegex = "Blueprint (\\d*): Each ore robot costs (\\d*) ore. Each clay robot costs (\\d*) ore. Each obsidian robot costs (\\d*) ore and (\\d*) clay. Each geode robot costs (\\d*) ore and (\\d*) obsidian.".toRegex()

var memo = hashMapOf<String, Int>()

val timeLimit = 24

var cost = listOf<List<Int>>()
var needed = listOf<Int>()
var quality = 0

File(file).forEachLine { blueprint ->
    val (n, oreCostOre, clayCostOre, obsidianCostOre, obsidianCostClay, geodeCostOre, geodeCostObsidian) = 
        blueprintRegex.find(blueprint)!!.destructured

    val robotCost = listOf(
        listOf(-oreCostOre.toInt(), 0, 0, 0), // ore, clay, obsidian, geode
        listOf(-clayCostOre.toInt(), 0, 0, 0),
        listOf(-obsidianCostOre.toInt(), -obsidianCostClay.toInt(), 0, 0),
        listOf(-geodeCostOre.toInt(), 0, -geodeCostObsidian.toInt(), 0)
    )
    val neededRobots = (0..3).map { robotCost.fold(0) { acc, cost -> acc - cost[it] } }
    cost = robotCost
    needed = neededRobots

    bestSoFar = 0
    val maxFromBlueprint = findMaxGeode(
        State(
            timeLimit, 
            0, 0, 0, 0, // resources
            1, 0, 0, 0, // robots
        )
    )
    println(blueprint)
    println("Max geodes in blueprint $n: $maxFromBlueprint")
    println()
    quality += maxFromBlueprint * n.toInt()
    memo.clear()
}

println("Quality sum: $quality")

var bestSoFar = 0

fun findMaxGeode(state: State): Int {
    
    var maxGeode = state.geode

    if (state.time == 0) {
        bestSoFar = Math.max(maxGeode, bestSoFar)
        return maxGeode
    }

    // is this path worth exploring?
    val potentialGeodes = state.geode + state.rGeode * state.time + (state.time) * (state.time - 1) / 2
    if (potentialGeodes < bestSoFar) {
        return 0
    }

    // Always build geode robot if we can
    if(canBuild(3, state)) {
        return findMaxGeode(buildRobot(state, 3))
    }

    // try to build robots
    for (robot in 0 until 3) {
        if(canBuild(robot, state) && shouldBuild(robot, state, maxGeode)) {
            maxGeode = Math.max(maxGeode, findMaxGeode(buildRobot(state, robot)))
        }
    }

    // or just gather resources if not enough
    if(state.ore <= 4){
        maxGeode = Math.max(maxGeode, findMaxGeode(incrementResources(state)))
    }

    return maxGeode
}

fun incrementResources(state: State) =
    with (state) {
        copy(
            time - 1,
            ore + rOre,
            clay + rClay,
            obsidian + rObsidian,
            geode + rGeode
        )
    }

fun buildRobot(state: State, robot: Int): State {
    with (state) {
        return copy(
            time = time - 1,
            ore = ore + rOre + cost[robot][0],
            clay = clay + rClay + cost[robot][1],
            obsidian = obsidian + rObsidian + cost[robot][2],
            geode = geode + rGeode,
            rOre = if(robot == 0) rOre + 1 else rOre,
            rClay = if(robot == 1) rClay + 1 else rClay,
            rObsidian = if(robot == 2) rObsidian + 1 else rObsidian,
            rGeode = if(robot == 3) rGeode + 1 else rGeode
        )
    }
}

fun canBuild(robot: Int, state: State): Boolean {
    for (resource in 0 until 4) {
        if (cost[robot][resource] + state.resource(resource) < 0) return false
    }
    return true
}

fun shouldBuild(robot: Int, state: State, localMax: Int): Boolean {
    return (robot == 3 || state.robot(robot) < needed[robot]) && localMax <= state.geode + (state.time)*(state.time - 1)/2 + state.rGeode * state.time
}

data class State(
    val time: Int,
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int,
    val rOre: Int,
    val rClay: Int,
    val rObsidian: Int,
    val rGeode: Int,
) {
    fun robot(i: Int) =
        when (i) {
            0 -> rOre
            1 -> rClay
            2 -> rObsidian
            3 -> rGeode
            else -> 0
        }

    fun resource(i: Int) =
        when (i) {
            0 -> ore
            1 -> clay
            2 -> obsidian
            3 -> geode
            else -> 0
        }
}