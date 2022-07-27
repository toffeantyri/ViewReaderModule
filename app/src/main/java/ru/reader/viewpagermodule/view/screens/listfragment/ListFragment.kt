package ru.reader.viewpagermodule.view.screens.listfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.BookListAdapter
import ru.reader.viewpagermodule.helpers.DialogHelper
import ru.reader.viewpagermodule.view.screens.MainActivity
import ru.reader.viewpagermodule.view.util.MyViewAnimator
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

class ListFragment : Fragment(), BookListAdapter.ItemBookClickListener {


    private val viewModel: ViewModelMainActivity by activityViewModels()

    private lateinit var btnChangeMemory: ExtendedFloatingActionButton

    private lateinit var adapter: BookListAdapter
    private lateinit var parentActivity: MainActivity
    private val animator = MyViewAnimator()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_list, container, false)
        parentActivity = activity as MainActivity
        btnChangeMemory = view0.btn_choose_memory
        return view0
    }

    override fun onStart() {
        super.onStart()
        initRv()

        viewModel.dataListBook.observe(viewLifecycleOwner) {
            //it.forEach { Log.d("MyLog", " ListFrag observe" + it.nameBook) }
            adapter.fillAdapter(it)
        }

        viewModel.fromMemoryState.observe(viewLifecycleOwner) { state ->
            Log.d("MyLog", "New state : $state")
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            btnChangeMemory.text = when (state) {
                ByMemoryState.FROM_DOWNLOAD -> getString(R.string.choose_memory_storage)
                ByMemoryState.FROM_DEVICE -> getString(R.string.choose_memory_download)
            }
            btnChooseSetClickListener(state)
            adapter.clearAdapterData()
            viewModel.clearBookList()
            if (state == ByMemoryState.FROM_DOWNLOAD) {
                if (viewModel.dataListBook.value.isNullOrEmpty()) {
                    setLoadingBooks(true)
                    viewModel.getPreloadBooks({ setLoadingBooks(false) }) {
                        viewModel.dataListBook.observe(viewLifecycleOwner) {
                            adapter.fillAdapter(it)
                        }
                    }
                }
            } else if (state == ByMemoryState.FROM_DEVICE) {
                if (viewModel.dataListBook.value.isNullOrEmpty()) {
                    setLoadingBooks(true)
                    viewModel.getBooks({ setLoadingBooks(false) }) {
                        viewModel.dataListBook.observe(viewLifecycleOwner) {
                            adapter.fillAdapter(it)
                        }
                    }
                }
            }


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
        list_rv.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) animator.run { btnChangeMemory.translationDownByY(false) }
            else animator.run { btnChangeMemory.translationDownByY(true) }
        }
    }

    override fun clickOpenBook(filePath: String, urlBook: ArrayList<String>) {
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

    private fun btnChooseSetClickListener(state: ByMemoryState) {
        btnChangeMemory.setOnClickListener {
            val newState = when (state) {
                ByMemoryState.FROM_DEVICE -> ByMemoryState.FROM_DOWNLOAD
                ByMemoryState.FROM_DOWNLOAD -> ByMemoryState.FROM_DEVICE
            }
            viewModel.setMemoryState(newState)
        }
    }


}

