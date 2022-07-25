package ru.reader.viewpagermodule.adapters

data class BookCardData(
    val author: String,
    val nameBook: String,
    val imageValue: String,
    val fileFullPath : String,
    val byWay : MemoryLocation,
    var isFavorite: Boolean = false
)
