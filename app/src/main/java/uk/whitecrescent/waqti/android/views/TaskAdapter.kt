package uk.whitecrescent.waqti.android.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.Task

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {

    val itemList: List<Task>
        get() = Caches.tasks.valueList()

    init {
        this.setHasStableIds(true)
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

        holder.itemView.delete_button.setOnClickListener {
            Caches.tasks.remove(itemList[position])
            notifyDataSetChanged()
        }
    }

}