package ru.reader.viewpagermodule.view.screens


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.pagination.CustomOnSwipeListener
import ru.reader.viewpagermodule.view.adapters.pagination.GestureListener
import ru.reader.viewpagermodule.view.adapters.pagination.OnSwipeListener
import ru.reader.viewpagermodule.view.adapters.pagination.PagedTextView
import ru.reader.viewpagermodule.view.models.BookStateForBundle
import java.io.File

class ViewPagerBookFragment : Fragment(), OnSwipeListener {

    @BindView(R.id.tv_book_name_panel)
    lateinit var nameBook: TextView

    @BindView(R.id.tv_pages_read_panel)
    lateinit var textPaging: TextView

    @BindView(R.id.tv_percent_read_panel)
    lateinit var pagePercent: TextView

    @BindView(R.id.outside_tv_pag)
    lateinit var myTextView: PagedTextView

    private lateinit var parentActivity: MainActivity

    lateinit var filePath: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_custom_view_pager, container, false)
        ButterKnife.bind(this, view0)
        parentActivity = activity as MainActivity
        val args = arguments?.getSerializable(BOOK_BUNDLE) as BookStateForBundle?
        args?.let {
            filePath = it.absolutePath
            nameBook.text = it.bookName
        }


        myTextView.setText(getText(), TextView.BufferType.NORMAL)
        myTextView.setPadding(8, 8, 8, 8)
        myTextView.setOnTouchListener(CustomOnSwipeListener(requireContext(), GestureListener(this)))

        return view0
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onStart() {
        parentActivity.supportActionBar?.hide()
        super.onStart()
    }

    override fun onDestroy() {
        parentActivity.supportActionBar?.show()
        super.onDestroy()
    }


    private fun getText(): String {
        //val inputStream = File(filePath).inputStream()

        val inputStream = resources.openRawResource(R.raw.sample_text)
        val bytes = ByteArray(inputStream.available())
        val s = inputStream.read(bytes)
        return String(bytes)
    }


    override fun onSwipeRight() {
        myTextView.next(myTextView.getPageIndex() + 1)
    }

    override fun onSwipeLeft() {
        myTextView.next(myTextView.getPageIndex() - 1)
    }

}
