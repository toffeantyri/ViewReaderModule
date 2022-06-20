package ru.reader.viewpagermodule.busines.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.adapters.BookCardData

class ListBookRepository() : BaseRepository<BookCardData>() {



   fun loadBooksFromCache(){
       CoroutineScope(Dispatchers.IO).launch{



       }
   }


    fun loadBooksFromDownload(){


    }

    fun loadBooksFromDocuments(){


    }


}