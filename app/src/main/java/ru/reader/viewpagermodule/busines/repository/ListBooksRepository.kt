package ru.reader.viewpagermodule.busines.repository

import kotlinx.coroutines.*
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.busines.storage.BookListHelper

class ListBooksRepository() : BaseRepository<List<BookCardData>>() {


    fun loadListBooks(onSuccess: () -> Unit) {
        val bh = BookListHelper()
        CoroutineScope(Dispatchers.IO).launch {
            val valueList = async {
                val listNames : HashSet<String> = bh.getListFB2NameFromCache()
                listNames.addAll(bh.getListFB2NameFromAsset())
                listNames.addAll(bh.getListFB2NameFromDownload())
                listNames.addAll(bh.getListFB2NameFromDocument())
                bh.getListBookItemsFromAssetCacheDownDocsByName(listNames)
            }.await()
            withContext(Dispatchers.Main) {
                liveData.value = valueList.sortedBy { it.nameBook }
                onSuccess()
            }
        }
    }

}