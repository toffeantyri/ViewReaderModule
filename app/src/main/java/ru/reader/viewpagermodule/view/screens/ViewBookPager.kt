package ru.reader.viewpagermodule.view.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.paginatedtextview.pagination.BookStateForBundle
import ru.reader.viewpagermodule.view.adapters.ViewPagerChapterAdapter
import ru.reader.viewpagermodule.view.parceradapter.ParcelAdapter

const val BOOK_BUNDLE = "BOOK_BUNDLE_STATE"

class ViewBookPager : Fragment() {


    @BindView(R.id.vp_chapter)
    lateinit var viewPager: ViewPager2

    @BindView(R.id.progress_load_v_pager)
    lateinit var loadingBar : SpinKitView

    private lateinit var adapterVp: ViewPagerChapterAdapter

    private var chapterIndex = 0
    private var pageNum = 1
    private var filePath = ""


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
        }
        loadingBar.isVisible = true
        return view0
    }

    override fun onStart() {
        CoroutineScope(Dispatchers.Default).launch {
            setupViewPagerAdapter(filePath)
        }
        super.onStart()
    }


    private suspend fun setupViewPagerAdapter(filePath: String) {
        adapterVp = ViewPagerChapterAdapter(childFragmentManager, lifecycle)
        val pAdapter = ParcelAdapter()
        pAdapter.setFb2File(filePath)
        viewPager.currentItem = chapterIndex
        withContext(Dispatchers.Main) {
            viewPager.adapter = adapterVp
            loadingBar.isVisible = false
        }
        Log.d("MyLog", "ViewPagerBook : end setup viewPager")

        //todo update adapter
    }


}
