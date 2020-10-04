package xyz.ramil.filemanager.view.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import java.util.*

class LockableViewPager : ViewPager {
    private var swipeable = false

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        swipeable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        try {
            if (swipeable) {
                return super.onInterceptTouchEvent(event)
            }
        } catch (e: Exception) {
            Log.e("LockableViewPager::", Objects.requireNonNull(e.message))
        }
        return false
    }

    fun setSwipeable(swipeable: Boolean) {
        this.swipeable = swipeable
    }
}