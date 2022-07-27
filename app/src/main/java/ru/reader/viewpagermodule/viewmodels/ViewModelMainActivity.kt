package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.data.api.ApiProvider
import ru.reader.viewpagermodule.view.adapters.BookCardData
import ru.reader.viewpagermodule.data.busines.repository.ListBookRepository
import ru.reader.viewpagermodule.data.busines.repository.LoadBookRepository
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.screens.listfragment.ByMemoryState


class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    private val repo by lazy { ListBookRepository() }
    private val loadRepo by lazy { LoadBookRepository() }
    private var job: Job? = null

    val dataListBook: MutableLiveData<ArrayList<BookCardData>> by lazy {
        MutableLiveData()
    }

    val fromMemoryState: MutableLiveData<ByMemoryState> = MutableLiveData()


    init {
        dataListBook.value = arrayListOf()
        fromMemoryState.value = ByMemoryState.FROM_DOWNLOAD //todo Изменить по умолчанию на память для загрузки
    }

    fun setMemoryState(memoryState: ByMemoryState) {
        fromMemoryState.value = memoryState
    }


    fun getPreloadBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repo.dataEmitter.subscribe {
                    if (it.author != BookListHelper.DUMMY_BOOK) {
                        dataListBook.value?.add(it)
                    }
                }
                repo.loadDownloadedBooksOrListWithEmptyBooksForDownload(onSuccess, onSuccessStep)
            }
        }
    }

    fun getBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repo.dataEmitter.subscribe {
                    if (dataListBook.value?.contains(it) == false && it.author != BookListHelper.DUMMY_BOOK) {
                        dataListBook.value?.add(it)
                    }
                }
                repo.loadListBooks(onSuccess, onSuccessStep)
            }
        }
    }

    fun clearBookList() {
        dataListBook.value = arrayListOf()
    }


    fun loadBookByUrl(listUrl: ArrayList<String>, onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
            loadRepo.loadBook(listUrl, onSuccess, onFail)
        }
    }


}



