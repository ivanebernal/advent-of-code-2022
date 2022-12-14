import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var overlappedAssignments = 0

fun List<String>.toIntArray(): Array<Int> {
    return Array<Int>(2) { i -> this[i].toInt() }
}

File(file).forEachLine { assignments: String ->
    val assignmentsArr = assignments.split(",")
    val elf1Range = assignmentsArr[0].split("-").toIntArray()
    val elf2Range = assignmentsArr[1].split("-").toIntArray()
    
    val firstRange = if(elf1Range[0] < elf2Range[0]) {
        elf1Range
    } else {
        elf2Range
    }
    val secondRange = if(firstRange == elf1Range) {
        elf2Range
    } else {
        elf1Range
    }

    if (firstRange[0] <= secondRange[0] && firstRange[1] >= secondRange[0]) {
        overlappedAssignments++
    }

}

println("Overlapped assignments: $overlappedAssignments")