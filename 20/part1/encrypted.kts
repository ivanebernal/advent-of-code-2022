import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var head: Node? = null
var currentNode = head
val order = ArrayDeque<Node>()
var nodeCount = 0
var zeroNode: Node? = null

File(file).forEachLine { value ->
    nodeCount++
    if (head == null) {
        head = Node(null, null, value.toInt())
        currentNode = head
    } else {
        val next = Node(currentNode, null, value.toInt())
        currentNode?.next = next
        currentNode = next
        if(currentNode?.value == 0) {
            zeroNode = currentNode
        }
    }
    order.addLast(currentNode)
}

head?.previous = currentNode
currentNode?.next = head

while (!order.isEmpty()) {
    val toMove = order.removeFirst()
    val moves = (toMove?.value ?: 0) % (nodeCount - 1)
    var destination = toMove
    for (i in 0 until Math.abs(moves)) {
        destination = if (moves > 0) {
            destination?.next
        } else {
            destination?.previous
        }
    }
    // connect two adjacent nodes
    if (moves != 0) {
        toMove?.previous?.next = toMove?.next
        toMove?.next?.previous = toMove?.previous
    }
    if (moves > 0) {
        toMove?.next = destination?.next
        destination?.next?.previous = toMove
        destination?.next = toMove
        toMove?.previous = destination
    } else if (moves < 0) {
        toMove?.previous = destination?.previous
        destination?.previous?.next = toMove
        destination?.previous = toMove
        toMove?.next = destination
    }
}

var currentDecrypted = zeroNode
var coord = Array<Int>(3) { 0 }

for(i in 0 until 3) {
    for(j in 0 until 1000) {
        currentDecrypted = currentDecrypted?.next
    }
    coord[i] = currentDecrypted?.value ?: 0
}

fun printLinkedList(node: Node?) {
    var current = node
    do {
        print("${current?.value}, ")
        current = current?.next
    } while (current != node)
    println()
}

val i = coord[0]; val j = coord[1]; val k = coord[2]

println("Coordinates: ($i, $j, $k). Sum: ${i + j + k}")

class Node(
    var previous: Node?,
    var next: Node?,
    val value: Int
)

/*

val values = File(file).readLines().map { it.toInt() to false }.toMutableList()

var idx = 0

while(idx < values.size) {
    val (value, hasMoved) = values[idx]
    if (hasMoved || value == 0) {
        if(value == 0) {
            values[idx] = 0 to true
        }
        idx++
        continue
    }
    
    val (toMove, _) = values.removeAt(idx)
    val newVal = idx + value
    if(newVal > 0) {
        val newIdx = newVal % values.size
        values.add(newIdx, toMove to true)
    } else {
       val newIdx = (newVal % values.size) + if(newVal <= 0) (values.size) else 0
       values.add(newIdx, toMove to true)
    }
    // println(values.map{it.first})
}

var zeroIndex = -1

for((n, v) in values.withIndex()) {
    if(v.first == 0) {
        zeroIndex = n
        break
    }
}

val (x, y, z) = Triple(values[(zeroIndex + 1000) % values.size].first, values[(zeroIndex + 2000) % values.size].first, values[(zeroIndex + 3000) % values.size].first)

println("Coordinates: ($x, $y, $z). Sum: ${x + y + z}")


 */