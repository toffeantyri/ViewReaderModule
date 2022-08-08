package ru.reader.viewpagermodule.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerChapterAdapter(val fm: FragmentManager, private val lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    private var chapters: ArrayList<String> = arrayListOf()

    override fun getItemCount(): Int = chapters.size

    override fun createFragment(position: Int): Fragment {

        TODO("Not yet implemented")
    }

}