import java.io.File
import kotlin.system.exitProcess

val repoUrl = "https://github.com/2chang5/PigsBrain"
val docsDir = File("docs")
val readmeFile = File("README.md")

fun generateReadme() {
    val content = StringBuilder()
    generateHeaderMessage(content)
    generateMainMessage(content)
    overrideReadMeFile(content)
}

fun generateHeaderMessage(content: StringBuilder) {
    content.appendWithLineBreak("# PigsBrain")
    content.appendWithLineBreak("🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗\n")
    content.appendWithLineBreak("블로그가 글을 짤막하게 쓰기는 안좋은거 같아서 파일 형태로 사용하는 지식 저장소")
    content.appendWithLineBreak("\n🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗")
    insertSectionDivider(content)
}

fun generateMainMessage(content: StringBuilder) {
    val folderList = docsDir.listFiles() ?: run {
        println("폴더가 아무것도 없는뎁쇼?🐗")
        exitProcess(0)
    }

    folderList.asSequence().filter { it.isDirectory }.sortedBy { it.name }.forEach { folder ->
        content.appendWithLineBreak("## ${folder.name}")
        appendFolderContent(content, folder)
        insertSectionDivider(content)
    }
}

fun appendFolderContent(content: StringBuilder, folder: File) {
    val items = folder.listFiles()?.sortedBy { it.name }
    items?.forEach { item ->
        if (item.isDirectory) {
            content.appendWithLineBreak("### ${item.name}")
            appendFolderContent(content, item)
            insertSectionDivider(content)
        } else if (item.isFile) {
            val filePath = "tree/main/docs/${folder.relativeTo(docsDir).path}/${item.name}"
            val fileUrl = "$repoUrl/$filePath"
            content.appendWithLineBreak("- [${item.name}]($fileUrl)")
        }
    }
}

fun overrideReadMeFile(content: StringBuilder) {
    readmeFile.writeText(content.toString())
    println("리드미 업데이트 완료 히히히")
}

fun insertSectionDivider(content: StringBuilder) {
    content.append("\n\n\n")
}

fun StringBuilder.appendWithLineBreak(value: String) {
    append(value + "\n")
}

// 최종 실행
generateReadme()
