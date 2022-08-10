package ru.reader.viewpagermodule.paginatedtextview.view

/**
 * ru.mamykin.widget:paginatedtextview:0.1.1
 * */

import android.content.Context
import android.graphics.Color
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import ru.reader.viewpagermodule.paginatedtextview.pagination.PageState
import ru.reader.viewpagermodule.paginatedtextview.pagination.PaginationController


class PaginatedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {


    private val textPaint = TextPaint(paint)
    private lateinit var controller: PaginationController
    private var isMeasured = false

    init {
        initPaginatedTextView()
    }

    override fun scrollTo(x: Int, y: Int) {}

    /**
     * Setup the TextView
     * @param text text to set
     */
    fun setup(text: CharSequence, pageIndex: Int) {
        if (isMeasured) {
            loadSelectedPage(text, pageIndex)
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    isMeasured = true
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    loadSelectedPage(text, pageIndex)
                }
            })
        }
    }

    private fun initPaginatedTextView() {
        //movementMethod = SwipeableMovementMethod()
        highlightColor = Color.TRANSPARENT
    }

    private fun setPageState(pageState: PageState) {
        this.text = pageState.pageText
        //actionListener?.onPageLoaded(pageState)
        //updateWordsSpannables()
    }


    private fun loadSelectedPage(text: CharSequence, pageIndex: Int) {
        val effectWidth = width - (paddingLeft + paddingRight)
        val effectHeight = height - (paddingTop + paddingBottom)
        controller = PaginationController(
            text,
            effectWidth,
            effectHeight,
            textPaint,
            lineSpacingMultiplier,
            lineSpacingExtra
        )
        controller.setCurrentPage(pageIndex)
        setPageState(controller.getCurrentPage())
    }
}
