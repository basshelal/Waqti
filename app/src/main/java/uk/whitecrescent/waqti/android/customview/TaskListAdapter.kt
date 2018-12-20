package uk.whitecrescent.waqti.android.customview

import android.content.ClipData
import android.content.ClipDescription
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
import uk.whitecrescent.waqti.android.logE
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.logE
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task
import kotlin.random.Random

class TaskListAdapter(var taskListID: ID = 0) : RecyclerView.Adapter<TaskViewHolder>() {

    val itemList: MutableList<Task> = Array(10, { Task("@ $now") }).toMutableList()
    lateinit var taskListView: TaskListView

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        assert(recyclerView is TaskListView)
        taskListView = recyclerView as TaskListView
        taskListID = Random.nextLong()
    }

    override fun onViewAttachedToWindow(holder: TaskViewHolder) {

        if (this.taskListID !in taskListView.board.taskListIDs) {
            taskListView.board.taskListIDs.add(this.taskListID)
            logE("Added ${this.taskListID}")
            logE("Size: ${taskListView.board.taskListIDs.size}")
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemId(position: Int): Long {
        return itemList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.itemView.task_textView.text = itemList[position].toString()
        holder.taskID = itemList[position].id

        holder.itemView.setOnLongClickListener {
            @Suppress("DEPRECATION")// using the updated requires minSDK >= 24, we are 21
            it.startDrag(
                    ClipData(
                            ClipDescription("TaskID", arrayOf("text/plain")),
                            ClipData.Item(holder.taskID.toString())
                    ),
                    View.DragShadowBuilder(it),
                    Triple(holder.taskID, this.taskListID, holder.adapterPosition),
                    // ^ Doesn't change when we drag around noice!
                    0
            )
            return@setOnLongClickListener true
        }

        /*
         * Put the current Task (so the one we have requested to Drag) somewhere up with the
         * startDrag stuff in ClipData since no matter what happens, the DragListener seems to
         * return the View we are over, whether we request holder.itemView or v (they're the same)
         * So what we can do is check if this view we are over matches the one we have clicked by
         * getting the event's ClipData, then we can do the changes to the list structure
         * accordingly, pseudo-code below:
         *
         * event.getClipData.getTask != holder.getTask -> swap; notifyDataSetChanged;
         *
         * This means we need to, in the TaskViewHolder, associate it with a Task, probably by ID
         */

        var swapped = false
        var (dTaskID, dTaskListID, dAdapterPosition) = Triple<ID, ID, Int>(0, 0, 0)

        // v is the View we are over with our touch not our bounds which is the same as itemView
        // event is our touch, v is the view we are over with our touch
        // onDragListener is basically like, do this when someone is dragging on top of you
        holder.itemView.setOnDragListener { v, event ->
            if (dTaskID == 0L && dTaskListID == 0L && dAdapterPosition == 0) {
                val it = event.localState as Triple<ID, ID, Int>
                dTaskID = it.first
                dTaskListID = it.second
                dAdapterPosition = it.third
            }
            when (event.action) {
                ACTION_DRAG_STARTED -> {
                    logE("ACTION_DRAG_STARTED")
                }
                ACTION_DRAG_ENTERED -> {
                    logE("ACTION_DRAG_ENTERED")
                    if (!swapped) {
                        if (dTaskID == holder.taskID && dTaskListID == this.taskListID) {
                            //v.snackBar("Same Task")
                        }
                        if (dTaskID != holder.taskID && dTaskListID == this.taskListID) {
                            //v.snackBar("In same list but not same Task!")
                            swapped = true
                        }
                        if (dTaskID != holder.taskID && dTaskListID != this.taskListID) {
                            val list = taskListView.board.taskListIDs
                            //below fails at 6th list because of stupid recycling!
                            if (list.indexOf(this.taskListID) > list.indexOf(dTaskListID)) {
                                v.snackBar("Dragging right!")
                                taskListView.board.smoothScrollBy(700, 0)
                                dTaskListID = this.taskListID
                                swapped = true
                            } else {
                                v.snackBar("Dragging left!")
                                taskListView.board.smoothScrollBy(-700, 0)
                                dTaskListID = this.taskListID
                                swapped = true
                            }
                        }
                    }
                }
                ACTION_DRAG_LOCATION -> {
                    "D -> ${event.x}, ${event.y}".logE()
                    if (holder.layoutPosition > dAdapterPosition + 2) {
                        taskListView.smoothScrollBy(0, 100)
                        v.snackBar("We need to scroll down!")
                    }
                    if (holder.layoutPosition < dAdapterPosition - 2) {
                        taskListView.smoothScrollBy(0, -100)
                        v.snackBar("We need to scroll up!")
                    }
                }
                ACTION_DRAG_EXITED -> {
                    logE("ACTION_DRAG_EXITED")
                    swapped = false
                }
                ACTION_DROP -> {
                    logE("ACTION_DROP")
                }
                ACTION_DRAG_ENDED -> {
                    logE("ACTION_DRAG_ENDED")
                }
            }
            return@setOnDragListener true
        }


        holder.itemView.delete_button.setOnClickListener {
            //Caches.tasks.remove(itemList[holder.adapterPosition])
            if (holder.adapterPosition != -1) {
                itemList.removeAt(holder.adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

}

private data class DragEventLocalState(val viewHolder: TaskViewHolder)