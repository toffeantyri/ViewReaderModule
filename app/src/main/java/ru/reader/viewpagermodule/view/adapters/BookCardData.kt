package ru.reader.viewpagermodule.view.adapters

data class BookCardData(
    val author: String,
    val nameBook: String,
    val imageValue: String,
    val fileFullPath : String,
    val byWay : MemoryLocation,
    val bookNameDefault : String = "",
    val urlForLoad : List<String> = emptyList(),
    var isFavorite: Boolean = false
)
