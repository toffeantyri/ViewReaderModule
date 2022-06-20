package ru.reader.viewpagermodule.busines.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

abstract class BaseRepository<T>() {

    /** горячие потоки? */
    //как RX BehaviorSubject
    @ObsoleteCoroutinesApi
    val emitter: ConflatedBroadcastChannel<T> = ConflatedBroadcastChannel()

    val liveData: LiveData<T> by lazy {
        MutableLiveData()
    }

    //как RX PublisheSubject  (emit last single value)
    // val channel : BroadcastChannel<T> = BroadcastChannel(1)


    //RX
//    //обьект Observer и Observable подписываемся на него в mainRepo enable
//    val dataEmitter: BehaviorSubject<T> = BehaviorSubject.create()
//    //если надо разное поведение в зависимости откуда получен ответ тогда много BehaviorSubj сделать


}