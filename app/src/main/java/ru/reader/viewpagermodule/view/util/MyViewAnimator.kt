package ru.reader.viewpagermodule.view.util

import android.view.View

class MyViewAnimator {

    fun View.translationDownByY(isVisible: Boolean) {
        val durationAnim: Long = 180
        val tranSizeStart = if (isVisible) 150f else 0f
        val tranSizeEnd = if (isVisible) 0f else 150f
        if (isVisible && this.translationY > 0f) {
            localAnimate(durationAnim, tranSizeStart, tranSizeEnd)
        } else if (!isVisible && this.translationY == 0f) {
            localAnimate(durationAnim, tranSizeStart, tranSizeEnd)
        }
    }

    private fun View.localAnimate(durationAnim: Long, startPoint: Float, endPoint: Float) {
        with(this.animate()) {
            duration = 0
            translationY(startPoint)
        }.withEndAction {
            with(this.animate()) {
                duration = durationAnim
                translationY(endPoint)
            }
        }
    }


}