package ru.reader.viewpagermodule.busines.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

abstract class BaseRepository<T>() {

    val liveData: MutableLiveData<T> by lazy {
        MutableLiveData()
    }


}