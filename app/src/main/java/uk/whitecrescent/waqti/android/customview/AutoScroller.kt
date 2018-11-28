package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.os.Handler

/*
 * This is just a Self Scrolling Entity, no views here just fancy Math that does the self
 * scrolling based on parameters and different types and directions of scrolling
 */
class AutoScroller(context: Context, private val listener: AutoScrollListener) {

    private val scrollSpeedDP = 8
    private val autoScrollUpdateDelay = 12L
    private val columnScrollUpdateDelay = 1000L

    private val handler = Handler()
    var isAutoScrolling = false
        private set

    private val scrollSpeed = (context.resources.displayMetrics.density * scrollSpeedDP).toInt()
    private var lastScrollTime = 0L
    var autoScrollMode = AutoScrollMode.POSITION

    fun startAutoScroll(direction: ScrollDirection) {
        when (direction) {
            ScrollDirection.UP -> startAutoScrollPositionBy(0, scrollSpeed)
            ScrollDirection.DOWN -> startAutoScrollPositionBy(0, -scrollSpeed)
            ScrollDirection.LEFT -> {
                if (autoScrollMode == AutoScrollMode.POSITION) startAutoScrollPositionBy(scrollSpeed, 0)
                else startAutoScrollColumnBy(1)
            }
            ScrollDirection.RIGHT -> {
                if (autoScrollMode == AutoScrollMode.POSITION) startAutoScrollPositionBy(-scrollSpeed, 0)
                else startAutoScrollColumnBy(-1)
            }
        }
    }

    fun stopAutoScroll() {
        isAutoScrolling = false
    }

    private fun startAutoScrollPositionBy(dx: Int, dy: Int) {
        if (!isAutoScrolling) {
            isAutoScrolling = true
            autoScrollPositionBy(dx, dy)
        }
    }

    private fun autoScrollPositionBy(dx: Int, dy: Int) {
        if (isAutoScrolling) {
            listener.onAutoScrollPositionBy(dx, dy)
            handler.postDelayed({
                autoScrollPositionBy(dx, dy)
            }, autoScrollUpdateDelay)
        }
    }

    private fun startAutoScrollColumnBy(columns: Int) {
        if (!isAutoScrolling) {
            isAutoScrolling = true
            autoScrollColumnBy(columns)
        }
    }

    private fun autoScrollColumnBy(columns: Int) {
        if (isAutoScrolling) {
            if (System.currentTimeMillis() - lastScrollTime > columnScrollUpdateDelay) {
                listener.onAutoScrollColumnBy(columns)
                lastScrollTime = System.currentTimeMillis()
            } else listener.onAutoScrollColumnBy(0)

            handler.postDelayed({
                autoScrollColumnBy(columns)
            }, autoScrollUpdateDelay)
        }
    }

    enum class AutoScrollMode {
        POSITION, COLUMN
    }

    enum class ScrollDirection {
        UP, DOWN, LEFT, RIGHT
    }

    interface AutoScrollListener {
        fun onAutoScrollPositionBy(dx: Int, dy: Int)

        fun onAutoScrollColumnBy(columns: Int)
    }
}