package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.reader.viewpagermodule.busines.repository.ListBookRepository

class ViewModelMainActivity(app : Application) : AndroidViewModel(app) {

    private val repo by lazy { ListBookRepository() }

    val data = repo.liveData



    fun getBooks(onSuccess : () -> Unit){
        repo.loadBooksFromCache()


    }



}