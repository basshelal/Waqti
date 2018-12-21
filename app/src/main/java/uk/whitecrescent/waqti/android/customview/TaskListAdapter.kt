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
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID

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

        /*if (this.taskListID !in taskListView.boardView.taskListIDs) {
            taskListView.boardView.taskListIDs.add(this.taskListID)
            logE("Added ${this.taskListID}")
            logE("TaskListIDs Size: ${taskListView.boardView.taskListIDs.size}")
        }

        if (this.taskListID !in taskListView.boardView.taskListAdapters.map { it.taskListID }) {
            taskListView.boardView.taskListAdapters.add(this)
            logE("TaskListAdapters Size: ${taskListView.boardView.taskListAdapters.size}")
        }*/
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
//                    logE("STARTED dragging ${draggingState.taskID}")
                }
                // TODO: 21-Dec-18 Optimizations needed, feels slow
                ACTION_DRAG_ENTERED -> {
//                    logE("ENTERED over ${holder.taskID} dragging ${draggingState.taskID}")
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




                            draggingState.adapterPosition != holder.adapterPosition &&
                                    draggingState.taskID != holder.taskID &&
                                    draggingState.taskListID == holder.taskListID -> {
                                val newDragPos = holder.adapterPosition
                                taskList.swap(draggingState.adapterPosition, holder.adapterPosition)
                                draggingState.adapterPosition = newDragPos
                                notifyDataSetChanged()
                                swapped = true
                                // by now the adapterPositions and taskList positions are identical
                                if (draggingState.taskListID == this.taskListID) {
                                    assert(draggingState.adapterPosition == taskList.indexOf(draggingState.taskID))
                                }
                            }
                        }
                    }
                }
                // TODO: 20-Dec-18 We need access to holder's adapter so that we can add the dragging view to it
                ACTION_DRAG_LOCATION -> {

                }
                ACTION_DRAG_EXITED -> {
//                    logE("EXITED from ${holder.taskID}")
                    swapped = false
                }
                ACTION_DROP -> {
                    draggingState.updateToMatch(holder)
                    /*taskListView.findViewHolderForAdapterPosition(draggingState.adapterPosition)
                            ?.itemView?.alpha = 1F*/
                    swapped = false
//                    logE("DROPPED ${draggingState.taskID}")
                }
                ACTION_DRAG_ENDED -> {
                    taskListView.findViewHolderForAdapterPosition(draggingState.adapterPosition)
                            ?.itemView?.alpha = 1F
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
        state.taskID == -1L || state.taskListID == -1L || state.adapterPosition == -1
