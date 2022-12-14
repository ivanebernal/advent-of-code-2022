import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val grid = arrayListOf<List<Int>>()

File(file).forEachLine { line: String ->
    grid.add(line.map { it - '0' })
}

val visibleFromTop = Array<Array<Boolean>>(grid.size) { Array<Boolean>(grid[it].size) { false } }
val visibleFromBottom = Array<Array<Boolean>>(grid.size) { Array<Boolean>(grid[it].size) { false } }
val visibleFromLeft = Array<Array<Boolean>>(grid.size) { Array<Boolean>(grid[it].size) { false } }
val visibleFromRight = Array<Array<Boolean>>(grid.size) { Array<Boolean>(grid[it].size) { false } }

for(col in 0 until grid[0].size) {
    var highestTree = -1
    for(row in 0 until grid.size) {
        visibleFromTop[row][col] = grid[row][col] > highestTree
        highestTree = Math.max(highestTree, grid[row][col])
    }
}

for(col in 0 until grid[0].size) {
    var highestTree = -1
    for(row in grid.size - 1 downTo 0) {
        visibleFromBottom[row][col] = grid[row][col] > highestTree
        highestTree = Math.max(highestTree, grid[row][col])
    }
}

for(row in 0 until grid.size) {
    var highestTree = -1
    for(col in 0 until grid[0].size) {
        visibleFromLeft[row][col] = grid[row][col] > highestTree
        highestTree = Math.max(highestTree, grid[row][col])
    }
}

for(row in 0 until grid.size) {
    var highestTree = -1
    for(col in grid[0].size - 1 downTo 0 ) {
        visibleFromRight[row][col] = grid[row][col] > highestTree
        highestTree = Math.max(highestTree, grid[row][col])
    }
}

var visibleTrees = 0

for(row in 0 until grid.size) {
    for(col in 0 until grid[0].size) {
        if (visibleFromTop[row][col] || 
                visibleFromBottom[row][col] || 
                visibleFromLeft[row][col] || 
                visibleFromRight[row][col]) {
                    visibleTrees++
        }
    }
}

println("Visible trees from outside: $visibleTrees")