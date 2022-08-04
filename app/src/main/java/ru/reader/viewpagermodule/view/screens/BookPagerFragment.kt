package ru.reader.viewpagermodule.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.reader.viewpagermodule.R


class BookPagerFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_book_pager, container, false)

        return view0
    }

}
