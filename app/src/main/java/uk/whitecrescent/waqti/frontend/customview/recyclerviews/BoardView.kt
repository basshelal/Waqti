package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.task_list.view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.AbstractWaqtiList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.TASK_LISTS_CACHE_SIZE
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.extensions.F
import uk.whitecrescent.waqti.extensions.doInBackground
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.mainActivity
import uk.whitecrescent.waqti.extensions.mainActivityViewModel
import uk.whitecrescent.waqti.extensions.notifySwapped
import uk.whitecrescent.waqti.extensions.parentView
import uk.whitecrescent.waqti.extensions.recycledViewPool
import uk.whitecrescent.waqti.extensions.setColorScheme
import uk.whitecrescent.waqti.extensions.setEdgeEffectColor
import uk.whitecrescent.waqti.extensions.verticalFABOnScrollListener
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.SimpleItemTouchHelperCallback
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode.LINEAR
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode.NONE
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode.PAGED
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewListFragment
import kotlin.math.roundToInt

private val listViewHolderPool = recycledViewPool(TASK_LISTS_CACHE_SIZE)

class BoardView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : WaqtiRecyclerView(context, attributeSet, defStyle) {

    /**
     * Gets the adapter as a [BoardAdapter] or `null` if there is no adapter
     */
    inline val boardAdapter: BoardAdapter?
        get() = this.adapter as? BoardAdapter?

    override fun onFinishInflate() {
        super.onFinishInflate()
        setRecycledViewPool(listViewHolderPool)
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false).also {
            it.isItemPrefetchEnabled = true
            it.initialPrefetchItemCount = 5
        }
    }

    /**
     * Redraws all elements and entities in this [BoardView] essentially recreating the whole
     * thing,this is an expensive operation so use sparingly
     */
    fun invalidateBoard() {
        recycledViewPool.clear()
        requestLayout()
        invalidate()
        boardAdapter?.taskListAdapters?.forEach {
            it.notifyDataSetChanged()
            it.onInflate = { listAdapter?.notifyDataSetChanged() }
        }
        boardAdapter?.notifyDataSetChanged()
    }
}

class BoardAdapter(val boardID: ID) : RecyclerView.Adapter<BoardViewHolder>() {

    val board = Caches.boards[boardID]
    lateinit var boardView: BoardView
    lateinit var itemTouchHelper: ItemTouchHelper
    var snapHelper: SnapHelper? = null
    var taskListWidth: Int = 600
    var listHeaderTextSize: Int = 28
    val taskListAdapters = ArrayList<TaskListAdapter>()
    var savedState: LinearLayoutManager.SavedState? = null
    var onInflate: BoardView.() -> Unit = {}

    var onStartDragList: (BoardViewHolder) -> Unit = { }
    var onStartDragTask: (TaskViewHolder) -> Unit = { }

    inline val linearLayoutManager: LinearLayoutManager? get() = boardView.linearLayoutManager
    inline val allCards: List<CardView> get() = taskListAdapters.flatMap { it.allListCards }

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        require(recyclerView is BoardView) {
            "Recycler View attached to a BoardAdapter must be a BoardView," +
                    " passed in ${recyclerView::class}"
        }
        boardView = recyclerView

        restoreState()

        onInflate(boardView)
        onInflate = {}

        val percent = boardView.mainActivity.preferences.listWidth / 100.0
        taskListWidth = (boardView.mainActivity.usableScreenWidth * percent).roundToInt()
        listHeaderTextSize = boardView.mainActivity.preferences.listHeaderTextSize

