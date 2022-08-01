package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.view.adapters.BookCardData
import ru.reader.viewpagermodule.data.busines.repository.ListBookRepository
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.adapters.LoadBookData
import ru.reader.viewpagermodule.view.screens.listfragment.ByMemoryState


class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    private val repo by lazy { ListBookRepository() }
    private var job: Job? = null


    private val listBookData: ArrayList<BookCardData> = arrayListOf()
    val dataListBook: MutableLiveData<ArrayList<BookCardData>> by lazy {
        MutableLiveData()
    }

    val fromMemoryState: MutableLiveData<ByMemoryState> = MutableLiveData()


    init {
        dataListBook.value = listBookData
        fromMemoryState.value =
            ByMemoryState.FROM_DOWNLOAD //todo Изменить по умолчанию на память для загрузки
    }

    fun setMemoryState(memoryState: ByMemoryState) {
        fromMemoryState.value = memoryState
    }


    fun getPreloadBooks(onSuccess: () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var subscriber: Disposable? = null
                subscriber = repo.dataEmitter.subscribe { bookItem ->
                    if (bookItem.author != BookListHelper.DUMMY_BOOK) replaceOrAddItem(bookItem)
                }

                repo.getDownloadedBooksOrListWithEmptyBooks(onSuccess = {
                    onSuccess()
                    subscriber.dispose()
                })
            }
        }
    }

    private fun replaceOrAddItem(item: BookCardData) {
        var ind = -1
        listBookData.filterIndexed { index, bookCardData ->
            if (bookCardData.bookNameDefault == item.bookNameDefault) ind = index
            false
        }
        if (ind >= 0) {
            listBookData.setToListLiveData(ind, item)
        } else listBookData.addToListLiveData(item)
    }

    fun getBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val subscriber = repo.dataEmitter.subscribe {
                    if (!listBookData.contains(it) && it.author != BookListHelper.DUMMY_BOOK) {
                        listBookData.addToListLiveData(it)
                    }
                }
                repo.loadListBooks(onSuccess = {
                    onSuccess()
                    subscriber.dispose()
                }, onSuccessStep = onSuccessStep)
            }
        }
    }

    fun clearBookList() {
        listBookData.clear()
        dataListBook.value?.clear()
    }

    private fun ArrayList<BookCardData>.addToListLiveData(item: BookCardData) {
        CoroutineScope(Dispatchers.Main).launch {
            this@addToListLiveData.add(item)
            dataListBook.value = listBookData
        }
    }

    private fun ArrayList<BookCardData>.setToListLiveData(index: Int, item: BookCardData) {
        if (this.size > index) return
        CoroutineScope(Dispatchers.Main).launch {
            this@setToListLiveData[index] = item
            dataListBook.value = listBookData
        }
    }

    fun loadBookByUrl(loadBookData: LoadBookData, onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
            repo.loadBook(loadBookData,
                onSuccess = {
                    getPreloadBooks(onSuccess)
                }, onFail = {
                    getPreloadBooks(onSuccess)
                    onFail()
                })
        }
    }


}



