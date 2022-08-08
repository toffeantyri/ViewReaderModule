package ru.reader.viewpagermodule.view.adapters

import java.io.Serializable

data class BookBodyData(
    val chapterName : String,
    val stringBody : String,
    val currentPage : Int
) : Serializable {

    companion object {
        fun getEmptyData() = BookBodyData("","",1)
    }
}