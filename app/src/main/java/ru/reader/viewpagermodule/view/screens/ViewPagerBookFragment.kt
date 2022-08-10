package ru.reader.viewpagermodule.view.screens


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.pageadapter.ViewPagerAdapter

class ViewPagerBookFragment : Fragment() {

    @BindView(R.id.rv_pager)
    lateinit var rvPager: RecyclerView
    private lateinit var vpAdapter: ViewPagerAdapter

    private lateinit var parentActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_custom_view_pager, container, false)
        ButterKnife.bind(this, view0)
        parentActivity = activity as MainActivity
        setupViewPager()

        return view0
    }

    private fun setupViewPager() {
        vpAdapter = ViewPagerAdapter()

        rvPager.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        rvPager.adapter = vpAdapter
        PagerSnapHelper().attachToRecyclerView(rvPager)


        val colors = listOf<Int>(
            R.color.blue,
            R.color.green,
            R.color.red,
            R.color.black,
            R.color.white,
        )
        vpAdapter.updateAdapter(colors)

    }


    override fun onStart() {
        parentActivity.supportActionBar?.hide()
        super.onStart()
    }

    override fun onDestroy() {
        parentActivity.supportActionBar?.show()
        super.onDestroy()
    }


}
