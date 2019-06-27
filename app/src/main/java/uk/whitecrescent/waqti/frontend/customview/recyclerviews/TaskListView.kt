@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.AbstractWaqtiList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.TASKS_CACHE_SIZE
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.convertDpToPx
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.frontend.startDragCompat
import uk.whitecrescent.waqti.lastPosition
import uk.whitecrescent.waqti.locationOnScreen
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.notifySwapped
import kotlin.math.roundToInt

private val taskViewHolderPool = object : RecyclerView.RecycledViewPool() {

    override fun setMaxRecycledViews(viewType: Int, max: Int) {
        super.setMaxRecycledViews(viewType, TASKS_CACHE_SIZE)
    }
}

private const val scrollAmount = 1.718281828459045 // E - 1
private const val draggingViewAlpha = 0F
private val defaultInterpolator = AccelerateDecelerateInterpolator()

class TaskListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    inline val listAdapter: TaskListAdapter
        get() = adapter as TaskListAdapter

    init {
        layoutManager = LinearLayoutManager(context, VERTICAL, false).also {
            it.isItemPrefetchEnabled = true
            it.initialPrefetchItemCount = 25
        }
        setRecycledViewPool(taskViewHolderPool)
        itemAnimator = TaskListItemAnimator()
    }

}

class TaskListAdapter(val taskListID: ID,
                      val boardAdapter: BoardAdapter) : Adapter<TaskViewHolder>() {

    lateinit var taskListView: TaskListView

    val taskList = Caches.taskLists[taskListID]

    inline val linearLayoutManager: LinearLayoutManager
        get() = taskListView.layoutManager as LinearLayoutManager

    inline val allCards: List<CardView>
        get() = taskListView.children.map { it as CardView }.toList()

    private var savedState: LinearLayoutManager.SavedState? = null

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        check(recyclerView is TaskListView)
        taskListView = recyclerView

        taskListView.apply {
            restoreState()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        saveState()
                    }
                }
            })
            setOnDragListener { _, event ->
                val draggingState = event.localState as DragEventLocalState
                when (event.action) {
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        if (this@TaskListAdapter.taskListID != draggingState.taskListID) {
                            val lastViewHolderY = taskListView.findViewHolderForAdapterPosition(lastPosition)?.itemView?.y
                            if (taskList.isEmpty() || (lastViewHolderY != null && event.y > lastViewHolderY)) {
                                val otherAdapter = boardAdapter.getListAdapter(draggingState.taskListID)
                                if (otherAdapter != null) {
                                    onDragAcrossEmptyList(draggingState, otherAdapter)
                                    onScrollAcrossEmptyList()
                                }
                            }
                        }
                    }
                }
                true
            }
        }
    }

    // This can be used to find the closest ViewHolder to the current touch position
    // so that we can do stuff with it.
    // This was made as a way to possibly replace the current dragging method but more importantly
    // to fix the dragging over an empty area problem, this function will essentially return
    // the ViewHolder vertically closest to the given point but within the margin offset,
    // currently 8dp.
    // If we call this and it returns null, but our list is not empty, we can safely assume the
    // user is dragging over the empty part of a list at the bottom, (not an empty list though)
    // we can then act accordingly
    private inline fun RecyclerView.findVerticallyClosestChildViewHolderUnderTouchPoint(
            touchPoint: Point): ViewHolder? {
        val margin = convertDpToPx(8, context)
        val localPoint = Point()

        val rvRect = Rect()
        this.getGlobalVisibleRect(rvRect)
        localPoint.apply {
            x = touchPoint.x - rvRect.left
            y = touchPoint.y - rvRect.top
        }
        (0..margin).forEach {
            findChildViewUnder(localPoint.x.toFloat(), localPoint.y.toFloat() + it).also {
                if (it != null) return findContainingViewHolder(it)
            }
            findChildViewUnder(localPoint.x.toFloat(), localPoint.y.toFloat() - it).also {
                if (it != null) return findContainingViewHolder(it)
            }
            findChildViewUnder(localPoint.x.toFloat() + it, localPoint.y.toFloat()).also {
                if (it != null) return findContainingViewHolder(it)
            }
            findChildViewUnder(localPoint.x.toFloat() - it, localPoint.y.toFloat()).also {
                if (it != null) return findContainingViewHolder(it)
            }
        }
        return null
    }

    override fun getItemCount() = taskList.size

    override fun getItemId(position: Int) = taskList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false),
                this
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.apply {
            taskID = taskList[position].id
            taskListID = this@TaskListAdapter.taskListID
            textView.text = taskList[position].name

            // TODO: 18-Jun-19 If we remove this from onBind
            //  we'll have the problem that the holder has the old onDragListener
            //  the only problem with that is the IDs are incorrect on that one,
            //  the problem appears when dragging across a few times then dragging down
            //  the correct list stops behaving and another list will do the correct behaviour
            cardView.setOnDragListener { _, event ->
                val draggingState = event.localState as DragEventLocalState
                val draggingView = taskListView
                        .findViewHolderForAdapterPosition(draggingState.adapterPosition)?.itemView
                when (event.action) {
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onDrag(draggingState, this)
                        }
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        if (draggingView?.alpha != draggingViewAlpha)
                            draggingView?.alpha = draggingViewAlpha
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        // Safety measure just in case
                        doInBackground {
                            allCards.filter { it != draggingView && it.alpha < 1F }
                                    .forEach { it.alpha = 1F }
                        }
                    }
                    DragEvent.ACTION_DROP -> {
                        draggingState.updateToMatch(this)
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        draggingView?.alpha = 1F
                        // Safety measure just in case
                        doInBackground { allCards.forEach { it.alpha = 1F } }
                    }
                }
                return@setOnDragListener true

            }
        }
    }

    override fun onViewAttachedToWindow(holder: TaskViewHolder) {
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(taskListView.context,
                R.anim.task_list_item_show_anim))
    }

    private inline fun onDrag(draggingState: DragEventLocalState, holder: TaskViewHolder): Boolean {
        return when {

            draggingState.taskListID == holder.taskListID -> {
                // they are the same
                if (draggingState.adapterPosition == holder.adapterPosition) {
                    false
                }
                // they are diff but in same list
                else {
                    onDragInSameList(draggingState, holder)

                    // Scroll down
                    checkForScrollDown(draggingState, holder)

                    // Scroll up
                    checkForScrollUp(draggingState, holder)

                    true
                }
            }

            // they are in diff lists
            draggingState.taskListID != holder.taskListID -> {

                val otherAdapter = boardAdapter.getListAdapter(draggingState.taskListID)
                if (otherAdapter != null) {

                    onDragAcrossFilledList(draggingState, holder, otherAdapter)

                    onScrollAcrossFilledList()

                    // Scroll down
                    checkForScrollDown(draggingState, holder)

                    // Scroll up
                    checkForScrollUp(draggingState, holder)

                    true
                } else false
            }

            // impossible
            else -> {
                false
            }

        }
    }

    private inline fun onDragInSameList(draggingState: DragEventLocalState, holder: TaskViewHolder) {
        val newDragPos = holder.adapterPosition
        val oldDragPos = draggingState.adapterPosition

        taskList.swap(oldDragPos, newDragPos).update()
        notifySwapped(oldDragPos, newDragPos)

        draggingState.adapterPosition = newDragPos
    }

    private inline fun checkForScrollDown(draggingState: DragEventLocalState, holder: TaskViewHolder) {

        if (draggingState.adapterPosition >= linearLayoutManager.findLastCompletelyVisibleItemPosition()) {
            val scrollBy = (holder.itemView.height * scrollAmount).roundToInt()
            taskListView.smoothScrollBy(0, scrollBy, defaultInterpolator)
        }

    }

    private inline fun checkForScrollUp(draggingState: DragEventLocalState, holder: TaskViewHolder) {

        if (draggingState.adapterPosition <= linearLayoutManager.findFirstCompletelyVisibleItemPosition()) {
            val scrollBy = (holder.itemView.height * -scrollAmount).roundToInt()
            taskListView.smoothScrollBy(0, scrollBy, defaultInterpolator)
        }

    }

    private inline fun onDragAcrossFilledList(draggingState: DragEventLocalState,
                                              holder: TaskViewHolder,
                                              otherAdapter: TaskListAdapter) {

        val otherTaskList = otherAdapter.taskList
        val task = otherTaskList[draggingState.taskID]
        val newDragPos = holder.adapterPosition

        AbstractWaqtiList.moveElement(
                listFrom = otherTaskList, listTo = this.taskList,
                element = task, toIndex = newDragPos
        )

        this.notifyItemInserted(newDragPos)
        otherAdapter.notifyItemRemoved(draggingState.adapterPosition)

        draggingState.taskListID = holder.taskListID
        draggingState.adapterPosition = newDragPos

        // TODO: 13-Jun-19 Add an onDragListener to the list
        //  so that when you're dragging a task over into the white part of the list
        //  it will still react, we'd need to know the point we're dragging over
        //  and what is the holder that is closest to it so that we act accordingly
    }

    private inline fun onDragAcrossEmptyList(draggingState: DragEventLocalState,
                                             otherAdapter: TaskListAdapter) {

        val otherTaskList = otherAdapter.taskList
        val task = otherTaskList[draggingState.taskID]
        val newDragPos = this.taskList.nextIndex

        AbstractWaqtiList.moveElement(
                listFrom = otherTaskList, listTo = this.taskList,
                element = task, toIndex = newDragPos
        )

        this.notifyItemInserted(newDragPos)
        otherAdapter.notifyItemRemoved(draggingState.adapterPosition)

        draggingState.taskListID = this.taskListID
        draggingState.adapterPosition = newDragPos
    }

    private inline fun onScrollAcrossFilledList() {

        taskListView.apply {
            // The list that we will be scrolling to
            val newBoardPosition = boardAdapter.indexOfAdapter(this@TaskListAdapter)

            val currentBoardPosition = mainActivityViewModel.boardPosition.position

            if (newBoardPosition > currentBoardPosition) scrollRight(currentBoardPosition)
            else if (newBoardPosition < currentBoardPosition) scrollLeft(currentBoardPosition)

            mainActivityViewModel.boardPosition.changeTo(true to newBoardPosition)

        }
    }

    private inline fun onScrollAcrossEmptyList() {

        taskListView.apply {
            // The list that we will be scrolling to
            val newBoardPosition = boardAdapter.indexOfAdapter(this@TaskListAdapter)

            val currentBoardPosition = mainActivityViewModel.boardPosition.position

            if (newBoardPosition > currentBoardPosition) scrollRight(currentBoardPosition)
            else if (newBoardPosition < currentBoardPosition) scrollLeft(currentBoardPosition)

            mainActivityViewModel.boardPosition.changeTo(true to newBoardPosition)

        }
    }

    private inline fun scrollLeft(currentBoardPosition: Int) {
        taskListView.apply {
            val scrollBy: Int

            val screenWidth = mainActivity.dimensions.first

            val listWidth = boardAdapter.taskListWidth

            if (currentBoardPosition == boardAdapter.lastPosition) {
                scrollBy = (listWidth) - ((screenWidth - listWidth) / 2)
            } else scrollBy = listWidth

            boardAdapter.boardView.smoothScrollBy(-scrollBy, 0, defaultInterpolator)
        }
    }

    private inline fun scrollRight(currentBoardPosition: Int) {
        taskListView.apply {
            val scrollBy: Int

            val screenWidth = mainActivity.dimensions.first

            val listWidth = boardAdapter.taskListWidth

            if (currentBoardPosition == 0) {
                scrollBy = (listWidth) - ((screenWidth - listWidth) / 2)
            } else scrollBy = listWidth

            boardAdapter.boardView.smoothScrollBy(scrollBy, 0, defaultInterpolator)
        }
    }

    fun saveState() {
        if (::taskListView.isInitialized) {
            savedState = taskListView.layoutManager?.onSaveInstanceState()
                    as LinearLayoutManager.SavedState
        }
    }

    fun restoreState() {
        if (::taskListView.isInitialized) {
            taskListView.layoutManager?.onRestoreInstanceState(savedState)
        }
    }

    fun invalidate() {
        if (::taskListView.isInitialized) {
            taskListView.invalidate()
        }
    }

}

