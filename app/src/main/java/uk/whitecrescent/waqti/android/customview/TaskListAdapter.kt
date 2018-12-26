package uk.whitecrescent.waqti.android.customview

import android.content.ClipData
import android.view.DragEvent.ACTION_DRAG_ENDED
import android.view.DragEvent.ACTION_DRAG_ENTERED
import android.view.DragEvent.ACTION_DRAG_EXITED
import android.view.DragEvent.ACTION_DRAG_LOCATION
import android.view.DragEvent.ACTION_DRAG_STARTED
import android.view.DragEvent.ACTION_DROP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.Bug
import uk.whitecrescent.waqti.model.FutureIdea
import uk.whitecrescent.waqti.model.MissingFeature
import uk.whitecrescent.waqti.model.NeedsReOrganizing
import uk.whitecrescent.waqti.model.collections.AbstractWaqtiList
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID
import kotlin.math.roundToInt

class TaskListAdapter(var taskListID: ID) : RecyclerView.Adapter<TaskViewHolder>() {

    @FutureIdea
    // TODO: 21-Dec-18 Use paging and LiveData from AndroidX

    val taskList = Database.taskLists[taskListID] ?: throw ElementNotFoundException(taskListID)
    lateinit var taskListView: TaskListView
    val linearLayoutManager: LinearLayoutManager
        get() = taskListView.layoutManager as LinearLayoutManager

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        assert(recyclerView is TaskListView)
        taskListView = recyclerView as TaskListView
    }

    override fun onViewAttachedToWindow(holder: TaskViewHolder) {
        if (this.taskListID !in taskListView.boardView.taskListAdapters.map { it.taskListID }) {
            taskListView.boardView.taskListAdapters.add(this)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun getItemId(position: Int): Long {
        return taskList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.itemView.task_textView.text = "${taskList[position].name} id: ${taskList[position].id}"
        holder.taskID = taskList[position].id
        holder.taskListID = this.taskListID

        holder.itemView.setOnLongClickListener {
            @Suppress("DEPRECATION")// using the updated requires minSDK >= 24, we are 21
            it.startDrag(
                    ClipData(
                            ClipData.newPlainText("", "")
                    ),
                    View.DragShadowBuilder(it),
                    DragEventLocalState(holder.taskID, holder.taskListID, holder.adapterPosition),
                    0
            )
            it.alpha = 0F
            return@setOnLongClickListener true
        }

        var swapped = false // maybe put this inside the dragListener

        // v is the View we are over with our touch not our bounds which is the same as itemView
        // event is our touch, v is the view we are over with our touch
        // onDragListener is basically like, do this when someone is dragging on top of you
        @NeedsReOrganizing // Too messy here
        holder.itemView.setOnDragListener { view, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                ACTION_DRAG_STARTED -> {
                }
                @MissingFeature
                // TODO: 21-Dec-18 Autoscrolling is missing
                ACTION_DRAG_ENTERED -> {
                    if (!swapped && holder.adapterPosition != -1) {
                        when {


                            /*
                             * ALL Possible Scenarios:
                             *
                             * Dragging item over itself (same everything)
                             * Dragging item over another in the same list (same taskListID)
                             * Dragging an item over another in different list with same Adapter Pos (same Adapter Pos)
                             * Dragging an item over another in different list with diff Adapter Pos (same nothing)
                             *
                             */

                            draggingState.taskListID == holder.taskListID -> {
                                // they are the same
                                if (draggingState.adapterPosition == holder.adapterPosition) {
                                    return@setOnDragListener false
                                }
                                // they are diff but in same list
                                else {
                                    val newDragPos = holder.adapterPosition
                                    taskList.swap(draggingState.adapterPosition, newDragPos).update()
                                    draggingState.adapterPosition = newDragPos
                                    notifyDataSetChanged()
                                    swapped = true

                                    // Scroll down
                                    if (draggingState.adapterPosition >=
                                            linearLayoutManager.findLastVisibleItemPosition()) {
                                        if (draggingState.adapterPosition != itemCount - 1 ||
                                                linearLayoutManager.findLastCompletelyVisibleItemPosition() != itemCount - 1) {
                                            taskListView.postDelayed(
                                                    {
                                                        holder.itemView.snackBar("Need to scroll down")
                                                        val scrollBy = (view.height * 1.25).roundToInt()
                                                        taskListView.smoothScrollBy(0, scrollBy)
                                                    },
                                                    600L
                                            )
                                        }
                                    }

                                    // Scroll up
                                    if (draggingState.adapterPosition <=
                                            linearLayoutManager.findFirstVisibleItemPosition()) {
                                        if (draggingState.adapterPosition != 0 ||
                                                linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                                            taskListView.postDelayed(
                                                    {
                                                        holder.itemView.snackBar("Need to scroll up")
                                                        val scrollBy = (view.height * -1.25).roundToInt()
                                                        taskListView.smoothScrollBy(0, scrollBy)
                                                    },
                                                    600L
                                            )
                                        }
                                    }

                                    return@setOnDragListener true
                                }
                            }

                            @MissingFeature
                            // TODO: 21-Dec-18 What about when the list is empty???

                            draggingState.taskListID != holder.taskListID -> {

                                val otherAdapter = taskListView.boardView.taskListAdapters
                                        .find { it.taskListID == draggingState.taskListID }
                                if (otherAdapter != null) {

                                    @Bug
                                    // TODO: 24-Dec-18 Another bug, when we keep switching left and right
                                    // it continues to add that task to the other list, making
                                    // that other list have multiple copies of the same task,
                                    // TODO:This looks like its been fixed, but need to confirm
                                    // properly as I've still seen instances of duplicate Tasks!

                                    val otherTaskList = otherAdapter.taskList
                                    val task = otherTaskList[draggingState.taskID]
                                    val newDragPos = holder.adapterPosition

                                    AbstractWaqtiList.moveElement(
                                            listFrom = otherTaskList, listTo = this.taskList,
                                            element = task, toIndex = newDragPos
                                    )

                                    draggingState.taskListID = holder.taskListID
                                    draggingState.adapterPosition = newDragPos

                                    this.notifyDataSetChanged()
                                    otherAdapter.notifyDataSetChanged()

                                    swapped = true

                                    taskListView.boardView.apply {

                                        postDelayed(
                                                {
                                                    // TODO: 26-Dec-18 Alpha is a problem
                                                    /*val vh = this@TaskListAdapter.taskListView
                                                            .findViewHolderForAdapterPosition(
                                                                    draggingState.adapterPosition)
                                                    logE("TRYING")
                                                    if (draggingState matches vh as TaskViewHolder) {
                                                        vh.itemView.alpha = 0F
                                                    } else logE("Couldn't do it")*/

                                                    val pos = taskListAdapters.indexOf(this@TaskListAdapter)
                                                    if (pos != -1) smoothScrollToPosition(pos)

                                                },
                                                500L
                                        )
                                    }

                                    return@setOnDragListener true
                                }
                            }

                            // impossible
                            else -> {
                                return@setOnDragListener false
                            }

                        }
                    }
                }
                ACTION_DRAG_LOCATION -> {

                }
                ACTION_DRAG_EXITED -> {
                    swapped = false
                }
                ACTION_DROP -> {
                    draggingState.updateToMatch(holder)
                    swapped = false
                }
                ACTION_DRAG_ENDED -> {
                    taskListView.findViewHolderForAdapterPosition(draggingState.adapterPosition)
                            ?.itemView?.let {
                        if (it.alpha < 1F) it.alpha = 1F
                    }
                    holder.itemView.let {
                        if (it.alpha < 1F) it.alpha = 1F
                    }
                    swapped = false
                }
            }
            return@setOnDragListener true
        }


        holder.itemView.delete_button.setOnClickListener {
            if (holder.adapterPosition != -1) {
                taskList.removeAt(holder.adapterPosition).update()
                notifyDataSetChanged()
            }
        }
    }

}

