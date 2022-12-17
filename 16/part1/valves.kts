import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val inputRegex = "Valve (.*) has flow rate=(\\d*); tunnels? leads? to valves? (.*)".toRegex()
val network = hashMapOf<String, Valve>()

File(file).forEachLine { line ->
    val (valve, flowRate, tunnelString) = inputRegex.find(line)!!.destructured
    val tunnels = tunnelString.split(", ")
    network[valve] = Valve(valve, flowRate.toInt(), tunnels)
}

var maxRelease = 0
val maxPressAtTimeFlow = hashMapOf<Pair<Int,Int>, Int>()
var maxStep: Step = Step(valve = network["AA"]!!, minute = 30)
var queue = ArrayDeque<Step>() // valve, time
var exploredSteps = hashSetOf<Step>()
queue.addFirst(Step(valve = network["AA"]!!))
while (!queue.isEmpty()) {
    val step = queue.removeFirst()!!
    val (currValve, currTime, currPress, currFlow, opened) = step
    exploredSteps.add(step)
    if(currTime == 30) {
        if(maxRelease < currPress) {
            maxRelease = currPress
            maxStep = step
        }
        continue
    }

    // Is this worth exploring?
    if(currPress < maxPressAtTimeFlow[Pair(currTime, currFlow)] ?: 0) {
        continue
    }
    maxPressAtTimeFlow[Pair(currTime, currFlow)] = currPress
    
    // open
    if(!opened.contains(currValve) && currValve.flowRate > 0) {
        queue.addFirst(
            Step(
                valve = currValve,
                minute = currTime + 1,
                releasedPressure = currPress + currFlow,
                totalFlowRate = currFlow + currValve.flowRate,
                openValves = HashSet(opened).apply { add(currValve) }
            )
        )
    }

    // traverse
    currValve.tunnels.forEach { tunnel ->
        val nextValve = network[tunnel]!!
        val newStep = Step(
            valve = nextValve,
            minute = currTime + 1,
            releasedPressure = currPress + currFlow,
            totalFlowRate = currFlow,
            openValves = opened
        )
        if (!exploredSteps.contains(newStep)) {
            queue.addFirst(newStep)
        }
    }
}

println(maxStep)
println("Total pressure released: $maxRelease")

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
    val minute: Int = 0,
    val releasedPressure: Int = 0,
    val totalFlowRate: Int = 0,
    val openValves: Set<Valve> = hashSetOf()
)