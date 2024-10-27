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
    content.appendWithLineBreak("블로그가 글을 짤막하게 쓰기는 안좋은거 같아서 파일 형태로 사용하는 지식 저장소\n")
    content.appendWithLineBreak("🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗")
    insertSectionDivider(content)
}

fun generateMainMessage(content: StringBuilder) {
    // 폴더 리스트 끌어오는 부분(Null 값일경우 디버깅 메시지 후 종료)
    val folderList = docsDir.listFiles() ?: run {
        println("폴더가 아무것도 없는뎁쇼?🐗")
        exitProcess(0)
    }

    folderList.asSequence().filter { it.isDirectory }.sortedBy { it.name }.forEach { folder ->
        content.appendWithLineBreak("## ${folder.name}")

        val fileList = folder.listFiles()?.filter { it.isFile }?.sortedBy { it.name }
        fileList?.forEach { file ->
            val filePath = "docs/${folder.name}/${file.name}"
            val fileUrl = "$repoUrl/$filePath"
            content.appendWithLineBreak("- [${file.name}]($fileUrl)")
        }

        insertSectionDivider(content)
    }
}


fun overrideReadMeFile(content: StringBuilder) {
    readmeFile.writeText(content.toString())
    println("리드미 업데이트 완료 히히히")
}

fun insertSectionDivider(content: StringBuilder) {
    content.append("\n")
    content.append("\n")
    content.append("\n")
}

fun StringBuilder.appendWithLineBreak(value: String) {
    append(value + "\n")
}

// 최종 실행
generateReadme()
