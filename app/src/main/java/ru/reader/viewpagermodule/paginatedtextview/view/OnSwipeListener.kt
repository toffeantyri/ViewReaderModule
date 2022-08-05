package ru.reader.viewpagermodule.paginatedtextview.view

/**
 * ru.mamykin.widget:paginatedtextview:0.1.1
 * */

/**
 * Interface definition for a callback to be invoked when user make a swipe
 */
interface OnSwipeListener {

    /**
     * Swipe from right to left
     */
    fun onSwipeLeft()

    /**
     * Swipe from left to right
     */
    fun onSwipeRight()
}