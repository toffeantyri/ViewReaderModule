package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.busines.repository.ListBookRepository
import ru.reader.viewpagermodule.busines.repository.ListBooksRepository

class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    //for get list items
    private val repo by lazy { ListBooksRepository() }

    //for get single item
    private val repo2 by lazy { ListBookRepository() }

    val data = repo.liveData


    val data2: MutableLiveData<BookCardData> by lazy {
        MutableLiveData()
    }

    fun getBooks(onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            repo.loadListBooks(
                { onSuccess() }, { onFail() }
            )
        }
    }


    fun getBookFlow(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            repo2.dataEmitter.collect {
                data2.value = it
                //todo test?
            }

        }
    }


}