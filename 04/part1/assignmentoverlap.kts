import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var fullyContainedAssignments = 0

fun List<String>.toIntArray(): Array<Int> {
    return Array<Int>(2) { i -> this[i].toInt() }
}

File(file).forEachLine { assignments: String ->
    val assignmentsArr = assignments.split(",")
    val elf1Range = assignmentsArr[0].split("-").toIntArray()
    val elf2Range = assignmentsArr[1].split("-").toIntArray()
    if((elf1Range[0] <= elf2Range[0] && elf1Range[1] >= elf2Range[1]) || // first elf's assignment fully contains the second elf's
        (elf2Range[0] <= elf1Range[0] && elf2Range[1] >= elf1Range[1])) // second elf's assignment fully contains the first elf's
    {
        fullyContainedAssignments++
    }
}

println("Fully contained assignments: $fullyContainedAssignments")