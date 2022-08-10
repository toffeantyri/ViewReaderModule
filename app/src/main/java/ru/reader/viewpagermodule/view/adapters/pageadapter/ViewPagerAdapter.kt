package ru.reader.viewpagermodule.view.adapters.pageadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import ru.reader.viewpagermodule.R

class ViewPagerAdapter : BasePagerRecyclerViewAdapter<ViewPagerAdapter.TextHolder>() {

    val list = arrayListOf<Int>()


    class TextHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.item_bg)
        lateinit var bg: FrameLayout

        fun setData(color: Int) {
            bg.setBackgroundColor(ContextCompat.getColor(view.context, color))
        }

        init {
            ButterKnife.bind(this, view)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TextHolder {
        return TextHolder(LayoutInflater.from(p0.context).inflate(R.layout.custom_color_item, p0, false))
    }

    override fun onBindViewHolder(holder: TextHolder, pos: Int) {
        holder.setData(list[pos])
    }

    override fun getItemCount(): Int = list.size


    fun updateAdapter(newList: List<Int>) {
        list.addAll(newList)
        notifyDataSetChanged()
    }


}