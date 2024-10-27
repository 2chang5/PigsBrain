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

private fun generateHeaderMessage(content: StringBuilder) {
    content.appendLine("# PigsBrain")
    content.appendLine("🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗")
    content.appendLine("블로그가 글을 짤막하게 쓰기는 안좋은거 같아서 파일 형태로 사용하는 지식 저장소")
    content.appendLine("🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗🚨🐗")
    insertSectionDivider(content)
}

private fun generateMainMessage(content: StringBuilder) {
    // 폴더 리스트 끌어오는 부분(Null 값일경우 디버깅 메시지 후 종료)
    val folderList = docsDir.listFiles() ?: run {
        println("폴더가 아무것도 없는뎁쇼?🐗")
        exitProcess(0)
    }

    folderList.asSequence().filter { it.isDirectory }.sortedBy { it.name }.forEach { folder ->
        content.appendLine("## ${folder.name}")

        val fileList = folder.listFiles()?.filter { it.isFile }?.sortedBy { it.name }
        fileList?.forEach { file ->
            val filePath = "docs/${folder.name}/${file.name}"
            val fileUrl = "$repoUrl/$filePath"
            content.appendLine("- [${file.name}]($fileUrl)")  /
        }

        insertSectionDivider(content)
    }
}



private fun overrideReadMeFile(content: StringBuilder) {
    readmeFile.writeText(content.toString())
    println("리드미 업데이트 완료 히히히")
}

private fun insertSectionDivider(content: StringBuilder) {
    content.appendLine()
    content.appendLine()
    content.appendLine()
}
// 최종 실행
generateReadme()
