package uk.whitecrescent.waqti.android.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_card.view.*
import uk.whitecrescent.waqti.model.task.Task

class TaskViewHolder(view: View)
    : RecyclerView.ViewHolder(view) {

    fun setText(task: Task) {
        itemView.task_textView.text = task.toString()
    }

}