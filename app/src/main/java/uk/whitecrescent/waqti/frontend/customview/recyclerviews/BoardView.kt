package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.Bug
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.Inconvenience
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.FABOnScrollListener
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.Orientation
import uk.whitecrescent.waqti.frontend.SimpleItemTouchHelperCallback
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewListFragment
import uk.whitecrescent.waqti.mainActivity
import kotlin.math.roundToInt

class BoardView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    val boardAdapter: BoardAdapter
        get() = this.adapter as BoardAdapter

    val taskListAdapters = ArrayList<TaskListAdapter>()

    lateinit var itemTouchHelper: ItemTouchHelper

    init {
        layoutManager = PreCachingLayoutManager(context, HORIZONTAL, false,
                resources.getDimensionPixelSize(R.dimen.taskListWidth) * 2)
    }

    override fun setAdapter(_adapter: Adapter<*>?) {
        super.setAdapter(_adapter)
        require(_adapter != null && _adapter is BoardAdapter) {
            "Adapter must be non null and a BoardAdapter, passed in $_adapter"
        }

        attachHelpers()
    }

    private fun attachHelpers() {

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

                boardAdapter.apply {
                    board.move(fromPos, toPos).update()
                    matchOrder()
                    notifyItemMoved(fromPos, toPos)
                }
                mainActivity.viewModel.boardPosition = true to toPos
            }

        })
        itemTouchHelper.attachToRecyclerView(this)

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

    val allCards: List<CardView>
        get() = taskListAdapters.flatMap { it.allCards }
}

class BoardAdapter(val boardID: ID) : RecyclerView.Adapter<BoardViewHolder>() {

    val board = Caches.boards[boardID]

    lateinit var boardView: BoardView

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        require(recyclerView is BoardView) {
            "Recycler View attached to a BoardAdapter must be a BoardView"
        }
        boardView = recyclerView
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

    @Bug
    @Inconvenience
    // TODO: 01-Feb-19 Scrolling while dragging across on Nexus 5 doesn't trigger an onBindViewHolder
    // meaning that we end up dragging from list 0 to list 1 and then no more because for
    // some reason the next list isn't being bound, this isn't an issue on Pixel API 28 but
    // is on Nexus 5 API 21 and API 23, it also seems like this has nothing to do with width,
    // as we tried to do it with lower width lists and still the issue remained, we'd have to
    // do a manual bind or something like that
    @FutureIdea
    // TODO: 29-Dec-18 Setting a background image isn't impossible
    // generally what we'd need to do is have the image saved and divide it by the number of
    // lists there are in the board, then in onBindViewHolder for the Board we'd set that
    // list's background as that page of the overall image, there will be stretching though,
    // both when the lists are too few and when the lists are too many, but overall it's not
    // too hard
    // the idea above could be used to have the board's board card in the ViewBoardListFragment be the
    // picture
    //holder.list.setBackgroundResource(R.mipmap.waqti_icon)
    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {

        // Adapters get created and destroyed because their associated views do too, actually
        // more specifically, they get recycled

        val taskListAdapter = TaskListAdapter(board[position].id)
        holder.list.adapter = taskListAdapter
        boardView.addListAdapterIfNotExists(taskListAdapter)

        matchOrder()

        holder.itemView.taskList_rootView.updateLayoutParams {
            width = (boardView.mainActivity.dimensions.first.toFloat() * 0.7F).roundToInt()
        }

        holder.header.apply {
            setOnLongClickListener {
                boardView.itemTouchHelper.startDrag(holder)
                true
            }
            text = board[position].name
            setOnClickListener {
                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivity.viewModel.listID = board[holder.adapterPosition].id

                    it.clearFocusAndHideSoftKeyboard()

                    addToBackStack("")
                    replace(R.id.fragmentContainer, ViewListFragment(), VIEW_LIST_FRAGMENT)
                }
            }
        }
        holder.footer.apply {
            setOnClickListener {

                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivity.viewModel.boardID = this@BoardAdapter.boardID
                    it.mainActivity.viewModel.listID = holder.list.listAdapter.taskListID

                    it.clearFocusAndHideSoftKeyboard()

                    replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                    addToBackStack("")
                }
            }
        }
        holder.list.addOnScrollListener(FABOnScrollListener(holder.footer, Orientation.VERTICAL))


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
    val footer: FloatingActionButton
        get() = itemView.taskListFooter_textView
}

class PreCachingLayoutManager(context: Context,
                              orientation: Int = HORIZONTAL,
                              reverseLayout: Boolean = false,
                              private val extraLayoutSpacePx: Int = 600) :
        LinearLayoutManager(context, orientation, reverseLayout) {

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return extraLayoutSpacePx
    }
}