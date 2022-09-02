package ru.reader.viewpagermodule.view.main_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.util.convertToBitmap
import ru.reader.viewpagermodule.view.adapters.models.BookCardData
import ru.reader.viewpagermodule.view.models.LoadBookData

class MainAdapter : RecyclerView.Adapter<MainAdapter.ProductHolder>() {

    private var arrayList = mutableListOf<ProductHolder>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductHolder {
        val inflater = LayoutInflater.from(p0.context)
            .inflate(R.layout.item_favorite_filter, p0, false)
        return ProductHolder(inflater)
    }

    override fun onBindViewHolder(p0: ProductHolder, p1: Int) {
        p0.bind(p1)
    }

    override fun getItemCount(): Int = arrayList.size



//    fun fillAdapter(list: ArrayList<ProductDto>) {
//        arrayList = list
//        notifyDataSetChanged()
//    }


    private fun View.setAnimationInsert() {
        this.startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 700 })
        this.alpha = 1f
    }

    fun clearAdapterData() {
        arrayList.clear()
        notifyDataSetChanged()
    }

    inner class ProductHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(pos: Int) {

        }
    }

}