data class DragEventLocalState(
        var taskID: ID,
        var taskListID: ID,
        var adapterPosition: Int) {

    inline fun updateToMatch(viewHolder: TaskViewHolder) {
        this.taskID = viewHolder.taskID
        this.taskListID = viewHolder.taskListID
        this.adapterPosition = viewHolder.adapterPosition
    }

    inline infix fun doesNotMatch(viewHolder: TaskViewHolder): Boolean {
        return this.taskID != viewHolder.taskID ||
                this.taskListID != viewHolder.taskListID ||
                this.adapterPosition != viewHolder.adapterPosition
    }

    inline infix fun matches(viewHolder: TaskViewHolder): Boolean {
        return this.taskID == viewHolder.taskID &&
                this.taskListID == viewHolder.taskListID &&
                this.adapterPosition == viewHolder.adapterPosition
    }

}

private class ShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)

        val viewPoint = view.locationOnScreen

        val x = view.mainActivity.currentTouchPoint.x - viewPoint.x
        val y = view.mainActivity.currentTouchPoint.y - viewPoint.y
        outShadowTouchPoint?.set(x, y)
    }

    override fun onDrawShadow(canvas: Canvas?) {
        super.onDrawShadow(canvas)
        canvas?.drawColor(view.solidColor, PorterDuff.Mode.DST)
    }

}

