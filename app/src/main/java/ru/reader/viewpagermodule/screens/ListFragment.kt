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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.*
import ru.reader.viewpagermodule.adapters.BookListAdapter
import ru.reader.viewpagermodule.helpers.DialogHelper
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

const val BOOK_NAME = "book_name"

class ListFragment : Fragment(), BookListAdapter.ItemBookClickListener {


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

        viewModel.dataListBook.observe(viewLifecycleOwner) {
            //it.forEach { Log.d("MyLog", " ListFrag observe" + it.nameBook) }
            adapter.fillAdapter(it)
        }


        if (viewModel.dataListBook.value.isNullOrEmpty()) {
            setLoadingBooks(true)
            viewModel.getBooks({
                setLoadingBooks(false)
            }, {
                viewModel.dataListBook.observe(viewLifecycleOwner) {
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
        adapter.itemBookClickListener = this
        list_rv.adapter = adapter
        list_rv.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }


    override fun clickOpenBook(filePath: String, urlBook: List<String>) {
        if (filePath.isEmpty()) {
            val dialogHelper = DialogHelper()
            CoroutineScope(Dispatchers.Main).launch {
                dialogHelper.createLoadDialog(requireActivity()) {
                    viewModel.loadBookByUrl(
                        urlBook,
                        { Log.d("MyLog", "view: onSuccess $urlBook") },
                        { Log.d("MyLog", "view: onFail $urlBook") })
                }
            }
        } else {
            Toast.makeText(this@ListFragment.context, filePath, Toast.LENGTH_SHORT).show()
            //todo open book
        }
    }
}

