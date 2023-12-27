
/** 이 method들은 독립적으로 사용 가능함 */

import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.log10
import kotlin.math.pow

//----------------------
/** Todokanai
 *
 * Function to get the character sequence from after the last instance of File.separatorChar in a path
 * @author Neeyat Lotlikar
 * @param path String path representing the file
 * @return String filename which is the character sequence from after the last instance of File.separatorChar in a path
 * if the path contains the File.separatorChar. Else, the same path.*/
fun getFilenameForPath_td(path: String): String =
    if (!path.contains(File.separatorChar)) path
    else path.subSequence(
        path.lastIndexOf(File.separatorChar) + 1, // Discard the File.separatorChar
        path.length // parameter is used exclusively. Substring produced till n - 1 characters are reached.
    ).toString()

/** Todokanai */
fun readableFileSize_td(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}


/** Todokanai
 *
 * files:Array <<File>> 과 하위 경로의 파일의 크기 합 */
fun getTotalSize_td(files: Array<File>): Long {
    var totalSize: Long = 0
    for (file in files) {
        if (file.isDirectory) {
            totalSize += getTotalSize_td(file.listFiles() ?: emptyArray())
        } else {
            totalSize += file.length()
        }
    }
    return totalSize
}

/** Todokanai
 *
 * sort 적용된 fileList를 반환
 *
 *  BY_DEFAULT = 1
 *  BY_NAME_ASCENDING = 2
 *  BY_NAME_DESCENDING = 3
 *  BY_SIZE_ASCENDING = 4
 *  BY_SIZE_DESCENDING = 5
 *  BY_TYPE_ASCENDING = 6
 *  BY_TYPE_DESCENDING = 7
 *  BY_DATE_ASCENDING = 8
 *  BY_DATE_DESCENDING = 9
 * */
fun sortedFileList_td(sortMode:Int, files:Array<File>):List<File>{
    /** 하위 디렉토리 포함한 크기 */
    fun File.getTotalSize(): Long {
        var size: Long = 0
        if(this.isDirectory) {
            val files = this.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        file.getTotalSize()
                    } else {
                        file.length()
                    }
                }
            }
        } else {
            return this.length()
        }
        return size
    }
    return when(sortMode){
        1 ->{
            files.sortedWith (compareBy({it.isFile},{it.name}))
        }
        2 ->{
            files.sortedBy{it.name}
        }
        3 ->{
            files.sortedByDescending{it.name}
        }
        4 ->{
            files.sortedBy{ it.getTotalSize() }
        }
        5 ->{
            files.sortedByDescending { it.getTotalSize() }
        }
        6 ->{
            files.sortedBy{it.extension}
        }
        7 ->{
            files.sortedByDescending { it.extension }
        }
        8 ->{
            files.sortedBy{it.lastModified()}
        }
        9 ->{
            files.sortedByDescending { it.lastModified() }
        } else -> {
            files.toList()
        }
    }
}

/** Todokanai
 *
 * Directory의 갯수는 포함이 되지 않음 */
fun getFileNumber_td(files:Array<File>):Int{
    var total = 0
    for (file in files) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}
/** Todokanai
 *
 * Directory와 File의 총 갯수*/
fun getFileAndFoldersNumber_td(files:Array<File>):Int{
    var total = 0
    for (file in files) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total ++
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}

/** Todokanai
 *
 *  file의 하위 파일/폴더들을 반환
 *
 *  권한 문제 / IOException 발생시에는 로그찍고나서 emptyArray 반환
 */
fun getFileArray_td(file:File):Array<File>{
    val listFiles = file.listFiles()
    if(listFiles==null){
        println("${file.name}.listFiles() returned null")
        return emptyArray()
    }else{
        return listFiles
    }
}

/** Todokanai */
fun dirTree_td(currentPath:File): List<File> {
    val result = mutableListOf<File>()
    var now = currentPath
    while (now.parent != null) {
        result.add(now)
        now = now.parentFile
    }
    return result.reversed()
}

fun zipFileEntrySize_td(file:java.util.zip.ZipFile):Long{
    var result = 0L

    val entries = file.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement() as ZipEntry
        result += entry.size
    }
    return result
}

/** Todokanai */
fun compressFilesRecursivelyToZip_td(files: Array<File>, zipFile: File) {
    val buffer = ByteArray(1024)
    val zipOutputStream = ZipOutputStream(zipFile.outputStream())

    fun addToZip(file: File, parentPath: String = "") {
        val entryName = if (parentPath.isNotEmpty()) "$parentPath/${file.name}" else file.name

        if (file.isFile) {
            val zipEntry = ZipEntry(entryName)
            zipOutputStream.putNextEntry(zipEntry)

            val inputStream = FileInputStream(file)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                zipOutputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            zipOutputStream.closeEntry()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            files?.forEach { childFile ->
                addToZip(childFile, entryName)
            }
        }
    }
    for (file in files) {
        addToZip(file)
    }
    zipOutputStream.close()
    println("파일 압축이 완료되었습니다.")
}

/** Todokanai
 *
 * 경로가 접근 가능할 경우 true 반환
 * **/
fun isAccessible_td(file: File): Boolean {
    return file.listFiles() != null
}
