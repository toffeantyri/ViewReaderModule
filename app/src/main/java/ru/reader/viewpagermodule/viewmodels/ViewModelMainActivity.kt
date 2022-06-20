package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.reader.viewpagermodule.busines.repository.ListBookRepository

class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    private val repo by lazy { ListBookRepository() }

    val data = repo.liveData

    fun getBooks(onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            repo.loadListBooks(
                { onSuccess() }, { onFail() }
            )
        }
    }
}