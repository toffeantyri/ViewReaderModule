package ru.reader.viewpagermodule.busines.repository

import kotlinx.coroutines.*
import ru.reader.viewpagermodule.*
import ru.reader.viewpagermodule.adapters.BookCardData

class ListBookRepository() : BaseRepository<List<BookCardData>>() {


    fun loadListBooks(onSuccess: () -> Unit, onFail: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val valueList = async {
                val listCache = getListFB2NameFromCacheAndAsset(APP_CONTEXT)
                val listAll = getListFileNameFromDownloadAndDocsCheckContainsCache(listCache).toList().sorted()
                getListBookFromAssetCacheDownDocsByName(APP_CONTEXT, listAll)
            }.await()
            withContext(Dispatchers.Main) {
                liveData.value = valueList
                onSuccess()
            }
        }
    }
}