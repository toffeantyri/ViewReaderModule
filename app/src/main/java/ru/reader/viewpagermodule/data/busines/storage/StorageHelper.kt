package ru.reader.viewpagermodule.data.busines.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.reader.viewpagermodule.App
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipFile

class StorageHelper {

    suspend fun saveFileToPublicLocalPath(responseBody: ResponseBody, toWillFileName: String): StateSave {
        withContext(Dispatchers.IO) {

        }



        return StateSave.STATE_SAVED
    }


    fun unzipFile(zipFilePath: File, targetPath: String, defaultNameFile: String) {
        File(targetPath).run {
            if (!exists()) {
                mkdirs()
            }
        }
        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val filePath = "$targetPath/$defaultNameFile"
                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdir()
                    }
                }
            }
        }
    }

    private fun extractFile(inputStream: InputStream, targetPathOut: String) {
        val bos = BufferedOutputStream(FileOutputStream(targetPathOut))
        val bytesIn = ByteArray(BUFFER_SIZE_ZIP)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }


    companion object {
        private const val BUFFER_SIZE_ZIP = 8096
        val localPathFile = App.getMyPublicPath

        enum class StateSave {
            STATE_SAVED,
            STATE_NOT_SAVED,
            STATE_RESPONSE_BODY_NULL
        }
    }
}