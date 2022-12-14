import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val detected = Array(26) { 0 }
val markerLength = 14
File(file).forEachLine { line: String ->
    for (i in 0 until markerLength) {
        detected[line[i].alphabetIndex()]++
    }
    if(detected.areAllUnique()) {
        println("Detected after 0")
        return@forEachLine
    }
    for (i in 0 until line.length - markerLength + 1) {
        detected[line[i].alphabetIndex()]--
        detected[line[i + markerLength].alphabetIndex()]++
        if(detected.areAllUnique()) {
            println("Detected after ${i + markerLength + 1}")
            return@forEachLine
        }
    }
}

fun Array<Int>.areAllUnique(): Boolean = 
    this.fold(true) { acc, value -> acc && value < 2 }

fun Char.alphabetIndex() = this - 'a'