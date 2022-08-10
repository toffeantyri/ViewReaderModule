package ru.reader.viewpagermodule.view.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.reader.viewpagermodule.view.screens.ChapterPagerFragment

class ViewPagerChapterAdapter(val fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private var chapters: MutableList<BookBodyData> = mutableListOf()

    override fun getItemCount(): Int = chapters.size

    override fun createFragment(position: Int): Fragment {
        return ChapterPagerFragment.newInstance(chapters[position])
    }

    fun createEmptyListWithSize(size: Int) {
        for (i in 0 until size) {
            chapters.add(BookBodyData.getEmptyData())
        }
        Log.d("MyLog", "ADAPTER list size : ${chapters.size}")
    }

    fun updateAdapter(pos: Int, bookBodyData: BookBodyData) {
        //todo
    }



}