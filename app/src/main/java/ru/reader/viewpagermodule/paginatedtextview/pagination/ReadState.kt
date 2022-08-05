package ru.reader.viewpagermodule.paginatedtextview.pagination

/**
 * ru.mamykin.widget:paginatedtextview:0.1.1
 * */

/**
 * A data structure, who represents the state of control:
 * - current page index
 * - total pages count
 * - read percentage
 * - text of current page
 */
data class ReadState(
        val currentIndex: Int,
        val pagesCount: Int,
        val readPercent: Float,
        val pageText: CharSequence
)