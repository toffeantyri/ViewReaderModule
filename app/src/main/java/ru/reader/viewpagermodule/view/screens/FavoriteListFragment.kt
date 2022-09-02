package ru.reader.viewpagermodule.view.screens


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

import ru.reader.viewpagermodule.R


class FavoriteListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    @BindView(R.id.arrowLeftBack)
    lateinit var buttonBack: AppCompatImageView

    lateinit var parentActivity: MainActivity

    private val filterAdapter: FilterArrayAdapter by lazy {
        FilterArrayAdapter(requireActivity(), resources.getStringArray(R.array.favorite_filter))
    }

    @BindView(R.id.filterList)
    lateinit var filterExpandableList: AppCompatSpinner

    @BindView(R.id.mainRecycler)
    lateinit var mainRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity
        parentActivity.supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_list, container, false)
        ButterKnife.bind(this, view)

        filterExpandableList.onItemSelectedListener = this
        filterExpandableList.adapter = filterAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonBack.setOnClickListener {
            parentActivity.onBackPressed()
        }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        filterAdapter.setCheckedPos(position)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}

class FilterArrayAdapter(context: Context, private val list: Array<String>) :
    ArrayAdapter<String>(context, R.layout.filter_box_layout, list) {

    private var checkedPosition = 0

    fun setCheckedPos(pos: Int) {
        this.checkedPosition = pos
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createView(position, convertView, parent)
        bindView(position, view)
        return view ?: super.getDropDownView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return if (position == checkedPosition) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_filter, parent, false)
        } else convertView
    }

    private fun bindView(position: Int, view: View?) {
        if (view != null && view is AppCompatTextView) {
            view.text = list[position]
            view.setTextColor(context.getColor(R.color.green_dark))
        }

    }

}
