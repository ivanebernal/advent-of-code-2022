import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val grid = arrayListOf<List<Int>>()

File(file).forEachLine { line: String ->
    grid.add(line.map { it - '0' })
}

val visibleOnTop = Array<Array<Int>>(grid.size) { Array<Int>(grid[it].size) { 0 } }
val visibleOnBottom = Array<Array<Int>>(grid.size) { Array<Int>(grid[it].size) { 0 } }
val visibleOnLeft = Array<Array<Int>>(grid.size) { Array<Int>(grid[it].size) { 0 } }
val visibleOnRight = Array<Array<Int>>(grid.size) { Array<Int>(grid[it].size) { 0 } }

for(col in 0 until grid[0].size) {
    val lastPositionByHeight = Array<Int>(10) { 0 }
    for(row in 0 until grid.size) {
        val height = grid[row][col]
        var closestBarrier = 0
        for (h in height until lastPositionByHeight.size) {
            closestBarrier = Math.max(closestBarrier, lastPositionByHeight[h])
        }
        visibleOnTop[row][col] = row - closestBarrier
        lastPositionByHeight[height] = row
    }
}

for(col in 0 until grid[0].size) {
    val lastPositionByHeight = Array<Int>(10) { grid.size - 1 }
    for(row in grid.size - 1 downTo 0) {
        val height = grid[row][col]
        var closestBarrier = grid.size - 1
        for (h in height until lastPositionByHeight.size) {
            closestBarrier = Math.min(closestBarrier, lastPositionByHeight[h])
        }
        visibleOnBottom[row][col] = closestBarrier - row
        lastPositionByHeight[height] = row
    }
}

for(row in 0 until grid.size) {
    val lastPositionByHeight = Array<Int>(10) { 0 }
    for(col in 0 until grid[0].size) {
        val height = grid[row][col]
        var closestBarrier = 0
        for (h in height until lastPositionByHeight.size) {
            closestBarrier = Math.max(closestBarrier, lastPositionByHeight[h])
        }
        visibleOnLeft[row][col] = col - closestBarrier
        lastPositionByHeight[height] = col
    }
}

for(row in 0 until grid.size) {
    val lastPositionByHeight = Array<Int>(10) { grid[0].size - 1 }
    for(col in grid[0].size - 1 downTo 0) {
        val height = grid[row][col]
        var closestBarrier = grid[0].size - 1
        for (h in height until lastPositionByHeight.size) {
            closestBarrier = Math.min(closestBarrier, lastPositionByHeight[h])
        }
        visibleOnRight[row][col] = closestBarrier - col
        lastPositionByHeight[height] = col
    }
}

var maxScenicScore = 0

for(row in 0 until grid.size) {
    for(col in 0 until grid[0].size) {
        maxScenicScore = Math.max(maxScenicScore, visibleOnTop[row][col] * 
                visibleOnBottom[row][col] *
                visibleOnLeft[row][col] *
                visibleOnRight[row][col])
    }
}

println("Max scenic score: $maxScenicScore")