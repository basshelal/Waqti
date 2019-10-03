@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.task_card.view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.AbstractWaqtiList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.TASKS_CACHE_SIZE
import uk.whitecrescent.waqti.backend.persistence.getParent
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.view.ViewTaskFragment
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.lastPosition
import uk.whitecrescent.waqti.locationOnScreen
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.notifySwapped
import uk.whitecrescent.waqti.recycledViewPool
import uk.whitecrescent.waqti.setIndeterminateColor
import kotlin.math.roundToInt

private val taskViewHolderPool = recycledViewPool(TASKS_CACHE_SIZE)
private const val scrollAmount = 1.718281828459045 // E - 1
private const val draggingViewAlpha = 0F
private val defaultInterpolator = AccelerateDecelerateInterpolator()

class TaskListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    var scrollBarColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT.colorScheme.text

    inline val listAdapter: TaskListAdapter?
        get() = adapter as TaskListAdapter?

    inline val allViewHolders: List<TaskViewHolder>
        get() = listAdapter?.taskList
                ?.map { findViewHolderForItemId(it.id) as? TaskViewHolder? }
                ?.filter { it != null }
                ?.map { it as TaskViewHolder } ?: emptyList()

    override fun onFinishInflate() {
        super.onFinishInflate()
        setRecycledViewPool(taskViewHolderPool)
        layoutManager = LinearLayoutManager(context, VERTICAL, false).also {
            it.isItemPrefetchEnabled = true
            it.initialPrefetchItemCount = 15
        }
        itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: ViewHolder?): Boolean {
                holder?.itemView?.alpha = draggingViewAlpha
                dispatchAddFinished(holder)
                return true
            }
        }
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        allViewHolders.forEach { it.setColorScheme(colorScheme) }
        listAdapter?.notifyDataSetChanged()
    }

    @Suppress("unused")
    protected fun onDrawHorizontalScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        scrollBar.setColorFilter(scrollBarColor.toAndroidColor, PorterDuff.Mode.SRC_ATOP)
        scrollBar.setBounds(l, t, r, b)
        scrollBar.draw(canvas)
    }

    @Suppress("unused")
    protected fun onDrawVerticalScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        scrollBar.setColorFilter(scrollBarColor.toAndroidColor, PorterDuff.Mode.SRC_ATOP)
        scrollBar.setBounds(l, t, r, b)
        scrollBar.draw(canvas)
    }

}

