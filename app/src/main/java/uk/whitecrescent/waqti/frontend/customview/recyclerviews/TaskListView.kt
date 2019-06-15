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
import android.view.View.DRAG_FLAG_OPAQUE
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.cardview.widget.CardView
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.AbstractWaqtiList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.TASK_CARD_TEXT_SIZE
import uk.whitecrescent.waqti.frontend.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.frontend.startDragCompat
import uk.whitecrescent.waqti.lastPosition
import uk.whitecrescent.waqti.locationOnScreen
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.notifySwapped
import kotlin.math.roundToInt

private const val animationDuration = 250L
private const val scrollAmount = 1.718281828459045 // E - 1
private const val draggingViewAlpha = 0F

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
    }

}

class TaskListAdapter(var taskListID: ID) :
        Adapter<TaskViewHolder>() {

    lateinit var taskListView: TaskListView

    val taskList = Caches.taskLists[taskListID]

    inline val linearLayoutManager: LinearLayoutManager
        get() = taskListView.layoutManager as LinearLayoutManager

    private val defaultInterpolator = AccelerateDecelerateInterpolator()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TaskViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.task_card, parent, false)
            )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.taskID = taskList[position].id
        holder.taskListID = this.taskListID

        holder.itemView.apply {
            task_textView.text = taskList[position].name
            task_textView.textSize =
                    mainActivity.waqtiSharedPreferences
                            .getInt(TASK_CARD_TEXT_SIZE, 18).toFloat()
            if (this is CardView)
                setCardBackgroundColor(Caches.boards[mainActivity.viewModel.boardID].cardColor.toAndroidColor)
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
                        DRAG_FLAG_OPAQUE
                )
                return@setOnLongClickListener true
            }

            // onDragListener is basically like, do this when someone is dragging on top of you
            setOnDragListener { v, event ->
                val draggingState = event.localState as DragEventLocalState
                val draggingView = taskListView.findViewHolderForAdapterPosition(draggingState.adapterPosition)?.itemView
                when (event.action) {
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (holder.adapterPosition != NO_POSITION) {
                            onDrag(draggingState, holder)
                        }
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        if (draggingView?.alpha != draggingViewAlpha)
                            draggingView?.alpha = draggingViewAlpha
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                    }
                    DragEvent.ACTION_DROP -> {
                        draggingState.updateToMatch(holder)
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        draggingView?.alpha = 1F
                    }
                }
                return@setOnDragListener true
            }
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

            taskListView.postDelayed(animationDuration) {
                val scrollBy = (holder.itemView.height * scrollAmount).roundToInt()
                taskListView.smoothScrollBy(0, scrollBy, defaultInterpolator)
            }

        }

    }

    private inline fun checkForScrollUp(draggingState: DragEventLocalState, holder: TaskViewHolder) {

        if (draggingState.adapterPosition <= linearLayoutManager.findFirstCompletelyVisibleItemPosition()) {

            taskListView.postDelayed(animationDuration) {
                val scrollBy = (holder.itemView.height * -scrollAmount).roundToInt()
                taskListView.smoothScrollBy(0, scrollBy, defaultInterpolator)
            }

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

            postDelayed(animationDuration * 2) {

                // The list that we will be scrolling to
                val boardPosition = boardAdapter.board.indexOf(this@TaskListAdapter.taskListID)

                // TODO: 11-Jun-19 Bug here with custom widths, possibly better to explicitly tell it how much to scroll
                //  and use the same properties we use for scrolling up and down

                // draft code below
                /*val percent = (mainActivity
                        .waqtiSharedPreferences
                        .getInt(TASK_LIST_WIDTH_KEY, 66) / 100.0)

                val width = (boardView.mainActivity.dimensions.first.toFloat() * percent).roundToInt()

                smoothScrollBy(width, 0, defaultInterpolator)*/

                smoothScrollToPosition(boardPosition)
                mainActivity.viewModel.boardPosition = true to boardPosition
            }
        }
    }

    private inline fun onScrollAcrossEmptyList(draggingState: DragEventLocalState, otherAdapter: TaskListAdapter) {

        taskListView.boardView.apply {

            postDelayed(animationDuration * 2) {

                // The list that we will be scrolling to
                val boardPosition = boardAdapter.board.indexOf(this@TaskListAdapter.taskListID)


                // TODO: 11-Jun-19 Bug here with custom widths, possibly better to explicitly tell it how much to scroll
                smoothScrollToPosition(boardPosition)
                mainActivity.viewModel.boardPosition = true to boardPosition
            }
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
