import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val decoderSignal1 = "[[2]]"
val decoderSignal2 = "[[6]]"

val lines = File(file).readLines().filter { !it.isEmpty() }.toMutableList()
lines.add(decoderSignal1)
lines.add(decoderSignal2)

lines.sortWith(LineComparator())

var decoderKey = 1

lines.forEachIndexed { idx, line ->
    if (line == decoderSignal1 || line == decoderSignal2) {
        decoderKey *= (idx + 1)
    }
}

println("Decoder key: $decoderKey")

class LineComparator: Comparator<String> {
    override fun compare(left: String, right: String): Int {
        if(left.isEmpty() && right.isEmpty()) {
            return 0
        } else if (left.isEmpty() && !right.isEmpty()) {
            return -1
        } else if (!left.isEmpty() && right.isEmpty()) {
            return 1
        } else {
            if (left.length >= 1 && left[0] != '[' && right.length >= 1 && right[0] != '[') {
                // Both are integers
                return left.toInt() - right.toInt()
            }

            var leftSubs = if (left.length > 1 && left[0] == '[') {
                left.substring(1, left.length - 1)
            } else {
                left
            }
            var rightSubs = if (right.length > 1 && right[0] == '[') {
                right.substring(1, right.length - 1)
            } else {
                right
            }
            val leftItems = getItems(leftSubs)
            val rightItems = getItems(rightSubs)
            for (i in 0 until Math.min(leftItems.size, rightItems.size)) {
                val comparison = compare(leftItems[i], rightItems[i])
                if (comparison != 0) {
                    return comparison
                }
            }
            if (leftItems.size > rightItems.size) {
                return 1
            } else if (rightItems.size > leftItems.size) {
                return -1
            } else {
                return 0
            }
        }
    }

    fun getItems(line: String): List<String> {
        var currentItem = StringBuilder()
        val result = arrayListOf<String>()
        var openBrackets = 0
        line.forEach { char ->
            if (char == ',' && openBrackets == 0) {
                result.add(currentItem.toString())
                currentItem = StringBuilder()
            } else {
                currentItem.append(char)
                if (char == ']') {
                    openBrackets--
                    if(openBrackets == 0) {
                        result.add(currentItem.toString())
                    }
                } else if(char == '[') {
                    openBrackets++
                }
            }
        }
        if (!currentItem.isEmpty()) {
            result.add(currentItem.toString())
        }
        return result
    }
}