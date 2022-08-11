package ru.reader.viewpagermodule.view.adapters.pagination

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.Exception

class CustomOnSwipeListener(val context: Context, gestureListener: GestureListener) : View.OnTouchListener {

    private var gestureDetector: GestureDetector = GestureDetector(context, gestureListener)

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }
}

class GestureListener(private val onSwipeListener: OnSwipeListener) : GestureDetector.SimpleOnGestureListener() {
    val SWIPE_THRESHOLD = 20
    val SWIPE_VELOCITY_THRESHOLD = 20
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffX: Float = if (e1 != null && e2 != null) e2.x - e1.x else 0f
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)
                if (diffX > 0) {
                    onSwipeListener.onSwipeLeft()
                } else {
                    onSwipeListener.onSwipeRight()
                }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}