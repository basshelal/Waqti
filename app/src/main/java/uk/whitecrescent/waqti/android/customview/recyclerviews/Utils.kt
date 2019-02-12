package uk.whitecrescent.waqti.android.customview.recyclerviews

import android.content.res.Resources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView

open class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(UP or DOWN or LEFT or RIGHT, 0)
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

    override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int,
                                              viewSizeOutOfBounds: Int, totalSize: Int,
                                              msSinceStartScroll: Long): Int {
        return super.interpolateOutOfBoundsScroll(
                recyclerView, viewSize, viewSizeOutOfBounds, totalSize, 1500)
    }

}

fun convertPixelsToDp(px: Float): Float {
    val metrics = Resources.getSystem().displayMetrics
    val dp = px / (metrics.densityDpi / 160f)
    return Math.round(dp).toFloat()
}

fun convertDpToPixel(dp: Float): Float {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return Math.round(px).toFloat()
}