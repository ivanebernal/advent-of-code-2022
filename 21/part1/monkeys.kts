import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val definedMonkeys = hashMapOf<String, Long>()
val remainingOperations = hashMapOf<String, Operation>()

File(file).forEachLine { statement ->
    val equation = statement.split(": ")
    val monkey = equation[0]
    val op = equation[1].split(" ")
    if (op.size == 1) {
        definedMonkeys[monkey] = op[0].toLong()
    } else {
        val a = op[0]; val operation = op[1]; val b = op[2]
        remainingOperations[monkey] = Operation(a, b, operation)
    }
}

println("root: ${solve("root")}")

fun solve(monkey: String): Long {
    if (monkey in definedMonkeys) {
        return definedMonkeys[monkey]!!
    }
    val (a, b, operation) = remainingOperations[monkey]!!
    val aInt = if (a in definedMonkeys) definedMonkeys[a]!! else solve(a)
    val bInt = if (b in definedMonkeys) definedMonkeys[b]!! else solve(b)
    val result = when(operation) {
        "+" -> aInt + bInt
        "-" -> aInt - bInt
        "*" -> aInt * bInt
        else -> aInt / bInt
    }
    definedMonkeys[monkey] = result
    return result
}

data class Operation(
    val a: String,
    val b: String,
    val operation: String
)