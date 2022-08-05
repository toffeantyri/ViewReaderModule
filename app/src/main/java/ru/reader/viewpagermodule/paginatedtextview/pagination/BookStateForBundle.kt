package ru.reader.viewpagermodule.paginatedtextview.pagination

import java.io.Serializable
import java.nio.file.Path

data class BookStateForBundle(
    val bookName: String,
    val tagName: String,
    val absolutePath : String,
    val pageIndex : Int
) : Serializable