class TaskViewHolder(view: View, private val adapter: TaskListAdapter) : ViewHolder(view) {

    var taskID: ID = 0L
    var taskListID: ID = adapter.taskListID
    val cardView: CardView = itemView.task_cardView
    val progressBar: ProgressBar = itemView.taskCard_progressBar
    val textView: TextView = itemView.task_textView

    inline val mainActivity: MainActivity get() = itemView.mainActivity

    init {
        doInBackground {
            textView.textSize = mainActivity.waqtiPreferences.taskCardTextSize.toFloat()
            cardView.apply {
                setCardBackgroundColor(Caches.boards[mainActivityViewModel.boardID].cardColor.toAndroidColor)
                setOnClickListener {
                    @GoToFragment
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivityViewModel.taskID = taskID
                        it.mainActivityViewModel.listID = taskListID

                        it.clearFocusAndHideSoftKeyboard()

                        addToBackStack(null)
                        replace(R.id.fragmentContainer, ViewTaskFragment(), VIEW_TASK_FRAGMENT)
                    }
                }
                setOnLongClickListener {
                    it.clearFocusAndHideSoftKeyboard()
                    it.startDragCompat(
                            ClipData.newPlainText("", ""),
                            ShadowBuilder(this),
                            DragEventLocalState(taskID, taskListID, adapterPosition),
                            View.DRAG_FLAG_OPAQUE
                    )
                    return@setOnLongClickListener true
                }
            }
            textView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}

class TaskListItemAnimator : DefaultItemAnimator() {

    override fun animateAdd(holder: ViewHolder?): Boolean {
        holder?.itemView?.alpha = draggingViewAlpha
        dispatchAddFinished(holder)
        return true
    }
}