        doInBackground {
            board.forEach {
                getOrCreateListAdapter(it.id)
            }
            matchOrder()
            attachHelpers()


            /*
            How many lists can fit into one screen?
            usableScreenWidth / tasklistWidth = ~1.4
            How many full screens will show, remember that the pointer will never
            reach the last list!

            val usableScreenWidth = boardView.mainActivity.screenDimensions.first.toFloat()
            val lists = usableScreenWidth.toDouble() / taskListWidth.toDouble()

            logE("ScreenWidth: $usableScreenWidth")
            logE("Percent: $percent")
            logE("TaskListWidth: $taskListWidth")
            logE("Lists in 1 screen: $lists")
            logE("Number of Lists: ${taskListAdapters.size}")

            */
        }
    }

    private fun attachHelpers() {

        boardView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_IDLE) {
                    saveState()
                    val currentBoardPos = linearLayoutManager
                            ?.findFirstCompletelyVisibleItemPosition() ?: 0
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

        if (snapHelper != null) {
            snapHelper?.attachToRecyclerView(null)
        }

        when (boardView.mainActivity.preferences.boardScrollSnapMode) {
            PAGED -> {
                snapHelper = PagerSnapHelper()
            }
            LINEAR -> {
                snapHelper = LinearSnapHelper()
            }
            NONE -> {
                snapHelper = null
            }
        }
        if (snapHelper != null) {
            boardView.onFlingListener = null
            snapHelper?.attachToRecyclerView(boardView)
            boardView.addVelocityTrackerOnFlingListener()
        }
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
        val taskList = board[position]
        holder.taskListView.adapter = getOrCreateListAdapter(taskList.id)
        holder.taskListView.listAdapter?.onStartDragTask = onStartDragTask
        holder.headerTextView.text = taskList.name

        val headerColorScheme =
                if (taskList.headerColor == WaqtiColor.INHERIT)
                    board.listColor.colorScheme
                else taskList.headerColor.colorScheme

        val listColorScheme =
                if (taskList.cardColor == WaqtiColor.INHERIT)
                    board.cardColor.colorScheme
                else taskList.cardColor.colorScheme

        holder.headerColorScheme = headerColorScheme
        holder.listColorScheme = listColorScheme
    }

    override fun onViewAttachedToWindow(holder: BoardViewHolder) {
        /*holder.itemView.startAnimation(AnimationUtils.loadAnimation(boardView.context,
                R.anim.task_list_show_anim))*/
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
        return taskListAdapters.find { it.taskListID == taskListID }?.also { it.restoreState() }
    }

    fun getOrCreateListAdapter(taskListID: ID): TaskListAdapter {
        return getListAdapter(taskListID) ?: addListAdapter(TaskListAdapter(taskListID, this))
    }

    fun indexOfAdapter(taskListAdapter: TaskListAdapter): Int {
        return taskListAdapters.indexOf(taskListAdapter)
    }

    fun saveState() {
        if (::boardView.isInitialized) {
            savedState = boardView.layoutManager?.onSaveInstanceState()
                    as LinearLayoutManager.SavedState
        }
    }

    fun restoreState() {
        if (::boardView.isInitialized) {
            boardView.layoutManager?.onRestoreInstanceState(savedState)
        }
    }

    fun setHeadersColorScheme(colorScheme: ColorScheme) {
        taskListAdapters.forEach {
            it.taskListView {
                (parentView as? ConstraintLayout?) {
                    taskList_recyclerView {
                        scrollBarColor = colorScheme.text
                        setEdgeEffectColor(colorScheme.dark)
                    }
                    taskListHeader { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
                    taskListHeader_textView { textColor = colorScheme.text.toAndroidColor }
                    taskListFooter_fab { setColorScheme(colorScheme) }
                }
            }
        }
    }

    fun setListsColorScheme(colorScheme: ColorScheme) {
        taskListAdapters.forEach { it.taskListView { setColorScheme(colorScheme) } }
    }

    fun findTaskViewHolder(taskID: ID): TaskViewHolder? {
        return taskListAdapters.find {
            it.allViewHolders.firstOrNull { it.taskID == taskID } != null
        }?.taskListView?.findViewHolderForItemId(taskID) as? TaskViewHolder
    }

    fun findTaskViewHolder(view: View): TaskViewHolder? {
        taskListAdapters.map { it.taskListView }.forEach {
            (it?.findContainingViewHolder(view) as? TaskViewHolder).also {
                if (it != null) return it
            }
        }
        return null
    }

    fun swapTaskViewHolders(oldViewHolder: TaskViewHolder, newViewHolder: TaskViewHolder) {
        when {
            oldViewHolder.taskID == newViewHolder.taskID -> return
            oldViewHolder.taskListID == newViewHolder.taskListID -> {
                // They are in the same list

                val oldTaskListAdapter = getListAdapter(oldViewHolder.taskListID)
                val newTaskListAdapter = getListAdapter(newViewHolder.taskListID)

                if (oldTaskListAdapter != newTaskListAdapter ||
                        oldTaskListAdapter == null || newTaskListAdapter == null) return

                val oldPosition = oldViewHolder.adapterPosition
                val newPosition = newViewHolder.adapterPosition

                if (oldPosition >= 0 && newPosition >= 0) {
                    oldTaskListAdapter.taskList.swap(oldPosition, newPosition).update()
                    oldTaskListAdapter.notifySwapped(oldPosition, newPosition)
                }
            }
            oldViewHolder.taskListID != newViewHolder.taskListID -> {
                // They are in different lists

                val oldTaskListAdapter = getListAdapter(oldViewHolder.taskListID)
                val newTaskListAdapter = getListAdapter(newViewHolder.taskListID)

                if (oldTaskListAdapter == newTaskListAdapter ||
                        oldTaskListAdapter == null || newTaskListAdapter == null) return

                val oldTaskList = oldTaskListAdapter.taskList
                val newTaskList = newTaskListAdapter.taskList
                val taskToMove = oldTaskList[oldViewHolder.taskID]
                val oldPosition = oldViewHolder.adapterPosition
                val newPosition = newViewHolder.adapterPosition

                if (oldPosition >= 0 && newPosition >= 0) {
                    AbstractWaqtiList.moveElement(
                            listFrom = oldTaskList, listTo = newTaskList,
                            element = taskToMove, toIndex = newPosition
                    )

                    newTaskListAdapter.notifyItemInserted(newPosition)
                    oldTaskListAdapter.notifyItemRemoved(oldPosition)
                }
            }
        }
    }

    fun swapBoardViewHolders(oldViewHolder: BoardViewHolder, newViewHolder: BoardViewHolder) {

    }

}


