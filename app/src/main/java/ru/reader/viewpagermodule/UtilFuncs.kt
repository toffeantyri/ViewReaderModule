package ru.reader.viewpagermodule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.kursx.parser.fb2.Binary
import com.kursx.parser.fb2.FictionBook
import org.xml.sax.SAXException
import ru.reader.viewpagermodule.adapters.BookCardData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.ParserConfigurationException


fun Context.getFileFromAssets(fileName: String): File {
    val file = File(this.cacheDir.toString() + "/" + fileName)
    //Log.d("MyLog", "start space ${file.totalSpace}")
    //Log.d("MyLog", "file exist ${file.path}")
    if (!file.exists()) try {
        val inputStream: InputStream = this.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val fileOutput = FileOutputStream(file)
        fileOutput.write(buffer)
        fileOutput.close()
        //Log.d("MyLog", "reading file no exist ${file.path}")
    } catch (e: java.lang.Exception) {
        throw RuntimeException(e)
    }
    //Log.d("MyLog", "end space ${file.totalSpace}")
    return file
}

fun getListFB2NameFromAsset(context: Context): List<String> {
    Log.d("MyLog", "start get list")
    val listFilesName = hashSetOf<String>()
    /** get list files from cache and add to list */
    context.cacheDir.listFiles()?.let { listFiles ->
        for (file in listFiles) {
            Log.d("MyLog", "file name : ${file.name}")
            val listName = file.name.split(".")
            if (listName.size == 2 && listName[1] == "fb2") {
                listFilesName.add(file.name)
            }
        }
    }

    //listFilesName.forEach { Log.d("MyLog", "cacheDir : name - " + it) }
    /** get list files from assets and add exclusive to list */
    context.assets.list("")?.let { arrayNames ->
        for (name in arrayNames) {
            Log.d("MyLog", "file name : $name")
            val listName = name.split(".")
            if (listName.size == 2 && listName[1] == "fb2" && !listFilesName.contains(name)) {
                listFilesName.add(name)
            }
        }
    }

    //listFilesName.forEach { Log.d("MyLog", "Assets : name - " + it) }

    return listFilesName.toList().sorted()
}

fun getListBookFromAssetByName(context: Context, namesOfFiles: List<String>): List<BookCardData> {
    val list = arrayListOf<BookCardData>()

    for (name in namesOfFiles) {
        val file = context.getFileFromAssets(name)
        try {
            val fb2 = FictionBook(file)
            list.add(fb2.toBookCardData(name))
        } catch (e: ParserConfigurationException) {
            e.stackTraceToString()
        } catch (e: IOException) {
            e.stackTraceToString()
        } catch (e: SAXException) {
            e.stackTraceToString()
        }
    }

    return list.toList()
}

fun FictionBook.toBookCardData(fileName: String): BookCardData {
    return BookCardData(
        author = this.description.titleInfo.authors[0]?.fullName ?: "",
        nameBook = this.description.titleInfo.bookTitle ?: "",
        fileName = fileName,
        imageValue = this.getTitleImageBinaryString()
    )

}

fun FictionBook.getTitleImageBinaryString(): String {
    val imageName = this.description?.titleInfo?.coverPage?.get(0)?.value?.replace("#", "") ?: ""
    return binaries[imageName]?.binary ?: ""
}


fun String.convertToBitmap(): Bitmap? {
    try {
        val decodeString: ByteArray = Base64.decode(this, Base64.URL_SAFE)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
        return bitmap
    } catch (e: Exception) {
       Log.d("MyLog", e.stackTraceToString())
        return try {
            val decodeString: ByteArray = Base64.decode(this, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
            bitmap
        } catch (e: Exception) {
            Log.d("MyLog", e.stackTraceToString())
            null
        }
    }
}

