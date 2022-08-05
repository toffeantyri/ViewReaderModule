package ru.reader.viewpagermodule.data.busines.storage

import android.os.Environment
import android.util.Log
import com.kursx.parser.fb2.FictionBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xml.sax.SAXException
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.App
import ru.reader.viewpagermodule.view.adapters.BookCardData
import java.io.*
import java.lang.NullPointerException
import java.util.concurrent.TimeoutException
import javax.xml.parsers.ParserConfigurationException


/** class for access to file-memory and search and get file book fb2*/
class BookListHelper() {

    companion object {
        const val DUMMY_BOOK = "DUMMY_BOOK"
        const val BOOK_LIST_DATA_FOR_LOAD = "BOOK_LIST_DATA_FOR_LOAD"
    }

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
            File(App.getDirDownloads.path + "/" + fileName)
        return if (downFile.exists()) downFile else null
    }

    fun getFileFromPathDocuments(fileName: String): File? {
        val downFile =
            File(App.getDireDocuments.path + "/" + fileName)
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
        //listFilesName.forEach { Log.d("MyLog", "cache file name - $it") }
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
        //listFilesName.forEach { Log.d("MyLog", "asset file name - $it") }
        return listFilesName
    }

    fun getListFB2NameFromDownload(): HashSet<String> {
        var listFileNames: HashSet<String> = hashSetOf()

        listFileNames = listFileNames.getEnvironmentPathListNames(Environment.DIRECTORY_DOWNLOADS)

        //listFileNames.forEach { Log.d("MyLog", "all files DOWNLOADS : $it") }
        return listFileNames
    }

    fun getListFB2NameFromDocument(): HashSet<String> {
        var listFileNames: HashSet<String> = hashSetOf()

        listFileNames = listFileNames.getEnvironmentPathListNames(Environment.DIRECTORY_DOCUMENTS)

        //listFileNames.forEach { Log.d("MyLog", "all files DOCUMENTS : $it") }
        return listFileNames
    }

    private fun getListFB2NameFromMyPublicPath(): HashSet<String> {
        val listFileNames: HashSet<String> = hashSetOf()
        App.getMyPublicPath.list()?.let { listFiles ->
            for (name in listFiles) {
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    listFileNames.add(name)
                }
            }
        }
        //listFileNames.forEach { Log.d("MyLogFiles", "all files MyPath : $it") }
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

    private fun FictionBook.toBookCardData(fullPath: String, defaultName: String = ""): BookCardData {
        val fb = this
        return BookCardData(
            tagName = defaultName
        ).apply {
            isLoading = false
            author = fb.description.titleInfo.authors?.let { if (it.size != 0) it[0]?.fullName ?: "" else "" } ?: ""
            nameBook = fb.description.titleInfo.bookTitle ?: ""
            fileFullPath = fullPath
            imageValue = fb.getTitleImageBinaryString()
        }
    }

    private fun FictionBook.getTitleImageBinaryString(): String {
        val imageName = this.description?.titleInfo?.coverPage?.let {
            if (it.size != 0) it[0]?.value?.replace("#", "")
            else ""
        } ?: ""
        return binaries?.get(imageName)?.binary ?: ""
    }

    fun tryFileToFb2ToBookItem(fb2File: File, fileFullPath: String, tagName: String): BookCardData? {
        try {
            Log.d("MyLog", "$-----------------------------try to fb2 $fb2File")
            val fb2 = FictionBook(fb2File)
            return fb2.toBookCardData(fileFullPath, tagName)
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

    suspend fun getBookListForDownloading(): ArrayList<BookCardData> {
        val resultList: ArrayList<BookCardData> = arrayListOf()
        val listEmptyBooks = BookAvailableForDownloadHelper().getListAvailableBooksForDownloads()
        val listBookFromPath = withContext(Dispatchers.IO) {
            getListFB2NameFromMyPublicPath()
        }

        for (data in listEmptyBooks) {
            val name = "${data.tagName}.fb2"
            Log.d("MyLogFiles", "path contains $name: ${listBookFromPath.contains(name)}")
            if (listBookFromPath.contains(name)) {
                val path = "${App.getMyPublicPath.path}/$name"
                val bookData = tryFileToFb2ToBookItem(File(path), path, data.tagName)
                Log.d("MyLogFiles", "bookData : $bookData")
                if (bookData != null) {
                    resultList.add(bookData)
                } else {
                    resultList.add(data)
                }
            } else {
                resultList.add(data)
            }
        }
        return resultList
    }

    fun getDummyBook(): BookCardData {
        return BookCardData(
            tagName = DUMMY_BOOK
        ).apply {
            isLoading = false
            author = DUMMY_BOOK
            nameBook = ""
            imageValue = ""
            fileFullPath = ""
        }
    }

}