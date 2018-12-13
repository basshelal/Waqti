package uk.whitecrescent.waqti.android.customview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.now

class BoardAdapter : RecyclerView.Adapter<BoardViewHolder>() {

    val itemList: MutableList<TaskList> = Array(10, { TaskList("@ $now") }).toMutableList()

    init {
        this.setHasStableIds(true)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemId(position: Int): Long {
        return itemList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {

        holder.itemView.taskListHeader_textView.text = itemList[position].name
    }
}