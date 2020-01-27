package uk.whitecrescent.waqti.frontend.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView
import uk.whitecrescent.waqti.extensions.logE
import uk.whitecrescent.waqti.extensions.shortSnackBar

class TaskCardView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : MaterialCardView(context, attributeSet, defStyle) {

    var onInterceptTouchEvent: (event: MotionEvent) -> Unit = {}
    var onTouchEvent: (event: MotionEvent) -> Unit = {}

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        logE("LAYOUT!\n" +
                "-----------------------------------------------------")
        shortSnackBar("LAYOUT!!!!")
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        logE("onInterceptTouchEvent in TaskCardView")
        onInterceptTouchEvent.invoke(event)
        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        logE("onTouchEvent in TaskCardView")
        onTouchEvent.invoke(event)
        if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
            logE("CANCELLED in TaskCardView\n" +
                    "-----------------------------------------------------")
            shortSnackBar("CANCELLED in TaskCardView")
        }
        return super.onTouchEvent(event)
    }
}