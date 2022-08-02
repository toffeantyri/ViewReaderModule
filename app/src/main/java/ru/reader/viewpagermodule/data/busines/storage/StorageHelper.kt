package ru.reader.viewpagermodule.data.busines.storage

import android.util.Log
import okhttp3.ResponseBody
import ru.reader.viewpagermodule.App
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.zip.ZipFile

class StorageHelper {

    fun saveFileToPublicLocalPath(responseBody: ResponseBody, toWillFileNameWithFormat: String): StateSave {
        File(localPathFile.path).run {
            if (!exists()) {
                mkdirs()
            }
        }
        var inputStream: InputStream? = null
        try {
            inputStream = responseBody.byteStream()
            val outputFile = FileOutputStream("${localPathFile.path}/$toWillFileNameWithFormat")
            outputFile.use { output ->
                val buffer = ByteArray(BUFFER_SIZE_FOR_SAVE)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return StateSave.STATE_SAVED
        } catch (e: Exception) {
            Log.d("MyLog", "saveFile Error : ${e.message}")
            return StateSave.STATE_NOT_SAVED
        } finally {
            inputStream?.close()
        }
    }


    fun unzipFile(zipFilePath: File, defaultNameFile: String) {
        File(localPathFile.path).run {
            if (!exists()) {
                mkdirs()
            }
        }
        val zipFile = ZipFile(zipFilePath, Charset.forName(Charsets.UTF_8.toString()))
        zipFile.use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    Log.d("MyLog", "UNZIP ENTRY :$entry")
                    Log.d("MyLog", "UNZIP ENTRY name :${entry.name}")
                    val filePath = "${localPathFile.path}/$entry" // todo default name
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
        private const val BUFFER_SIZE_FOR_SAVE = 8096
        val localPathFile: File = App.getMyPublicPath

        enum class StateSave {
            STATE_SAVED,
            STATE_NOT_SAVED,
            STATE_RESPONSE_BODY_NULL
        }
    }
}