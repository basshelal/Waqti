package uk.whitecrescent.waqti.android.customview.recyclerviews

import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.Bug
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.MissingFeature
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.android.mainActivity
import uk.whitecrescent.waqti.model.collections.AbstractWaqtiList
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID
import kotlin.math.roundToInt

class TaskListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) :
        RecyclerView(context, attributeSet, defStyle) {

    val listAdapter: TaskListAdapter
        get() = adapter as TaskListAdapter
    val boardView: BoardView
        get() = parent.parent as BoardView

    init {
        layoutManager = LinearLayoutManager(getContext(), VERTICAL, false)
    }

}

class TaskListAdapter(var taskListID: ID) : RecyclerView.Adapter<TaskViewHolder>() {

    val taskList = Database.taskLists[taskListID] ?: throw ElementNotFoundException(taskListID)

    lateinit var taskListView: TaskListView

    val linearLayoutManager: LinearLayoutManager
        get() = taskListView.layoutManager as LinearLayoutManager

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        check(recyclerView is TaskListView)
        taskListView = recyclerView as TaskListView

        taskListView.setOnDragListener { v, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {

                    @MissingFeature
                    @Inconvenience
                    // TODO: 28-Dec-18 Works when the list is empty but what about when we're dragging
                    // over the bottom of it when it's not empty
                    // This is a big inconvenience, close to missing feature because
                    // without it we can only drag across non empty lists when we drag over a
                    // Task card
                    if (this.taskList.isEmpty()) {
                        val otherAdapter = taskListView.boardView.getListAdapter(draggingState.taskListID)
                        if (otherAdapter != null) {
                            onDragInDifferentList(draggingState, otherAdapter)
                            dragAcrossLists()
                        }
                    } else {
                        //taskListView.snackBar("Dragging over non-empty list")
                    }

                }
            }
            true
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

