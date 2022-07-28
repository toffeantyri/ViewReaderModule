package ru.reader.viewpagermodule.data.busines.repository

import java.io.Serializable


data class LoadingBookStateByName(
    val name: String,
    val state: LoadingBookState
) : Serializable

enum class LoadingBookState {
    LOADING,
    SUCCESS_LOAD,
    LOAD_FAIL,
    IDLE_LOAD,
    STATE_COMPLETE
}