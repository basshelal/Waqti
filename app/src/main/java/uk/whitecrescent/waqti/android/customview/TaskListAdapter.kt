package uk.whitecrescent.waqti.android.customview

import android.content.ClipData
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.DragEvent.ACTION_DRAG_ENDED
import android.view.DragEvent.ACTION_DRAG_ENTERED
import android.view.DragEvent.ACTION_DRAG_EXITED
import android.view.DragEvent.ACTION_DROP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.fragments.ViewTaskFragment
import uk.whitecrescent.waqti.model.Bug
import uk.whitecrescent.waqti.model.FutureIdea
import uk.whitecrescent.waqti.model.Inconvenience
import uk.whitecrescent.waqti.model.MissingFeature
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

        taskListView.setOnDragListener { v, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                ACTION_DRAG_ENTERED -> {
                    @Bug
                    // TODO: 28-Dec-18 It is possible to continously add the same Task to one or more lists,
                    // difficult to recreate but basically add a new List then drag a task to it when
                    // it's empty, this may also be repeatable with non-empty lists as well

                    @MissingFeature
                    @Inconvenience
                    // TODO: 28-Dec-18 Works when the list is empty but what about when we're dragging
                    // over the bottom of it when it's not empty
                    // This is a big inconvenience, close to missing to missing feature because
                    // without it we can only drag across non empty lists when we drag over a
                    // Task card
                    if (this.taskList.isEmpty()) {
                        val otherAdapter = taskListView.boardView.taskListAdapters
                                .find { it.taskListID == draggingState.taskListID }
                        if (otherAdapter != null) {
                            onDragInDifferentList(draggingState, otherAdapter)
                            dragAcrossLists()
                        }
                    }

                }
            }
            true
        }
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

        holder.itemView.setOnClickListener {
            @GoToFragment()
            (it.context as MainActivity).supportFragmentManager.beginTransaction().apply {
                val fragment = ViewTaskFragment.newInstance()
                val bundle = Bundle()
                bundle.putLong("taskID", holder.taskID)
                fragment.arguments = bundle
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
                replace(R.id.blank_constraintLayout, fragment, VIEW_TASK_FRAGMENT)
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
            it.alpha = 0F
            //it.visibility = View.INVISIBLE
            return@setOnLongClickListener true
        }

        // onDragListener is basically like, do this when someone is dragging on top of you
        holder.itemView.setOnDragListener { view, event ->
            val draggingState = event.localState as DragEventLocalState
            when (event.action) {
                ACTION_DRAG_ENTERED -> {
                    if (holder.adapterPosition != -1) {
                        onDrag(draggingState, holder)
                    }
                }
                ACTION_DRAG_EXITED -> {
                }
                ACTION_DROP -> {
                    draggingState.updateToMatch(holder)
                }
                ACTION_DRAG_ENDED -> {
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
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.menu_task_card)
            popupMenu.setOnMenuItemClickListener {
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
            popupMenu.show()
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

                    // Scroll down
                    checkForScrollDown(draggingState, holder)

                    // Scroll up
                    checkForScrollUp(draggingState, holder)

                    true
                }
            }

            // they are in diff lists
            draggingState.taskListID != holder.taskListID -> {

                val otherAdapter = taskListView.boardView.taskListAdapters
                        .find { it.taskListID == draggingState.taskListID }
                if (otherAdapter != null) {

                    onDragInDifferentLists(draggingState, holder, otherAdapter)

                    dragAcrossLists()

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

    private fun checkForScrollUp(draggingState: DragEventLocalState, holder: TaskViewHolder) {
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

    private fun dragAcrossLists() {
        taskListView.boardView.apply {

            @Bug
            // TODO: 28-Dec-18 Moves across randomly, I think the positions are wrong

            @Bug
            // TODO: 28-Dec-18 Serious bugs when we do anything with the boardView.taskListAdapters
            // we need to fix this big time because it's the cause of the last remaining bugs here
            postDelayed(
                    {
                        @MissingFeature
                        // TODO: 26-Dec-18 Alpha is a problem
                        // maybe we could use visibility??

                        val pos = taskListAdapters.indexOf(this@TaskListAdapter)
                        if (pos != -1) smoothScrollToPosition(pos)

                    },
                    500L
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
        //view.task_materialCardView.draw(canvas)
        canvas?.drawColor(view.solidColor, PorterDuff.Mode.DST_OVER)
    }
}
