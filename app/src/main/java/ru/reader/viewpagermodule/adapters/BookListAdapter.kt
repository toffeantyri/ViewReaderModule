package ru.reader.viewpagermodule.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.convertToBitmap

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.BookNameHolder>() {

    lateinit var itemBookClickListener: ItemBookClickListener
    private var bookList = mutableListOf<BookCardData>()

    inner class BookNameHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameBook: TextView = view.findViewById(R.id.tv_name_book)
        private val author: TextView = view.findViewById(R.id.tv_author_book)
        private val imageBook: ImageView = view.findViewById(R.id.iv_book)
        private val progressBarImage: ProgressBar = view.findViewById(R.id.item_progress_bar_iv)

        fun bind(position: Int) {
            nameBook.text = bookList[position].nameBook
            author.text = bookList[position].author
            if (bookList[position].imageValue.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    progressBarImage.visibility = View.VISIBLE
                    val image = convertToBitmap(bookList[position].imageValue)
                    imageBook.setImageBitmap(image)
                    progressBarImage.visibility = View.GONE
                }
            }
            itemView.setOnClickListener {
                itemBookClickListener.clickOpenBook(bookList[position].fileFullPath)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BookNameHolder {
        val inflater = LayoutInflater.from(p0.context).inflate(R.layout.item_rv_list, p0, false)
        return BookNameHolder(inflater)
    }

    override fun onBindViewHolder(p0: BookNameHolder, p1: Int) {
        p0.bind(p1)
    }

    override fun getItemCount(): Int = bookList.size

    override fun onViewAttachedToWindow(holder: BookNameHolder) {
        holder.itemView.setAnimationInsert()
    }


    fun fillAdapter(list: HashSet<BookCardData>) {
        val startCount = bookList.size
        val bookList2 = bookList.toMutableSet()
        bookList2.addAll(list.toMutableSet())
        bookList = bookList2.toMutableList()
        val endCount = bookList.size
        notifyItemRangeInserted(startCount, endCount - startCount)
    }

    fun fillAdapterSingleItem(item: BookCardData) {
        bookList.add(item)
        notifyItemChanged(bookList.lastIndex)
    }


    interface ItemBookClickListener {
        fun clickOpenBook(filePath: String)
    }


    private fun View.setAnimationInsert() {
        this.startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 900 })
        this.alpha = 1f
    }

}