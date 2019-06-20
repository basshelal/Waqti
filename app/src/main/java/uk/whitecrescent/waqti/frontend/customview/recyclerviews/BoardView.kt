package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.TASK_LISTS_CACHE_SIZE
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.SimpleItemTouchHelperCallback
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewListFragment
import uk.whitecrescent.waqti.logE
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.verticalFABOnScrollListener
import kotlin.math.roundToInt


private val listViewHolderPool = object : RecyclerView.RecycledViewPool() {

    override fun setMaxRecycledViews(viewType: Int, max: Int) {
        super.setMaxRecycledViews(viewType, TASK_LISTS_CACHE_SIZE)
    }
}

class BoardView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    inline val boardAdapter: BoardAdapter
        get() = this.adapter as BoardAdapter
    inline val linearLayoutManager: LinearLayoutManager
        get() = this.layoutManager as LinearLayoutManager

    init {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false).also {
            it.isItemPrefetchEnabled = true
            it.initialPrefetchItemCount = 5
        }
        setRecycledViewPool(listViewHolderPool)
        this.isNestedScrollingEnabled = false
        logE("New BoardView")
    }
}

class BoardAdapter(val boardID: ID)
    : RecyclerView.Adapter<BoardViewHolder>() {

    val board = Caches.boards[boardID]

    lateinit var boardView: BoardView
    lateinit var itemTouchHelper: ItemTouchHelper
    lateinit var snapHelper: PagerSnapHelper
    var taskListWidth: Int = 600
    var clickedTaskListView: TaskListView? = null

    private val taskListAdapters = ArrayList<TaskListAdapter>()

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        require(recyclerView is BoardView) {
            "Recycler View attached to a BoardAdapter must be a BoardView," +
                    " passed in ${recyclerView::class}"
        }
        boardView = recyclerView

        val percent = boardView.mainActivity.waqtiPreferences.taskListWidth / 100.0
        taskListWidth = (boardView.mainActivity.dimensions.first.toFloat() * percent).roundToInt()

        doInBackground {
            board.forEach {
                getOrCreateListAdapter(it.id)
            }
            matchOrder()
            attachHelpers()
        }
    }

    private fun attachHelpers() {

        boardView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_IDLE && recyclerView is BoardView) {
                    val currentBoardPos = recyclerView.linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    boardView.mainActivityViewModel.boardPosition.changeTo(true to currentBoardPos)
                }
            }
        })

        itemTouchHelper = ItemTouchHelper(object : SimpleItemTouchHelperCallback() {

            override fun isLongPressDragEnabled() = false

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

            override fun onMoved(recyclerView: RecyclerView, viewHolder: ViewHolder, fromPos: Int,
                                 target: ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                board.move(fromPos, toPos).update()
                matchOrder()
                notifyItemMoved(fromPos, toPos)
                boardView.mainActivityViewModel.boardPosition.changeTo(true to toPos)
            }

        })
        itemTouchHelper.attachToRecyclerView(boardView)

        snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(boardView)
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
                        .inflate(R.layout.task_list, parent, false),
                this
        )
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.taskListView.adapter = getOrCreateListAdapter(board[position].id)
        holder.header.text = board[position].name
    }

    private fun matchOrder() {

        val taskListAdaptersCopy = ArrayList(taskListAdapters)
        if (doesNotMatchOrder()) {

            board.filter { taskList -> taskList.id in taskListAdaptersCopy.map { it.taskListID } }
                    .mapIndexed { index, taskList -> index to taskList }.toMap()
                    .forEach { entry ->
                        val (index, taskList) = entry

                        taskListAdapters[index] =
                                taskListAdaptersCopy.find { it.taskListID == taskList.id }!!
                    }
        }
    }

    private fun doesNotMatchOrder(): Boolean {
        val adapterIDs = taskListAdapters.map { it.taskListID }

        return adapterIDs != board.take(adapterIDs.size).map { it.id }
    }

    fun addListAdapter(taskListAdapter: TaskListAdapter): TaskListAdapter {
        return taskListAdapter.also {
            taskListAdapters.add(it)
        }
    }

    fun getListAdapter(taskListID: ID): TaskListAdapter? {
        return taskListAdapters.find { it.taskListID == taskListID }
    }

    fun getOrCreateListAdapter(taskListID: ID): TaskListAdapter {
        return getListAdapter(taskListID) ?: addListAdapter(TaskListAdapter(taskListID, this))
    }

    fun indexOfAdapter(taskListAdapter: TaskListAdapter): Int {
        return taskListAdapters.indexOf(taskListAdapter)
    }
}


class BoardViewHolder(view: View,
                      val adapter: BoardAdapter) : ViewHolder(view) {
    val header: TextView = itemView.taskListHeader_textView
    val taskListView: TaskListView = itemView.taskList_recyclerView
    val addButton: FloatingActionButton = itemView.taskListFooter_fab
    val rootView: ConstraintLayout = itemView.taskList_rootView

    inline val mainActivity: MainActivity get() = itemView.mainActivity


    init {
        doInBackground {
            rootView.updateLayoutParams {
                width = adapter.taskListWidth
            }
            taskListView.apply {
                clearOnScrollListeners()
                addOnScrollListener(addButton.verticalFABOnScrollListener)
            }
            header.apply {
                setOnClickListener {
                    @GoToFragment
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        adapter.clickedTaskListView = taskListView

                        it.mainActivityViewModel.listID = adapter.board[adapterPosition].id

                        it.clearFocusAndHideSoftKeyboard()

                        addToBackStack(null)
                        replace(R.id.fragmentContainer, ViewListFragment(), VIEW_LIST_FRAGMENT)
                    }
                }
                setOnLongClickListener {
                    adapter.itemTouchHelper.startDrag(this@BoardViewHolder)
                    true
                }
            }
            addButton.apply {
                setOnClickListener {

                    @GoToFragment
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivityViewModel.boardID = adapter.boardID
                        it.mainActivityViewModel.listID = taskListView.listAdapter.taskListID

                        it.clearFocusAndHideSoftKeyboard()

                        replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                        addToBackStack(null)
                    }
                }
            }
        }
    }

}

class PreCachingLayoutManager(context: Context,
                              @RecyclerView.Orientation
                              orientation: Int = HORIZONTAL,
                              reverseLayout: Boolean = false,
                              private val extraLayoutSpacePx: Int = 600) :
        LinearLayoutManager(context, orientation, reverseLayout) {


    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        super.calculateExtraLayoutSpace(state, IntArray(2) { extraLayoutSpacePx })
    }
}