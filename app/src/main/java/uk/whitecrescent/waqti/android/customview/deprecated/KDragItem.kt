package uk.whitecrescent.waqti.android.customview.deprecated

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

/*
 * This is just a wrapper for a Draggable View, see the documentation below but basically we have
 * 2 views, one is the actual view and the other is the view that is being dragged right now
 *
 *
 * Why not have this contain 1 View instead of 2? I think I know why!
 *
 * Because when we are dragging we want an empty placeholder available to show where we currently
 * will drop the item that we are dragging! So I think we MUST have 2 Views
 */
class KDragItem(context: Context) {

    //region Properties

    private val animationDuration = 250

    /**
     * A view that is dragged around that acts like it's the one that is actually there but it's
     * not, what's happening is we have this guy pretending to be the realDragView, this guy
     * becomes irrelevant when the drag is ended but is essential for that
     */
    var dragView: View = View(context)
    /**
     * The actual view that wants to be dragged, it doesn't actually get dragged though, instead
     * we just use dragView and make that guy look like it's this, this guy doesn't actually get
     * dragged
     */
    var realDragView: View? = null
    var offsetX: Float = 0F
    var offsetY: Float = 0F

    var posX: Float = 0F
        set(value) {
            field = value
            updatePosition()
        }
    var posY: Float = 0F
        set(value) {
            field = value
            updatePosition()
        }

    var posTouchDx: Float = 0F
    var posTouchDy: Float = 0F

    var animationDx: Float = 0F
        set(value) {
            field = value
            updatePosition()
        }
    var animationDy: Float = 0F
        set(value) {
            field = value
            updatePosition()
        }

    var position = Pair(0F, 0F)
        set(value) {
            field = value
            posX = value.first + posTouchDx
            posY = value.second + posTouchDy
            updatePosition()
        }

    var offset = Pair(0F, 0F)
        set(value) {
            field = value
            val (x, y) = value
            offsetX = x
            offsetY = y
            updatePosition()
        }

    var canDragHorizontally = true
    var isSnapToTouch = true

    val isDragging: Boolean
        get() = dragView.visibility == View.VISIBLE

    //endregion Properties

    init {
        hide()
    }

    fun startDrag(startFromView: View, touchedX: Float, touchedY: Float) {
        show()
        realDragView = startFromView
        onBindDragView(startFromView, dragView)
        onMeasureDragView(startFromView, dragView)

        val startX =
                startFromView.x - (dragView.measuredWidth - startFromView.measuredWidth) / 2 +
                        dragView.measuredWidth / 2
        val startY =
                startFromView.y - (dragView.measuredHeight - startFromView.measuredHeight) / 2 +
                        dragView.measuredHeight / 2

        if (this.isSnapToTouch) {
            posTouchDx = 0F
            posTouchDy = 0F
            position = touchedX to touchedY
            animationDx = startX - touchedX
            animationDy = startY - touchedY

            val xPropertyHolder = PropertyValuesHolder.ofFloat("AnimationDx", animationDx, 0F)
            val yPropertyHolder = PropertyValuesHolder.ofFloat("AnimationDy", animationDy, 0F)

            val animator = ObjectAnimator.ofPropertyValuesHolder(this, xPropertyHolder, yPropertyHolder)
            animator.interpolator = DecelerateInterpolator()
            animator.duration = animationDuration.toLong()
            animator.start()
        } else {
            posTouchDx = startX - touchedX
            posTouchDy = startY - touchedY

            position = touchedX to touchedY
        }
    }

    fun endDrag(endToView: View, listenerAdapter: AnimatorListenerAdapter) {

        val endX =
                endToView.x - (dragView.measuredWidth - endToView.measuredWidth) / 2 + dragView.measuredWidth / 2
        val endY =
                endToView.y - (dragView.measuredHeight - endToView.measuredHeight) / 2 + dragView.measuredHeight / 2

        val xPropertyHolder = PropertyValuesHolder.ofFloat("posX", posX, endX)
        val yPropertyHolder = PropertyValuesHolder.ofFloat("posY", posY, endY)
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, xPropertyHolder, yPropertyHolder)

        animator.interpolator = DecelerateInterpolator()
        animator.duration = animationDuration.toLong()
        animator.addListener(listenerAdapter)
        animator.start()
    }

    private fun onBindDragView(clickedView: View, dragView: View) {
        val bitmap = Bitmap.createBitmap(clickedView.width, clickedView.height, Bitmap.Config.ARGB_8888)
        clickedView.draw(Canvas(bitmap))
        dragView.background = BitmapDrawable(clickedView.resources, bitmap)
    }

    private fun onMeasureDragView(clickedView: View, dragView: View) {
        dragView.layoutParams = FrameLayout.LayoutParams(clickedView.measuredWidth, clickedView.measuredHeight)
        dragView.measure(
                View.MeasureSpec.makeMeasureSpec(clickedView.measuredWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(clickedView.measuredHeight, View.MeasureSpec.EXACTLY)
        )
    }

    fun hide() {
        dragView.visibility = View.GONE
        realDragView = null
    }

    private fun show() {
        dragView.visibility = View.VISIBLE
    }

    private fun updatePosition() {
        if (canDragHorizontally)
            dragView.x = posX + offsetX + animationDx - (dragView.measuredWidth / 2)

        dragView.y = posY + offsetY + animationDy - (dragView.measuredHeight / 2)
        dragView.invalidate()
    }
}