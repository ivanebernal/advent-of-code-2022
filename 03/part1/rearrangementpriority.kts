import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var prioritySum = 0

File(file).forEachLine { rucksack: String ->
    val bag = mutableSetOf<Char>()
    val bagSize = rucksack.length / 2
    for (i in 0 until bagSize) {
        bag.add(rucksack[i])
    }
    for (i in bagSize until rucksack.length) {
        val item = rucksack[i]
        if (bag.contains(item)) {
            prioritySum += item.priority()
            break
        }
    }
}

fun Char.priority() = 
    if(isLowerCase()) {
        this - 'a' + 1
    } else {
        this - 'A' + 27
    }

println("Sum of priorities: $prioritySum")