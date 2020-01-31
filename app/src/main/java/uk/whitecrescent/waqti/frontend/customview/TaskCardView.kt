package uk.whitecrescent.waqti.frontend.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView
import uk.whitecrescent.waqti.extensions.logE

// TODO: 30-Jan-20 Remove this later!

class TaskCardView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : MaterialCardView(context, attributeSet, defStyle) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        logE("onInterceptTouchEvent in TaskCardView")
        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        logE("onTouchEvent in TaskCardView")
        return super.onTouchEvent(event)
    }
}