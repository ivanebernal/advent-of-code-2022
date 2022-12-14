import java.io.File
import java.util.ArrayDeque
import java.util.Arrays

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

class Monkey {
    val items: ArrayDeque<Long> = ArrayDeque()
    var operation: String = "*"
    var factor: String = "1"
    var testDiv: Long = 1L
    var monkeyIfTrue: Int = 0
    var monkeyIfFalse: Int = 0
}

val monkeys = arrayListOf<Monkey>()

File(file).forEachLine { line: String ->
    when {
        line.contains("Monkey ") -> {
            monkeys.add(Monkey())
        }
        line.contains("Starting items") -> {
            val title = "Starting items: "
            val items = line.substring(line.indexOf(title) + title.length)
            val monkey = monkeys[monkeys.size - 1]
            items.split(", ").forEach {
                monkey.items.add(it.toLong())
            }
        }
        line.contains("Operation") -> {
            val operationStart = "new = old "
            val operationEnd = line.substring(line.indexOf(operationStart) + operationStart.length)
            val operationEndParts = operationEnd.split(" ")
            val monkey = monkeys[monkeys.size - 1]
            monkey.operation = operationEndParts[0]
            monkey.factor = operationEndParts[1]
        }
        line.contains("Test") -> {
            val testStart = "divisible by "
            val factor = line.substring(line.indexOf(testStart) + testStart.length).toLong()
            val monkey = monkeys[monkeys.size - 1]
            monkey.testDiv = factor
        }
        line.contains("If true") -> {
            val actionStart = "throw to monkey "
            val monkeyIfTrue = line.substring(line.indexOf(actionStart) + actionStart.length).toInt()
            val monkey = monkeys[monkeys.size - 1]
            monkey.monkeyIfTrue = monkeyIfTrue
        }
        line.contains("If false") -> {
            val actionStart = "throw to monkey "
            val monkeyIfFalse = line.substring(line.indexOf(actionStart) + actionStart.length).toInt()
            val monkey = monkeys[monkeys.size - 1]
            monkey.monkeyIfFalse = monkeyIfFalse
        }
    }
}

val monkeyActivity = Array(monkeys.size) { 0 }

for (i in 0 until 20) {
    monkeys.forEachIndexed { idx, monkey ->
        while (!monkey.items.isEmpty()) {
            var item = monkey.items.pop()
            
            // Worry increases
            val factor = if (monkey.factor == "old") item else monkey.factor.toLong()
            when (monkey.operation) {
                "+" -> {
                    item = item + factor
                }
                "*" -> {
                    item = item * factor
                }
            }
            
            // Worry decreases
            item = item / 3L

            // Test and throw
            if (item % monkey.testDiv == 0L) {
                monkeys[monkey.monkeyIfTrue].items.add(item)
            } else {
                monkeys[monkey.monkeyIfFalse].items.add(item)
            }
            monkeyActivity[idx]++
        }
    }
}

Arrays.sort(monkeyActivity)

println("Monkey business level: ${monkeyActivity[monkeyActivity.size - 1] * monkeyActivity[monkeyActivity.size - 2]}")