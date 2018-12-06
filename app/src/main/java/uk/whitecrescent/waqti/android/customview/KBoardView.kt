package uk.whitecrescent.waqti.android.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Configuration
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Scroller
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import uk.whitecrescent.waqti.R


class KBoardView
@JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) :
        HorizontalScrollView(context, attributeSet, defStyle), KAutoScroller.AutoScrollListener {

    val scrollAnimationDuration = 325
    lateinit var scroller: Scroller
    lateinit var autoScroller: KAutoScroller
    lateinit var gestureDetector: GestureDetector
    lateinit var rootLayout: FrameLayout
    lateinit var columnLayout: LinearLayout
    val lists = ArrayList<KDragItemRecyclerView>()
    val headers = ArrayList<View>()
    var currentRecyclerView: KDragItemRecyclerView? = null
    lateinit var dragItem: KDragItem
    lateinit var dragColumn: KDragItem
    var boardListener: BoardListener? = null
    var boardCallback: BoardCallback? = null
    var snapToColumnWhenScrolling = true
    var snapToColumnWhenDragging = true
        set(value) {
            field = value
            autoScroller.autoScrollMode = if (snapToColumnWhenDragging())
                KAutoScroller.AutoScrollMode.COLUMN
            else
                KAutoScroller.AutoScrollMode.POSITION
        }

    var snapToColumnInLandscape = false
        set(value) {
            field = value
            autoScroller.autoScrollMode = if (snapToColumnWhenDragging())
                KAutoScroller.AutoScrollMode.COLUMN
            else
                KAutoScroller.AutoScrollMode.POSITION
        }

    var snapPosition = ColumnSnapPosition.CENTER

    var currentColumn: Int = 0
    var touchX: Float = 0.toFloat()
    var touchY: Float = 0.toFloat()
    var dragColumnStartScrollX: Float = 0.toFloat()
    var columnWidth: Int = 0
    var dragStartColumn: Int = 0
    var dragStartRow: Int = 0
    var hasLaidOut: Boolean = false
    var dragEnabled = true
        set(value) {
            field = value
            if (lists.size > 0) {
                for (list in lists) {
                    list.dragEnabled = value
                }
            }
        }
    var lastDragColumn = NO_POSITION
    var lastDragRow = NO_POSITION
    private var savedState: SavedState? = null

    val isDraggingColumn: Boolean
        get() = currentRecyclerView != null && dragColumn.isDragging

    val isDragging: Boolean
        get() =
            currentRecyclerView != null && (currentRecyclerView!!.isDragging || isDraggingColumn)


    override fun onFinishInflate() {
        super.onFinishInflate()
        val res = resources
        val isPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        if (isPortrait) columnWidth = (res.displayMetrics.widthPixels * 0.87).toInt()
        else columnWidth = (res.displayMetrics.density * 320).toInt()

        gestureDetector = GestureDetector(context, GestureListener())
        scroller = Scroller(context, DecelerateInterpolator(1.1F))
        autoScroller = KAutoScroller(context, this)
        autoScroller.autoScrollMode =
                if (snapToColumnWhenDragging) KAutoScroller.AutoScrollMode.COLUMN
                else KAutoScroller.AutoScrollMode.POSITION
        dragItem = KDragItem(context)
        dragColumn = KDragItem(context)
        dragColumn.isSnapToTouch = false

        rootLayout = FrameLayout(context)
        rootLayout.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

        columnLayout = LinearLayout(context)
        columnLayout.apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            isMotionEventSplittingEnabled = false
        }

        rootLayout.addView(columnLayout)
        rootLayout.addView(dragItem.dragView)
        addView(rootLayout)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Snap to closes column after first layout.
        // This is needed so correct column is scrolled to after a rotation.
        if (!hasLaidOut && savedState != null) {
            currentColumn = savedState!!.currentColumn!!
            savedState = null

            post { scrollToColumn(currentColumn, false) }
            hasLaidOut = true
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val ss = state as SavedState
        super.onRestoreInstanceState(state)
        savedState = ss
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, if (snapToColumnWhenScrolling) currentColumn else getClosestSnapColumn())
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return handleTouchEvent(event!!) || super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return handleTouchEvent(event!!) || super.onTouchEvent(event)
    }

    fun handleTouchEvent(event: MotionEvent): Boolean {
        if (lists.size == 0) return false

        touchX = event.x
        touchY = event.y

        if (isDragging) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (!autoScroller.isAutoScrolling) updateScrollPosition()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    autoScroller.stopAutoScroll()
                    if (isDraggingColumn) endDragColumn()
                    else currentRecyclerView!!.onDragEnded()

                    if (snapToColumnWhenScrolling) scrollToColumn(getColumnOfList
                    (currentRecyclerView!!), true)
                    invalidate()
                }
            }
            return true
        } else {
            if (snapToColumnWhenScrolling && gestureDetector.onTouchEvent(event)) {
                // A page fling occurred, consume event
                return true
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> if (!scroller.isFinished()) {
                    // View was grabbed during animation
                    scroller.forceFinished(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (snapToColumnWhenScrolling) {
                    scrollToColumn(getClosestSnapColumn(), true)
                }
            }
            return false
        }
    }

    override fun computeScroll() {
        if (!scroller.isFinished && scroller.computeScrollOffset()) {
            val x = scroller.currX
            val y = scroller.currY
            if (scrollX != x || scrollY != y) scrollTo(x, y)

            // If auto scrolling at the same time as the scroller is running,
            // then update the drag item position to prevent stuttering item
            if (autoScroller.isAutoScrolling && isDragging) {
                if (isDraggingColumn) {
                    dragColumn.position = Pair(touchX + scrollX - dragColumnStartScrollX, touchY)
                } else {
                    dragItem.position = Pair(getRelativeViewTouchX(currentRecyclerView!!.parent
                            as View), getRelativeViewTouchY(currentRecyclerView!!))
                }
            }

            ViewCompat.postInvalidateOnAnimation(this)

        } else if (!snapToColumnWhenScrolling) super.computeScroll()
    }

    override fun onAutoScrollPositionBy(dx: Int, dy: Int) {
        if (isDragging) {
            scrollBy(dx, dy)
            updateScrollPosition()
        } else {
            autoScroller.stopAutoScroll()
        }
    }

    override fun onAutoScrollColumnBy(columns: Int) {
        if (isDragging) {
            val newColumn = currentColumn + columns
            if (columns != 0 && newColumn >= 0 && newColumn < lists.size) {
                scrollToColumn(newColumn, true)
            }
            updateScrollPosition()
        } else {
            autoScroller.stopAutoScroll()
        }
    }

    fun updateScrollPosition() {
        if (isDraggingColumn) {
            val currentList = getCurrentRecyclerView(touchX + scrollX)
            if (currentRecyclerView != currentList) moveColumn(getColumnOfList
            (currentRecyclerView!!), getColumnOfList(currentList))
            // Need to subtract with scrollX at the beginning of the column drag because of how drag item position is calculated
            dragColumn.position = Pair(touchX + scrollX - dragColumnStartScrollX, touchY)
        } else {
            // Updated event to scrollview coordinates
            val currentList = getCurrentRecyclerView(touchX + scrollX)
            if (currentRecyclerView != currentList) {
                val oldColumn = getColumnOfList(currentRecyclerView!!)
                val newColumn = getColumnOfList(currentList)
                val itemId = currentRecyclerView!!.dragItemId

                // Check if it is ok to drop the item in the new column first
                val newPosition = currentList.getDragPositionForY(getRelativeViewTouchY(currentList))
                if (boardCallback == null || boardCallback!!.canDropItemAtPosition(dragStartColumn, dragStartRow, newColumn, newPosition)) {
                    val item = currentRecyclerView!!.removeDragItemAndEnd()
                    if (item != null) {
                        currentRecyclerView = currentList
                        currentRecyclerView!!.addDragItemAndStart(getRelativeViewTouchY
                        (currentRecyclerView!!), item, itemId)
                        dragItem.offset = Pair((currentRecyclerView!!.parent as View).left.toFloat(), currentRecyclerView!!.top.toFloat())

                        if (boardListener != null) boardListener!!.onItemChangedColumn(oldColumn, newColumn)
                    }
                }
            }
            // Updated event to list coordinates
            currentRecyclerView!!.onDragging(getRelativeViewTouchX(currentRecyclerView!!.parent as View),
                    getRelativeViewTouchY(currentRecyclerView!!))
        }

        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val scrollEdge = resources.displayMetrics.widthPixels * if (isPortrait) 0.18F else 0.14F
        if (touchX > width - scrollEdge && scrollX < columnLayout.width) {
            autoScroller.startAutoScroll(KAutoScroller.ScrollDirection.LEFT)
        } else if (touchX < scrollEdge && scrollX > 0) {
            autoScroller.startAutoScroll(KAutoScroller.ScrollDirection.RIGHT)
        } else {
            autoScroller.stopAutoScroll()
        }
        invalidate()
    }

    fun getRelativeViewTouchX(view: View): Float {
        return touchX + scrollX - view.left
    }

    fun getRelativeViewTouchY(view: View): Float {
        return touchY - view.top
    }

    private fun getCurrentRecyclerView(x: Float): KDragItemRecyclerView {
        for (list in lists) {
            val parent = list.parent as View
            if (parent.left <= x && parent.right > x) {
                return list
            }
        }
        return currentRecyclerView!!
    }

    private fun getColumnOfList(list: KDragItemRecyclerView): Int {
        var column = 0
        for (i in lists.indices) {
            val tmpList = lists[i]
            if (tmpList === list) {
                column = i
            }
        }
        return column
    }

    fun getCurrentColumn(posX: Float): Int {
        for (i in lists.indices) {
            val list = lists[i]
            val parent = list.parent as View
            if (parent.left <= posX && parent.right > posX) {
                return i
            }
        }
        return 0
    }

    fun getClosestSnapColumn(): Int {
        var column = 0
        var minDiffX = Integer.MAX_VALUE
        for (i in lists.indices) {
            val listParent = lists.get(i).getParent() as View

            var diffX = 0
            when (snapPosition) {
                KBoardView.ColumnSnapPosition.LEFT -> {
                    val leftPosX = scrollX
                    diffX = Math.abs(listParent.left - leftPosX)
                }
                KBoardView.ColumnSnapPosition.CENTER -> {
                    val middlePosX = scrollX + measuredWidth / 2
                    diffX = Math.abs(listParent.left + columnWidth / 2 - middlePosX)
                }
                KBoardView.ColumnSnapPosition.RIGHT -> {
                    val rightPosX = scrollX + measuredWidth
                    diffX = Math.abs(listParent.right - rightPosX)
                }
            }

            if (diffX < minDiffX) {
                minDiffX = diffX
                column = i
            }
        }
        return column
    }

    fun snapToColumnWhenScrolling(): Boolean {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return snapToColumnWhenScrolling && (isPortrait || snapToColumnInLandscape)
    }

    fun snapToColumnWhenDragging(): Boolean {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return snapToColumnWhenDragging && (isPortrait || snapToColumnInLandscape)
    }

    fun getRecyclerView(column: Int): RecyclerView? {
        return if (column >= 0 && column < lists.size) {
            lists.get(column)
        } else null
    }

    fun getAdapter(column: Int): KDragItemAdapter<*, *>? {
        return if (column >= 0 && column < lists.size) {
            lists.get(column).adapter
        } else null
    }

    fun getItemCount(): Int {
        var count = 0
        for (list in lists) {
            count += list.getAdapter()!!.getItemCount()
        }
        return count
    }

    fun getItemCount(column: Int): Int {
        return if (lists.size > column) {
            lists.get(column).getAdapter()!!.getItemCount()
        } else 0
    }

    fun getColumnCount(): Int {
        return lists.size
    }

    fun getHeaderView(column: Int): View {
        return headers.get(column)
    }

    fun getColumnOfHeader(header: View): Int {
        for (i in headers.indices) {
            if (headers.get(i) === header) {
                return i
            }
        }
        return -1
    }

    fun removeItem(column: Int, row: Int) {
        if (!isDragging && lists.size > column && lists.get(column).getAdapter()!!.getItemCount() > row) {
            val adapter = lists.get(column).getAdapter() as KDragItemAdapter<*, *>
            adapter.removeItem(row)
        }
    }

    fun addItem(column: Int, row: Int, item: Any, scrollToItem: Boolean) {
        if (!isDragging && lists.size > column && lists.get(column).getAdapter()!!.getItemCount() >= row) {
            val adapter = lists.get(column).getAdapter() as KDragItemAdapter<Any, *>
            adapter.addItem(row, item)
            if (scrollToItem) {
                scrollToItem(column, row, false)
            }
        }
    }

    fun moveItem(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int, scrollToItem: Boolean) {
        if (!isDragging && lists.size > fromColumn && lists.get(fromColumn).getAdapter()!!
                        .getItemCount() > fromRow
                && lists.size > toColumn && lists.get(toColumn).getAdapter()!!.getItemCount() >=
                toRow) {
            var adapter = lists.get(fromColumn).adapter
            val item = adapter!!.removeItem(fromRow)
            adapter = lists.get(toColumn).adapter
            adapter!!.addItem(toRow, item!!)
            if (scrollToItem) {
                scrollToItem(toColumn, toRow, false)
            }
        }
    }

    fun moveItem(itemId: Long, toColumn: Int, toRow: Int, scrollToItem: Boolean) {
        for (i in lists.indices) {
            val adapter = lists.get(i).adapter
            val count = adapter!!.getItemCount()
            for (j in 0 until count) {
                val id = adapter!!.getItemId(j)
                if (id == itemId) {
                    moveItem(i, j, toColumn, toRow, scrollToItem)
                    return
                }
            }
        }
    }

    fun replaceItem(column: Int, row: Int, item: Any, scrollToItem: Boolean) {
        if (!isDragging && lists.size > column && lists.get(column).adapter!!.getItemCount() > row) {
            val adapter = lists.get(column).adapter
            adapter!!.removeItem(row)
            adapter.addItem(row, item)
            if (scrollToItem) {
                scrollToItem(column, row, false)
            }
        }
    }

    fun scrollToItem(column: Int, row: Int, animate: Boolean) {
        if (!isDragging && lists.size > column && lists.get(column).adapter!!.getItemCount() > row) {
            scroller.forceFinished(true)
            scrollToColumn(column, animate)
            if (animate) {
                lists.get(column).smoothScrollToPosition(row)
            } else {
                lists.get(column).scrollToPosition(row)
            }
        }
    }

    fun scrollToColumn(column: Int, animate: Boolean) {
        if (lists.size <= column) {
            return
        }

        val parent = lists.get(column).getParent() as View
        var newX = 0
        when (snapPosition) {
            KBoardView.ColumnSnapPosition.LEFT -> newX = parent.left
            KBoardView.ColumnSnapPosition.CENTER -> newX = parent.left - (measuredWidth - parent.measuredWidth) / 2
            KBoardView.ColumnSnapPosition.RIGHT -> newX = parent.right - measuredWidth
        }

        val maxScroll = rootLayout.getMeasuredWidth() - measuredWidth
        newX = if (newX < 0) 0 else newX
        newX = if (newX > maxScroll) maxScroll else newX
        if (scrollX != newX) {
            scroller.forceFinished(true)
            if (animate) {
                scroller.startScroll(scrollX, scrollY, newX - scrollX, 0, scrollAnimationDuration)
                ViewCompat.postInvalidateOnAnimation(this)
            } else {
                scrollTo(newX, scrollY)
            }
        }

        val oldColumn = currentColumn
        currentColumn = column
        if (boardListener != null && oldColumn != currentColumn) {
            boardListener!!.onFocusedColumnChanged(oldColumn, currentColumn)
        }
    }

    fun clearBoard() {
        val count = lists.size
        for (i in count - 1 downTo 0) {
            columnLayout.removeViewAt(i)
            headers.removeAt(i)
            lists.removeAt(i)
        }
    }

    fun removeColumn(column: Int) {
        if (column >= 0 && lists.size > column) {
            columnLayout.removeViewAt(column)
            headers.removeAt(column)
            lists.removeAt(column)
        }
    }

    fun getFocusedColumn(): Int {
        return if (!snapToColumnWhenScrolling()) {
            0
        } else currentColumn
    }

    fun setSnapDragItemToTouch(snapToTouch: Boolean) {
        dragItem.isSnapToTouch = snapToTouch
    }

    fun setCustomDragItem(dragItem: KDragItem?) {
        val newDragItem = dragItem ?: KDragItem(context)
        newDragItem.isSnapToTouch = dragItem!!.isSnapToTouch
        this.dragItem = newDragItem
        rootLayout.removeViewAt(1)
        rootLayout.addView(this.dragItem.dragView)
    }

    fun setCustomColumnDragItem(dragItem: KDragItem?) {
        dragColumn = dragItem ?: KDragItem(context)
    }

    fun startDragColumn(recyclerView: KDragItemRecyclerView, posX: Float, posY: Float) {
        dragColumnStartScrollX = scrollX.toFloat()
        currentRecyclerView = recyclerView

        val columnView = columnLayout.getChildAt(getColumnOfList(recyclerView))
        dragColumn.startDrag(columnView, posX, posY)
        rootLayout.addView(dragColumn.dragView)
        columnView.alpha = 0F

        if (boardListener != null) {
            boardListener!!.onColumnDragStarted(getColumnOfList(currentRecyclerView!!))
        }
    }

    fun endDragColumn() {
        dragColumn.endDrag(dragColumn.realDragView!!, object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                dragColumn.realDragView!!.setAlpha(1f)
                dragColumn.hide()
                rootLayout.removeView(dragColumn.dragView)

                if (boardListener != null) {
                    boardListener!!.onColumnDragEnded(getColumnOfList(currentRecyclerView!!))
                }
            }
        })
    }

    fun moveColumn(fromIndex: Int, toIndex: Int) {
        val list = lists.removeAt(fromIndex)
        lists.add(toIndex, list)

        val header = headers.removeAt(fromIndex)
        headers.add(toIndex, header)

        val column1 = columnLayout.getChildAt(fromIndex)
        val column2 = columnLayout.getChildAt(toIndex)
        columnLayout.removeViewAt(fromIndex)
        columnLayout.addView(column1, toIndex)

        columnLayout.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                columnLayout.removeOnLayoutChangeListener(this)
                column2.setTranslationX(column2.getTranslationX() + column1.getLeft() - column2.getLeft())
                column2.animate().translationX(0f).setDuration(350).start()
            }
        })

        if (boardListener != null) {
            boardListener!!.onColumnDragChangedPosition(fromIndex, toIndex)
        }
    }

    fun insertColumn(adapter: KDragItemAdapter<Any, KDragItemAdapter.ViewHolder>, index: Int, header: View?,
                     columnDragView:
                     View?, hasFixedItemSize: Boolean): KDragItemRecyclerView {
        val recyclerView = insertColumn(adapter, index, header, hasFixedItemSize)
        setupColumnDragListener(columnDragView, recyclerView)
        return recyclerView
    }

    fun <T, VH : KDragItemAdapter.ViewHolder>
            addColumn(adapter: KDragItemAdapter<T, VH>, header: View?,
                      columnDragView: View?, hasFixedItemSize: Boolean): KDragItemRecyclerView {
        val recyclerView = insertColumn(adapter, getColumnCount(), header, hasFixedItemSize)
        setupColumnDragListener(columnDragView, recyclerView)
        return recyclerView
    }

    private fun setupColumnDragListener(columnDragView: View?, recyclerView: KDragItemRecyclerView) {
        columnDragView?.setOnLongClickListener {
            startDragColumn(recyclerView, touchX, touchY)
            true
        }
    }

    fun <T, VH : KDragItemAdapter.ViewHolder> insertColumn(adapter: KDragItemAdapter<T, VH>, index: Int, header: View?,
                                                           hasFixedItemSize: Boolean): KDragItemRecyclerView {
        if (index > getColumnCount()) {
            throw IllegalArgumentException("Index is out of bounds")
        }

        val recyclerView = LayoutInflater.from(context).inflate(R.layout.drag_item_recycler_view, this, false) as KDragItemRecyclerView
        recyclerView.id = getColumnCount()
        recyclerView.isHorizontalScrollBarEnabled = false
        recyclerView.isVerticalScrollBarEnabled = false
        recyclerView.isMotionEventSplittingEnabled = false
        recyclerView.dragItem = dragItem
        recyclerView.layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(hasFixedItemSize)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.dragItemListener = object : KDragItemRecyclerView.DragItemListener {
            override fun onDragStarted(itemPosition: Int, x: Float, y: Float) {
                dragStartColumn = getColumnOfList(recyclerView)
                dragStartRow = itemPosition
                currentRecyclerView = recyclerView
                dragItem.offset = Pair((currentRecyclerView!!.parent as View).x, currentRecyclerView!!.y)
                if (boardListener != null) {
                    boardListener!!.onItemDragStarted(dragStartColumn, dragStartRow)
                }
                invalidate()
            }

            override fun onDragging(itemPosition: Int, x: Float, y: Float) {
                val column = getColumnOfList(recyclerView)
                val positionChanged = column != lastDragColumn || itemPosition != lastDragRow
                if (boardListener != null && positionChanged) {
                    lastDragColumn = column
                    lastDragRow = itemPosition
                    boardListener!!.onItemChangedPosition(dragStartColumn, dragStartRow, column, itemPosition)
                }
            }

            override fun onDragEnded(newItemPosition: Int) {
                lastDragColumn = NO_POSITION
                lastDragRow = NO_POSITION
                if (boardListener != null) {
                    boardListener!!.onItemDragEnded(dragStartColumn, dragStartRow, getColumnOfList
                    (recyclerView), newItemPosition)
                }
            }
        }

        recyclerView.dragItemCallback = object : KDragItemRecyclerView.DragItemCallback {
            override fun canDragItemAtPosition(dragPosition: Int): Boolean {
                val column = getColumnOfList(recyclerView)
                return boardCallback == null || boardCallback!!.canDragItemAtPosition(column, dragPosition)
            }

            override fun canDropItemAtPosition(dropPosition: Int): Boolean {
                val column = getColumnOfList(recyclerView)
                return boardCallback == null || boardCallback!!.canDropItemAtPosition(dragStartColumn,
                        dragStartRow, column, dropPosition)
            }
        }

        recyclerView.setAdapter(adapter)
        recyclerView.dragEnabled = this.dragEnabled
        adapter.dragStartCallback = (object : KDragItemAdapter.DragStartCallback {
            override fun startDrag(itemView: View, itemId: Long): Boolean {
                return recyclerView.startDrag(itemView, itemId, getRelativeViewTouchX(recyclerView.parent as View), getRelativeViewTouchY(recyclerView))
            }

            override val isDragging: Boolean
                get() = recyclerView.isDragging

        })

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = FrameLayout.LayoutParams(columnWidth, FrameLayout.LayoutParams.MATCH_PARENT)
        var columnHeader = header
        if (header == null) {
            columnHeader = View(context)
            columnHeader.visibility = View.GONE
        }
        layout.addView(columnHeader)
        headers.add(columnHeader!!)

        layout.addView(recyclerView)

        lists.add(index, recyclerView)
        columnLayout.addView(layout, index)
        return recyclerView
    }

    //region Classes and Interfaces

    enum class ColumnSnapPosition {
        LEFT, CENTER, RIGHT
    }

    interface BoardListener {
        fun onItemDragStarted(column: Int, row: Int)

        fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int)

        fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int)

        fun onItemChangedColumn(oldColumn: Int, newColumn: Int)

        fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int)

        fun onColumnDragStarted(position: Int)

        fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int)

        fun onColumnDragEnded(position: Int)
    }

    interface BoardCallback {
        fun canDragItemAtPosition(column: Int, row: Int): Boolean

        fun canDropItemAtPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int): Boolean
    }

    abstract class BoardListenerAdapter : BoardListener {
        override fun onItemDragStarted(column: Int, row: Int) {}

        override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {}

        override fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int) {}

        override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {}

        override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {}

        override fun onColumnDragStarted(position: Int) {}

        override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {}

        override fun onColumnDragEnded(position: Int) {}
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private var startScrollX = 0F
        private var startColumn = 0

        override fun onDown(e: MotionEvent?): Boolean {
            startScrollX = scrollX.toFloat()
            startColumn = currentColumn
            return super.onDown(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val closestColumn = getClosestSnapColumn()
            var newColumn = closestColumn

            // This can happen if you start to drag in one direction and then fling in the other direction.
            // We should then switch column in the fling direction.
            val wrongSnapDirection = newColumn > startColumn && velocityX > 0 ||
                    newColumn < startColumn && velocityX < 0

            if (startScrollX == scrollX.toFloat()) newColumn = startColumn
            else if (startColumn == closestColumn || wrongSnapDirection) {
                if (velocityX < 0) newColumn = closestColumn + 1
                else newColumn = closestColumn - 1
            }

            if (newColumn < 0 || newColumn > lists.size - 1)
                newColumn = if (newColumn < 0) 0 else lists.size - 1

            // Calc new scrollX position
            scrollToColumn(newColumn, true)
            return true
        }
    }

    internal class SavedState(source: Parcel?) : BaseSavedState(source) {
        var currentColumn = source?.readInt()

        //This can't work because Kotlin :/
        constructor(superState: Parcelable, currentColumn: Int) : this(Parcel.obtain()) {
            this.currentColumn = currentColumn
        }


        override fun describeContents() = 0

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(currentColumn!!)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    //endregion Classes and Interfaces
}

