package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.task_list.view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.TASK_LISTS_CACHE_SIZE
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.SimpleItemTouchHelperCallback
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode.LINEAR
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode.NONE
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.ScrollSnapMode.PAGED
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewListFragment
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.parentView
import uk.whitecrescent.waqti.setColorScheme
import uk.whitecrescent.waqti.setEdgeEffectColor
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

    inline val boardAdapter: BoardAdapter?
        get() = this.adapter as BoardAdapter?

    // this actually only shows all the visible ViewHolders, not too useful to be honest :/
    inline val allViewHolders: List<BoardViewHolder>
        get() = boardAdapter?.board
                ?.map { findViewHolderForItemId(it.id) as? BoardViewHolder? }
                ?.filter { it != null }
                ?.map { it as BoardViewHolder } ?: emptyList()

    init {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false).also {
            it.isItemPrefetchEnabled = true
            it.initialPrefetchItemCount = 5
        }
        setRecycledViewPool(listViewHolderPool)
    }

    fun invalidateBoard() {
        allViewHolders.forEach {
            it.taskListView.recycledViewPool.clear()
            it.taskListView.allViewHolders.forEach {
                it.cardView {
                    this.requestLayout()
                    this.invalidate()
                }
            }
            it.rootView {
                this.requestLayout()
                this.invalidate()
            }
        }
        recycledViewPool.clear()
        requestLayout()
        invalidate()
    }

    fun setColorScheme(headerColorScheme: ColorScheme,
                       listColorScheme: ColorScheme) {
        setHeadersColorScheme(headerColorScheme)
        setListsColorScheme(listColorScheme)
    }

    fun setHeadersColorScheme(colorScheme: ColorScheme) {
        boardAdapter?.setHeadersColorScheme(colorScheme)
    }

    fun setListsColorScheme(colorScheme: ColorScheme) {
        boardAdapter?.setListsColorScheme(colorScheme)
    }

}

class BoardAdapter(val boardID: ID)
    : RecyclerView.Adapter<BoardViewHolder>() {

    val board = Caches.boards[boardID]

    lateinit var boardView: BoardView
    lateinit var itemTouchHelper: ItemTouchHelper
    private var snapHelper: SnapHelper? = null
    var taskListWidth: Int = 600

    val taskListAdapters = ArrayList<TaskListAdapter>()

    private inline val linearLayoutManager: LinearLayoutManager
        get() = boardView.layoutManager as LinearLayoutManager

    private var savedState: LinearLayoutManager.SavedState? = null

    var onInflate: BoardView.() -> Unit = {}

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
                if (newState == SCROLL_STATE_IDLE) {
                    saveState()
                    val currentBoardPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
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

        when (boardView.mainActivity.waqtiPreferences.boardScrollSnapMode) {
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
        snapHelper?.attachToRecyclerView(boardView)
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
        holder.headerTextView.text = taskList.name

        val headerColorScheme = if (taskList.headerColor == WaqtiColor.INHERIT)
            board.listColor.colorScheme
        else taskList.headerColor.colorScheme

        val cardColorScheme = if (taskList.cardColor == WaqtiColor.INHERIT)
            board.cardColor.colorScheme
        else taskList.cardColor.colorScheme

        holder.setColorScheme(headerColorScheme, cardColorScheme)
    }

    override fun onViewAttachedToWindow(holder: BoardViewHolder) {
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(boardView.context,
                R.anim.task_list_show_anim))
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
            it.taskListViewSafe {
                (parentView as? ConstraintLayout?) {
                    taskList_recyclerView { setEdgeEffectColor(colorScheme.dark) }
                    taskListHeader { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
                    taskListHeader_textView { textColor = colorScheme.text.toAndroidColor }
                    taskListFooter_fab { setColorScheme(colorScheme) }
                }
            }
        }
    }

    fun setListsColorScheme(colorScheme: ColorScheme) {
        taskListAdapters.forEach {
            it.taskListViewSafe { setColorScheme(colorScheme) }
        }
    }
}


class BoardViewHolder(view: View,
                      val adapter: BoardAdapter) : ViewHolder(view) {
    val header: CardView = itemView.taskListHeader
    val headerTextView: TextView = itemView.taskListHeader_textView
    val taskListView: TaskListView = itemView.taskList_recyclerView
    val addButton: FloatingActionButton = itemView.taskListFooter_fab
    val rootView: ConstraintLayout = itemView.taskList_rootView

    inline val mainActivity: MainActivity get() = itemView.mainActivity


    init {
        doInBackground {
            rootView.updateLayoutParams {
                width = adapter.taskListWidth
            }
            taskListView {
                addOnScrollListener(addButton.verticalFABOnScrollListener)
            }
            header {
                setOnClickListener {
                    @FragmentNavigation(from = VIEW_BOARD_FRAGMENT, to = VIEW_LIST_FRAGMENT)
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivityViewModel.listID = adapter.board[adapterPosition].id

                        it.clearFocusAndHideKeyboard()

                        addToBackStack(null)
                        replace(R.id.fragmentContainer, ViewListFragment(), VIEW_LIST_FRAGMENT)
                    }
                }
                setOnLongClickListener {
                    adapter.itemTouchHelper.startDrag(this@BoardViewHolder)
                    true
                }
            }
            addButton {
                setOnClickListener {
                    @FragmentNavigation(from = VIEW_BOARD_FRAGMENT, to = CREATE_TASK_FRAGMENT)
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivityViewModel.boardID = adapter.boardID
                        it.mainActivityViewModel.listID = taskListView.listAdapter?.taskListID ?: 0

                        it.clearFocusAndHideKeyboard()

                        replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                        addToBackStack(null)
                    }
                }
            }
        }
    }

    fun setColorScheme(headerColorScheme: ColorScheme,
                       listColorScheme: ColorScheme) {
        setHeaderColorScheme(headerColorScheme)
        setListColorScheme(listColorScheme)
    }

    fun setHeaderColorScheme(colorScheme: ColorScheme) {
        taskListView {
            setEdgeEffectColor(colorScheme.dark)
        }
        header { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
        headerTextView { textColor = colorScheme.text.toAndroidColor }
        addButton { setColorScheme(colorScheme) }
    }

    fun setListColorScheme(colorScheme: ColorScheme) {
        taskListView {
            setColorScheme(colorScheme)
        }
    }
}

enum class ScrollSnapMode {
    PAGED, LINEAR, NONE
}