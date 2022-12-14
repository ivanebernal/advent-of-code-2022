import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val normalized = mapOf(
    "A" to 1,
    "B" to 2,
    "C" to 3,
    "X" to 1,
    "Y" to 2,
    "Z" to 3
)
var totalScore = 0
File(file).forEachLine { line: String ->
    val match = line.split(" ")
    val opponent = normalized[match[0]]!!
    val mine = normalized[match[1]]!!
    val result = mine - opponent
    totalScore += when(result) {
        1, -2 -> { // Win
            6
        }
        -1, 2 -> { // Lose
            0
        }
        0 -> { // Draw
            3
        }
        else -> {
            throw IllegalStateException("Invalid value ${result}")
        }
    }
    totalScore += mine
}
println("Total score: $totalScore")