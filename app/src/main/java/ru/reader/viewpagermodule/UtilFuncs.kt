package ru.reader.viewpagermodule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.kursx.parser.fb2.FictionBook
import org.xml.sax.SAXException
import ru.reader.viewpagermodule.adapters.BookCardData
import java.io.*
import javax.xml.parsers.ParserConfigurationException


fun Context.getFileFromAssetsAndCache(fileName: String): File? {
    val cacheFile = File(this.cacheDir.toString() + "/" + fileName)
    if (cacheFile.exists()) {
        return cacheFile
    } else {
        try {
            val inputStream: InputStream = this.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val fileOutput = FileOutputStream(cacheFile)
            fileOutput.write(buffer)
            fileOutput.close()
            return cacheFile
        } catch (e: FileNotFoundException) {
            Log.d("MyLog", "getFileAsset - $e")
            return null
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

fun getFileFromPathDownloads(fileName: String): File? {
    val downFile =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/" + fileName)
//    Log.d("MyLog", "FILE NAME ${downFile.name}")
//    Log.d("MyLog", "FILE exist ${downFile.exists()}")
    return if (downFile.exists()) downFile else null
}

fun getFileFromPathDocuments(fileName: String): File? {
    val downFile =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/" + fileName)
    return if (downFile.exists()) downFile else null
}

fun getListFB2NameFromCacheAndAsset(context: Context): HashSet<String> {
    Log.d("MyLog", "start get list")
    val listFilesName = hashSetOf<String>()
    /** get list files from cache and add to list */
    context.cacheDir.list()?.let { listFiles ->
        for (name in listFiles) {
            //Log.d("MyLog", "cache file name : ${name}")
            val listName = name.split(".")
            if (listName.size >= 2 && listName.last() == "fb2") {
                listFilesName.add(name)
            }
        }
    }

    //listFilesName.forEach { Log.d("MyLog", "cacheDir : name - " + it) }
    /** get list files from assets and add exclusive to list */
    context.assets.list("")?.let { arrayNames ->
        for (name in arrayNames) {
            //Log.d("MyLog", "assets file name : $name")
            val listName = name.split(".")
            if (listName.size >= 2 && listName.last() == "fb2" && !listFilesName.contains(name)) {
                listFilesName.add(name)
            }
        }
    }

    listFilesName.forEach { Log.d("MyLog", "Assets + cache list files - $it") }
    return listFilesName
}

/** get list file name from paths downloads and documents, check if cache not contain it name add to list*/
fun getListFileNameFromDownloadAndDocsCheckContainsCache(listNamesFiles: HashSet<String>): HashSet<String> {
    val listFileNames: HashSet<String> = listNamesFiles.toHashSet()
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.list()?.let { arrayNames ->
        for (name in arrayNames) {
            Log.d("MyLog", "download file name : $name")
            val listName = name.split(".")
            if (listName.size >= 2 && listName.last() == "fb2" && !listFileNames.contains(name)) {
                listFileNames.add(name)
            }
        }
    }
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)?.list()?.let { arrayNames ->
        for (name in arrayNames) {
            Log.d("MyLog", "document file name : $name")
            val listName = name.split(".")
            if (listName.size >= 2 && listName.last() == "fb2" && !listFileNames.contains(name)) {
                listFileNames.add(name)
            }
        }
    }
    listFileNames.forEach { Log.d("MyLog", "all files : $it") }
    return listFileNames
}


fun getListBookFromAssetCacheDownDocsByName(context: Context, namesOfFiles: List<String>): List<BookCardData> {
    val list = hashSetOf<BookCardData>()

    fun tryFileToFb2(fb2File: File, fileName: String) {
        try {
            val fb2 = FictionBook(fb2File)
            list.add(fb2.toBookCardData(fileName))
        } catch (e: ParserConfigurationException) {
            Log.d("MyLog", e.stackTraceToString())
        } catch (e: IOException) {
            Log.d("MyLog", e.stackTraceToString())
        } catch (e: SAXException) {
            Log.d("MyLog", e.stackTraceToString())
        }
    }

    for (name in namesOfFiles) {
        var file = context.getFileFromAssetsAndCache(name)
        if (file != null) {
            tryFileToFb2(file, name)
        } else {
            file = getFileFromPathDownloads(name)
            if (file != null) tryFileToFb2(file, name)
            else {
                file = getFileFromPathDocuments(name)
                if (file != null) tryFileToFb2(file, name) else Log.d("MyLog", "file $name is not exist always")
            }
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
        val decodeString: ByteArray = Base64.decode(this, Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
        return bitmap
    } catch (e: Exception) {
        Log.d("MyLog", e.stackTraceToString())
        return try {
            val decodeString: ByteArray = Base64.decode(this, Base64.URL_SAFE)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
            bitmap
        } catch (e: Exception) {
            Log.d("MyLog", e.stackTraceToString())
            null
        }
    }
}

