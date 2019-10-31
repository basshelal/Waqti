@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.OverScrollBounceEffectDecoratorBase
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.extensions.D
import uk.whitecrescent.waqti.extensions.F
import uk.whitecrescent.waqti.extensions.I
import uk.whitecrescent.waqti.extensions.addOnScrollListener
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.now

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

    inline val linearLayoutManager: LinearLayoutManager? get() = layoutManager as? LinearLayoutManager

    inline val horizontalScrollOffset: Int get() = computeHorizontalScrollOffset()
    inline val verticalScrollOffset: Int get() = computeVerticalScrollOffset()

    inline val maxHorizontalScroll: Int get() = computeHorizontalScrollRange() - computeHorizontalScrollExtent()
    inline val maxVerticalScroll: Int get() = computeVerticalScrollRange() - computeVerticalScrollExtent()

    var flingVelocityX: Int = 0
        protected set

    var flingVelocityY: Int = 0
        protected set

    var overScroller: OverScrollBounceEffectDecoratorBase? = null

    var horizontalScrollSpeed: Int = 0
    var verticalScrollSpeed: Int = 0

    private var oldHorizontalScrollOffset: Int = 0
    private var oldVerticalScrollOffset: Int = 0
    private var oldTime: Time = Time.MIN

    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)

        setUpOverScroller()
    }

    private inline fun setUpOverScroller() {

        if (layoutManager is LinearLayoutManager) {
            overScroller = if (linearLayoutManager?.orientation == LinearLayoutManager.VERTICAL)
                VerticalOverScroller(this) else HorizontalOverScroller(this)
        }

        addVelocityTrackerOnFlingListener()

        addOnScrollListener(
                onScrolled = { dx, dy ->
                    val dY = verticalScrollOffset.D - oldVerticalScrollOffset.D
                    val dX = horizontalScrollOffset.D - oldHorizontalScrollOffset.D
                    val dSecs = (now.nano - oldTime.nano).D / 1E9.D

                    verticalScrollSpeed = (dY / dSecs).I
                    horizontalScrollSpeed = (dX / dSecs).I

                    if (dy != 0 && scrollState == SCROLL_STATE_SETTLING &&
                            (verticalScrollOffset == 0 || verticalScrollOffset == maxVerticalScroll)) {
                        (overScroller as? VerticalOverScroller)?.overScroll()
                    }

                    if (dx != 0 && scrollState == SCROLL_STATE_SETTLING &&
                            (horizontalScrollOffset == 0 || horizontalScrollOffset == maxHorizontalScroll)) {
                        (overScroller as? HorizontalOverScroller)?.overScroll()
                    }

                    oldVerticalScrollOffset = verticalScrollOffset
                    oldHorizontalScrollOffset = horizontalScrollOffset
                    oldTime = now
                },
                onScrollStateChanged = { newState -> }
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

private const val overScrollThreshold = 20.0

private class VerticalOverScroller(val recyclerView: WaqtiRecyclerView) :
        VerticalOverScrollBounceEffectDecorator(
                RecyclerViewOverScrollDecorAdapter(recyclerView)) {

    inline fun overScroll() {
        val threshold = (recyclerView.height.D / overScrollThreshold)

        val amount = -(recyclerView.verticalScrollSpeed.F / threshold.F)

        issueStateTransition(mOverScrollingState)

        translateView(recyclerView, amount)

        issueStateTransition(mBounceBackState)

    }
}

private class HorizontalOverScroller(val recyclerView: WaqtiRecyclerView) :
        HorizontalOverScrollBounceEffectDecorator(
                RecyclerViewOverScrollDecorAdapter(recyclerView)) {

    inline fun overScroll() {

        val threshold = (recyclerView.width.D / overScrollThreshold)

        val amount = -(recyclerView.horizontalScrollSpeed.F / threshold.F)

        issueStateTransition(mOverScrollingState)

        translateView(recyclerView, amount)

        issueStateTransition(mBounceBackState)

    }

}