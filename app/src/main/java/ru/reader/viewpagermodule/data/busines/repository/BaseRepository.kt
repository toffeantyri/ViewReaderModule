package ru.reader.viewpagermodule.data.busines.repository

import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class BaseRepository<T>() {

    val dataEmitter: BehaviorSubject<T> by lazy { BehaviorSubject.create() }

    var stateEmitter: BehaviorSubject<T> = BehaviorSubject.create()

}