class TaskListAdapter(val taskListID: ID,
                      val boardAdapter: BoardAdapter) : Adapter<TaskViewHolder>() {

    val taskList = Caches.taskLists[taskListID]
    var taskListView: TaskListView? = null
    var savedState: LinearLayoutManager.SavedState? = null
    var onInflate: TaskListView.() -> Unit = { }

    var onStartDragTask: (TaskViewHolder) -> Unit = { }

    inline val linearLayoutManager: LinearLayoutManager? get() = taskListView?.layoutManager as? LinearLayoutManager?
    inline val allViewHolders: List<TaskViewHolder>
        get() = taskListView?.allViewHolders ?: emptyList()
    inline val allListCards: List<CardView> get() = allViewHolders.map { it.cardView }
    inline val allBoardCards: List<CardView> get() = boardAdapter.allCards

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        check(recyclerView is TaskListView)
        taskListView = recyclerView

        taskListView {
            restoreState()
            onInflate(this)
            onInflate = {}
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        saveState()
                    }
                }
            })
            setOnDragListener { _, event ->
                val draggingState = event.localState as DragEventLocalState
                when (event.action) {
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        if (this@TaskListAdapter.taskListID != draggingState.taskListID) {
                            val lastViewHolderY = findViewHolderForAdapterPosition(lastPosition)
                                    ?.itemView?.y
                            if (taskList.isEmpty() || (lastViewHolderY != null && event.y > lastViewHolderY)) {
                                val otherAdapter = boardAdapter.getListAdapter(draggingState.taskListID)
                                if (otherAdapter != null) {
                                    onDragAcrossEmptyList(draggingState, otherAdapter)
                                    onScrollAcrossEmptyList()
                                }
                            }
                        }
                    }
                }
                true
            }
        }
    }

    override fun getItemCount() = taskList.size

    override fun getItemId(position: Int) = taskList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false),
                this)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.doInBackground {
            taskID = taskList[position].id
            taskListID = this@TaskListAdapter.taskListID
            setColorScheme(if (taskList.cardColor == WaqtiColor.INHERIT)
                taskList.getParent().cardColor.colorScheme
            else taskList.cardColor.colorScheme)
            textView.text = taskList[position].name
        }
        holder.apply {
            cardView.setOnDragListener { _, event ->
                val draggingState = event.localState as DragEventLocalState
                val draggingView = taskListView
                        ?.findViewHolderForAdapterPosition(draggingState.adapterPosition)?.itemView
                when (event.action) {
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onDrag(draggingState, this)
                        }
                        // Safety measure just in case
                        doInBackground {
                            allBoardCards.filter { it != draggingView && it.alpha < 1F }
                                    .forEach { it.alpha = 1F }
                        }
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        if (draggingView?.alpha != draggingViewAlpha)
                            draggingView?.alpha = draggingViewAlpha
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        // Safety measure just in case
                        doInBackground {
                            allBoardCards.filter { it != draggingView && it.alpha < 1F }
                                    .forEach { it.alpha = 1F }
                        }
                    }
                    DragEvent.ACTION_DROP -> {
                        draggingState updateToMatch this
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        draggingView?.alpha = 1F
                        // Safety measure just in case
                        doInBackground { allBoardCards.forEach { it.alpha = 1F } }
                    }
                }
                return@setOnDragListener true

            }
        }
    }

    override fun onViewAttachedToWindow(holder: TaskViewHolder) {
        /*holder.itemView.startAnimation(AnimationUtils.loadAnimation(taskListView.context,
                R.anim.task_list_item_show_anim))*/
    }

    private inline fun onDrag(draggingState: DragEventLocalState, holder: TaskViewHolder): Boolean {
        return when {

            draggingState.taskListID == holder.taskListID -> {
                // they are the same
                if (draggingState.adapterPosition == holder.adapterPosition) {
                    false
                }
                // they are diff but in same list
                else {
                    onDragInSameList(draggingState, holder)

                    // Scroll down
                    checkForScrollDown(draggingState, holder)

                    // Scroll up
                    checkForScrollUp(draggingState, holder)

                    true
                }
            }

            // they are in diff lists
            draggingState.taskListID != holder.taskListID -> {

                val otherAdapter = boardAdapter.getListAdapter(draggingState.taskListID)
                if (otherAdapter != null) {

                    onDragAcrossFilledList(draggingState, holder, otherAdapter)

                    onScrollAcrossFilledList()

                    // Scroll down
                    checkForScrollDown(draggingState, holder)

                    // Scroll up
                    checkForScrollUp(draggingState, holder)

                    true
                } else false
            }

            // impossible
            else -> {
                false
            }

        }
    }

    private inline fun onDragInSameList(draggingState: DragEventLocalState, holder: TaskViewHolder) {
        val newDragPos = holder.adapterPosition
        val oldDragPos = draggingState.adapterPosition

        taskList.swap(oldDragPos, newDragPos).update()
        notifySwapped(oldDragPos, newDragPos)

        draggingState.adapterPosition = newDragPos
    }

    private inline fun checkForScrollDown(draggingState: DragEventLocalState, holder: TaskViewHolder) {

        if (draggingState.adapterPosition >=
                (linearLayoutManager?.findLastCompletelyVisibleItemPosition() ?: -1)) {
            val scrollBy = (holder.itemView.height * scrollAmount).roundToInt()
            taskListView?.smoothScrollBy(0, scrollBy, defaultInterpolator)
        }

    }

    private inline fun checkForScrollUp(draggingState: DragEventLocalState, holder: TaskViewHolder) {

        if (draggingState.adapterPosition <=
                (linearLayoutManager?.findFirstCompletelyVisibleItemPosition() ?: -1)) {
            val scrollBy = (holder.itemView.height * -scrollAmount).roundToInt()
            taskListView?.smoothScrollBy(0, scrollBy, defaultInterpolator)
        }

    }

    private inline fun onDragAcrossFilledList(draggingState: DragEventLocalState,
                                              holder: TaskViewHolder,
                                              otherAdapter: TaskListAdapter) {

        val otherTaskList = otherAdapter.taskList
        val task = otherTaskList[draggingState.taskID]
        val newDragPos = holder.adapterPosition

        AbstractWaqtiList.moveElement(
                listFrom = otherTaskList, listTo = this.taskList,
                element = task, toIndex = newDragPos
        )

        this.notifyItemInserted(newDragPos)
        otherAdapter.notifyItemRemoved(draggingState.adapterPosition)

        draggingState.taskListID = holder.taskListID
        draggingState.adapterPosition = newDragPos

        // TODO: 13-Jun-19 Add an onDragListener to the list
        //  so that when you're dragging a task over into the white part of the list
        //  it will still react, we'd need to know the point we're dragging over
        //  and what is the holder that is closest to it so that we act accordingly
    }

    private inline fun onDragAcrossEmptyList(draggingState: DragEventLocalState,
                                             otherAdapter: TaskListAdapter) {

        val otherTaskList = otherAdapter.taskList
        val task = otherTaskList[draggingState.taskID]
        val newDragPos = this.taskList.nextIndex

        AbstractWaqtiList.moveElement(
                listFrom = otherTaskList, listTo = this.taskList,
                element = task, toIndex = newDragPos
        )

        this.notifyItemInserted(newDragPos)
        otherAdapter.notifyItemRemoved(draggingState.adapterPosition)

        draggingState.taskListID = this.taskListID
        draggingState.adapterPosition = newDragPos
    }

    private inline fun onScrollAcrossFilledList() {

        taskListView {
            // The list that we will be scrolling to
            val newBoardPosition = boardAdapter.indexOfAdapter(this@TaskListAdapter)

            val currentBoardPosition = mainActivityViewModel.boardPosition.position

            if (newBoardPosition > currentBoardPosition) scrollRight(currentBoardPosition)
            else if (newBoardPosition < currentBoardPosition) scrollLeft(currentBoardPosition)

            mainActivityViewModel.boardPosition.changeTo(true to newBoardPosition)

        }
    }

    private inline fun onScrollAcrossEmptyList() {

        taskListView {
            // The list that we will be scrolling to
            val newBoardPosition = boardAdapter.indexOfAdapter(this@TaskListAdapter)

            val currentBoardPosition = mainActivityViewModel.boardPosition.position

            if (newBoardPosition > currentBoardPosition) scrollRight(currentBoardPosition)
            else if (newBoardPosition < currentBoardPosition) scrollLeft(currentBoardPosition)

            mainActivityViewModel.boardPosition.changeTo(true to newBoardPosition)

        }
    }

    private inline fun scrollLeft(currentBoardPosition: Int) {
        taskListView {
            val scrollBy: Int

            val screenWidth = mainActivity.dimensions.first

            val listWidth = boardAdapter.taskListWidth

            if (currentBoardPosition == boardAdapter.lastPosition) {
                scrollBy = (listWidth) - ((screenWidth - listWidth) / 2)
            } else scrollBy = listWidth

            boardAdapter.boardView.smoothScrollBy(-scrollBy, 0, defaultInterpolator)
        }
    }

    private inline fun scrollRight(currentBoardPosition: Int) {
        taskListView {
            val scrollBy: Int

            val screenWidth = mainActivity.dimensions.first

            val listWidth = boardAdapter.taskListWidth

            if (currentBoardPosition == 0) {
                scrollBy = (listWidth) - ((screenWidth - listWidth) / 2)
            } else scrollBy = listWidth

            boardAdapter.boardView.smoothScrollBy(scrollBy, 0, defaultInterpolator)
        }
    }

    fun saveState() {
        savedState = linearLayoutManager?.onSaveInstanceState() as? LinearLayoutManager.SavedState?
    }

    fun restoreState() {
        linearLayoutManager?.onRestoreInstanceState(savedState)
    }

}

