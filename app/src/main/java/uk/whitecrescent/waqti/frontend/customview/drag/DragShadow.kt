@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.drag

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.drawToBitmap
import androidx.core.view.updateLayoutParams
import uk.whitecrescent.waqti.extensions.logE

/**
 * An [ImageView] used to represent the draggable "shadow" of any [View].
 *
 * Instead of making a [View] draggable, one can just have one [DragShadow] for every draggable
 * [View] in the Fragment, this saves on performance and allows the draggable element appear to
 * move all across the Fragment view even outside the parent of the View it is mirroring.
 *
 * This is useful in Waqti's case for draggable Task Cards, the Board Fragment can have one
 * single [DragShadow] which represents the currently dragging Task, the shadow is a child of the
 * root view of the Fragment so it can exist anywhere inside it, unlike a Task Card which can
 * only exist within the bounds of its parent, the list it is in.
 *
 * This shadow contains no real functionality other than to mirror the [View] that is passed to
 * it in [updateToMatch]. Its dragging functionality is contained in [dragBehavior], an
 * [ObservableDragBehavior] responsible for all the dragging of the shadow. The passed in [View]
 * is not modified here in any way and is thus the caller's responsibility to update its
 * appearance to simulate that the these 2 elements (the View and the Shadow) are actually 1
 * element.
 */
class DragShadow
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : AppCompatImageView(context, attributeSet, defStyle) {

    constructor(view: View) : this(view.context) {
        this updateToMatch view
    }

    val dragBehavior: ObservableDragBehavior = this.addObservableDragBehavior()

    inline infix fun updateToMatch(view: View) {
        if (view.layoutParams != null) {
            if (this.layoutParams == null) {
                this.layoutParams = ViewGroup.LayoutParams(
                        view.layoutParams.width,
                        view.layoutParams.height
                )
            } else {
                updateLayoutParams {
                    width = view.layoutParams.width
                    height = view.layoutParams.height
                }
            }
        }
        this.setImageBitmap(view.drawToBitmap())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        logE("onTouchEvent in DragShadow")
        return super.onTouchEvent(event)
    }

}