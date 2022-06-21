package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.busines.repository.ListBookRepository
import ru.reader.viewpagermodule.busines.repository.ListBooksRepository

class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    //for get list items
    private val repo by lazy { ListBooksRepository() }

    private val repo2 by lazy { ListBookRepository() }

//    val data = repo.liveData
//
//    fun getBooks(onSuccess: () -> Unit) {
//        viewModelScope.launch(Dispatchers.Main) {
//            repo.loadListBooks { onSuccess() }
//        }
//    }

    val data: MutableLiveData<HashSet<BookCardData>> by lazy {
        MutableLiveData()
    }

    init {
        data.value = hashSetOf()
    }


    fun getBooks(onSuccess: () -> Unit) {
        repo2.dataEmitter.subscribe {
            Log.d("MyLog", "VM : " + it.fileName)
            if (data.value?.contains(it) == false) {
                Log.d("MyLog", "VM add : " + it.fileName)
                data.value?.add(it)
            }
        }
        repo2.loadListBooks() {
            onSuccess()
        }
    }


}



