import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val fileSys = Directory("/", hashMapOf(), null)

var currentDir = fileSys

File(file).forEachLine { line: String ->
    if (line[0] == '$') { // command
        val elements = line.split(" ")
        val command = elements[1]
        when(command) {
            "cd" -> {
                val destination = elements[2]
                if (destination == "..") {
                    currentDir = currentDir.parent ?: currentDir
                } else {
                    currentDir = (currentDir.content[destination] as? Directory) ?: currentDir
                }
            }
            "ls" -> {
                // ignore
            }
        }
    } else {
        val elements = line.split(" ")
        when (elements[0]) {
            "dir" -> {
                val dirName = elements[1]
                if (currentDir.content[dirName] == null) {
                    currentDir.content[dirName] = Directory(dirName, hashMapOf(), currentDir)
                }
            }
            else -> {
                val fileName = elements[1]
                val fileSize = elements[0].toInt()
                if(currentDir.content[fileName] == null) {
                    currentDir.content[fileName] = SysFile(fileName, fileSize)
                }
            }
        }
    }
}

val updateSize = 30_000_000
val diskSize = 70_000_000

fun getDirSize(dir: Directory): Int {
    var size = 0
    dir.content.forEach { _, content ->
        when (content) {
            is SysFile -> {
                size += content.size
            }
            is Directory -> {
                size += getDirSize(content)
            }
        }
    }
    dir.size = size
    return size
}

val usedSpace = getDirSize(fileSys)

val minSpaceToDelete = usedSpace - (diskSize - updateSize)

println("Min space to delete: $minSpaceToDelete")

var minDirSize = Int.MAX_VALUE

val queue = ArrayDeque<Directory>()
queue.add(fileSys)
while(!queue.isEmpty()) {
    val dir = queue.poll()
    if(dir.size >= minSpaceToDelete) {
        minDirSize = Math.min(minDirSize, dir.size)
    }
    dir.content.forEach { _, content ->
        if(content is Directory) {
            queue.add(content)
        }
    }
}

println("Min dir size to delete: $minDirSize")

sealed interface Content
class Directory(val name: String, val content: HashMap<String, Content>, val parent: Directory?, var size: Int = 0): Content
class SysFile(val name: String, val size: Int): Content