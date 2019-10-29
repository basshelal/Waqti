package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import uk.whitecrescent.waqti.extensions.addOnScrollListener
import uk.whitecrescent.waqti.extensions.shortSnackBar
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

    inline val linearLayoutManager: LinearLayoutManager? get() = layoutManager as? LinearLayoutManager

    inline val horizontalScrollOffset: Int get() = computeHorizontalScrollOffset()
    inline val verticalScrollOffset: Int get() = computeVerticalScrollOffset()

    inline val maxHorizontalScroll: Int get() = computeHorizontalScrollRange() - computeHorizontalScrollExtent()
    inline val maxVerticalScroll: Int get() = computeVerticalScrollRange() - computeVerticalScrollExtent()

    var flingVelocityX: Int = 0
        protected set

    var flingVelocityY: Int = 0
        protected set

    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)

        if (layoutManager is LinearLayoutManager) {
            OverScrollDecoratorHelper.setUpOverScroll(this,
                    if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL)
                        OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
                    else OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        }

        val originalOnFlingListener = onFlingListener
        onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                flingVelocityX = velocityX
                flingVelocityY = velocityY
                return originalOnFlingListener?.onFling(velocityX, velocityY) ?: false
            }
        }

        addOnScrollListener(
                onScrolled = { dx, dy ->
                    if (dy != 0 && scrollState == SCROLL_STATE_SETTLING &&
                            (verticalScrollOffset == 0 || verticalScrollOffset == maxVerticalScroll)) {
                        shortSnackBar("Y: $flingVelocityY")
                    }

                    if (dx != 0 && scrollState == SCROLL_STATE_SETTLING &&
                            (horizontalScrollOffset == 0 || horizontalScrollOffset == maxHorizontalScroll)) {
                        shortSnackBar("X: $flingVelocityX")
                    }
                },
                onScrollStateChanged = { newState -> }
        )

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