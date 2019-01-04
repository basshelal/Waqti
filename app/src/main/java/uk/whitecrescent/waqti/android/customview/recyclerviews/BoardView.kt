package uk.whitecrescent.waqti.android.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialConfirmDialog
import uk.whitecrescent.waqti.android.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.android.fragments.view.ViewListFragment
import uk.whitecrescent.waqti.android.mainActivity
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.ID

class BoardView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    val boardAdapter: BoardAdapter
        get() = this.adapter as BoardAdapter

    val taskListAdapters = ArrayList<TaskListAdapter>()

    init {
        layoutManager = LinearLayoutManager(this.context, HORIZONTAL, false)
    }

    override fun setAdapter(_adapter: Adapter<*>?) {
        super.setAdapter(_adapter)
        require(this.adapter != null &&
                this.adapter is BoardAdapter
        ) { "Adapter must be non null and a BoardAdapter, passed in ${_adapter}" }

        attachHelpers()
    }

    private fun attachHelpers() {

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            // TODO: 24-Dec-18 remember to make the dragging only doable from the header, currently its from anywhere
            // so a very fast scroll or a hold on an empty list will trigger a drag

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN
                        or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
            }

            override fun isLongPressDragEnabled() = true

            override fun isItemViewSwipeEnabled() = false

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                /*This will never be called as we do not support swiping*/
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder is BoardViewHolder) {
                    viewHolder.itemView.alpha = 1F
                }
            }

            override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (viewHolder != null && viewHolder is BoardViewHolder) {
                    viewHolder.itemView.alpha = 0.7F
                }
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                                 target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                boardAdapter.apply {
                    board.move(fromPos, toPos).update()
                    matchOrder()
                    notifyItemMoved(fromPos, toPos)
                }
                mainActivity.viewModel.boardPosition = true to toPos
            }

            override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int,
                                                      viewSizeOutOfBounds: Int, totalSize: Int,
                                                      msSinceStartScroll: Long): Int {
                return super.interpolateOutOfBoundsScroll(
                        recyclerView, viewSize, viewSizeOutOfBounds, totalSize, 1500)
                // TODO: 13-Dec-18 Override this to make better when we drag outside the bounds
            }

        }).attachToRecyclerView(this)

        object : PagerSnapHelper() {
            override fun findTargetSnapPosition(layoutManager: LayoutManager?, velocityX: Int, velocityY: Int): Int {
                val currentBoardPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                mainActivity.viewModel.boardPosition = true to currentBoardPos
                return currentBoardPos
            }
        }.attachToRecyclerView(this)
    }

    fun addListAdapterIfNotExists(taskListAdapter: TaskListAdapter) {
        taskListAdapter.let {
            if (!adapterExists(it.taskListID)) taskListAdapters.add(it)
        }
    }

    fun removeListAdapterIfExists(taskListAdapter: TaskListAdapter) {
        getListAdapter(taskListAdapter.taskListID).apply {
            if (this != null) taskListAdapters.remove(this)
        }
    }

    fun getListAdapter(taskListID: ID): TaskListAdapter? {
        return taskListAdapters.find { it.taskListID == taskListID }
    }

    fun adapterExists(taskListID: ID): Boolean {
        return taskListID in taskListAdapters.map { it.taskListID }
    }
}

class BoardAdapter(val boardID: ID) : RecyclerView.Adapter<BoardViewHolder>() {

    val board = Caches.boards[boardID]

    lateinit var boardView: BoardView

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        check(recyclerView is BoardView)
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

        val taskListAdapter = TaskListAdapter(board[position].id)
        holder.list.adapter = taskListAdapter
        boardView.addListAdapterIfNotExists(taskListAdapter)

        matchOrder()

        holder.header.text = board[position].name
        holder.footer.text = boardView.mainActivity.getText(R.string.addTask)

        holder.header.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.listID = board[holder.adapterPosition].id

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
                replace(R.id.fragmentContainer, ViewListFragment.newInstance(), VIEW_LIST_FRAGMENT)
            }.commit()
        }

        holder.footer.setOnClickListener {

            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = this@BoardAdapter.boardID
                it.mainActivity.viewModel.listID = holder.list.listAdapter.taskListID
                it.mainActivity.viewModel.createdTaskTime = DEFAULT_TIME

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, CreateTaskFragment.newInstance(), CREATE_TASK_FRAGMENT)
                addToBackStack("")
            }.commit()
        }

        holder.itemView.overflow_imageView.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_list)
                setOnMenuItemClickListener {
                    return@setOnMenuItemClickListener when (it.itemId) {
                        R.id.deleteList_menuItem -> {

                            if (holder.adapterPosition != -1) {
                                MaterialConfirmDialog().apply {
                                    title = boardView.mainActivity.getString(R.string.deleteListQuestion)
                                    message = boardView.mainActivity.getString(R.string.deleteListDetails)
                                    onConfirm = View.OnClickListener {
                                        this.dismiss()
                                        boardView.removeListAdapterIfExists(holder.list.listAdapter)
                                        board.removeAt(holder.adapterPosition).update()
                                        notifyDataSetChanged()
                                    }
                                }.show(boardView.mainActivity.supportFragmentManager, "MaterialConfirmDialog")
                                true
                            } else false

                            /*if (holder.adapterPosition != -1) {
                                boardView.removeListAdapterIfExists(holder.list.listAdapter)
                                board.removeAt(holder.adapterPosition).update()
                                notifyDataSetChanged()
                                true
                            } else false*/
                        }
                        else -> false
                    }
                }
            }.show()
        }


        @FutureIdea 0
        // TODO: 29-Dec-18 Setting a background image isn't impossible
        // generally what we'd need to do is have the image saved and divide it by the number of
        // lists there are in the board, then in onBindViewHolder for the Board we'd set that
        // list's background as that page of the overall image, there will be stretching though,
        // both when the lists are too few and when the lists are too many, but overall it's not
        // too hard
        // the idea above could be used to have the board's board card in the ViewBoardListFragment be the
        // picture
        //holder.list.setBackgroundResource(R.mipmap.waqti_icon)
    }

    fun matchOrder() {
        val taskListAdaptersCopy = ArrayList(boardView.taskListAdapters)
        if (doesNotMatchOrder()) {

            board.filter { taskList -> taskList.id in taskListAdaptersCopy.map { it.taskListID } }
                    .mapIndexed { index, taskList -> index to taskList }.toMap()
                    .forEach { entry ->
                        val (index, taskList) = entry

                        boardView.taskListAdapters[index] =
                                taskListAdaptersCopy.find { it.taskListID == taskList.id }!!
                    }

        }
    }

    private fun doesNotMatchOrder(): Boolean {

        /*
         * Possible Optimization is to check that doing the matchOrder() operation will change
         * anything or not but seems a little unnecessary right now
         */

        return boardView.taskListAdapters.size != board.size ||
                boardView.taskListAdapters.map { it.taskListID } != board.map { it.id }
    }
}


class BoardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val header: TextView
        get() = itemView.taskListHeader_textView
    val list: TaskListView
        get() = itemView.taskList_recyclerView
    val footer: Button
        get() = itemView.taskListFooter_textView
}