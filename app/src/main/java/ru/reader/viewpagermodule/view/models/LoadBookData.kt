package ru.reader.viewpagermodule.view.models

import java.io.Serializable

data class LoadBookData(
    val defaultNameBook: String,
    val absolutePath: String,
    val listOfUrls: List<String>
) : Serializable {

    companion object {
        fun emtpyIntance(): LoadBookData = LoadBookData("", "", emptyList())
    }
}