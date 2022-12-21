import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val definedMonkeys = hashMapOf<String, Long>()
val remainingOperations = hashMapOf<String, Operation>()

File(file).forEachLine { statement ->
    val equation = statement.split(": ")
    val monkey = equation[0]
    val op = equation[1].split(" ")
    if (monkey != "humn") {
        if (op.size == 1) {
            definedMonkeys[monkey] = op[0].toLong()
        } else {
            val a = op[0]; val operation = op[1]; val b = op[2]
            remainingOperations[monkey] = Operation(a, b, operation)
        }
    }
}

val root = remainingOperations["root"]!!

var isHalfSolved = false

val solvedA = solve(root.a)
val solvedB = solve(root.b)

if (solvedA != null) {
    definedMonkeys[root.b] = solvedA
} else if (solvedB != null) {
    definedMonkeys[root.a] = solvedB
}

isHalfSolved = true

println("humn: ${solve("humn")}")

fun solve(monkey: String): Long? {
    if(monkey == "humn" && !isHalfSolved) return null
    if (monkey in definedMonkeys) {
        return definedMonkeys[monkey]!!
    }
    val (a, b, operation) = remainingOperations[monkey]!!
    val aInt = if (a in definedMonkeys) definedMonkeys[a]!! else solve(a)
    val bInt = if (b in definedMonkeys) definedMonkeys[b]!! else solve(b)
    if (aInt == null || bInt == null) {
        addReverseOperation(remainingOperations[monkey]!!, monkey)
        return null
    }
    val result = when(operation) {
        "+" -> aInt + bInt
        "-" -> aInt - bInt
        "*" -> aInt * bInt
        else -> aInt / bInt
    }
    definedMonkeys[monkey] = result
    remainingOperations.remove(monkey)
    return result
}

fun addReverseOperation(o: Operation, monkey: String) { 
    val (a, b, operation) = o
    if(a !in definedMonkeys) {
        remainingOperations[a] = 
            when(operation) {
                "+" -> Operation(monkey, b, "-")
                "-" -> Operation(b, monkey, "+")   
                "*" -> Operation(monkey, b, "/")
                else -> Operation(b, monkey, "*")
            }
    }
    if(b !in definedMonkeys) {
        remainingOperations[b] = 
            when(operation) {
                "+" -> Operation(monkey, a, "-")
                "-" -> Operation(a, monkey, "-")   
                "*" -> Operation(monkey, a, "/")
                else -> Operation(a, monkey, "/")
            }
    }
}

data class Operation(
    val a: String,
    val b: String,
    val operation: String
)