package uk.whitecrescent.waqti.android.views

import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.checkWritePermission
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.isEmpty
import uk.whitecrescent.waqti.model.persistence.size
import uk.whitecrescent.waqti.model.task.Task


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkWritePermission()

        Database.clearAllDBs().commit()

        /*add_button.setOnClickListener {
            if (recyclerView.adapter != null) {
                Caches.tasks.put(Task("New Task @ $now"))
                recyclerView.adapter?.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(recyclerView.adapter?.itemCount!! - 1)
            }
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        Caches.close()
    }
}


class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = UP or DOWN or LEFT or RIGHT
        return makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                         target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        recyclerView.snackBar("MOVED!")
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        recyclerView.snackBar("DONE!")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

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

}

fun seedDB() {
    if (Database.tasks.size < 500)
        (0..100).forEach { Task("Auto Generated Task $it") }
    if (Database.taskLists.isEmpty()) TaskList("Task List 1", Caches.tasks.toList())
}

fun clearDB() {
    Caches.clearAllCaches().commit()
    Database.clearAllDBs().commit()
}
