package ru.reader.viewpagermodule.viewmodels

import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.reader.viewpagermodule.view.screens.SwipeDirection

class ViewPagerViewModel : ViewModel() {

    val eventMotionData : MutableLiveData<MotionEvent> by lazy {
        MutableLiveData()
    }

    val viewPagerSwipeState : MutableLiveData<SwipeDirection> by lazy {
        MutableLiveData()
    }

    init {
        viewPagerSwipeState.value = SwipeDirection.NONE
    }

    val eventMotionData : MutableLiveData<MotionEvent> by lazy {
        MutableLiveData()
    }

}