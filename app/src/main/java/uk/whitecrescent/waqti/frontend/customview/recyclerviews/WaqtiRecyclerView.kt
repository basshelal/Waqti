package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor

/**
 * Contains common [RecyclerView] functionality that is desired across the entire application.
 *
 * Don't directly use a [WaqtiRecyclerView], only extend it.
 */
open class WaqtiRecyclerView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : RecyclerView(context, attributeSet, defStyle) {

    var savedState: SavedState? = null
    var scrollBarColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT.colorScheme.text
    val currentPosition: Int
        get() = (layoutManager as? LinearLayoutManager)
                ?.findFirstCompletelyVisibleItemPosition() ?: 0

    fun saveState(): SavedState? {
        savedState = this.onSaveInstanceState() as? SavedState?
        return savedState
    }

    fun restoreState(state: SavedState? = savedState) {
        this.onRestoreInstanceState(state)
    }

    /**
     * Called automatically by the Android framework in [onDrawScrollBars]
     */
    @Suppress("unused")
    protected fun onDrawHorizontalScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        scrollBar.setColorFilter(scrollBarColor.toAndroidColor, PorterDuff.Mode.SRC_ATOP)
        scrollBar.setBounds(l, t, r, b)
        scrollBar.draw(canvas)
    }

    /**
     * Called automatically by the Android framework in [onDrawScrollBars]
     */
    @Suppress("unused")
    protected fun onDrawVerticalScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        scrollBar.setColorFilter(scrollBarColor.toAndroidColor, PorterDuff.Mode.SRC_ATOP)
        scrollBar.setBounds(l, t, r, b)
        scrollBar.draw(canvas)
    }

}

/**
 * Contains common [RecyclerView.Adapter] functionality that is desired across the entire application.
 *
 * Don't directly use [WaqtiAdapter], only extend it.
 */
abstract class WaqtiAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>()

abstract class WaqtiViewHolder<V : View>(view: V) : RecyclerView.ViewHolder(view) {

    abstract fun bind(adapterPosition: Int): V

}