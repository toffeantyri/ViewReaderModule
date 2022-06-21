package ru.reader.viewpagermodule.busines.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

abstract class BaseRepository<T>() {

    val liveData: MutableLiveData<T> by lazy {
        MutableLiveData()
    }

    val dataEmitter: BehaviorSubject<T> by lazy { BehaviorSubject.create() }

}