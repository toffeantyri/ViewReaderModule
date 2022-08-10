package ru.reader.viewpagermodule.view.adapters.pageadapter

import android.text.TextPaint
import androidx.recyclerview.widget.RecyclerView
import ru.reader.viewpagermodule.paginatedtextview.pagination.PaginationController

abstract class BasePagerRecyclerViewAdapter<T : RecyclerView.ViewHolder>() : RecyclerView.Adapter<T>() {

    protected var controller: PaginationController? = null
    protected var isMeasured = false

//todo adapter extend this - need fun setTextPaint and setController

}