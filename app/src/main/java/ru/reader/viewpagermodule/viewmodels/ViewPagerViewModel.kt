package ru.reader.viewpagermodule.viewmodels

import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewPagerViewModel : ViewModel() {

    val eventMotionData : MutableLiveData<MotionEvent> by lazy {
        MutableLiveData()
    }

}