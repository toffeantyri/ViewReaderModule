package ru.reader.viewpagermodule.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.reader.viewpagermodule.R

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.BookNameHolder>() {

    private var bookList = listOf<BookCardData>()


    inner class BookNameHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameBook: TextView = view.findViewById(R.id.tv_name_book)
        val author : TextView = view.findViewById(R.id.tv_author_book)
        val imageBook: ImageView = view.findViewById(R.id.iv_book)

        fun bind(position: Int) {
            nameBook.text = bookList[position].nameBook
            author.text = bookList[position].author
            //todo set image
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


    fun fillAdapter(list: List<BookCardData>) {
        bookList = list
        notifyDataSetChanged()
    }

}