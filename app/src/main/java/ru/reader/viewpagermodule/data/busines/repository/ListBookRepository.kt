package ru.reader.viewpagermodule.data.busines.repository

import android.os.Environment
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.App
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.adapters.BookCardData

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
                        dataEmitter.onNext(bookItem)
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

    fun loadBook(listUrl: ArrayList<String>, onSuccess: () -> Unit, onFail: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            repoLoader.dataEmitter.subscribe {
                if (it == LoadingBookState.SUCCESS_LOAD) {
                    onSuccess()
                }
                if (it == LoadingBookState.LOAD_FAIL) {
                    onFail()
                }
            }
            repoLoader.loadBook(listUrl)
        }
    }
}