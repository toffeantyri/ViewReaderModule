package ru.reader.viewpagermodule.screens.listfragment

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
import ru.reader.viewpagermodule.adapters.BookListAdapter
import ru.reader.viewpagermodule.helpers.DialogHelper
import ru.reader.viewpagermodule.screens.MainActivity
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

class ListFragment : Fragment(), BookListAdapter.ItemBookClickListener {


    private val viewModel: ViewModelMainActivity by activityViewModels()

    private lateinit var btnChangeMemory: ExtendedFloatingActionButton

    private lateinit var adapter: BookListAdapter
    private lateinit var parentActivity: MainActivity

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
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            btnChangeMemory.text = when (state) {
                ByMemoryState.FROM_DOWNLOAD -> getString(R.string.choose_memory_storage)
                ByMemoryState.FROM_DEVICE -> getString(R.string.choose_memory_download)
            }
            btnChooseSetClickListener(state)

            if (viewModel.dataListBook.value.isNullOrEmpty()) {
                setLoadingBooks(true)
                viewModel.getBooks({
                    setLoadingBooks(false)
                }, {
                    viewModel.dataListBook.observe(viewLifecycleOwner) {
                        adapter.fillAdapter(it)
                    }
                })
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
            if (scrollY > oldScrollY) btnChangeMemory.setVisibilityWithAnimation(false)
            else btnChangeMemory.setVisibilityWithAnimation(true)
        }
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

    private fun btnChooseSetClickListener(state: ByMemoryState) {
        btnChangeMemory.setOnClickListener {
            val newState = when (state) {
                ByMemoryState.FROM_DEVICE -> ByMemoryState.FROM_DOWNLOAD
                ByMemoryState.FROM_DOWNLOAD -> ByMemoryState.FROM_DEVICE
            }
            viewModel.setMemoryState(newState)
        }
    }

    private fun ExtendedFloatingActionButton.setVisibilityWithAnimation(isVisible: Boolean) {
        val durationAnim: Long = 180
        val tranSizeStart = if (isVisible) 100f else 0f
        val tranSizeEnd = if (isVisible) 0f else 100f

        fun localAnimate() {
            with(this.animate()) {
                duration = 0
                translationY(tranSizeStart)
            }.withEndAction {
                with(this.animate()) {
                    duration = durationAnim
                    translationY(tranSizeEnd)
                }
            }
        }
        if (isVisible && this.translationY > 0f) {
            localAnimate()
        } else if (!isVisible && this.translationY == 0f) {
            localAnimate()
        }
    }
}
