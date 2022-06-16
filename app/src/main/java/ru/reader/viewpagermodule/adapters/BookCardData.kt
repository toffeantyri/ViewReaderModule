package ru.reader.viewpagermodule.adapters

data class BookCardData(
    val author: String,
    val nameBook: String,
    val imageValue: String,
    val fileName : String,
    var isFavorite: Boolean = false
)
