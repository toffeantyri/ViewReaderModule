package ru.reader.viewpagermodule.view.adapters

/**
 * ru.mamykin.widget:paginatedtextview:0.1.1
 * */

import java.io.Serializable
import java.nio.file.Path

data class BookStateForBundle(
    val bookName: String,
    val tagName: String,
    val absolutePath : String,
    val chapterIndex : Int,
    val pageNum : Int
) : Serializable