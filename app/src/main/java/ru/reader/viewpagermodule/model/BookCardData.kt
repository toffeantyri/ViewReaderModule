package ru.reader.viewpagermodule.model

data class BookCardData(
    val author: String,
    val nameBook: String,
    val Image: Int,
    var isFavorite: Boolean = false
)
