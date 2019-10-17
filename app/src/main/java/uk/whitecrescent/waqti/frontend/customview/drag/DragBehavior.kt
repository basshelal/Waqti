@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.drag

import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import org.jetbrains.anko.childrenRecursiveSequence
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.NonFinal
import uk.whitecrescent.waqti.globalVisibleRect
import uk.whitecrescent.waqti.parentViewGroup

/* TODO: 15-Oct-19
 * An idea to make Dragging be an added behavior (a decorator) applied to a View rather than
 * creating an actual View just for dragging
 */
@NonFinal
@ForLater
open class DragBehavior(val view: View) {

    protected val dPoint = PointF()
    protected val touchPoint = PointF()
    val returnPoint = PointF()

    protected var isDragging = false
    protected var stealChildrenTouchEvents = false

    protected var currentView: View? = null

    private var downCalled = false

    private var touchPointOutOfParentBounds = false

    protected var dampingRatio = 0.6F
    protected var stiffness = 1000F

    var dragListener: DragListener? = null

    protected var dragState: DragState = DragState.IDLE
        private set(value) {
            field = value
            dragListener?.onDragStateChanged(view, value)
        }

    private val onTouchListener = object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            touchPoint.set(event.rawX, event.rawY)
            return if (isDragging) {
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

    init {
        returnPoint.set(view.x, view.y)
        view.setOnTouchListener(onTouchListener)
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
        dragListener?.onUpdateLocation(view, touchPoint)
    }

    private inline fun animateReturn() {
        dragState = DragState.SETTLING
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
        dragState = DragState.IDLE
        dragListener?.onEndDrag(view)
    }

    fun startDrag() {
        returnPoint.set(view.x, view.y)
        dragState = DragState.DRAGGING
        dragListener?.onStartDrag(view)
        isDragging = true
        stealChildrenTouchEvents = true
    }

    fun endDrag() {
        view.cancelLongPress()
        dragListener?.onReleaseDrag(view, touchPoint)
        animateReturn()
    }

    fun endDragNow() {
        view.cancelLongPress()
        returnPoint.set(view.x, view.y)
        afterEndAnimation()
    }

    fun startDragFromView(otherView: View) {
        require(otherView in this.view.parentViewGroup!!.childrenRecursiveSequence()) {
            "The passed in view must be a descendant of this DragView's parent!"
        }
        dragState = DragState.DRAGGING
        dragListener?.onStartDrag(view)

        this.view.bringToFront()

        val parentBounds = this.view.parentViewGroup!!.globalVisibleRect
        val viewBounds = otherView.globalVisibleRect

        this.view.x = viewBounds.left.toFloat() - parentBounds.left.toFloat()
        this.view.y = viewBounds.top.toFloat() - parentBounds.top.toFloat()
        returnPoint.set(this.view.x, this.view.y)

        isDragging = true
        stealChildrenTouchEvents = true
        otherView.setOnTouchListener { v, event ->
            if (isDragging) {
                touchPoint.set(event.rawX, event.rawY)
                this.view.dispatchTouchEvent(event)
                v.onTouchEvent(event)
                v.parentViewGroup?.requestDisallowInterceptTouchEvent(true)
                true
            } else {
                false
            }
        }
    }

    companion object {

        val longPressTime: Int
            get() = ViewConfiguration.getLongPressTimeout()
    }

    enum class DragState {
        /** View is idle, no movement */
        IDLE,
        /** View is being dragged by user, movement from user */
        DRAGGING,
        /** View is settling into final position, movement is not from user */
        SETTLING
    }

    interface DragListener {

        /**
         * Called before the drag operation starts
         */
        fun onStartDrag(dragView: View)

        /**
         * Called when the drag touch location is updated, meaning when the user moves their
         * finger while dragging.
         *
         * Warning: This gets called **VERY OFTEN**, any code called in here should not be too
         * expensive, else you may experience jank while dragging. It is preferred to use
         * [onEnteredView] instead to receive events when the touch point enters a new view.
         */
        fun onUpdateLocation(dragView: View, touchPoint: PointF)

        /**
         * Called when the user's touch is release. The [dragView] will start to animate its return.
         */
        fun onReleaseDrag(dragView: View, touchPoint: PointF)

        /**
         * Called when the dragging operation has fully ended and the [dragView] has ended its
         * return animation.
         */
        fun onEndDrag(dragView: View)

        /**
         * Called when the [DragState] of the [dragView] is changed
         */
        fun onDragStateChanged(dragView: View, newState: DragState)

    }

    abstract class SimpleDragListener : DragListener {
        override fun onStartDrag(dragView: View) {}
        override fun onUpdateLocation(dragView: View, touchPoint: PointF) {}
        override fun onReleaseDrag(dragView: View, touchPoint: PointF) {}
        override fun onEndDrag(dragView: View) {}
        override fun onDragStateChanged(dragView: View, newState: DragState) {}
    }

}

inline fun View.addDragBehavior() = DragBehavior(this)