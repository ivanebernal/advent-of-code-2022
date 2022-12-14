import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var x = 1
var cycle = 0

var score = 0

File(file).forEachLine { line: String ->
    val instruction = line.split(" ")
    val command = instruction[0]
    when (command) {
        "noop" -> {
            step()
        }
        "addx" -> {
            step()
            step()
            x += instruction[1].toInt()
        }
    }
}

fun step() {
    cycle++
    if ((cycle - 20) % 40 == 0) {
        score += x * cycle
    }
}

println("Signal strength: $score")