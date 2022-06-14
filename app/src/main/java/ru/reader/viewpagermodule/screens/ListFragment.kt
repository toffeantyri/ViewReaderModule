package ru.reader.viewpagermodule.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_list.*
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.adapters.BookListAdapter
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity

class ListFragment : Fragment() {


    val viewModel : ViewModelMainActivity by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()
    }

    lateinit var adapter : BookListAdapter

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
    }

    private fun initRv(){
        adapter = BookListAdapter()
        list_rv.adapter = adapter
        list_rv.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL, false)
    }



}

