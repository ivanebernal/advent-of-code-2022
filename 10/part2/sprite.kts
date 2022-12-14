import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var x = 1
var cycle = 0

val screen = Array<Array<Char>>(6) { Array<Char>(40) { '.' } }

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
    val row = cycle / 40
    val col = cycle % 40

    val low = Math.max(0, x - 1)
    val high = Math.min(39, x + 1)

    if (low <= col && high >= col) {
        screen[row][col] = '#'
    }
    
    cycle++
}

screen.forEach { row ->
    row.forEach { pixel ->
        print(pixel)
    }
    println()
}