@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.doInBackgroundDelayed
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.frontend.startDragCompat
import uk.whitecrescent.waqti.lastPosition
import uk.whitecrescent.waqti.locationOnScreen
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.notifySwapped
import kotlin.math.roundToInt

private const val scrollAmount = 1.718281828459045 // E - 1
private const val draggingViewAlpha = 0F
private val defaultInterpolator = AccelerateDecelerateInterpolator()

private val taskViewHolderPool = object : RecyclerView.RecycledViewPool() {

    override fun setMaxRecycledViews(viewType: Int, max: Int) {
        super.setMaxRecycledViews(viewType, TASKS_CACHE_SIZE)
    }
}

open class TaskListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    inline val listAdapter: TaskListAdapter
        get() = adapter as TaskListAdapter

    inline val boardView: BoardView
        get() = parent.parent as BoardView

    init {
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        itemAnimator = TaskListItemAnimator()
        this.setRecycledViewPool(taskViewHolderPool)
    }

}

class TaskListAdapter(var taskListID: ID) : Adapter<TaskViewHolder>() {

    lateinit var taskListView: TaskListView

    val taskList = Caches.taskLists[taskListID]

    inline val linearLayoutManager: LinearLayoutManager
        get() = taskListView.layoutManager as LinearLayoutManager

    inline val allCards: List<CardView>
        get() = taskListView.children.map { it as CardView }.toList()

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        check(recyclerView is TaskListView)
        taskListView = recyclerView

        taskListView.setOnDragListener { _, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (this.taskListID != draggingState.taskListID &&
                            draggingState.adapterPosition > lastPosition) {
                        val otherAdapter = taskListView.boardView.getListAdapter(draggingState.taskListID)
                        if (otherAdapter != null) {
                            onDragAcrossEmptyList(draggingState, otherAdapter)
                            onScrollAcrossEmptyList(draggingState, otherAdapter)
                        }
                    }

                }
            }
            true
        }
    }

    override fun getItemCount() = taskList.size

    override fun getItemId(position: Int) = taskList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.itemView.apply {
            setOnDragListener { _, event ->
                val draggingState = event.localState as DragEventLocalState
                val draggingView = taskListView.findViewHolderForAdapterPosition(draggingState.adapterPosition)?.itemView
                when (event.action) {
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                            onDrag(draggingState, holder)
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
                        draggingState.updateToMatch(holder)
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

        // simulated lag/delay
        holder.itemView.doInBackgroundDelayed(750) {
            holder.taskID = taskList[position].id
            holder.taskListID = this@TaskListAdapter.taskListID

            task_textView.text = taskList[position].name
            task_textView.textSize =
                    mainActivity.waqtiPreferences.taskCardTextSize.toFloat()
            if (this is CardView)
                setCardBackgroundColor(Caches.boards[mainActivity.viewModel.boardID].cardColor.toAndroidColor)
            // onDragListener is basically like, do this when someone is dragging on top of you
            setOnClickListener {
                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivity.viewModel.taskID = holder.taskID
                    it.mainActivity.viewModel.listID = taskListID

                    it.clearFocusAndHideSoftKeyboard()

                    addToBackStack("")
                    replace(R.id.fragmentContainer, ViewTaskFragment(), VIEW_TASK_FRAGMENT)
                }
            }
            setOnLongClickListener {
                it.clearFocusAndHideSoftKeyboard()
                it.startDragCompat(
                        null,
                        ShadowBuilder(it.task_materialCardView),
                        DragEventLocalState(holder.taskID, holder.taskListID, holder.adapterPosition),
                        View.DRAG_FLAG_OPAQUE
                )
                return@setOnLongClickListener true
            }
            taskCard_progressBar.visibility = View.GONE
            taskCard_parent.visibility = View.VISIBLE
        }
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

                val otherAdapter = taskListView.boardView.getListAdapter(draggingState.taskListID)
                if (otherAdapter != null) {

                    onDragAcrossFilledList(draggingState, holder, otherAdapter)

                    onScrollAcrossFilledList(draggingState, holder, otherAdapter)

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

        // TODO: 12-Jun-19 Bug probably here, drag from left list to right many times,
        //  then without letting go all the way back to left, you'll notice duplicates appearing

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

    private inline fun onDragAcrossEmptyList(draggingState: DragEventLocalState, otherAdapter: TaskListAdapter) {

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

    private inline fun onScrollAcrossFilledList(draggingState: DragEventLocalState,
                                                holder: TaskViewHolder,
                                                otherAdapter: TaskListAdapter) {

        taskListView.boardView.apply {
            // The list that we will be scrolling to
            val newBoardPosition = boardAdapter.board.indexOf(this@TaskListAdapter.taskListID)

            val currentBoardPosition = mainActivity.viewModel.boardPosition.second

            if (newBoardPosition > currentBoardPosition) scrollRight(currentBoardPosition)
            else if (newBoardPosition < currentBoardPosition) scrollLeft(currentBoardPosition)

            mainActivity.viewModel.boardPosition = true to newBoardPosition

        }
    }

    private inline fun onScrollAcrossEmptyList(draggingState: DragEventLocalState, otherAdapter: TaskListAdapter) {

        taskListView.boardView.apply {
            // The list that we will be scrolling to
            val newBoardPosition = boardAdapter.board.indexOf(this@TaskListAdapter.taskListID)

            val currentBoardPosition = mainActivity.viewModel.boardPosition.second

            if (newBoardPosition > currentBoardPosition) scrollRight(currentBoardPosition)
            else if (newBoardPosition < currentBoardPosition) scrollLeft(currentBoardPosition)

            mainActivity.viewModel.boardPosition = true to newBoardPosition

        }
    }

    private inline fun scrollLeft(currentBoardPosition: Int) {
        taskListView.boardView.apply {
            val scrollBy: Int

            val percent = mainActivity.waqtiPreferences.taskListWidth / 100.0

            val screenWidth = mainActivity.dimensions.first

            val listWidth = (screenWidth.toFloat() * percent).roundToInt()

            if (currentBoardPosition == boardAdapter.lastPosition) {
                scrollBy = (listWidth) - ((screenWidth - listWidth) / 2)
            } else scrollBy = listWidth

            taskListView.boardView.smoothScrollBy(-scrollBy, 0, defaultInterpolator)
        }
    }

    private inline fun scrollRight(currentBoardPosition: Int) {
        taskListView.boardView.apply {
            val scrollBy: Int

            val percent = mainActivity.waqtiPreferences.taskListWidth / 100.0

            val screenWidth = mainActivity.dimensions.first

            val listWidth = (screenWidth.toFloat() * percent).roundToInt()

            if (currentBoardPosition == 0) {
                scrollBy = (listWidth) - ((screenWidth - listWidth) / 2)
            } else scrollBy = listWidth

            taskListView.boardView.smoothScrollBy(scrollBy, 0, defaultInterpolator)
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

class TaskViewHolder(view: View) : ViewHolder(view) {

    //the ID of the Task that this ViewHolder contains
    var taskID: ID = 0L

    //the ID of the TaskList that this ViewHolder's Task is in
    var taskListID: ID = 0L
}

class TaskListItemAnimator : DefaultItemAnimator() {

    override fun animateAdd(holder: ViewHolder?): Boolean {
        holder?.itemView?.alpha = draggingViewAlpha
        dispatchAddFinished(holder)
        return true
    }
}
