package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView

class TaskCardView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : MaterialCardView(context, attributeSet, defStyle) {

    var onInterceptTouchEvent: (event: MotionEvent) -> Unit = {}

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        onInterceptTouchEvent.invoke(event)
        return super.onInterceptTouchEvent(event)
    }
}