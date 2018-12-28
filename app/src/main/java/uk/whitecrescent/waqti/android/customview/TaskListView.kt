package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.model.task.ID


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
        //LinearSnapHelper().attachToRecyclerView(this)
    }

}

class TaskViewHolder(view: View)
    : RecyclerView.ViewHolder(view) {

    //the ID of the Task that this ViewHolder contains
    var taskID: ID = 0L

    //the ID of the TaskList that this ViewHolder's Task is in
    var taskListID: ID = 0L
}