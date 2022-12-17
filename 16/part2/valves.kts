import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val inputRegex = "Valve (.*) has flow rate=(\\d*); tunnels? leads? to valves? (.*)".toRegex()
val network = hashMapOf<String, Valve>()
val pressuredValves = hashSetOf<String>()

File(file).forEachLine { line ->
    val (valve, flowRate, tunnelString) = inputRegex.find(line)!!.destructured
    val tunnels = tunnelString.split(", ")
    val v = Valve(valve, flowRate.toInt(), tunnels)
    network[valve] = v
    if(v.flowRate > 0) {
        pressuredValves.add(valve)
    }
}

val distances = hashMapOf<Pair<String, String>, Int>()

// Calculate distances
for (origin in HashSet(pressuredValves).apply { add("AA") }) {
    for(destination in pressuredValves) {
        val visited = hashSetOf<String>()
        val key = Pair(origin, destination)
        val queue = ArrayDeque<Pair<String, Int>>()
        queue.add(Pair(origin, 0))
        while(!queue.isEmpty()) {
            val (current, distance) = queue.removeFirst()
            if(current == destination) {
                distances[key] = distance
                break
            }
            visited.add(current) 
            for (tunnel in network[current]!!.tunnels) {
                if(visited.contains(tunnel)) continue
                queue.add(Pair(tunnel, distance + 1))
            }
            if (distances[key] != null) {
                break
            }
        }
    }
}

var maxRelease = 0
val cache = hashMapOf<String, Int>()
val timeLimit = 26

fun findLongest(step: Step): Int {
    val key = key(step)
    val (v, time, pressure, remaining, isElephant) = step

    if(cache.contains(key)) {
        return cache[key]!!
    }

    var maxPressure = 0
    for(valve in pressuredValves) {
        val nextValve = network[valve]!!
        if (!remaining.contains(nextValve.name)) continue
        val d = distances[Pair(v.name, valve)]!!
        val remainingTime = timeLimit - time - d - 1
        var localPressure = 0

        if(remainingTime >= 1) {
            localPressure = nextValve.flowRate * remainingTime
        }

        // Leave it up to the elephant
        if(remainingTime <= 1 && !isElephant) {
            val nextStep = Step(
                valve = network["AA"]!!,
                time = 0,
                pressure = localPressure,
                remainingValves = HashSet(remaining - nextValve.name),
                isElephant = true
            )
            localPressure += findLongest(nextStep)
        } else if (remainingTime > 1) {
            // I move to the next valve and open it
            val nextStep = Step(
                valve = nextValve,
                time = time + d + 1,
                pressure = pressure + (remainingTime * nextValve.flowRate),
                remainingValves = HashSet(remaining - nextValve.name),
                isElephant = isElephant
            )
            localPressure += findLongest(nextStep)
        }
        maxPressure = Math.max(localPressure, maxPressure)
    }
    cache[key] = maxPressure
    return maxPressure
}

println("Total pressure released: ${findLongest(Step(network["AA"]!!))}")

fun key(step: Step): String {
    val key = "${step.remainingValves.sorted().joinToString()}-${step.valve}-${step.time}-${step.isElephant}"
    // println(key)
    return key
}

data class Valve(
    val name: String,
    val flowRate: Int,
    val tunnels: List<String>
) {
    override fun toString(): String {
        return name
    }
}

data class Step(
    val valve: Valve,
    val time: Int = 0,
    val pressure: Int = 0,
    val remainingValves: Set<String> = pressuredValves,
    val isElephant: Boolean = false,
)