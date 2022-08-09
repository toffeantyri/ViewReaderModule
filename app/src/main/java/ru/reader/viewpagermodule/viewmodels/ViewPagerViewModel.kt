package ru.reader.viewpagermodule.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewPagerViewModel : ViewModel() {


    val viewPagerUnblock : MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    init {
        viewPagerUnblock.value = false
    }

}