class TaskViewHolder(view: View, private val adapter: TaskListAdapter) : ViewHolder(view) {

    var taskID: ID = 0L
    var taskListID: ID = adapter.taskListID
    val cardView: CardView = itemView.task_cardView
    val progressBar: ProgressBar = itemView.taskCard_progressBar
    val textView: TextView = itemView.task_textView

    inline val mainActivity: MainActivity get() = itemView.mainActivity

    init {
        doInBackground {
            textView.textSize = mainActivity.preferences.cardTextSize.toFloat()
            cardView {
                setOnClickListener {
                    mainActivityViewModel.taskID = taskID
                    mainActivityViewModel.listID = taskListID
                    ViewTaskFragment.show(mainActivity)
                }
                setOnLongClickListener {

                    adapter.onStartDragTask(this@TaskViewHolder)


                    return@setOnLongClickListener false
                }
            }
            textView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    fun updateDragShadowColorScheme(colorScheme: ColorScheme) {
        (itemView as CardView) {
            updateDragShadow(ShadowBuilder(this.also {
                it.taskCard_progressBar { setIndeterminateColor(colorScheme.text) }
                it.task_cardView { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
                it.task_textView { textColor = colorScheme.text.toAndroidColor }
            }))
        }
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        progressBar { setIndeterminateColor(colorScheme.text) }
        cardView { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
        textView { textColor = colorScheme.text.toAndroidColor }
    }
}

data class DragEventLocalState(
        var taskID: ID,
        var taskListID: ID,
        var adapterPosition: Int) {

    inline infix fun updateToMatch(viewHolder: TaskViewHolder) {
        this.taskID = viewHolder.taskID
        this.taskListID = viewHolder.taskListID
        this.adapterPosition = viewHolder.adapterPosition
    }

    inline infix fun doesNotMatch(viewHolder: TaskViewHolder): Boolean {
        return this.taskID != viewHolder.taskID ||
                this.taskListID != viewHolder.taskListID ||
                this.adapterPosition != viewHolder.adapterPosition
    }

    inline infix fun matches(viewHolder: TaskViewHolder): Boolean {
        return this.taskID == viewHolder.taskID &&
                this.taskListID == viewHolder.taskListID &&
                this.adapterPosition == viewHolder.adapterPosition
    }

}

private class ShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)

        val viewPoint = view.locationOnScreen

        val x = view.mainActivity.currentTouchPoint.x - viewPoint.x
        val y = view.mainActivity.currentTouchPoint.y - viewPoint.y
        outShadowTouchPoint?.set(x, y)
    }

}