class BoardViewHolder(view: View,
                      val adapter: BoardAdapter) : ViewHolder(view) {
    val header: MaterialCardView = itemView.taskListHeader
    val headerTextView: TextView = itemView.taskListHeader_textView
    val taskListView: TaskListView = itemView.taskList_recyclerView
    val addButton: FloatingActionButton = itemView.taskListFooter_fab
    val rootView: ConstraintLayout = itemView.taskList_rootView

    inline val mainActivity: MainActivity get() = itemView.mainActivity

    var headerColorScheme: ColorScheme = ColorScheme.WAQTI_DEFAULT
        set(value) {
            field = value
            taskListView {
                scrollBarColor = value.text
                setEdgeEffectColor(value.dark)
            }
            header { setCardBackgroundColor(value.main.toAndroidColor) }
            headerTextView { textColor = value.text.toAndroidColor }
            addButton { setColorScheme(value) }
        }

    var listColorScheme: ColorScheme = ColorScheme.WAQTI_DEFAULT
        set(value) {
            field = value
            taskListView { setColorScheme(value) }
        }

    init {
        doInBackground {
            rootView.updateLayoutParams {
                width = adapter.taskListWidth
            }
            headerTextView { textSize = adapter.listHeaderTextSize.F }
            taskListView {
                addOnScrollListener(addButton.verticalFABOnScrollListener)
            }
            header {
                setOnClickListener {
                    mainActivityViewModel.listID = adapter.board[adapterPosition].id
                    ViewListFragment.show(mainActivity)
                }
                setOnLongClickListener {
                    adapter.onStartDragList(this@BoardViewHolder)
                    true
                }
            }
            addButton {
                setOnClickListener {
                    mainActivityViewModel.boardID = adapter.boardID
                    mainActivityViewModel.listID = taskListView.listAdapter?.taskListID ?: 0
                    CreateTaskFragment.show(mainActivity)
                }
            }
        }
    }
}

enum class ScrollSnapMode {
    PAGED, LINEAR, NONE
}