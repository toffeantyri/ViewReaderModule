package ru.reader.viewpagermodule.paginatedtextview.pagination

/**
 * ru.mamykin.widget:paginatedtextview:0.1.1
 * */

import android.text.TextPaint
import androidx.annotation.RequiresPermission

/**
 * Helper class for work with text pages
 */
class PaginationController(
        text: CharSequence,
        width: Int,
        height: Int,
        paint: TextPaint,
        spacingMult: Float,
        spacingExtra: Float
) {
    private val paginator = Paginator(text, width, height, paint, spacingMult, spacingExtra)

    fun getCurrentPage() = PageState(
            paginator.currentIndex + 1,
            paginator.pagesCount,
            getReadPercent(),
            paginator.getCurrentPage()
    )

    fun getPageByIndex(pageIndex : Int) : PageState {
        setCurrentPage(pageIndex)
        return getCurrentPage()
    }

    fun setCurrentPage(pageIndex : Int) {
        paginator.currentIndex = pageIndex
    }

    private fun getReadPercent(): Float = when (paginator.pagesCount) {
        0 -> 0f
        else -> (paginator.currentIndex + 1) / paginator.pagesCount.toFloat() * 100
    }
}