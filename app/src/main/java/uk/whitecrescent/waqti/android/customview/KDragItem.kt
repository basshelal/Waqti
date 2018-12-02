package uk.whitecrescent.waqti.android.customview

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
 * Not 100% sure what this thing does, I believe it's just a wrapper for a draggable View
 *
 */
class KDragItem(context: Context) {

    //region Properties

    private val animationDuration = 250
    /* TODO Question: Why do we have 2, dragView and realDragView */
    var dragView: View = View(context)
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