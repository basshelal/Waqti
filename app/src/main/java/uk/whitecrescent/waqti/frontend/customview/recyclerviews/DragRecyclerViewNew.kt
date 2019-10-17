package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import uk.whitecrescent.waqti.frontend.customview.drag.DragView

abstract class DragRecyclerViewNew
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : WaqtiRecyclerView(context, attributeSet, defStyle) {

    interface DragListener {

        /**
         * Called before the drag operation starts
         */
        fun onStartDrag(dragView: DragView)

        /**
         * Called when the drag touch location is updated, meaning when the user moves their
         * finger while dragging.
         *
         * Warning: This gets called **VERY OFTEN**, any code called in here should not be too
         * expensive, else you may experience jank while dragging. It is preferred to use
         * [onEnteredView] instead to receive events when the touch point enters a new view.
         */
        fun onUpdateLocation(dragView: DragView, touchPoint: PointF)

        /**
         * Called when the user's touch point enters a new View.
         *
         * The [newView] is the new view that the [touchPoint] has entered whereas [oldView] is
         * the view that the [touchPoint] has exited. [touchPoint] can never be over more than 1
         * view.
         *
         * The [newView] will always be a descendant of the [dragView]'s parent.
         * If there are multiple views stacked the topmost one under the touch point will be the
         * [newView].
         *
         * In most cases [oldView] will never be null and will instead be the [dragView]'s
         * parent, but this is rarely not the case.
         *
         * @return true if [dragView] can be released on top of [newView] meaning if the user
         * were to release drag the [dragView] will drop on top of [newView], false meaning it
         * cannot. If [dragView] has never entered an acceptable drop view, it will return to its
         * original position upon release
         */
        fun onEnteredView(dragView: DragView, newView: View,
                          oldView: View?, touchPoint: PointF): Boolean

        /**
         * Called when the user's touch point exits the bounds of the [dragView]'s parent view.
         */
        fun onExitedParentBounds(dragView: DragView, touchPoint: PointF)

        /**
         * Called when the user's touch point re-enters the bounds of the [dragView]'s parent
         * view after it had exited
         */
        fun onEnteredParentBounds(dragView: DragView, touchPoint: PointF)

        /**
         * Called when the user's touch is release. The [dragView] will start to animate its return.
         */
        fun onReleaseDrag(dragView: DragView, touchPoint: PointF)

        /**
         * Called when the dragging operation has fully ended and the [dragView] has ended its
         * return animation.
         */
        fun onEndDrag(dragView: DragView)

    }

    abstract class SimpleDragListener : DragListener {
        override fun onStartDrag(dragView: DragView) {}
        override fun onUpdateLocation(dragView: DragView, touchPoint: PointF) {}
        override fun onEnteredView(dragView: DragView, newView: View,
                                   oldView: View?, touchPoint: PointF): Boolean = false

        override fun onExitedParentBounds(dragView: DragView, touchPoint: PointF) {}
        override fun onEnteredParentBounds(dragView: DragView, touchPoint: PointF) {}
        override fun onReleaseDrag(dragView: DragView, touchPoint: PointF) {}
        override fun onEndDrag(dragView: DragView) {}

        companion object {
            @Suppress("UNUSED_ANONYMOUS_PARAMETER")
            inline operator fun invoke(
                    crossinline onStartDrag: (dragView: DragView) -> Unit =
                            { dragView -> },
                    crossinline onUpdateLocation: (dragView: DragView,
                                                   touchPoint: PointF) -> Unit =
                            { dragView, touchPoint -> },
                    crossinline onEnteredView: (DragView, View,
                                                View?, PointF) -> Boolean =
                            { dragView, newView, oldView, touchPoint -> false },

                    crossinline onExitedParentBounds: (dragView: DragView,
                                                       touchPoint: PointF) -> Unit =
                            { dragView, touchPoint -> },
                    crossinline onEnteredParentBounds: (dragView: DragView,
                                                        touchPoint: PointF) -> Unit =
                            { dragView, touchPoint -> },
                    crossinline onReleaseDrag: (dragView: DragView,
                                                touchPoint: PointF) -> Unit =
                            { dragView, touchPoint -> },
                    crossinline onEndDrag: (dragView: DragView) -> Unit = { dragView -> }
            ): DragListener {
                return object : SimpleDragListener() {
                    override fun onStartDrag(dragView: DragView) =
                            onStartDrag(dragView)

                    override fun onUpdateLocation(dragView: DragView, touchPoint: PointF) =
                            onUpdateLocation(dragView, touchPoint)

                    override fun onEnteredView(dragView: DragView, newView: View,
                                               oldView: View?, touchPoint: PointF) =
                            onEnteredView(dragView, newView, oldView, touchPoint)

                    override fun onExitedParentBounds(dragView: DragView, touchPoint: PointF) =
                            onExitedParentBounds(dragView, touchPoint)

                    override fun onEnteredParentBounds(dragView: DragView, touchPoint: PointF) =
                            onEnteredParentBounds(dragView, touchPoint)

                    override fun onReleaseDrag(dragView: DragView, touchPoint: PointF) =
                            onReleaseDrag(dragView, touchPoint)

                    override fun onEndDrag(dragView: DragView) =
                            onEndDrag(dragView)
                }
            }
        }
    }

}