import java.io.File

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

val maxDirSize = 100_000

var sumOfDirSizes = 0

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
    if (size <= maxDirSize) {
        sumOfDirSizes += size
    }
    return size
}

getDirSize(fileSys)

println("Sum of sizes: $sumOfDirSizes")

sealed interface Content
class Directory(val name: String, val content: HashMap<String, Content>, val parent: Directory?): Content
class SysFile(val name: String, val size: Int): Content