package ru.reader.viewpagermodule.paginatedtextview.pagination


data class PageState(
        val currentIndex: Int,
        val pagesCount: Int,
        val readPercent: Float,
        val pageText: CharSequence
)