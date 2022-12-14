import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val detected = Array(26) { 0 }
File(file).forEachLine { line: String ->
    for (i in 0 until 4) {
        detected[line[i].alphabetIndex()]++
    }
    if(detected.areAllUnique()) {
        println("Detected after 0")
        return@forEachLine
    }
    for (i in 0 until line.length - 5) {
        detected[line[i].alphabetIndex()]--
        detected[line[i + 4].alphabetIndex()]++
        if(detected.areAllUnique()) {
            println("Detected after ${i + 5}")
            return@forEachLine
        }
    }
}

fun Array<Int>.areAllUnique(): Boolean = 
    this.fold(true) { acc, value -> acc && value < 2 }

fun Char.alphabetIndex() = this - 'a'