import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val normalized = mapOf(
    "A" to 0,
    "B" to 1,
    "C" to 2
)
val results = mapOf(
    "X" to 2,
    "Y" to 0,
    "Z" to 1
)
val score = mapOf(
    "X" to 0,
    "Y" to 3,
    "Z" to 6
)
var totalScore = 0
File(file).forEachLine { line: String ->
    val match = line.split(" ")
    val opponent = normalized[match[0]]!!
    val result = results[match[1]]!!
    val mine = (opponent + result) % 3
    totalScore += 1 + mine + score[match[1]]!!
}
println("Total score: $totalScore")