private data class DragEventLocalState(
        var taskID: ID,
        var taskListID: ID,
        var adapterPosition: Int) {

    fun updateToMatch(viewHolder: TaskViewHolder) {
        this.taskID = viewHolder.taskID
        this.taskListID = viewHolder.taskListID
        this.adapterPosition = viewHolder.adapterPosition
    }


    infix fun doesNotMatch(viewHolder: TaskViewHolder): Boolean {
        return this.taskID != viewHolder.taskID ||
                this.taskListID != viewHolder.taskListID ||
                this.adapterPosition != viewHolder.adapterPosition
    }

    infix fun matches(viewHolder: TaskViewHolder): Boolean {
        return this.taskID == viewHolder.taskID &&
                this.taskListID == viewHolder.taskListID &&
                this.adapterPosition == viewHolder.adapterPosition
    }

    fun possibilities(that: TaskViewHolder): Possibilities {
        return when {
            // Below are impossible!
            taskID != that.taskID &&
                    taskListID == that.taskListID &&
                    adapterPosition == that.adapterPosition -> Possibilities.I_DIFFERENT_TASKID

            taskID == that.taskID &&
                    taskListID != that.taskListID &&
                    adapterPosition == that.adapterPosition -> Possibilities.I_DIFFERENT_LISTID

            taskID == that.taskID &&
                    taskListID == that.taskListID &&
                    adapterPosition != that.adapterPosition -> Possibilities.I_DIFFERENT_APOS

            taskID == that.taskID &&
                    taskListID != that.taskListID &&
                    adapterPosition != that.adapterPosition -> Possibilities.I_DIFFERENT_LISTID_AND_APOS

            // Below are possible
            taskID != that.taskID &&
                    taskListID == that.taskListID &&
                    adapterPosition == that.adapterPosition -> Possibilities.DIFFERENT_TASKID_AND_LISTID

            taskID != that.taskID &&
                    taskListID == that.taskListID &&
                    adapterPosition != that.adapterPosition -> Possibilities.DIFFERENT_TASKID_AND_APOS

            taskID != that.taskID &&
                    taskListID != that.taskListID &&
                    adapterPosition != that.adapterPosition -> Possibilities.DIFFERENT_EVERYTHING

            else -> Possibilities.SAME
        }
    }
}

private enum class Possibilities {
    SAME,                        // -> They are the same
    I_DIFFERENT_TASKID,            // -> Impossible, APOS must be diff
    I_DIFFERENT_LISTID,            // -> Impossible, TaskID must be diff
    I_DIFFERENT_APOS,              // -> Impossible, TaskID or ListID must be diff
    DIFFERENT_TASKID_AND_LISTID, // -> Diff List, same position, so we're moving it left/right only
    DIFFERENT_TASKID_AND_APOS,   // -> Diff Task, same list, so we're moving it up/down only
    I_DIFFERENT_LISTID_AND_APOS,   // -> Impossible, TaskID must be diff
    DIFFERENT_EVERYTHING         // -> Diff List and Task, basically we moved left/right and up/down
}
