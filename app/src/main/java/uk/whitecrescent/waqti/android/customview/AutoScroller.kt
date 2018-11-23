package uk.whitecrescent.waqti.android.customview

import android.content.Context
import android.os.Handler

class AutoScroller(context: Context, val listener: AutoScrollListener) {

    private val SCROLL_SPEED_DP = 8
    private val AUTO_SCROLL_UPDATE_DELAY = 12L
    private val COLUMN_SCROLL_UPDATE_DELAY = 1000

    private val handler = Handler()
    var isAutoScrolling = false
        private set

    private val scrollSpeed = (context.resources.displayMetrics.density * SCROLL_SPEED_DP).toInt()
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

    fun startAutoScrollPositionBy(dx: Int, dy: Int) {
        if (!isAutoScrolling) {
            isAutoScrolling = true
            autoScrollPositionBy(dx, dy)
        }
    }

    fun autoScrollPositionBy(dx: Int, dy: Int) {
        if (isAutoScrolling) {
            listener.onAutoScrollPositionBy(dx, dy)
            handler.postDelayed({
                autoScrollPositionBy(dx, dy)
            }, AUTO_SCROLL_UPDATE_DELAY)
        }
    }

    fun startAutoScrollColumnBy(columns: Int) {
        if (!isAutoScrolling) {
            isAutoScrolling = true
            autoScrollColumnBy(columns)
        }
    }

    fun autoScrollColumnBy(columns: Int) {
        if (isAutoScrolling) {
            if (System.currentTimeMillis() - lastScrollTime > COLUMN_SCROLL_UPDATE_DELAY) {
                listener.onAutoScrollColumnBy(columns)
                lastScrollTime = System.currentTimeMillis()
            } else listener.onAutoScrollColumnBy(0)

            handler.postDelayed({
                autoScrollColumnBy(columns)
            }, AUTO_SCROLL_UPDATE_DELAY)
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