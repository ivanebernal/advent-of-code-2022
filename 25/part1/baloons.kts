import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var sum = 0L

File(file).forEachLine { l ->
    sum += snafuToDec(l)
}

println("Sum: $sum")
println("Snafu: ${decToSnafu(sum)}")
println("Snafu: ${snafuToDec(decToSnafu(sum))}")

fun snafuToDec(snafu: String): Long =
    snafu.fold(0) { acc, char ->
        (acc * 5) + when (char) {
            '-' -> -1
            '=' -> -2
            else -> char.digitToInt()
        }
    }

fun decToSnafu(dec: Long): String {
    return generateSequence(dec) { (it + 2) / 5 }
        .takeWhile { it != 0L }
        .map { "012=-"[(it % 5).toInt()] }
        .joinToString("")
        .reversed()
}