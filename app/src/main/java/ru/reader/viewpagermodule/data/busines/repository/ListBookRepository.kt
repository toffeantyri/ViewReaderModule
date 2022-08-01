package ru.reader.viewpagermodule.data.busines.repository

import android.os.Environment
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.App
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.adapters.BookCardData
import ru.reader.viewpagermodule.view.adapters.LoadBookData

class ListBookRepository() : BaseRepository<BookCardData>() {

    private val bh by lazy { BookListHelper() }
    private val repoLoader by lazy { LoadBookRepositoryHelper() }

    suspend fun loadListBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val valueNames = async {
                    val list = bh.getListFB2NameFromCache()
                    list.addAll(bh.getListFB2NameFromAsset())
                    return@async list
                }.await()

                valueNames.forEach { name ->
                    bh.getFileFromAssetsAndCache(name)?.let {
                        val path = App.getDirCache.toString() + "/" + name
                        val bookItem = bh.tryFileToFb2ToBookItem(fb2File = it, fileFullPath = path)
                        bookItem?.let { dataEmitter.onNext(bookItem) }
                    }
                }
                withContext(Dispatchers.Main) {
                    onSuccessStep()
                }

                val valueNames2 = async {
                    bh.getListFB2NameFromDownload()
                }.await()

                valueNames2.forEach { name ->
                    bh.getFileFromPathDownloads(name)?.let {
                        val path =
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                            ).path + "/" + name
                        val bookItem = bh.tryFileToFb2ToBookItem(fb2File = it, fileFullPath = path)
                        dataEmitter.onNext(bookItem)
                    }
                }
                withContext(Dispatchers.Main) {
                    onSuccessStep()
                }


                val valueNames3 = async {
                    bh.getListFB2NameFromDocument()
                }.await()

                valueNames3.forEach { name ->
                    bh.getFileFromPathDocuments(name)?.let {
                        val path =
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS
                            ).path + "/" + name
                        val bookItem = bh.tryFileToFb2ToBookItem(fb2File = it, fileFullPath = path)
                        dataEmitter.onNext(bookItem)
                    }
                }
                withContext(Dispatchers.Main) {
                    onSuccessStep()
                    onSuccess()
                    dataEmitter.onNext(bh.getDummyBook())
                }
            }
        }
    }

    suspend fun getDownloadedBooksOrListWithEmptyBooks(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val list = bh.getBookListForDownloading()
                list.forEach { book ->
                    dataEmitter.onNext(book)
                    withContext(Dispatchers.Main) {
                    }
                }
                withContext(Dispatchers.Main) {
                    onSuccess()
                    dataEmitter.onNext(bh.getDummyBook())
                }
            }
        }
    }

    fun loadBook(loadBookData: LoadBookData, onSuccess: () -> Unit, onFail: () -> Unit) {
        var subscriber: Disposable? = null
        CoroutineScope(Dispatchers.IO).launch {
            subscriber = repoLoader.getStateEmitter().subscribe {
                //Log.d("MyLog", "to REPO state $it")
                if (it.state == LoadingBookState.SUCCESS_LOAD && it.name == loadBookData.defaultNameBook) {
                    //todo сначала получает книгу из памяти затем
                    onSuccess()
                }
                if (it.state == LoadingBookState.LOAD_FAIL && it.name == loadBookData.defaultNameBook) {
                    onFail()
                }
                if (it.state == LoadingBookState.IDLE_LOAD && it.name == loadBookData.defaultNameBook) {
                    subscriber?.dispose()
                }
            }
            repoLoader.loadBook(loadBookData)
        }
    }
}