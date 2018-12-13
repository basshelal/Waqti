package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.android.views.TaskAdapter
import java.util.Collections


class DragRecyclerView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) :
        RecyclerView(context, attributeSet, defStyle) {

    val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, 0)
        }

        override fun isLongPressDragEnabled() = true

        override fun isItemViewSwipeEnabled() = false

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                             target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

            if (adapter != null && adapter is TaskAdapter) {

                if (fromPos < toPos) {
                    for (i in fromPos until toPos) {
                        Collections.swap((adapter as TaskAdapter).itemList, i, i + 1)
                        viewHolder.itemView.snackBar("Moved down!")
                    }
                } else {
                    for (i in fromPos downTo toPos + 1) {
                        Collections.swap((adapter as TaskAdapter).itemList, i, i - 1)
                        viewHolder.itemView.snackBar("Moved up!")
                    }
                }
                adapter!!.notifyItemMoved(fromPos, toPos)

                /*viewHolder.itemView.task_textView.text = "FROM"
                target.itemView.task_textView.text = "TO"
                recyclerView.snackBar("MOVED!")*/
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            recyclerView.snackBar("DONE!")
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            if (viewHolder.itemView.x <
                    recyclerView.x - viewHolder.itemView.width / 4) {
                recyclerView.snackBar("LEFT!")
            }
            if (viewHolder.itemView.x + viewHolder.itemView.width >
                    recyclerView.x + recyclerView.width + viewHolder.itemView.width / 4) {
                recyclerView.snackBar("RIGHT!")
            }
        }

        override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int, viewSizeOutOfBounds: Int, totalSize: Int, msSinceStartScroll: Long): Int {
            return super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll)
            // TODO: 13-Dec-18 Override this!
        }

    }
    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)


    init {
        layoutManager = LinearLayoutManager(getContext(), VERTICAL, false)
        adapter = TaskAdapter()
        itemTouchHelper.attachToRecyclerView(this)
    }

}