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


    fun unzipFb2File(zipFilePath: File, defaultNameFile: String) {
        File(localPathFile.path).run {
            if (!exists()) {
                mkdirs()
            }
        }
        ZipFile(zipFilePath, ZipFile.OPEN_READ, Charsets.ISO_8859_1).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val format = entry.name.split(".").last()
                    if (format == FORMAT_FB2) {
                        val filePath = "${localPathFile.path}/$defaultNameFile.$FORMAT_FB2"
                        if (!entry.isDirectory) {
                            extractFile(input, filePath)
                        } else {
                            val dir = File(filePath)
                            dir.mkdir()
                        }
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
        private const val FORMAT_FB2 = "fb2"
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