        holder.itemView.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.taskID = holder.taskID
                it.mainActivity.viewModel.listID = taskListID

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
                replace(R.id.fragmentContainer, ViewTaskFragment.newInstance(), VIEW_TASK_FRAGMENT)
            }.commit()
        }

        holder.itemView.setOnLongClickListener {
            @Suppress("DEPRECATION")// using the updated requires minSDK >= 24, we are 21
            it.startDrag(
                    ClipData(
                            ClipData.newPlainText("", "")
                    ),
                    ShadowBuilder(holder.itemView.task_materialCardView),
                    DragEventLocalState(holder.taskID, holder.taskListID, holder.adapterPosition),
                    0
            )
            it.alpha = 0.2F
            //it.visibility = View.INVISIBLE
            return@setOnLongClickListener true
        }

        // onDragListener is basically like, do this when someone is dragging on top of you
        holder.itemView.setOnDragListener { view, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (holder.adapterPosition != -1) {
                        onDrag(draggingState, holder)
                    }
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                }
                DragEvent.ACTION_DROP -> {
                    draggingState.updateToMatch(holder)
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    taskListView.findViewHolderForAdapterPosition(draggingState.adapterPosition)
                            ?.itemView?.let {
                        if (it.alpha < 1F) it.alpha = 1F
                    }
                    holder.itemView.let {
                        if (it.alpha < 1F) it.alpha = 1F
                    }
                }
            }
            return@setOnDragListener true
        }


        holder.itemView.delete_button.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_task)
                setOnMenuItemClickListener {
                    return@setOnMenuItemClickListener when (it.itemId) {
                        R.id.deleteTask_menuItem -> {
                            if (holder.adapterPosition != -1) {
                                taskList.removeAt(holder.adapterPosition).update()
                                notifyDataSetChanged()
                                true
                            } else false
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    private fun onDrag(draggingState: DragEventLocalState, holder: TaskViewHolder): Boolean {
        return when {

            draggingState.taskListID == holder.taskListID -> {
                // they are the same
                if (draggingState.adapterPosition == holder.adapterPosition) {
                    false
                }
                // they are diff but in same list
                else {
                    onDragInSameList(draggingState, holder)

                    @Bug
                    @Inconvenience
                    // TODO: 29-Dec-18 When the bottom or top of the list fits perfectly into the list we cant scoll

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

                    onDragInDifferentLists(draggingState, holder, otherAdapter)

                    dragAcrossLists(draggingState)

                    true
                } else false
            }

            // impossible
            else -> {
                false
            }

        }
    }

    private fun onDragInSameList(draggingState: DragEventLocalState, holder: TaskViewHolder) {
        val newDragPos = holder.adapterPosition
        taskList.swap(draggingState.adapterPosition, newDragPos).update()
        draggingState.adapterPosition = newDragPos
        notifyDataSetChanged()
    }

    private fun checkForScrollDown(draggingState: DragEventLocalState, holder: TaskViewHolder) {
        if (draggingState.adapterPosition <= itemCount - 1) {
            if (draggingState.adapterPosition >=
                    linearLayoutManager.findLastVisibleItemPosition()) {
                if (draggingState.adapterPosition != itemCount - 1 ||
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() != itemCount - 1) {
                    taskListView.postDelayed(
                            {
                                val scrollBy = (holder.itemView.height * 1.25).roundToInt()
                                taskListView.smoothScrollBy(0, scrollBy)
                            },
                            600L
                    )
                }
            }
        }
    }

    private fun checkForScrollUp(draggingState: DragEventLocalState, holder: TaskViewHolder) {
        if (draggingState.adapterPosition >= 0) {
            if (draggingState.adapterPosition <=
                    linearLayoutManager.findFirstVisibleItemPosition()) {
                if (draggingState.adapterPosition != 0 ||
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    taskListView.postDelayed(
                            {
                                val scrollBy = (holder.itemView.height * -1.25).roundToInt()
                                taskListView.smoothScrollBy(0, scrollBy)
                            },
                            600L
                    )
                }
            }
        }
    }

    private fun onDragInDifferentLists(draggingState: DragEventLocalState,
                                       holder: TaskViewHolder,
                                       otherAdapter: TaskListAdapter) {

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
    }

    private fun onDragInDifferentList(draggingState: DragEventLocalState, otherAdapter: TaskListAdapter) {
        val otherTaskList = otherAdapter.taskList
        val task = otherTaskList[draggingState.taskID]
        val newDragPos = this.taskList.nextIndex

        AbstractWaqtiList.moveElement(
                listFrom = otherTaskList, listTo = this.taskList,
                element = task, toIndex = newDragPos
        )

        draggingState.taskListID = this.taskListID
        draggingState.adapterPosition = newDragPos

        this.notifyDataSetChanged()
        otherAdapter.notifyDataSetChanged()
    }

    private fun dragAcrossLists(draggingState: DragEventLocalState? = null) {

        taskListView.boardView.apply {

            postDelayed(
                    {

                        val pos = boardAdapter.board.indexOf(this@TaskListAdapter.taskListID)
                        smoothScrollToPosition(pos)

                    },
                    450L
            )

            postDelayed(
                    {
                        @Inconvenience
                        @Bug
                        // TODO: 29-Dec-18 Alpha changing only works with delay of around 450+,
                        // the view briefly appears

                        val position = if (draggingState != null) draggingState.adapterPosition else 0

                        this@TaskListAdapter.taskListView
                                .findViewHolderForAdapterPosition(position)
                                ?.itemView?.alpha = 0.25F

                    },
                    450L
            )
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

}

private class ShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
    }

    override fun onDrawShadow(canvas: Canvas?) {
        super.onDrawShadow(canvas)
        canvas?.drawColor(view.solidColor, PorterDuff.Mode.DST)
    }

}

class TaskViewHolder(view: View)
    : RecyclerView.ViewHolder(view) {

    //the ID of the Task that this ViewHolder contains
    var taskID: ID = 0L

    //the ID of the TaskList that this ViewHolder's Task is in
    var taskListID: ID = 0L
}