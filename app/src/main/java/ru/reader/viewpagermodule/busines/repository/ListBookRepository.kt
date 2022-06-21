package ru.reader.viewpagermodule.busines.repository

import kotlinx.coroutines.*
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.busines.storage.BookListHelper

class ListBookRepository() : BaseRepository<BookCardData>() {


    fun loadListBooks(onSuccess: () -> Unit) {
        val bh = BookListHelper()
        CoroutineScope(Dispatchers.IO).launch {

            launch {
                val valueNames = async {
                    val list = bh.getListFB2NameFromCache()
                    list.addAll(bh.getListFB2NameFromAsset())
                    return@async list
                }.await()

                valueNames.forEach { name ->
                    bh.getFileFromAssetsAndCache(name)?.let {
                        val bookItem = bh.tryFileToFb2ToBookItem(fb2File = it, fileName = name)
                        dataEmitter.onNext(bookItem)
                    }
                }

                val valueNames2 = async {
                    bh.getListFB2NameFromDownload()
                }.await()

                valueNames2.forEach { name ->
                    bh.getFileFromPathDownloads(name)?.let {
                        val bookItem = bh.tryFileToFb2ToBookItem(fb2File = it, fileName = name)
                        dataEmitter.onNext(bookItem)
                    }
                }


                val valueNames3 = async {
                    bh.getListFB2NameFromDocument()
                }.await()

                valueNames3.forEach { name ->
                    bh.getFileFromPathDocuments(name)?.let {
                        val bookItem = bh.tryFileToFb2ToBookItem(fb2File = it, fileName = name)
                        dataEmitter.onNext(bookItem)
                    }
                }
                withContext(Dispatchers.Main) {
                    onSuccess()
                }

            }
        }
    }
}