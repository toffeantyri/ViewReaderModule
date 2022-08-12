package ru.reader.viewpagermodule.view.screens


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.pagination.CustomOnSwipeListener
import ru.reader.viewpagermodule.view.adapters.pagination.GestureListener
import ru.reader.viewpagermodule.view.adapters.pagination.OnSwipeListener
import ru.reader.viewpagermodule.view.adapters.pagination.PagedTextView
import ru.reader.viewpagermodule.view.bookparcer.ParserBookToString
import ru.reader.viewpagermodule.view.models.BookStateForBundle
import ru.reader.viewpagermodule.view.util.LoadingListener
import kotlin.math.roundToInt

class ViewPagerBookFragment : Fragment(), OnSwipeListener, LoadingListener {

    @BindView(R.id.tv_book_name_panel)
    lateinit var infoNameBook: TextView

    @BindView(R.id.tv_pages_read_panel)
    lateinit var infoPages: TextView

    @BindView(R.id.progress_loading_text)
    lateinit var progressLoading: ProgressBar

    @BindView(R.id.tv_percent_read_panel)
    lateinit var infoPagePercent: TextView

    @BindView(R.id.outside_tv_pag)
    lateinit var tvBook: PagedTextView

    private lateinit var parentActivity: MainActivity

    lateinit var filePath: String

    private var savedIndex = 1850

    var durationAnimationBySwipe = 150L
    private lateinit var inRight: Animation
    private lateinit var inLeft: Animation

    private var job: Job? = null

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
            infoNameBook.text = it.bookName
        }
        setupAnimationRightLeft()
        tvBook.setLoadingListener(this)



        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            progressLoading.visibility = View.VISIBLE
            val text = getText()
            withContext(Dispatchers.Main) {
                tvBook.setText(text, TextView.BufferType.NORMAL)
                tvBook.setOnTouchListener(
                    CustomOnSwipeListener(
                        requireContext(),
                        GestureListener(this@ViewPagerBookFragment)
                    )
                )
            }
        }


        return view0
    }


    override fun onStart() {
        parentActivity.supportActionBar?.hide()
        super.onStart()
    }

    override fun onDestroy() {
        parentActivity.supportActionBar?.show()
        job?.cancel()
        super.onDestroy()
    }


    private fun getText(): CharSequence {
        val parser = ParserBookToString()
        return parser.getText(filePath)

    }


    override fun onSwipeRight() {
        if (tvBook.getPageIndex() < tvBook.size() - 1) {
            tvBook.startAnimation(inRight)
            tvBook.next(tvBook.getPageIndex() + 1)
            updateInfo()
        }
    }

    override fun onSwipeLeft() {
        if (tvBook.getPageIndex() > 0) {
            tvBook.startAnimation(inLeft)
            tvBook.next(tvBook.getPageIndex() - 1)
            updateInfo()
        }
    }

    private fun setupAnimationRightLeft() {
        inRight = AnimationUtils.loadAnimation(this.context, R.anim.slide_in_right)
        inLeft = AnimationUtils.loadAnimation(this.context, R.anim.slide_in_left)
        inRight.duration = durationAnimationBySwipe
        inLeft.duration = durationAnimationBySwipe
    }

    override fun loadingIsEnd() {
        progressLoading.visibility = View.GONE
        tvBook.next(savedIndex)
        updateInfo()
    }


    @SuppressLint("SetTextI18n")
    private fun updateInfo(){

        infoPages.text = (tvBook.getPageIndex() + 1).toString() + "/" + (tvBook.size())
        val percent = (((tvBook.getPageIndex().toFloat() + 1) / tvBook.size()) * 100)
        infoPagePercent.text = String.format("%.1f", percent) + "%"
    }




}
