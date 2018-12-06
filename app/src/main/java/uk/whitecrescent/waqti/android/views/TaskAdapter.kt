package uk.whitecrescent.waqti.android.views

import android.view.LayoutInflater
import android.view.ViewGroup
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.KDragItemAdapter
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.Task

class TaskAdapter : KDragItemAdapter<Task, TaskViewHolder>() {

    override val itemList: MutableList<Task>
        get() = Caches.tasks.valueList().toMutableList()

    init {
    }

    override fun getUniqueItemId(position: Int): Long {
        return itemList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val vh = TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false)
        )
        return vh
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.text = itemList[position].name
    }


}