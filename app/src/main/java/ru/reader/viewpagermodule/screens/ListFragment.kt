package ru.reader.viewpagermodule.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import ru.reader.viewpagermodule.*
import ru.reader.viewpagermodule.adapters.BookListAdapter
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

const val BOOK_NAME = "book_name"

class ListFragment : Fragment() {


    private val viewModel: ViewModelMainActivity by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()

    }

    lateinit var adapter: BookListAdapter
    lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_list, container, false)
        parentActivity = activity as MainActivity
        return view0
    }

    override fun onStart() {
        super.onStart()
        initRv()

        viewModel.data.observe(viewLifecycleOwner) {
            //it.forEach { Log.d("MyLog", " ListFrag observe" + it.nameBook) }
            adapter.fillAdapter(it)
        }


        if (viewModel.data.value.isNullOrEmpty()) {
            setLoadingBooks(true)
            viewModel.getBooks({
                setLoadingBooks(false)
            }, {
                viewModel.data.observe(viewLifecycleOwner) {
                    //it.forEach { Log.d("MyLog", " ListFrag observe" + it.nameBook) }
                    adapter.fillAdapter(it)
                }
            })
        }


    }


    private fun setLoadingBooks(flag: Boolean) {
        progress_bar_rv.visibility = if (flag) View.VISIBLE else View.GONE
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
            //todo open book by name
        }
    }


}

