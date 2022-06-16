package ru.reader.viewpagermodule.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_list.*
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.adapters.BookCardData
import ru.reader.viewpagermodule.adapters.BookListAdapter
import ru.reader.viewpagermodule.getListBookFromAssetByName
import ru.reader.viewpagermodule.getListFB2NameFromAsset
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

const val BOOK_NAME = "book_name"

class ListFragment : Fragment() {


    val viewModel: ViewModelMainActivity by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()

    }


    lateinit var adapter: BookListAdapter

    lateinit var listOfBook: List<BookCardData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_list, container, false)

        return view0
    }

    override fun onStart() {
        super.onStart()
        initRv()
        val list = getListFB2NameFromAsset(this.context!!)
        //list.forEach { Log.d("MyLog", "mainActivity, name $it") }
        //val listBook = getListBookFromAsset(this.context!!, list)
        //listBook.forEach { Log.d("MyLog", "MA: BOOK : author : ${it.author} nameBook : ${it.nameBook}") }

        adapter.fillAdapter(getListBookFromAssetByName(this.context!!, list))
    }

    private fun initRv() {
        adapter = BookListAdapter()
        adapter.itemBookClickListener = itemClickListener
        list_rv.adapter = adapter
        list_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

    }

    private val itemClickListener = object : BookListAdapter.ItemBookClickListener {
        override fun clickOpenBook(fileName: String) {
            Toast.makeText(this@ListFragment.context, fileName, Toast.LENGTH_SHORT).show()
        }
    }


}

