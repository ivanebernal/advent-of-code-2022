import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val lines = File(file).readLines().iterator()

var rightOrderIdxSum = 0
var currentIdx = 1

while (lines.hasNext()) {
    val left = lines.next()
    val right = lines.next()
    if (lines.hasNext()) { // Blank line
        lines.next()
    }
    val comparison = compare(left, right)
    if(comparison > 0) {
        rightOrderIdxSum += currentIdx
    }
    currentIdx++
}

fun compare(left: String, right: String): Int {
    if (left.length >= 1 && left[0] != '[' && right.length >= 1 && right[0] != '[') {
        // Both are integers
        return right.toInt() - left.toInt()
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
    return rightItems.size - leftItems.size
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

println("Sum of correct indices: $rightOrderIdxSum")