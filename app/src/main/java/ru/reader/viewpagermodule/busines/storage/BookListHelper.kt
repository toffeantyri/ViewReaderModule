package ru.reader.viewpagermodule.busines.storage

import android.os.Environment
import android.util.Log
import com.kursx.parser.fb2.FictionBook
import org.xml.sax.SAXException
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.App
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.BookCardData
import ru.reader.viewpagermodule.view.adapters.MemoryLocation
import java.io.*
import java.lang.NullPointerException
import java.util.concurrent.TimeoutException
import javax.xml.parsers.ParserConfigurationException


/** class for access to file-memory and search and get file book fb2*/
class BookListHelper() {

    private val context by lazy { APP_CONTEXT }

    fun getFileFromAssetsAndCache(fileName: String): File? {
        val cacheFile = File(App.getDirCache.toString() + "/" + fileName)
        if (cacheFile.exists()) {
            Log.d("MyLog", "file cache is Exist : ${cacheFile.name}")
            return cacheFile
        } else {
            try {
                val inputStream: InputStream = App.getAssetManager.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val fileOutput = FileOutputStream(cacheFile)
                fileOutput.write(buffer)
                fileOutput.close()
                Log.d("MyLog", "file asset : ${cacheFile.name}")
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

    fun getListFB2NameFromCache(): HashSet<String> {
        val listFilesName = hashSetOf<String>()
        App.getDirCache.list()?.let { listFiles ->
            for (name in listFiles) {
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    listFilesName.add(name)
                }
            }
        }
        listFilesName.forEach { Log.d("MyLog", "cache file name - $it") }
        return listFilesName
    }

    fun getListFB2NameFromAsset(): HashSet<String> {
        val listFilesName = hashSetOf<String>()
        App.getAssetManager.list("")?.let { listNames ->
            for (name in listNames) {
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    listFilesName.add(name)
                }
            }
        }
        listFilesName.forEach { Log.d("MyLog", "asset file name - $it") }
        return listFilesName
    }

    fun getListFB2NameFromDownload(): HashSet<String> {
        var listFileNames: HashSet<String> = hashSetOf()

        listFileNames = listFileNames.getEnvironmentPathListNames(Environment.DIRECTORY_DOWNLOADS)

        listFileNames.forEach { Log.d("MyLog", "all files DOWNLOADS : $it") }
        return listFileNames
    }

    fun getListFB2NameFromDocument(): HashSet<String> {
        var listFileNames: HashSet<String> = hashSetOf()

        listFileNames = listFileNames.getEnvironmentPathListNames(Environment.DIRECTORY_DOCUMENTS)

        listFileNames.forEach { Log.d("MyLog", "all files DOCUMENTS : $it") }
        return listFileNames
    }

    private fun HashSet<String>.getEnvironmentPathListNames(path: String): HashSet<String> {
        val hashSet = this
        Environment.getExternalStoragePublicDirectory(path)?.list()?.let { arrayNames ->
            for (name in arrayNames) {
                Log.d("MyLog", "$path file name : $name")
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    hashSet.add(name)
                }
            }
        }
        return hashSet
    }

    private fun FictionBook.toBookCardData(fileFullPath: String): BookCardData {
        return BookCardData(
            author = this.description.titleInfo.authors[0]?.fullName ?: "",
            nameBook = this.description.titleInfo.bookTitle ?: "",
            fileFullPath = fileFullPath,
            imageValue = this.getTitleImageBinaryString(),
            byWay = MemoryLocation.IN_DEVICE_MEMORY
        )

    }

    private fun FictionBook.getTitleImageBinaryString(): String {
        val imageName = this.description?.titleInfo?.coverPage?.get(0)?.value?.replace("#", "") ?: ""
        return binaries[imageName]?.binary ?: ""
    }

    fun getListBookItemsFromAssetCacheDownDocsByName(namesOfFiles: HashSet<String>): List<BookCardData> {
        val list = hashSetOf<BookCardData>()

        for (name in namesOfFiles) {
            var file = getFileFromAssetsAndCache(name)
            if (file != null) {
                tryFileToFb2ToBookItem(file, name)?.let { list.add(it) }
            } else {
                file = getFileFromPathDownloads(name)
                if (file != null) tryFileToFb2ToBookItem(file, name)?.let { list.add(it) }
                else {
                    file = getFileFromPathDocuments(name)
                    if (file != null) tryFileToFb2ToBookItem(file, name)?.let { list.add(it) }
                    else Log.d("MyLog", "file $name is not exist always")
                }
            }
        }
        return list.toList()
    }

    fun tryFileToFb2ToBookItem(fb2File: File, fileFullPath: String): BookCardData? {
        try {
            Log.d("MyLog", "$------------------------------------------- $fb2File")
            val fb2 = FictionBook(fb2File)
            return fb2.toBookCardData(fileFullPath)
        } catch (e: ParserConfigurationException) {
            Log.d("MyLog", e.stackTraceToString())
        } catch (e: IOException) {
            Log.d("MyLog", e.stackTraceToString())
        } catch (e: SAXException) {
            Log.d("MyLog", e.stackTraceToString())
        } catch (e: NullPointerException) {
            Log.d("MyLog", e.stackTraceToString())
        } catch (e: TimeoutException) {
            Log.d("MyLog", e.stackTraceToString())
        }
        return null
    }

    fun getBookListForDownloading(): HashSet<BookCardData> {
        //todo check if book is here - return book else return empty book
        val listBook = hashSetOf<BookCardData>()
        listBook.add(
            BookCardData(
                author = "-",
                nameBook = context.getString(R.string.BhagavadGitaTitle),
                imageValue = "",
                fileFullPath = "",
                byWay = MemoryLocation.NOT_DOWNLOADED,
                urlForLoad = APP_CONTEXT.resources.getStringArray(R.array.array_url_bhagavad_gita).toList(),
                isFavorite = false,
                bookNameDefault = context.getString(R.string.BhagavadGitaTitle)
            )
        )
        return listBook
    }

}