package ru.reader.viewpagermodule.data.busines.repository

class LoadBookRepository : BaseRepository<LoadingBookState>() {

    suspend fun loadBook(listUrl: List<String>, onSuccess: () -> Unit, onFail: () -> Unit){

        onSuccess()

    }
}

enum class LoadingBookState{
    LOADING,
    SUCCESS_LOAD,
    LOAD_FAIL,
    IDLE_LOAD
}