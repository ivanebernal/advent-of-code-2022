import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val decryptKey = 811589153L

var head: Node? = null
var currentNode = head
val order = Array(2) { ArrayDeque<Node>() }
var nodeCount = 0
var zeroNode: Node? = null

File(file).forEachLine { value ->
    nodeCount++
    if (head == null) {
        head = Node(null, null, value.toLong() * decryptKey)
        currentNode = head
    } else {
        val next = Node(currentNode, null, value.toLong() * decryptKey)
        currentNode?.next = next
        currentNode = next
        if(currentNode?.value == 0L) {
            zeroNode = currentNode
        }
    }
    order[0].addLast(currentNode)
}

head?.previous = currentNode
currentNode?.next = head
(0 until 10).forEach { idx ->
    while (!order[idx % 2].isEmpty()) {
        val toMove = order[idx % 2].removeFirst()
        order[(idx + 1) % 2].addLast(toMove)
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
        if (moves != 0L) {
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
}

var currentDecrypted = zeroNode
var coord = Array<Long>(3) { 0L }

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
    val value: Long
)