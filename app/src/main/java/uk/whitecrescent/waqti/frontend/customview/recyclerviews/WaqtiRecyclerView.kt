@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import uk.whitecrescent.waqti.extensions.D
import uk.whitecrescent.waqti.extensions.F
import uk.whitecrescent.waqti.extensions.I
import uk.whitecrescent.waqti.extensions.addOnScrollListener
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
        private set

    var scrollBarColor: WaqtiColor = WaqtiColor.WAQTI_DEFAULT.colorScheme.text.inv()

    inline val linearLayoutManager: LinearLayoutManager? get() = layoutManager as? LinearLayoutManager

    inline val horizontalScrollOffset: Int get() = computeHorizontalScrollOffset()
    inline val verticalScrollOffset: Int get() = computeVerticalScrollOffset()

    inline val maxHorizontalScroll: Int get() = computeHorizontalScrollRange() - computeHorizontalScrollExtent()
    inline val maxVerticalScroll: Int get() = computeVerticalScrollRange() - computeVerticalScrollExtent()

    private var flingVelocityX: Int = 0
    private var flingVelocityY: Int = 0

    var overScroller: OverScroller? = null

    var horizontalScrollSpeed: Int = 0
    var verticalScrollSpeed: Int = 0

    private var oldHorizontalScrollOffset: Int = 0
    private var oldVerticalScrollOffset: Int = 0
    private var oldTime: Long = System.currentTimeMillis()

    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)

        setUpOverScroller()
    }

    private inline fun setUpOverScroller() {

        if (layoutManager is LinearLayoutManager) {
            overScroller = if (linearLayoutManager?.orientation == LinearLayoutManager.VERTICAL)
                VerticalOverScroller(this) else HorizontalOverScroller(this)
        }

        isScrollbarFadingEnabled = true
        scrollBarFadeDuration = 500
        overScrollMode = View.OVER_SCROLL_NEVER

        addOnScrollListener(
                onScrolled = { dx, dy ->
                    val dY = verticalScrollOffset.D - oldVerticalScrollOffset.D
                    val dX = horizontalScrollOffset.D - oldHorizontalScrollOffset.D

                    val dSecs = (System.currentTimeMillis() - oldTime).D * 1E-3.D

                    verticalScrollSpeed = (dY / dSecs).I
                    horizontalScrollSpeed = (dX / dSecs).I

                    if (dy != 0 && (verticalScrollOffset == 0 || verticalScrollOffset == maxVerticalScroll)) {
                        overScroller?.overScroll((verticalScrollSpeed.F * overScrollMultiplier.F) / height.F)
                    }

                    if (dx != 0 && (horizontalScrollOffset == 0 || horizontalScrollOffset == maxHorizontalScroll)) {
                        overScroller?.overScroll((horizontalScrollSpeed.F * overScrollMultiplier.F) / width.F)
                    }

                    oldVerticalScrollOffset = verticalScrollOffset
                    oldHorizontalScrollOffset = horizontalScrollOffset

                    oldTime = System.currentTimeMillis()
                }
        )
    }

    fun addVelocityTrackerOnFlingListener() {
        val originalOnFlingListener = onFlingListener
        onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                flingVelocityX = velocityX
                flingVelocityY = velocityY
                return originalOnFlingListener?.onFling(velocityX, velocityY) ?: false
            }
        }
    }

    fun saveState(): SavedState? {
        savedState = this.onSaveInstanceState() as? SavedState?
        return savedState
    }

    fun restoreState(state: SavedState? = savedState) {
        this.onRestoreInstanceState(state)
    }

    inline fun findFirstCompletelyVisibleViewHolder(): ViewHolder? {
        linearLayoutManager?.also {
            return findViewHolderForAdapterPosition(it.findFirstCompletelyVisibleItemPosition())
        }
        return null
    }

    inline fun findFirstVisibleViewHolder(): ViewHolder? {
        linearLayoutManager?.also {
            return findViewHolderForAdapterPosition(it.findFirstVisibleItemPosition())
        }
        return null
    }

    inline fun findLastCompletelyVisibleViewHolder(): ViewHolder? {
        linearLayoutManager?.also {
            return findViewHolderForAdapterPosition(it.findLastCompletelyVisibleItemPosition())
        }
        return null
    }

    inline fun findLastVisibleViewHolder(): ViewHolder? {
        linearLayoutManager?.also {
            return findViewHolderForAdapterPosition(it.findLastVisibleItemPosition())
        }
        return null
    }

    /**
     * Called automatically by the Android framework in [onDrawScrollBars]
     */
    @Suppress("unused")
    protected fun onDrawHorizontalScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        drawScrollBar(canvas, scrollBar, l, t, r, b)
    }

    /**
     * Called automatically by the Android framework in [onDrawScrollBars]
     */
    @Suppress("unused")
    protected fun onDrawVerticalScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        drawScrollBar(canvas, scrollBar, l, t, r, b)
    }

    protected inline fun drawScrollBar(canvas: Canvas, scrollBar: Drawable, l: Int, t: Int, r: Int, b: Int) {
        scrollBar.colorFilter = PorterDuffColorFilter(scrollBarColor.toAndroidColor, PorterDuff.Mode.SRC)
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

private const val overScrollMultiplier = 37.0

interface OverScroller {
    fun overScroll(amount: Float)
}

private class VerticalOverScroller(val recyclerView: WaqtiRecyclerView) :
        VerticalOverScrollBounceEffectDecorator(
                RecyclerViewOverScrollDecorAdapter(recyclerView)), OverScroller {

    override fun overScroll(amount: Float) {
        issueStateTransition(mOverScrollingState)
        translateView(recyclerView, -amount)
        issueStateTransition(mBounceBackState)
    }
}

private class HorizontalOverScroller(val recyclerView: WaqtiRecyclerView) :
        HorizontalOverScrollBounceEffectDecorator(
                RecyclerViewOverScrollDecorAdapter(recyclerView)), OverScroller {

    override fun overScroll(amount: Float) {
        issueStateTransition(mOverScrollingState)
        translateView(recyclerView, -amount)
        issueStateTransition(mBounceBackState)
    }

}