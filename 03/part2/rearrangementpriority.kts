import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val elf1 = mutableSetOf<Char>()
val commonItems = mutableSetOf<Char>()
var prioritySum = 0

File(file).forEachLine { rucksack: String ->
    if (elf1.isEmpty() && commonItems.isEmpty()) {
        rucksack.forEach { item ->
            elf1.add(item)
        }
    } else if (commonItems.isEmpty()) {
        rucksack.forEach { item ->
            if (elf1.contains(item)) {
                commonItems.add(item)
            }
        }
    } else {
        rucksack.forEach { item ->
            if (commonItems.contains(item)) {
                prioritySum += item.priority()
                elf1.clear()
                commonItems.clear()
            }
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