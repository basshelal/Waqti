@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.view.children
import uk.whitecrescent.waqti.logE
import uk.whitecrescent.waqti.parentViewGroup
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class DragView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    companion object {
        const val IDLE = 0
        const val DRAGGING = 1
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(IDLE, DRAGGING)
    annotation class DragState

    private var dx = 0F
    private var dy = 0F
    private var lastAction = MotionEvent.ACTION_UP
    private var isDragging = false
    private var stealChildrenTouchEvents = true

    @DragState
    var dragState = 0
        private set

    var downCalled = false

    private var lastView: View? = null

    inline var itemView: View?
        set(value) {
            removeAllViews()
            addView(value)
        }
        get() = getChildAt(0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // This gets called before on Touch but only once!
        // So it allows us to do stuff before onTouch gets called
        // return true to steal all touch events from children
        // not sure if we want this yet
        logE("Intercept Touch Event")
        return stealChildrenTouchEvents
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isDragging) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    onDown(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    onDown(event)
                    onMove(event)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    endDrag()
                }
                else -> return super.onTouchEvent(event)
            }
            super.onTouchEvent(event)
            return true
        } else {
            return super.onTouchEvent(event)
        }
    }

    private inline fun onDown(event: MotionEvent) {
        if (!downCalled) {
            dx = this.x - event.rawX
            dy = this.y - event.rawY
            lastAction = MotionEvent.ACTION_DOWN
            downCalled = true
        }
    }

    private inline fun onMove(event: MotionEvent) {
        this.x = event.rawX + dx
        this.y = event.rawY + dy

        val newView = getViewUnder(event.rawX, event.rawY)
        if (lastView != newView) {
            onEntered(lastView, newView)
        }

        lastAction = MotionEvent.ACTION_MOVE
    }

    private inline fun onEntered(oldView: View?, newView: View?) {
        // exited oldView
        // entered newView
    }

    private inline fun init() {
        setOnLongClickListener {
            startDrag()
            true
        }
    }

    private fun getViewUnder(pointX: Float, pointY: Float): View? {
        (this.parentViewGroup!!.childCount - 1 downTo 0)
                .map { parentViewGroup!!.getChildAt(it) }
                .forEach {
                    val bounds = Rect()
                    it.getGlobalVisibleRect(bounds)
                    if (bounds.contains(pointX.roundToInt(), pointY.roundToInt())
                            && it != this && it !in this.children)
                        return it
                }
        return null
    }

    // API Draft

    fun startDrag(fromX: Float = 0F, fromY: Float = 0F) {
        isDragging = true
    }

    fun endDrag() {
        cancelLongPress()
        isDragging = false
        downCalled = false
    }

    // CallBacks

    fun onStartDrag() {}

    // We enter a view when the touch point is on top of that View, so we only
    // need to enter its rect
    fun onEnteredView(view: View, touchPoint: Point) {}

    // We exit a view when the touch point is no longer on top of that View, so we only
    // need to exit its rect
    fun onExitedView(view: View, touchPoint: Point) {}

    fun onUpdateLocation(newPoint: Point) {}

    fun onEndDrag() {}

    /*
    *
    * An idea to implement dragging in a more controllable and flexible way
    * instead of using ItemTouchHelper or startDragAndDrop().
    *
    * This is a FrameLayout because it is a wrapper to allow dragging on any View.
    *
    * If you want dragging functionality in a ViewGroup such as BoardView or TaskListView,
    * you must have 1 DragView placed in the ViewGroup which will contain the bounds of the
    * dragging.
    *
    * Let's use BoardView as the example here since it's the main difficulty we're dealing with.
    *
    * We would add 1 DragView as a child of BoardView, it can be anywhere, doesn't matter because
    * it will be View.GONE until a drag operation is initiated by long click.
    *
    * When a long click happens, we find the position of the long click and place this DragView over
    * the view that was long clicked and we update it to look the same (worry about this later).
    *
    * We then hide (alpha = 0F) the original view and now this DragView will be being moved.
    *
    * DragView will have callbacks when it is begun, over a new view, left a view, ended, and
    * when updated location, like the Drag and Drop API. Except in this case it will be done in
    * DragView, not in the view that's being dragged over. Within these callbacks you can access
    * the view that has been entered or exited so that you can do stuff, so in our case we'd find
    * the ViewHolder and TaskListView that contains that View and do stuff accordingly.
    *
    * This is really good and intuitive, the only hard part will inevitably be actually
    * implementing the dragging functionality, especially in an efficient way (it might work but
    * it could be demanding?)
    *
    * This implementation gives us the most control and flexibility, this way we can restrict the
    * dragging View to be shown only in the ViewGroup it is relevant in and get convenient
    * callbacks for when new events occur, which will give us the Views we are over or leaving.
    *
    *
    * */

}