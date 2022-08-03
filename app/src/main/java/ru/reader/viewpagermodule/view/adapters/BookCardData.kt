package ru.reader.viewpagermodule.view.adapters

data class BookCardData(
    val tagName: String,
) {
    var isLoading: Boolean = false
    lateinit var author: String
    lateinit var nameBook: String
    lateinit var imageValue: String
    lateinit var fileFullPath: String
    var urlForLoad: List<String> = emptyList()
    var isFavorite: Boolean = false

    fun copyAllField() = BookCardData(this@BookCardData.tagName).apply {
        isLoading = this@BookCardData.isLoading
        author = this@BookCardData.author
        nameBook = this@BookCardData.nameBook
        imageValue = this@BookCardData.imageValue
        fileFullPath = this@BookCardData.fileFullPath
        urlForLoad = this@BookCardData.urlForLoad
        isFavorite = this@BookCardData.isFavorite
    }

}


