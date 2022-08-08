package ru.reader.viewpagermodule.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife

import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.paginatedtextview.pagination.BookStateForBundle
import ru.reader.viewpagermodule.view.adapters.ViewPagerChapterAdapter
import ru.reader.viewpagermodule.view.parceradapter.ParcerAdapter


class ViewBookPager : Fragment() {


    @BindView(R.id.vp_chapter)
    lateinit var viewPager: ViewPager2

    private lateinit var adapterVp: ViewPagerChapterAdapter

    private var chapterIndex = 0
    private var pageNum = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_view_book_pager, container, false)
        ButterKnife.bind(this, view0)
        val arg = arguments?.getSerializable(BOOK_BUNDLE) as BookStateForBundle?
        arg?.let {
            chapterIndex = it.chapterIndex
            pageNum = it.pageNum
            setupViewPagerAdapter(it.absolutePath)
        }

        return view0
    }


    private fun setupViewPagerAdapter(filePath: String) {
        adapterVp = ViewPagerChapterAdapter(childFragmentManager, lifecycle)
        ParcerAdapter.setFb2File(filePath)
        //todo update adapter
        viewPager.adapter = adapterVp
        viewPager.currentItem = chapterIndex

    }


}
