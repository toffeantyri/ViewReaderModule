package ru.reader.viewpagermodule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.busines.repository.ListBookRepository
import ru.reader.viewpagermodule.busines.repository.LoadBookRepository


class ViewModelMainActivity(app: Application) : AndroidViewModel(app) {

    //for get list items
    private val repo by lazy { ListBookRepository() }
    private val loadRepo by lazy { LoadBookRepository() }


    val dataListBook: MutableLiveData<HashSet<BookCardData>> by lazy {
        MutableLiveData()
    }

    init {
        dataListBook.value = hashSetOf()
    }


    fun getBooks(onSuccess: () -> Unit, onSuccessStep: () -> Unit) {
        repo.dataEmitter.subscribe {
            //Log.d("MyLog", "VM : " + it.fileName)
            if (dataListBook.value?.contains(it) == false) {
                //Log.d("MyLog", "VM add : " + it.fileName)
                dataListBook.value?.add(it)
            }
        }
        repo.loadDownloadedBooksOrListWithEmptyBooksForDownload() { onSuccessStep() }

        repo.loadListBooks(
            { onSuccess() }, {
                onSuccessStep()
            }
        )
    }

    fun loadBookByUrl(listUrl: List<String>, onSuccess: () -> Unit, onFail: () -> Unit) {
        loadRepo.loadBook(listUrl, onSuccess, onFail)
    }


}



