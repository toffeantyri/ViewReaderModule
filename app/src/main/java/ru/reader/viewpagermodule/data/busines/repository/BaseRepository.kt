package ru.reader.viewpagermodule.data.busines.repository

import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class BaseRepository<T>() {

    val dataEmitter: BehaviorSubject<T> by lazy { createEmitter() }

    private fun createEmitter(): BehaviorSubject<T> = BehaviorSubject.create()

}