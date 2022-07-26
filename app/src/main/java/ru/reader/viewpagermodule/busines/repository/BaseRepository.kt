package ru.reader.viewpagermodule.busines.repository

import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class BaseRepository<T>() {

    val dataEmitter: BehaviorSubject<T> by lazy { BehaviorSubject.create() }

}