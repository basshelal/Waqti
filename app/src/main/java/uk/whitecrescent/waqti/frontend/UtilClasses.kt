@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import uk.whitecrescent.waqti.extensions.doInBackgroundDelayed

open class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(editable: Editable?) {}

    override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {}
}

open class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
    }

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        /*We do not support swiping*/
    }

    override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int,
                                              viewSizeOutOfBounds: Int, totalSize: Int,
                                              msSinceStartScroll: Long): Int {
        return super.interpolateOutOfBoundsScroll(
                recyclerView, viewSize, viewSizeOutOfBounds, totalSize, 1500)
    }

}

open class FABOnScrollListener(val fab: FloatingActionButton,
                               val orientation: Orientation
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        when (orientation) {
            Orientation.HORIZONTAL -> {
                if (dx > 0 && fab.isVisible) fab.hide()
                else if (dx < 0 && !fab.isVisible) fab.show()
            }
            Orientation.VERTICAL -> {
                if (dy > 0 && fab.isVisible) fab.hide()
                else if (dy < 0 && !fab.isVisible) fab.show()
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && fab.isVisible) {
            doInBackgroundDelayed(2000) {
                if (fab.isVisible) fab.hide()
            }
        }
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}

open class SimpleOnSeekChangeListener : OnSeekChangeListener {
    override fun onSeeking(seekParams: SeekParams?) {}
    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
}

data class ChangedPositionPair(var positionChanged: Boolean = false,
                               var position: Int = 0) {

    inline fun changeTo(pair: Pair<Boolean, Int>) = changeTo(pair.first, pair.second)

    inline fun changeTo(positionChanged: Boolean, position: Int) {
        this.positionChanged = positionChanged
        this.position = position
    }
}

/**
 * Utility annotation to show us where any manual navigation between Fragments occurs including
 * back stack popping, to find them just use "Find Usages" in the IDE.
 *
 * The constructor parameters must be one of the strings in [uk.whitecrescent.waqti.frontend.Tags.kt],
 * such as [uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT]
 *
 * You should prefer to use named parameters when using this annotation for better readability
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
annotation class FragmentNavigation(val from: String, val to: String)

data class Quadruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
) {

    override fun toString(): String = "($first, $second, $third, $fourth)"
}

inline fun <T> Quadruple<T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth)

data class Quintuple<out A, out B, out C, out D, out E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
) {

    override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}

inline fun <T> Quintuple<T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth)

data class Sextuple<out A, out B, out C, out D, out E, out F>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F
) {

    override fun toString(): String = "($first, $second, $third, $fourth, $fifth, $sixth)"
}

inline fun <T> Sextuple<T, T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth, sixth)

data class Septuple<out A, out B, out C, out D, out E, out F, out G>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F,
        val seventh: G
) {

    override fun toString(): String = "($first, $second, $third, $fourth, $fifth, $sixth)"
}

inline fun <T> Septuple<T, T, T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth, sixth, seventh)