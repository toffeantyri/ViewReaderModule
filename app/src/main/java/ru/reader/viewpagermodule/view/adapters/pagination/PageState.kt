package ru.reader.viewpagermodule.view.adapters.pagination


data class PageState(
        val currentIndex: Int,
        val pagesCount: Int,
        val readPercent: Float,
        val pageText: CharSequence
)