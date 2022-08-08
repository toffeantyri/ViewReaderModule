package ru.reader.viewpagermodule.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.reader.viewpagermodule.paginatedtextview.pagination.BookBodyData
import ru.reader.viewpagermodule.view.screens.ChapterPagerFragment

class ViewPagerChapterAdapter(val fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private var chapters: ArrayList<BookBodyData> = arrayListOf()

    override fun getItemCount(): Int = chapters.size

    override fun createFragment(position: Int): Fragment {
        return ChapterPagerFragment.newInstance(chapters[position])
    }

}