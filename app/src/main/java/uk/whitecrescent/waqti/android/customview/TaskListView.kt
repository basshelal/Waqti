package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.model.task.ID


class TaskListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) :
        RecyclerView(context, attributeSet, defStyle) {

    val listAdapter: TaskListAdapter
        get() = adapter as TaskListAdapter
    //val itemTouchHelper: ItemTouchHelper
    val board: BoardView
        get() = parent.parent as BoardView

    init {
        layoutManager = LinearLayoutManager(getContext(), VERTICAL, false)
        adapter = TaskListAdapter()
        /*itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, 0)
            }

            override fun isLongPressDragEnabled() = true

            override fun isItemViewSwipeEnabled() = false

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                recyclerView.snackBar("MOVING!")
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                                 target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                if (adapter != null && adapter is TaskListAdapter) {

                    if (fromPos < toPos) {
                        for (i in fromPos until toPos) {
                            Collections.swap((adapter as TaskListAdapter).itemList, i, i + 1)
                            viewHolder.itemView.snackBar("Moved down!")
                        }
                    } else {
                        for (i in fromPos downTo toPos + 1) {
                            Collections.swap((adapter as TaskListAdapter).itemList, i, i - 1)
                            viewHolder.itemView.snackBar("Moved up!")
                        }
                    }
                    adapter!!.notifyItemMoved(fromPos, toPos)

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
                    recyclerView.snackBar("Moving ${viewHolder.adapterPosition} left!")
                }
                if (viewHolder.itemView.x + viewHolder.itemView.width >
                        recyclerView.x + recyclerView.width + viewHolder.itemView.width / 4) {
                    recyclerView.snackBar("Moving ${viewHolder.adapterPosition} right!")

                    if (viewHolder.adapterPosition != -1 &&
                             listAdapter.itemList[viewHolder.adapterPosition] in listAdapter.itemList) {

                         val rightListIndex = p.views.indexOf(this@TaskListView) + 1
                         val rightList = p.views[rightListIndex]
                         val task = this@TaskListView.listAdapter.itemList[viewHolder.adapterPosition]

                         if (task in listAdapter.itemList && task !in rightList.listAdapter.itemList) {
                             p.smoothScrollToPosition(rightListIndex)
                             rightList.listAdapter.itemList.add(task)
                             this@TaskListView.listAdapter.itemList.remove(task)
                             rightList.listAdapter.notifyDataSetChanged()
                             this@TaskListView.listAdapter.notifyDataSetChanged()


                         }
                     }
                }
            }

            override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int, viewSizeOutOfBounds: Int, totalSize: Int, msSinceStartScroll: Long): Int {
                return super.interpolateOutOfBoundsScroll(recyclerView, viewSize,
                        viewSizeOutOfBounds, totalSize, 1500)
                // TODO: 13-Dec-18 Improve this later
            }

        })
        itemTouchHelper.attachToRecyclerView(this)*/
    }

}

class TaskViewHolder(view: View)
    : RecyclerView.ViewHolder(view) {
    var taskID: ID = 0L
}