@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.collections.forEachReversedByIndex
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.NonFinal
import uk.whitecrescent.waqti.allChildren
import uk.whitecrescent.waqti.globalVisibleRect
import uk.whitecrescent.waqti.parentViewGroup
import kotlin.math.roundToInt

/* TODO: 15-Oct-19
 * An idea to make Dragging be an added behavior (a decorator) applied to a View rather than
 * creating an actual View just for dragging
 */
@NonFinal
@ForLater
open class DragBehavior(val view: View) {

    protected val dPoint = PointF()
    protected val touchPoint = PointF()
    protected val returnPoint = PointF()

    protected var isDragging = false
    protected var stealChildrenTouchEvents = false

    protected var currentView: View? = null

    protected var downCalled = false

    private var touchPointOutOfParentBounds = false

    protected var dampingRatio = 0.6F
    protected var stiffness = 1000F

    init {
        returnPoint.set(view.x, view.y)

        view.setOnTouchListener { v, event ->
            touchPoint.set(event.rawX, event.rawY)
            if (isDragging) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        onDown(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        onDown(event)
                        onMove(event)
                    }
                    MotionEvent.ACTION_UP -> {
                        endDrag()
                    }
                    else -> view.onTouchEvent(event)
                }
                view.onTouchEvent(event)
                true
            } else {
                view.onTouchEvent(event)
            }
        }
    }

    private inline fun onDown(event: MotionEvent) {
        if (!downCalled) {
            dPoint.x = view.x - event.rawX
            dPoint.y = view.y - event.rawY
            touchPoint.set(event.rawX, event.rawY)
            view.parentViewGroup?.requestDisallowInterceptTouchEvent(true)
            downCalled = true
        }
    }

    private inline fun onMove(event: MotionEvent) {
        view.x = event.rawX + dPoint.x
        view.y = event.rawY + dPoint.y
        touchPoint.set(event.rawX, event.rawY)
        onEntered(event)
    }

    private inline fun onEntered(event: MotionEvent) {
        val newView = getViewUnder(event.rawX, event.rawY) ?: parentViewGroup
        if (currentView != newView) {
            currentView = newView
        }
        onBounds()
    }

    private inline fun onBounds() {
        val outOfBounds = !parentViewGroup.globalVisibleRect
                .contains(touchPoint.x.roundToInt(), touchPoint.y.roundToInt())
        if (touchPointOutOfParentBounds != outOfBounds) {
            touchPointOutOfParentBounds = outOfBounds
        }
    }

    private inline fun getViewUnder(pointX: Float, pointY: Float): View? {
        parentViewGroup.allChildren.forEachReversedByIndex {
            if (it.globalVisibleRect.contains(pointX.roundToInt(), pointY.roundToInt())
                    && it != view && it !in view.childrenRecursiveSequence()) {
                return it
            }
        }
        return null
    }

    private inline fun animateReturn() {
        SpringAnimation(view, DynamicAnimation.X, returnPoint.x).also {
            it.spring.dampingRatio = dampingRatio
            it.spring.stiffness = stiffness
        }.start()
        SpringAnimation(view, DynamicAnimation.Y, returnPoint.y).also {
            it.spring.dampingRatio = dampingRatio
            it.spring.stiffness = stiffness
            it.addEndListener { _, _, _, _ -> afterEndAnimation() }
        }.start()
    }

    private inline fun afterEndAnimation() {
        currentView = null
        isDragging = false
        stealChildrenTouchEvents = false
        touchPointOutOfParentBounds = false
        downCalled = false
    }

    /**
     * Call this to start dragging from this DragView or any of its descendants, note that this
     * will "steal" those children's touch events while this DragView is being dragged.
     *
     * To start dragging from a View that is not a descendant of this DragView, use [startDragFromView]
     */
    fun startDrag() {
        returnPoint.set(view.x, view.y)
        isDragging = true
        stealChildrenTouchEvents = true
    }

    /**
     * Call this to end the drag, this will start an animation to return this DragView to its
     * last acceptable drop location determined by the [dragListener]'s
     * [DragListener.onEnteredView] return value, if none was determined then this DragView will
     * return to its original location.
     */
    fun endDrag() {
        view.cancelLongPress()
        animateReturn()
    }

    /**
     * Call this to end the drag operation immediately, this is identical to [endDrag] except
     * without an animation
     */
    fun endDragNow() {
        view.cancelLongPress()
        returnPoint.set(view.x, view.y)
        afterEndAnimation()
    }

    private inline val parentViewGroup: ViewGroup
        get() = view.parentViewGroup
                ?: throw IllegalStateException("Parent must be a non null ViewGroup" +
                        " parent is ${view.parent}")

}

inline fun View.addDragBehavior() = DragBehavior(this)