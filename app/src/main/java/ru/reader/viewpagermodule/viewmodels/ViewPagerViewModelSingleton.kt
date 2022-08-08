package ru.reader.viewpagermodule.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewPagerViewModelSingleton : ViewModel() {

    companion object {
        private var instance: ViewPagerViewModelSingleton? = null

        fun getInstanceOfVM(): ViewPagerViewModelSingleton {
            if (instance == null) {
                instance = ViewPagerViewModelSingleton()
                return instance as ViewPagerViewModelSingleton
            } else {
                return instance as ViewPagerViewModelSingleton
            }
        }
    }

    val dataTest: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

}