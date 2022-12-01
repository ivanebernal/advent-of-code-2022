import java.io.File
import java.util.PriorityQueue
import java.util.Comparator

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

// 1. Read file line per line
// 2. Sum calories until we get an empty line
// 3. Store sum in priority queue 
val sortedCalories = PriorityQueue<Int>(Comparator.reverseOrder())
var currentSum = 0
File(file).forEachLine { line: String ->
    if(line.isEmpty()) {
        sortedCalories.add(currentSum)
        currentSum = 0
    } else {
        currentSum += line.toInt()
    }
}
println("Most calories: ${sortedCalories.peek()}")