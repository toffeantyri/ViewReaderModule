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
import androidx.fragment.app.viewModels
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.BookBodyData
import ru.reader.viewpagermodule.paginatedtextview.pagination.ReadState
import ru.reader.viewpagermodule.paginatedtextview.view.OnActionListener
import ru.reader.viewpagermodule.paginatedtextview.view.OnSwipeListener
import ru.reader.viewpagermodule.paginatedtextview.view.PaginatedTextView
import ru.reader.viewpagermodule.viewmodels.ViewPagerViewModelSingleton
import java.io.InputStream
import java.lang.StringBuilder

const val BOOK_BUNDLE_CHAPTER = "BOOK_BUNDLE_CHAPTER_STATE"

class ChapterPagerFragment : Fragment(), OnSwipeListener, OnActionListener {

    companion object {
        fun newInstance(bookBodyData: BookBodyData): Fragment = ChapterPagerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BOOK_BUNDLE_CHAPTER, bookBodyData)
            }
        }
    }

    private lateinit var viewModelSingleton : ViewPagerViewModelSingleton


    @BindView(R.id.tv_book_name_panel)
    lateinit var tvNameBook: TextView

    @BindView(R.id.tv_pages_read_panel)
    lateinit var tvPage: TextView

    @BindView(R.id.tv_percent_read_panel)
    lateinit var tvPercent: TextView

    @BindView(R.id.ptv_book_text)
    lateinit var tvBookContent: PaginatedTextView

    private lateinit var parentActivity: MainActivity

    private var durationAnimationBySwipe = 150L
    private lateinit var inRight: Animation
    private lateinit var inLeft: Animation

    private var pageNum = 1



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_chapter_page, container, false)
        ButterKnife.bind(this, view0)
        viewModelSingleton = ViewPagerViewModelSingleton.getInstanceOfVM()
        val arg = arguments?.getSerializable(BOOK_BUNDLE_CHAPTER) as BookBodyData?
        Log.d("MyLog", "ChapterPagerFragment : onCreateView ars : $arg")
        arg?.let {
            tvNameBook.text = "test"//it.chapterName
            pageNum = 1 //it.currentPage
        }

        setupAnimationRightLeft()
        tvBookContent.setOnActionListener(this)
        tvBookContent.setOnSwipeListener(this)

        viewModelSingleton.dataTest.observe(viewLifecycleOwner){
            Log.d("MyLog", "VP: $it")
        }

        startCount()

        return view0
    }

    private fun startCount() {
        CoroutineScope(Dispatchers.Main).launch {

            for (i in 0..10) {
                delay(1000)
                viewModelSingleton.dataTest.value = i
            }

        }
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


}
