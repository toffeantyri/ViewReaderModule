package ru.reader.viewpagermodule.view.models

import java.io.Serializable
import java.nio.file.Path

data class BookStateForBundle(
    val bookName: String,
    val tagName: String,
    val absolutePath : String,
    val chapterIndex : Int,
    val pageNum : Int
) : Serializable