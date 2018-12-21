package uk.whitecrescent.waqti.android.customview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

class BoardAdapter(val boardID: ID = 0) : RecyclerView.Adapter<BoardViewHolder>() {

    val board = Database.boards[boardID] ?: throw ElementNotFoundException(boardID)

    lateinit var boardView: BoardView

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        assert(recyclerView is BoardView)
        boardView = recyclerView as BoardView
    }

    override fun getItemCount(): Int {
        return board.size
    }

    override fun getItemId(position: Int): Long {
        return board[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {

        // Adapters get created and destroyed because their associated views do too, actually
        // more specifically, they get recycled

        holder.list.adapter = TaskListAdapter(board[position].id)

        holder.header.text = board[position].name
        holder.footer.text = "Add Task"

        holder.footer.setOnClickListener {
            val adapter = holder.list.listAdapter
            adapter.taskList.add(Task("New Task @ $now"))
            adapter.notifyDataSetChanged()
            holder.list.smoothScrollToPosition(adapter.itemCount - 1)
        }

        holder.itemView.taskList_deleteButton.setOnClickListener {
            if (holder.adapterPosition != -1) {
                board.removeAt(holder.adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    fun add(taskList: TaskList) {
        board.add(taskList)
        notifyDataSetChanged()
    }
}