package ru.reader.viewpagermodule.view.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.fragment_book_pager.*
import kotlinx.android.synthetic.main.fragment_book_pager.view.*
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.paginatedtextview.pagination.BookStateForBundle
import ru.reader.viewpagermodule.paginatedtextview.pagination.ReadState
import ru.reader.viewpagermodule.paginatedtextview.view.OnActionListener
import ru.reader.viewpagermodule.paginatedtextview.view.OnSwipeListener
import ru.reader.viewpagermodule.paginatedtextview.view.PaginatedTextView
import java.io.InputStream
import java.lang.StringBuilder


class BookPagerFragment : Fragment(), OnSwipeListener, OnActionListener {

    @BindView(R.id.tv_book_name_panel)
    lateinit var tvNameBook: TextView

    @BindView(R.id.tv_pages_read_panel)
    lateinit var tvPage: TextView

    @BindView(R.id.tv_percent_read_panel)
    lateinit var tvPercent: TextView

    @BindView(R.id.ptv_book_text)
    lateinit var tvBookContent: PaginatedTextView

    private lateinit var parentActivity : MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        parentActivity.supportActionBar?.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_book_pager, container, false)
        ButterKnife.bind(this, view0)
        val arg = arguments?.getSerializable("BOOK") as BookStateForBundle?

        tvNameBook.text = arg?.bookName ?: getString(R.string.unknown_name_book)
        tvBookContent.setup(getText(resources.openRawResource(R.raw.sample_text)))

        tvBookContent.setOnActionListener(this)
        tvBookContent.setOnSwipeListener(this)

        return view0
    }

    override fun onStart() {
        parentActivity.supportActionBar?.hide()
        super.onStart()
    }

    override fun onSwipeLeft() {
    }

    override fun onSwipeRight() {
    }

    override fun onPageLoaded(state: ReadState) {
        displayReadState(state)
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

    private fun displayReadState(readState: ReadState) {
        tvPage.text = StringBuilder()
            .append("${readState.currentIndex}")
            .append("/")
            .append("${readState.pagesCount}").toString()
        tvPercent.text = "${readState.readPercent.toInt()}%"
    }

}
