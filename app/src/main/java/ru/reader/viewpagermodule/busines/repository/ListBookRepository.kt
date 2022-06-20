package ru.reader.viewpagermodule.busines.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.getListBookFromAssetCacheDownDocsByName
import ru.reader.viewpagermodule.getListFB2NameFromCacheAndAsset
import ru.reader.viewpagermodule.getListFileNameFromDownloadAndDocsCheckContainsCache

class ListBookRepository() : BaseRepository<BookCardData>() {




    fun refreshBookListAsFlow(){
        CoroutineScope(Dispatchers.IO).launch {
            val valueList = async {

            }.await()



        }
    }

}