package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.reader.viewpagermodule.view.adapters.BookCardData
import ru.reader.viewpagermodule.busines.repository.ListBookRepository
import ru.reader.viewpagermodule.busines.repository.LoadBookRepository
import ru.reader.viewpagermodule.view.screens.listfragment.ByMemoryState


class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    private val repo by lazy { ListBookRepository() }
    private val loadRepo by lazy { LoadBookRepository() }

    val dataListBook: MutableLiveData<ArrayList<BookCardData>> by lazy {
        MutableLiveData()
    }

    val fromMemoryState: MutableLiveData<ByMemoryState> = MutableLiveData()


    init {
        dataListBook.value = arrayListOf()
        fromMemoryState.value = ByMemoryState.FROM_DOWNLOAD
    }

    fun setMemoryState(memoryState: ByMemoryState) {
        fromMemoryState.value = memoryState
    }


    fun getPreloadBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        dataListBook.value?.clear()
        repo.dataEmitter.subscribe {
            dataListBook.value?.add(it)
        }
        repo.loadDownloadedBooksOrListWithEmptyBooksForDownload(onSuccess,onSuccessStep)
    }

    fun getBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        repo.dataEmitter.subscribe {
            //Log.d("MyLog", "VM : " + it.fileName)
            if (dataListBook.value?.contains(it) == false) {
                //Log.d("MyLog", "VM add : " + it.fileName)
                dataListBook.value?.add(it)
            }
        }
        repo.loadListBooks(onSuccess,onSuccessStep)
    }


    fun loadBookByUrl(listUrl: List<String>, onSuccess: () -> Unit, onFail: () -> Unit) {
        loadRepo.loadBook(listUrl, onSuccess, onFail)
    }


}



