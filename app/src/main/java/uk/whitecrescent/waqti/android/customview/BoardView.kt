package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_list.view.*
import uk.whitecrescent.waqti.android.scrollToEnd
import uk.whitecrescent.waqti.model.collections.TaskList

class BoardView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) :
        RecyclerView(context, attributeSet, defStyle) {

    val boardAdapter: BoardAdapter
        get() = this.adapter as BoardAdapter

    val taskListAdapters = ArrayList<TaskListAdapter>()

    init {
        layoutManager = LinearLayoutManager(this.context, HORIZONTAL, false)
    }

    override fun setAdapter(_adapter: Adapter<*>?) {
        super.setAdapter(_adapter)
        assert(this.adapter != null)
        assert(this.adapter is BoardAdapter)
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            // TODO: 24-Dec-18 remember to make the dragging only doable from the header, currently its from anywhere
            // so a very fast scroll or a hold on an empty list will trigger a drag

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, 0)
            }

            override fun isLongPressDragEnabled() = true

            override fun isItemViewSwipeEnabled() = false

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                // TODO: 15-Dec-18 check what this does?
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                /*This will never be called as we do not support swiping*/
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                                 target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                /*if (adapter != null && adapter is BoardAdapter) {

                    if (fromPos < toPos) {
                        (fromPos until toPos).forEach {
                            Collections.swap((adapter as BoardAdapter).itemList, it, it + 1)
                        }
                    } else {
                        (fromPos downTo toPos + 1).forEach {
                            Collections.swap((adapter as BoardAdapter).itemList, it, it - 1)
                        }
                    }
                    adapter!!.notifyItemMoved(fromPos, toPos)
                }*/
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
            }

            override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (viewHolder.itemView.x <
                        recyclerView.x - viewHolder.itemView.width / 4) {
                }
                if (viewHolder.itemView.x + viewHolder.itemView.width >
                        recyclerView.x + recyclerView.width + viewHolder.itemView.width / 4) {
                }
            }

            override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int, viewSizeOutOfBounds: Int, totalSize: Int, msSinceStartScroll: Long): Int {
                return super.interpolateOutOfBoundsScroll(
                        recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll)
                // TODO: 13-Dec-18 Override this to make better when we drag outside the bounds
            }

        }).attachToRecyclerView(this)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // TODO: 13-Dec-18 Here we put AutoScrolling horizontally
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    // We need to make EVERYTHING exist in a single class/ file, the best way to emulate the
    // problem is by trying to add a new Empty List, in Board we may need to have a list of all
    // the lists (DragRecyclerViews) that way we have access to their adapters as well and so we
    // can modify them directly easily and completely

    fun addNewEmptyList(name: String) {
        boardAdapter.add(TaskList(name))
        scrollToEnd()
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