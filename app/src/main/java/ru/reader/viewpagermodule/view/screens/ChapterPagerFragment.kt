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
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.BookBodyData
import ru.reader.viewpagermodule.paginatedtextview.pagination.ReadState
import ru.reader.viewpagermodule.paginatedtextview.view.OnActionListener
import ru.reader.viewpagermodule.paginatedtextview.view.OnSwipeListener
import ru.reader.viewpagermodule.paginatedtextview.view.PaginatedTextView
import ru.reader.viewpagermodule.viewmodels.ViewPagerViewModel
import java.io.InputStream
import java.lang.Exception
import java.lang.StringBuilder

const val BOOK_BUNDLE_CHAPTER = "BOOK_BUNDLE_CHAPTER_STATE"

class ChapterPagerFragment : Fragment(), OnSwipeListener, OnActionListener, View.OnTouchListener {

    companion object {
        fun newInstance(bookBodyData: BookBodyData): Fragment = ChapterPagerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BOOK_BUNDLE_CHAPTER, bookBodyData)
            }
        }
    }

    var swipeDirection = SwipeDirection.NONE

    private lateinit var viewModel: ViewPagerViewModel

    @BindView(R.id.tv_book_name_panel)
    lateinit var tvNameBook: TextView

    @BindView(R.id.tv_pages_read_panel)
    lateinit var tvPage: TextView

    @BindView(R.id.tv_percent_read_panel)
    lateinit var tvPercent: TextView

    @BindView(R.id.ptv_book_text)
    lateinit var tvBookContent: PaginatedTextView

    private lateinit var parentActivity: MainActivity
    private lateinit var parFrag: ViewBookPagerFragment

    private var durationAnimationBySwipe = 150L
    private lateinit var inRight: Animation
    private lateinit var inLeft: Animation

    private var pageNum = 1

    lateinit var currentReadState: ReadState


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_chapter_page, container, false)
        ButterKnife.bind(this, view0)
        parFrag = parentFragment as ViewBookPagerFragment
        viewModel = parFrag.getParentViewModel()


        val arg = arguments?.getSerializable(BOOK_BUNDLE_CHAPTER) as BookBodyData?

        arg?.let {
            tvNameBook.text = "test"//it.chapterName
            pageNum = 1 //it.currentPage
        }

        setupAnimationRightLeft()
        tvBookContent.setOnActionListener(this)
        tvBookContent.setOnSwipeListener(this)
        tvBookContent.setOnTouchListener(this)

        Log.d("MyLog", "ChapterPagerFragment : onCreateView")
        return view0
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("MyLog", "ChapterPagerFragment : onViewCreated")
        tvBookContent.setup(getText(resources.openRawResource(R.raw.sample_text)), pageNum)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Log.d("MyLog", "ChapterPagerFragment : onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("MyLog", "ChapterPagerFragment : onResume")
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity
        lockRotationPortrait(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        lockRotationPortrait(false)
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
        Log.d("MyLog", "ChapterPagerFrag : onPageLoaded")
        displayReadState(state)
        currentReadState = state

        swipeDirection = when (currentReadState.currentIndex) {
            currentReadState.pagesCount -> {
                SwipeDirection.RIGHTTOLEFT
            }
            1 -> {
                SwipeDirection.LEFTTORIGHT
            }
            else -> {
                SwipeDirection.NONE
            }
        }

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


    var initialX: Float? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {

            if (event.action == MotionEvent.ACTION_UP) return false
            if (event.action == MotionEvent.ACTION_DOWN) {
                initialX = event.x
                return false
            }

            if (!isSwipeAllowed(event)) {
                Log.d("MyLog", "onTouch P")
                tvBookContent.onTouchEvent(event)
            } else {
                Log.d("MyLog", "onTouch VP")
                parFrag.viewPager.apply {
                        isUserInputEnabled=true
                        onInterceptTouchEvent(event)
                }
            }
        }
        return true
    }


    private fun isSwipeAllowed(event: MotionEvent): Boolean {
        if (swipeDirection == SwipeDirection.ALL) return true
        if (swipeDirection == SwipeDirection.NONE) return false
//        if (event.action == MotionEvent.ACTION_UP) return false
//        if (event.action == MotionEvent.ACTION_DOWN) {
//            initialX = event.x
//            return false
//        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            try {
                val diffX: Float = if (initialX != null) {
                    event.x - initialX!!
                } else 0f
                if (diffX > 0 && swipeDirection == SwipeDirection.RIGHTTOLEFT) {
                    return false
                } else if (diffX < 0 && swipeDirection == SwipeDirection.LEFTTORIGHT) {
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

}
