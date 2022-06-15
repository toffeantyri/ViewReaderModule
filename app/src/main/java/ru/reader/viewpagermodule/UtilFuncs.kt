package ru.reader.viewpagermodule

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun Context.getFileFromAssets(fileName: String): File {
    val file = File(this.cacheDir.toString() + "/" + fileName)
    //Log.d("MyLog", "start space ${file.totalSpace}")
    //Log.d("MyLog", "file exist ${file.path}")
    if (!file.exists()) try {
        val inputStream: InputStream = this.assets.open(fileName)
        val size = inputStream.available()
        val buffer: ByteArray = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val fileOutput: FileOutputStream = FileOutputStream(file)
        fileOutput.write(buffer)
        fileOutput.close()
        //Log.d("MyLog", "reading file no exist ${file.path}")
    } catch (e: java.lang.Exception) {
        throw RuntimeException(e)
    }
    //Log.d("MyLog", "end space ${file.totalSpace}")
    return file
}