package ru.reader.viewpagermodule.view.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_book_pager.*
import kotlinx.android.synthetic.main.fragment_book_pager.view.*
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.paginatedtextview.pagination.BookStateForBundle
import ru.reader.viewpagermodule.paginatedtextview.pagination.ReadState
import ru.reader.viewpagermodule.paginatedtextview.view.OnActionListener
import ru.reader.viewpagermodule.paginatedtextview.view.OnSwipeListener
import ru.reader.viewpagermodule.paginatedtextview.view.PaginatedTextView
import java.io.InputStream


class BookPagerFragment : Fragment(), OnSwipeListener, OnActionListener {

    private lateinit var tvNameBook: TextView
    private lateinit var tvPage: TextView
    private lateinit var tvPercent: TextView
    private lateinit var tvBookContent : PaginatedTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_book_pager, container, false)
        val arg = arguments?.getSerializable("BOOK") as BookStateForBundle


        tvBookContent = view0.ptv_book_text
        tvBookContent.setup(getText(resources.openRawResource(R.raw.sample_text)))
        tvBookContent.setOnActionListener(this)
        tvBookContent.setOnSwipeListener(this)
        return view0
    }

    override fun onSwipeLeft() {
    }

    override fun onSwipeRight() {
    }

    override fun onPageLoaded(state: ReadState) {
    }

    override fun onClick(paragraph: String) {
    }

    override fun onLongClick(word: String) {
    }


    private fun getText(inputStream: InputStream): String {
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        return String(bytes)
    }

}
