package ru.reader.viewpagermodule.busines.repository

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.reader.viewpagermodule.APP_CONTEXT

class LoadBookRepository : BaseRepository<LoadingBookState>() {

    fun loadBook(listUrl: List<String>, onSuccess: () -> Unit, onFail: () -> Unit){
        val constrains = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val downloadRequest =
            OneTimeWorkRequestBuilder<DownloadBooksHelper>().setConstraints(constrains).build()

        WorkManager.getInstance(APP_CONTEXT)
            .enqueue(downloadRequest)

        onSuccess()

    }
}

enum class LoadingBookState{
    LOADING,
    SUCCESS_LOAD,
    LOAD_FAIL,
    IDLE_LOAD
}