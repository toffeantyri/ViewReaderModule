package ru.reader.viewpagermodule.view.screens


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

import ru.reader.viewpagermodule.R


class FavoriteListFragment : Fragment() {

    @BindView(R.id.arrowLeftBack)
    lateinit var buttonBack: AppCompatImageView

    lateinit var parentActivity: MainActivity

    @BindView(R.id.filterList)
    lateinit var filterExpandableList: Spinner

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

        val filterAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(
                parentActivity,
                R.array.favorite_filter,
                R.layout.item_favorite_filter
            )
        filterAdapter.setDropDownViewResource(R.layout.filter_box_layout)
        filterExpandableList.adapter = filterAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonBack.setOnClickListener {
            parentActivity.onBackPressed()
        }


    }


}
