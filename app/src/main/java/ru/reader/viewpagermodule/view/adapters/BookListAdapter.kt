package ru.reader.viewpagermodule.view.adapters

import android.util.Log
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

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.BookNameHolder>() {

    lateinit var itemBookClickListener: ItemBookClickListener
    private var bookList = mutableListOf<BookCardData>()

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

    override fun onViewRecycled(holder: BookNameHolder) {
        super.onViewRecycled(holder)
        holder.imageBook.setImageDrawable(null)
        holder.progressBarLoading.visibility = View.GONE
    }

    fun fillAdapter(list: ArrayList<BookCardData>) {
        val startCount = bookList.size
        val tempList = LinkedHashSet(bookList)
        tempList.addAll(list)
        bookList = tempList.toMutableList()
        val endCount = bookList.size
        notifyItemRangeInserted(startCount, endCount - startCount)
    }

    fun updateItemByPos(item: BookCardData, pos: Int) {
        if (bookList.size < pos + 1) return
        if (item.bookNameDefault == bookList[pos].bookNameDefault) {
            bookList[pos] = item
            notifyItemChanged(pos)
        }
    }

    private fun View.setAnimationInsert() {
        this.startAnimation(AlphaAnimation(0.0f, 1.0f).apply { duration = 700 })
        this.alpha = 1f
    }

    fun clearAdapterData() {
        bookList.clear()
        notifyDataSetChanged()
    }

    fun setLoadingByPos(pos: Int, isLoading: Boolean) {
        val updatedItem = bookList[pos].copy(isLoading = isLoading)
        bookList[pos] = updatedItem
        notifyItemChanged(pos)
    }

    interface ItemBookClickListener {
        fun clickOpenBook(loadBookData: LoadBookData, adapterPos: Int)
    }

    inner class BookNameHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameBook: TextView = view.findViewById(R.id.tv_name_book)
        private val author: TextView = view.findViewById(R.id.tv_author_book)
        val imageBook: ImageView = view.findViewById(R.id.iv_book)
        private val progressBarImage: ProgressBar = view.findViewById(R.id.item_progress_bar_iv)
        val progressBarLoading: ProgressBar = view.findViewById(R.id.item_progress_bar_loading)

        fun bind(pos: Int) {
            nameBook.text = bookList[pos].nameBook
            author.text = bookList[pos].author
            if (!bookList[pos].isLoading) {
                progressBarLoading.visibility = View.GONE
                if (bookList[pos].imageValue.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        progressBarImage.visibility = View.VISIBLE
                        val image = convertToBitmap(bookList[pos].imageValue)
                        imageBook.setImageBitmap(image)
                        progressBarImage.visibility = View.GONE
                    }
                }
                itemView.setOnClickListener {
                    val bookListUrl: ArrayList<String> = arrayListOf()
                    bookListUrl.addAll(bookList[pos].urlForLoad)
                    itemBookClickListener.clickOpenBook(
                        LoadBookData(
                            nameBook = bookList[pos].nameBook,
                            absolutePath = bookList[pos].fileFullPath, bookListUrl
                        ),
                        adapterPos = adapterPosition
                    )
                }
            } else {
                imageBook.setImageDrawable(null)
                progressBarLoading.visibility = View.VISIBLE
                itemView.setOnClickListener {
                    Log.d("MyLog", "ЗАГРУЖАЕТСЯ already")
                    val context = itemView.context
                    Toast.makeText(context, context.getString(R.string.toast_loading), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}