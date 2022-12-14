import java.io.File
import java.util.LinkedList


val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val stacks = Array(9) { LinkedList<Char>() }
val instruction = "move (\\d*) from (\\d*) to (\\d*)".toRegex()

File(file).forEachLine { line: String ->
    if(line.isEmpty()) {
        // Do nothing
    } else if(line[1] != '1' && line[0] != 'm') { // Crate row
        for (i in 0 until 9) {
            val crate = line[1 + i * 4]
            if(crate - 'A' >= 0 && crate - 'A' <= 26) {
                stacks[i].add(crate)
            }
        }
    } else if(line[0] == 'm') { // instruction
        val movement = instruction.find(line)!!.groupValues
        val quantity = movement[1].toInt()
        val from = movement[2].toInt()
        val to = movement[3].toInt()
        for(i in 0 until quantity) {
            val crate = stacks[from - 1].remove()
            stacks[to - 1].addFirst(crate)
        }
    }
    
}

print("Top crates: ")
stacks.forEach { stack -> print(stack.peek()) }
println()
