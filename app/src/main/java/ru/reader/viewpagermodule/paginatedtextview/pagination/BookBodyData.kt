package ru.reader.viewpagermodule.paginatedtextview.pagination

import java.io.Serializable

data class BookBodyData(
    val chapterName : String,
    val stringBody : String,
    val currentPage : Int
) : Serializable