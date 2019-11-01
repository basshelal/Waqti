@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.drag

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.drawToBitmap
import androidx.core.view.updateLayoutParams
import uk.whitecrescent.waqti.extensions.parentViewGroup

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
            if (layoutParams == null) {
                layoutParams = ViewGroup.LayoutParams(
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

}

inline fun View.addDragShadow(addToParent: ViewParent? = this.parentViewGroup) =
        DragShadow(this).also {
            (addToParent as? ViewGroup)?.addView(it)
        }