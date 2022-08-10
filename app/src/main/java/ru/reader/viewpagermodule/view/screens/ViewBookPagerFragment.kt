package ru.reader.viewpagermodule.view.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.android.synthetic.main.fragment_view_book_pager.*
import kotlinx.coroutines.*

import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.BookStateForBundle
import ru.reader.viewpagermodule.view.adapters.ViewPagerChapterAdapter
import ru.reader.viewpagermodule.view.parceradapter.ParcelAdapter
import ru.reader.viewpagermodule.viewmodels.ViewPagerViewModel
import java.lang.Exception

const val BOOK_BUNDLE = "BOOK_BUNDLE_STATE"

class ViewBookPagerFragment : Fragment() {

    private val viewModel: ViewPagerViewModel by viewModels()

    @BindView(R.id.vp_chapter)
    lateinit var viewPager: ViewPager2

    @BindView(R.id.progress_load_v_pager)
    lateinit var loadingBar: SpinKitView

    private lateinit var adapterVp: ViewPagerChapterAdapter

    private lateinit var parentActivity: MainActivity

    private var chapterIndex = 0
    private var pageNum = 1
    private var filePath = ""
    private var countOfChapters = 10


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MyLog", "ViewPagerBook : onCreateView")
        val view0 = inflater.inflate(R.layout.fragment_view_book_pager, container, false)
        ButterKnife.bind(this, view0)

        val arg = arguments?.getSerializable(BOOK_BUNDLE) as BookStateForBundle?
        arg?.let {
            chapterIndex = it.chapterIndex
            pageNum = it.pageNum
            filePath = it.absolutePath
            countOfChapters = 10 //todo
        }
        loadingBar.isVisible = true


        return view0
    }


    override fun onStart() {
        CoroutineScope(Dispatchers.Default).launch {
            setupViewPagerAdapter(filePath)
        }
        parentActivity.supportActionBar?.hide()
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        parentActivity = activity as MainActivity
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        parentActivity.supportActionBar?.show()
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    private suspend fun setupViewPagerAdapter(filePath: String) {
        adapterVp = ViewPagerChapterAdapter(childFragmentManager, lifecycle)
        val pAdapter = ParcelAdapter()
        pAdapter.setFb2File(filePath)

        viewPager.currentItem = chapterIndex
        adapterVp.createEmptyListWithSize(countOfChapters)

        withContext(Dispatchers.Main) {
            viewPager.adapter = adapterVp
            loadingBar.isVisible = false
        }


        viewPager.isUserInputEnabled = false
        viewPager.requestDisallowInterceptTouchEvent(false)



        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                Log.d("MyLog", "onPageScrollState state: $state")
                viewPager.isUserInputEnabled = chapterIndex == state
            }
        })

        Log.d("MyLog", "ViewPagerBook : end setup viewPager")

        //todo update adapter
    }

    fun getParentViewModel(): ViewPagerViewModel = viewModel


}

enum class SwipeDirection {
    ALL,
    RIGHTTOLEFT,
    LEFTTORIGHT,
    NONE
}
