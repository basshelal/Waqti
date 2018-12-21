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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.logE
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID

// TODO: 21-Dec-18 Needs a lot of optimizing, a lot of things are very slow on a physical device!
// I suspect it's the taskList since it's accessing the DB all the time but I could be wrong

class TaskListAdapter(var taskListID: ID) : RecyclerView.Adapter<TaskViewHolder>() {

    val taskList = Database.taskLists[taskListID] ?: throw ElementNotFoundException(taskListID)
    lateinit var taskListView: TaskListView

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
            logE("TaskListAdapters Size: ${taskListView.boardView.taskListAdapters.size}")
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

        holder.itemView.task_textView.text = taskList[position].toString()
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

        var swapped = false

        // v is the View we are over with our touch not our bounds which is the same as itemView
        // event is our touch, v is the view we are over with our touch
        // onDragListener is basically like, do this when someone is dragging on top of you
        holder.itemView.setOnDragListener { view, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                ACTION_DRAG_STARTED -> {
                }
                // TODO: 21-Dec-18 Optimizations needed, feels slow
                ACTION_DRAG_ENTERED -> {
                    if (/*!swapped && */holder.adapterPosition != -1) {
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
                                    taskList.swap(draggingState.adapterPosition, newDragPos)
                                    draggingState.adapterPosition = newDragPos
                                    notifyDataSetChanged()
                                    swapped = true
                                    return@setOnDragListener true
                                }
                            }

                            // TODO: 21-Dec-18 What about when the list is empty???

                            draggingState.taskListID != holder.taskListID -> {

                                val otherAdapter = taskListView.boardView.taskListAdapters
                                        .find { it.taskListID == draggingState.taskListID }
                                if (otherAdapter != null) {

                                    val otherTaskList = taskListView.boardView.boardAdapter.board[draggingState.taskListID]
                                    val task = otherTaskList[draggingState.taskID]

                                    this.taskList.addAt(holder.adapterPosition, task)
                                    this.notifyDataSetChanged()

                                    otherAdapter.taskList.removeAll(task)
                                    otherAdapter.notifyDataSetChanged()

                                    draggingState.taskListID = holder.taskListID
                                }

                                // moved left/right only
                                if (draggingState.adapterPosition == holder.adapterPosition) {

                                }
                                // moved left/right and up/down
                                else {

                                }
                            }
                        }
                    }
                }
                // TODO: 20-Dec-18 We need access to holder's adapter so that we can add the dragging view to it
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
                taskList.removeAt(holder.adapterPosition)
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
}

private enum class Possibilities {
    // TODO: 21-Dec-18 Try doing this whole thing without TaskID
    DIFF_NOTHING,           // -> They are the same
    DIFF_TASKID,            // -> Impossible, APOS must be diff
    DIFF_LISTID,            // -> Impossible, TaskID must be diff
    DIFF_APOS,              // -> Impossible, TaskID or ListID must be diff
    DIFF_TASKID_AND_LISTID, // -> Diff List, same position, so we're moving it left/right only
    DIFF_TASKID_AND_APOS,   // -> Diff Task, same list, so we're moving it up/down only
    DIFF_LISTID_AND_APOS,   // -> Impossible, TaskID must be diff
    DIFF_EVERYTHING         // -> Diff List and Task, basically we moved left/right and up/down
}

private fun containsNull(state: DragEventLocalState) =
        state.taskListID == -1L || state.adapterPosition == -1
