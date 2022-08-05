package ru.reader.viewpagermodule.view.screens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import butterknife.BindView
import butterknife.ButterKnife
import com.kursx.parser.fb2.FictionBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.paginatedtextview.pagination.BookStateForBundle
import ru.reader.viewpagermodule.paginatedtextview.pagination.ReadState
import ru.reader.viewpagermodule.paginatedtextview.view.OnActionListener
import ru.reader.viewpagermodule.paginatedtextview.view.OnSwipeListener
import ru.reader.viewpagermodule.paginatedtextview.view.PaginatedTextView
import java.io.File
import java.io.InputStream
import java.lang.StringBuilder

const val BOOK_BUNDLE = "BOOK_BUNDLE_STATE"

class BookPagerFragment : Fragment(), OnSwipeListener, OnActionListener {

    @BindView(R.id.tv_book_name_panel)
    lateinit var tvNameBook: TextView

    @BindView(R.id.tv_pages_read_panel)
    lateinit var tvPage: TextView

    @BindView(R.id.tv_percent_read_panel)
    lateinit var tvPercent: TextView

    @BindView(R.id.ptv_book_text)
    lateinit var tvBookContent: PaginatedTextView

    private var pageNum = 1
    private val bh = BookListHelper()
    private var currentFullText: String = ""

    var durationAnimationBySwipe = 150L
    private lateinit var inRight: Animation
    private lateinit var inLeft: Animation

    private lateinit var parentActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity
        lockRotationPortrait(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentActivity.supportActionBar?.show()
        lockRotationPortrait(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_book_pager, container, false)
        ButterKnife.bind(this, view0)
        val arg = arguments?.getSerializable(BOOK_BUNDLE) as BookStateForBundle?
        arg?.let {
            pageNum = it.pageIndex
            tvNameBook.text = it.bookName


            tvBookContent.setup(getDataByFb2(it), pageNum)


        }

        setupAnimationRightLeft()
        tvBookContent.setOnActionListener(this)
        tvBookContent.setOnSwipeListener(this)

        return view0
    }

    override fun onStart() {
        parentActivity.supportActionBar?.hide()
        super.onStart()
    }

    override fun onSwipeLeft() {
        tvBookContent.startAnimation(inLeft)
    }

    override fun onSwipeRight() {
        tvBookContent.startAnimation(inRight)
    }

    override fun onClick(paragraph: String) {
    }

    override fun onLongClick(word: String) {
    }

    override fun onPageLoaded(state: ReadState) {
        displayReadState(state)
    }

    private fun getText(inputStream: InputStream): String {
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        return String(bytes)
    }

    @SuppressLint("SetTextI18n")
    private fun displayReadState(readState: ReadState) {
        tvPage.text = StringBuilder()
            .append("${readState.currentIndex}")
            .append("/")
            .append("${readState.pagesCount}").toString()
        tvPercent.text = readState.readPercent.toInt().toString() + "%"
    }

    private fun lockRotationPortrait(value: Boolean) {
        if (value) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

    private fun setupAnimationRightLeft() {
        inRight = AnimationUtils.loadAnimation(this.context, R.anim.slide_in_right)
        inLeft = AnimationUtils.loadAnimation(this.context, R.anim.slide_in_left)
        inRight.duration = durationAnimationBySwipe
        inLeft.duration = durationAnimationBySwipe
    }

    private fun getDataByFb2(bookState: BookStateForBundle): CharSequence {
        val fb2File = File(bookState.absolutePath)
        val fb2 = FictionBook(fb2File)
        val fullText = fb2.authors[0].fullName

        return fullText
    }
}
