package uk.whitecrescent.waqti.android.customview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.logE
import uk.whitecrescent.waqti.model.now
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

class BoardAdapter(val boardID: ID) : RecyclerView.Adapter<BoardViewHolder>() {

    // TODO: 21-Dec-18 Use paging and LiveData from AndroidX

    val board = Database.boards[boardID] ?: throw ElementNotFoundException(boardID)

    lateinit var boardView: BoardView

    init {
        this.setHasStableIds(true)
        logE("$board")
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

        holder.header.text = "${board[position].name} id: ${board[position].id}"
        holder.footer.text = "Add Task"

        holder.footer.setOnClickListener {

            /*(it.context as MainActivity).supportFragmentManager.beginTransaction().apply {
                val fragment = CreateTaskFragment.newInstance()
                val bundle = Bundle()
                bundle.putLong("boardID", this@BoardAdapter.boardID)
                bundle.putLong("listID", holder.list.listAdapter.taskListID)
                fragment.arguments = bundle
                replace(R.id.blank_constraintLayout, fragment, "Create Task")
                commit()
            }*/

            val adapter = holder.list.listAdapter
            adapter.taskList.add(Task("New Task @ $now"))
            adapter.notifyDataSetChanged()
            adapter.taskList.update()
            board.update()
            holder.list.smoothScrollToPosition(adapter.itemCount - 1)
        }

        holder.itemView.taskList_deleteButton.setOnClickListener {
            if (holder.adapterPosition != -1) {
                // TODO: 25-Dec-18 There has to be a better way to do the bottom!
                if (holder.list.listAdapter.taskListID in
                        boardView.taskListAdapters.map { it.taskListID }) {
                    val toRemove = boardView.taskListAdapters.find {
                        it.taskListID == holder.list.listAdapter.taskListID
                    }
                    boardView.taskListAdapters.remove(toRemove)
                }
                board.removeAt(holder.adapterPosition).update()
                this.notifyDataSetChanged()
                logE("TaskListAdapters Size: ${boardView.taskListAdapters.size}")
            }
        }
    }

    fun add(taskList: TaskList) {
        board.add(taskList).update()
        notifyDataSetChanged()
    }
}