package ru.reader.viewpagermodule.busines.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

abstract class BaseRepository<T>() {

    val liveData: MutableLiveData<T> by lazy {
        MutableLiveData()
    }


    val dataEmitter : Flow<T> by lazy { emptyFlow() }


}