package uk.whitecrescent.waqti.android.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class KDragItemRecyclerView
@JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) :
        RecyclerView(context, attributeSet, defStyle),
        KAutoScroller.AutoScrollListener {

    var autoScroller: KAutoScroller = KAutoScroller(getContext(), this)
    var dragItemListener: DragItemListener? = null
    var dragItemCallback: DragItemCallback? = null
    var dragState: DragState = DragState.DRAG_ENDED
    var adapter: KDragItemAdapter<*, out KDragItemAdapter.ViewHolder>? = null
    var dragItem: KDragItem? = null
    var dropTargetBackgroundDrawable: Drawable? = null
    var dropTargetForegroundDrawable: Drawable? = null
    var dragItemId: Long = NO_ID
    var holdChangePosition: Boolean = false
    var dragItemPosition: Int = 0
    var touchSlop: Int = ViewConfiguration.get(getContext()).scaledTouchSlop
    var startY: Float = 0F
    var clipToPadding0: Boolean = false
    var canNotDragAboveTop: Boolean = false
    var canNotDragBelowBottom: Boolean = false
    var scrollingEnabled = true
    var disableReorderWhenDragging: Boolean = false
    var dragEnabled = true

    var dropTargetDrawables =
            Pair(dropTargetBackgroundDrawable, dropTargetForegroundDrawable)
        get() = Pair(dropTargetBackgroundDrawable, dropTargetForegroundDrawable)
        set(value) {
            field = value
            dropTargetBackgroundDrawable = value.first
            dropTargetForegroundDrawable = value.second
        }
    val isDragging: Boolean
        get() = dragState != DragState.DRAG_ENDED

    init {
        addItemDecoration(
                object : ItemDecoration() {
                    override fun onDraw(c: Canvas, parent: RecyclerView, state: State) {
                        super.onDraw(c, parent, state)
                        drawDecoration(c, parent, dropTargetBackgroundDrawable)
                    }

                    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: State) {
                        super.onDrawOver(c, parent, state)
                        drawDecoration(c, parent, dropTargetForegroundDrawable)
                    }

                    fun drawDecoration(c: Canvas, parent: RecyclerView, drawable: Drawable?) {
                        if (adapter == null || adapter!!.dropTargetId == NO_ID || drawable == null) return

                        (0..parent.childCount).forEach {
                            val item = parent.getChildAt(it)
                            val position = getChildAdapterPosition(item)

                            if (position != NO_POSITION &&
                                    adapter!!.getItemId(position) == adapter!!.dropTargetId)
                                drawable.apply {
                                    setBounds(
                                            item.left, item.top, item.right, item.bottom
                                    )
                                    draw(c)
                                }

                        }
                    }
                }
        )
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (!scrollingEnabled) return false

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY = abs(event.y - startY)
                if (diffY > touchSlop * 0.5) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun setClipToPadding(clipToPadding: Boolean) {
        super.setClipToPadding(clipToPadding)
        clipToPadding0 = clipToPadding
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        checkAdapter(adapter)
        super.setAdapter(adapter)
        this.adapter = adapter as KDragItemAdapter<Any, KDragItemAdapter.ViewHolder>
    }

    override fun swapAdapter(adapter: Adapter<*>?, removeAndRecycleExistingViews: Boolean) {
        checkAdapter(adapter)
        super.swapAdapter(adapter, removeAndRecycleExistingViews)
        this.adapter = adapter as KDragItemAdapter<Any, KDragItemAdapter.ViewHolder>
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        if (layout !is LinearLayoutManager) {
            throw IllegalArgumentException("Layout must be a LinearLayoutManager")
        }
    }

    override fun onAutoScrollPositionBy(dx: Int, dy: Int) {
        if (isDragging) {
            scrollBy(dx, dy)
            updateDragPositionAndScroll()
        } else {
            autoScroller.stopAutoScroll()
        }
    }

    override fun onAutoScrollColumnBy(columns: Int) {

    }

    fun findChildView(x: Float, y: Float): View? {
        val count = childCount
        if (y <= 0 && count > 0) return getChildAt(0)

        for (i in count - 1 downTo 0) {
            val child = getChildAt(i)
            val params = child.layoutParams as MarginLayoutParams
            if (x >= child.left - params.leftMargin &&
                    x <= child.right + params.rightMargin &&
                    y >= child.top - params.topMargin &&
                    y <= child.bottom + params.bottomMargin
            ) return child
        }

        return null
    }

    private fun shouldChangeItemPosition(newPosition: Int): Boolean {
        // Check if drag position is changed and valid and that we are not in a hold position state
        if (holdChangePosition || dragItemPosition == RecyclerView.NO_POSITION ||
                dragItemPosition == newPosition) {
            return false
        }
        // If we are not allowed to drag above top or bottom and new pos is 0 or item count then return false
        if (canNotDragAboveTop && newPosition == 0 ||
                canNotDragBelowBottom && newPosition == adapter!!.itemCount - 1) {
            return false
        }
        // Check with callback if we are allowed to drop at this position
        return !(dragItemCallback != null && !dragItemCallback!!.canDropItemAtPosition(newPosition))
    }

    fun updateDragPositionAndScroll() {
        val view = findChildView(dragItem!!.posX, dragItem!!.posY)!!
        var newPosition = getChildLayoutPosition(view)
        if (newPosition == NO_POSITION || newPosition == null) return

        // If using a LinearLayoutManager and the new view has a bigger height we need to check if passing centerY as well.
        // If not doing this extra check the bigger item will move back again when dragging slowly over it.
        if (layoutManager is LinearLayoutManager && layoutManager !is GridLayoutManager) {
            val params = view.layoutParams as MarginLayoutParams
            val viewHeight = view.measuredHeight + params.topMargin + params.bottomMargin
            val viewCenterY = view.top - params.topMargin + (viewHeight / 2)
            val dragDown = dragItemPosition < getChildLayoutPosition(view)
            val movedPastCenterY =
                    if (dragDown) dragItem!!.posY > viewCenterY else dragItem!!.posY < viewCenterY

            // If new height is bigger then current and not passed centerY then reset back to current position
            if (viewHeight > dragItem!!.dragView.measuredHeight && !movedPastCenterY) {
                newPosition = dragItemPosition
            }
        }

        val layoutManager = layoutManager as LinearLayoutManager
        if (shouldChangeItemPosition(newPosition)) {
            if (disableReorderWhenDragging) {
                adapter!!.dropTargetId = adapter!!.getItemId(newPosition)
                adapter!!.notifyDataSetChanged()
            } else {
                val position = layoutManager.findFirstVisibleItemPosition()
                val positionView = layoutManager.findViewByPosition(position)
                adapter!!.changeItemPosition(dragItemPosition, newPosition)
                dragItemPosition = newPosition

                // Since notifyItemMoved scrolls the list we need to scroll back to where we were after the position change.
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    val topMargin = (positionView!!.layoutParams as MarginLayoutParams).topMargin
                    layoutManager.scrollToPositionWithOffset(position, positionView!!.top - topMargin)
                } else {
                    val leftMargin = (positionView!!.layoutParams as MarginLayoutParams).leftMargin
                    layoutManager.scrollToPositionWithOffset(position, positionView!!.left - leftMargin)
                }
            }
        }

        var lastItemReached = false
        var firstItemReached = false

        val top = if (clipToPadding0) paddingTop else 0
        val bottom = if (clipToPadding0) height - paddingBottom else height
        val left = if (clipToPadding0) paddingLeft else 0
        val right = if (clipToPadding0) width - paddingRight else width

        val lastChild = findViewHolderForLayoutPosition(adapter!!.itemCount - 1)
        val firstChild = findViewHolderForLayoutPosition(0)

        // Check if first or last item has been reached
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
            if (lastChild != null && lastChild.itemView.bottom <= bottom) lastItemReached = true
            if (firstChild != null && firstChild.itemView.top >= top) firstItemReached = true
        } else {
            if (lastChild != null && lastChild.itemView.right <= right) lastItemReached = true
            if (firstChild != null && firstChild.itemView.left >= left) firstItemReached = true
        }

        // Start auto scroll if at the edge
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
            if (dragItem!!.posY > height - (view.height / 2) && !lastItemReached)
                autoScroller.startAutoScroll(KAutoScroller.ScrollDirection.UP)
            else if (dragItem!!.posY < (view.height / 2) && !firstItemReached)
                autoScroller.startAutoScroll(KAutoScroller.ScrollDirection.DOWN)
            else autoScroller.stopAutoScroll()
        } else {
            if (dragItem!!.posX > width - (view.width / 2) && !lastItemReached)
                autoScroller.startAutoScroll(KAutoScroller.ScrollDirection.LEFT)
            else if (dragItem!!.posX < view.width / 2 && !firstItemReached)
                autoScroller.startAutoScroll(KAutoScroller.ScrollDirection.RIGHT)
            else autoScroller.stopAutoScroll()
        }

    }

    fun startDrag(itemView: View, itemId: Long, x: Float, y: Float): Boolean {
        val dragItemPosition0 = adapter!!.getPositionForItemId(itemId)
        if (!dragEnabled || (canNotDragAboveTop && (dragItemPosition0 == 0)) ||
                (canNotDragBelowBottom && (dragItemPosition0 == adapter!!.itemCount - 1)))
            return false

        if (dragItemCallback != null &&
                dragItemCallback!!.canDragItemAtPosition(dragItemPosition0)) return false

        // If a drag is starting the parent must always be allowed to intercept
        parent.requestDisallowInterceptTouchEvent(false)
        dragState = DragState.DRAG_STARTED
        dragItemId = itemId
        dragItem!!.startDrag(itemView, x, y)
        dragItemPosition = dragItemPosition0
        updateDragPositionAndScroll()

        adapter!!.dragItemId = dragItemId
        adapter!!.notifyDataSetChanged()
        if (dragItemListener != null)
            dragItemListener!!.onDragStarted(dragItemPosition, dragItem!!.posX, dragItem!!.posY)

        invalidate()
        return true
    }

    fun onDragging(x: Float, y: Float) {
        if (dragState == DragState.DRAG_ENDED) return

        dragState = DragState.DRAGGING
        dragItemPosition = adapter!!.getPositionForItemId(dragItemId)
        dragItem!!.position = Pair(x, y)

        if (!autoScroller.isAutoScrolling) updateDragPositionAndScroll()
        if (dragItemListener != null) dragItemListener!!.onDragging(dragItemPosition, x, y)

        invalidate()
    }

    fun onDragEnded() {
        // Need check because sometimes the framework calls drag end twice in a row
        if (dragState == DragState.DRAG_ENDED) return

        autoScroller.stopAutoScroll()
        isEnabled = false

        if (disableReorderWhenDragging) {
            val newPosition = adapter!!.getPositionForItemId(adapter!!.dropTargetId)
            if (newPosition != NO_POSITION) {
                adapter!!.swapItems(dragItemPosition, newPosition)
                dragItemPosition = newPosition
            }
            adapter!!.dropTargetId = NO_ID
        }

        // Post so layout is done before we start end animation
        post {
            // Sometimes the holder will be null if a holder has not yet been set for the position
            val holder = findViewHolderForAdapterPosition(dragItemPosition)
            if (holder != null) {
                itemAnimator?.endAnimation(holder)
                dragItem!!.endDrag(holder.itemView, object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        holder.itemView.alpha = 1F
                        onDragItemAnimationEnd()
                    }
                })
            } else onDragItemAnimationEnd()
        }
    }

    fun onDragItemAnimationEnd() {
        adapter!!.apply {
            dragItemId = NO_ID
            dropTargetId = NO_ID
            notifyDataSetChanged()
        }

        dragState = DragState.DRAG_ENDED
        if (dragItemListener != null) dragItemListener!!.onDragEnded(dragItemPosition)

        dragItemId = NO_ID
        dragItem!!.hide()
        isEnabled = true
        invalidate()
    }

    fun getDragPositionForY(y: Float): Int {
        val child = findChildView(0F, y)
        var pos: Int

        // If child is null and child count is not 0 it means that an item was
        // dragged below the last item in the list, then put it after that item
        if (child == null && childCount > 0) pos = getChildLayoutPosition(getChildAt(childCount - 1)) + 1
        else pos = getChildLayoutPosition(child!!)

        // If pos is NO_POSITION it means that the child has not been laid out yet,
        // this only happens for pos 0 as far as I know
        if (pos == NO_POSITION) pos = 0
        return pos
    }

    fun addDragItemAndStart(y: Float, item: Any, itemId: Long) {
        val pos = getDragPositionForY(y)

        dragState = DragState.DRAG_STARTED
        dragItemId = itemId
        adapter!!.addItem(pos, item)
        dragItemPosition = pos

        holdChangePosition = true
        postDelayed({ holdChangePosition = false }, itemAnimator!!.moveDuration)
        invalidate()
    }

    fun removeDragItemAndEnd(): Any? {
        if (dragItemPosition == NO_POSITION) return null
        autoScroller.stopAutoScroll()
        val item = adapter!!.removeItem(dragItemPosition)
        adapter!!.dragItemId = NO_ID
        dragState = DragState.DRAG_ENDED
        dragItemId = NO_ID

        invalidate()
        return item
    }

    private fun checkAdapter(adapter: Adapter<*>?) {
        if (!isInEditMode) {
            if (adapter !is KDragItemAdapter<*, *>) throw IllegalArgumentException("Adapter must extend DragItemAdapter")
            if (!adapter.hasStableIds()) throw IllegalStateException("Adapter must have stable Ids")
        }
    }

    //region Classes and Interfaces

    interface DragItemListener {
        fun onDragStarted(itemPosition: Int, x: Float, y: Float)

        fun onDragging(itemPosition: Int, x: Float, y: Float)

        fun onDragEnded(newItemPosition: Int)
    }

    interface DragItemCallback {
        fun canDragItemAtPosition(dragPosition: Int): Boolean

        fun canDropItemAtPosition(dropPosition: Int): Boolean
    }

    enum class DragState {
        DRAG_STARTED, DRAGGING, DRAG_ENDED
    }


    //endregion Classes and Interfaces
}

