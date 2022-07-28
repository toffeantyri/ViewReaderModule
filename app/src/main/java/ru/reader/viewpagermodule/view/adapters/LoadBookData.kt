package ru.reader.viewpagermodule.view.adapters

import java.io.Serializable

data class LoadBookData(
    val nameBook: String,
    val absolutePath: String,
    val listOfUrls: ArrayList<String>
) : Serializable