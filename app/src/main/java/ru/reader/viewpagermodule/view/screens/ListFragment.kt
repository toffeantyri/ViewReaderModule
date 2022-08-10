package ru.reader.viewpagermodule.view.screens

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.helpers.DialogHelper
import ru.reader.viewpagermodule.view.adapters.models.BookCardData
import ru.reader.viewpagermodule.view.adapters.BookListAdapter
import ru.reader.viewpagermodule.view.models.LoadBookData
import ru.reader.viewpagermodule.view.util.MyViewAnimator
import ru.reader.viewpagermodule.viewmodels.ByMemoryState
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

class ListFragment : Fragment(), BookListAdapter.ItemBookClickListener {


    companion object {
        const val CHANGE_CONFIG_KEY = "CHANGE_CONFIG_KEY"
    }

    private var configChanged = false

    private val viewModel: ViewModelMainActivity by activityViewModels()

    private lateinit var btnChangeMemory: ExtendedFloatingActionButton
    private lateinit var progressBarLoading: SpinKitView
    private lateinit var recycler: RecyclerView

    private lateinit var adapter: BookListAdapter
    private lateinit var parentActivity: MainActivity
    private val animator = MyViewAnimator()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_list, container, false)
        savedInstanceState?.let { configChanged = savedInstanceState.getBoolean(CHANGE_CONFIG_KEY) }
        parentActivity = activity as MainActivity
        btnChangeMemory = view0.btn_choose_memory
        progressBarLoading = view0.progress_bar_rv
        recycler = view0.list_rv
        initRv()
        parentActivity.supportActionBar?.hide()
        return view0
    }

    override fun onDestroy() {
        parentActivity.supportActionBar?.show()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(CHANGE_CONFIG_KEY, true)
        super.onSaveInstanceState(outState)

    }

    override fun onStart() {
        super.onStart()
        viewModel.fromMemoryState.observe(viewLifecycleOwner) { state ->
            btnChooseMemorySetState(state)
            adapter.clearAdapterData()
            if (!configChanged) {
                lockRotation(false)
                viewModel.clearBookList()
            }
            if (viewModel.dataListBook.value.isNullOrEmpty()) {
                if (state == ByMemoryState.FROM_DOWNLOAD) {
                    setLoadingAndHideBtnChoose(true)
                    viewModel.getPreloadBooks(onSuccess = {
                        setLoadingAndHideBtnChoose(false)
                    })
                } else if (state == ByMemoryState.FROM_DEVICE) {
                    setLoadingAndHideBtnChoose(true)
                    viewModel.getBooks(
                        onSuccess = { setLoadingAndHideBtnChoose(false) },
                        onSuccessStep = {}
                    )
                }
            }

            viewModel.dataListBook.observe(viewLifecycleOwner) { list ->
                Log.d("MyLog", "view : list observers $list")
                adapter.fillAdapter(list)
            }

            configChanged = false
        }
    }

    private fun initRv() {
        adapter = BookListAdapter()
        adapter.itemBookClickListener = this@ListFragment
        recycler.adapter = adapter
        recycler.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recycler.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) this.animator.run { btnChangeMemory.translationDownByY(false) }
            else this.animator.run { btnChangeMemory.translationDownByY(true) }
        }
    }

    override fun clickOpenBook(loadBookData: LoadBookData, adapterPos: Int) {
        if (loadBookData.absolutePath.isEmpty()) {
            val dialogHelper = DialogHelper()
            CoroutineScope(Dispatchers.Main).launch {
                dialogHelper.createLoadDialogWithAction(requireActivity()) {
                    lockRotation(true)
                    viewModel.loadBookByUrl(
                        loadBookData = loadBookData,
                        itemPosition = adapterPos,
                        onSuccess = {
                            Log.d(
                                "MyLog",
                                "view: callback onSuccess ${loadBookData.defaultNameBook} pos : $adapterPos"
                            )
                            val newItem = getBookFromViewModelByPosOrNull(adapterPos)
                            newItem?.let { adapter.updateItemByPos(it, adapterPos) }
                            lockRotation(false)
                        },
                        onFail = {
                            showToast(getString(R.string.toast_error_load))
                            val newItem = getBookFromViewModelByPosOrNull(adapterPos)
                            newItem?.let { adapter.updateItemByPos(it, adapterPos) }
                            lockRotation(false)
                        },
                        onLoading = {
                            Log.d("MyLog", "View call back onLoading ${loadBookData.defaultNameBook}")
                            val newItem = getBookFromViewModelByPosOrNull(adapterPos)
                            Log.d("MyLog", "View callback isLoading $newItem")
                            newItem?.let { adapter.updateItemByPos(newItem, adapterPos) }
                        }
                    )
                }
            }
        } else {
            showToast(loadBookData.absolutePath)
            //todo open book
        }
    }

    private fun setLoadingAndHideBtnChoose(flag: Boolean) {
        progressBarLoading.visibility = if (flag) View.VISIBLE else View.GONE
        if (flag) {
            btnChangeMemory.hide()
        } else {
            btnChangeMemory.show()
        }
    }

    private fun btnChooseMemorySetState(state: ByMemoryState) {
        btnChangeMemory.apply {
            text = when (state) {
                ByMemoryState.FROM_DOWNLOAD -> getString(R.string.choose_memory_storage)
                ByMemoryState.FROM_DEVICE -> getString(R.string.choose_memory_download)
            }
            setOnClickListener {
                val newState = when (state) {
                    ByMemoryState.FROM_DEVICE -> ByMemoryState.FROM_DOWNLOAD
                    ByMemoryState.FROM_DOWNLOAD -> ByMemoryState.FROM_DEVICE
                }
                viewModel.setMemoryState(newState)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun getBookFromViewModelByPosOrNull(pos: Int): BookCardData? {
        return if (viewModel.dataListBook.value?.size!! > pos) {
            viewModel.dataListBook.value?.get(pos)
        } else null
    }

    private fun lockRotation(value: Boolean) {
        if (value) {
            val currentOrientation = resources.configuration.orientation
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            } else activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

}

