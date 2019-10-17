package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import uk.whitecrescent.waqti.frontend.customview.drag.DragView

open class DragRecyclerView
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : WaqtiRecyclerView(context, attributeSet, defStyle) {

    override fun onFinishInflate() {
        super.onFinishInflate()

        init()
    }

    protected fun init() {
        /*setChildDrawingOrderCallback { childCount, i ->
            if (draggingPosition == -1) return@setChildDrawingOrderCallback i
            if (i == childCount - 1) return@setChildDrawingOrderCallback draggingPosition
            if (i < draggingPosition) i else i + 1
        }*/
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?) = true
            override fun onLongPress(e: MotionEvent) {
                /*boardListView.findChildViewUnder(e.x, e.y)?.also { view ->
                    draggingViewHolder = (boardListView.getChildViewHolder(view) as BoardListViewHolder)
                    draggingPosition = boardListView.indexOfChild(view)
                    (view as DragView).startDrag()
                }*/
            }
        }
        val gestureDetector = GestureDetectorCompat(context, gestureListener)
        addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                // return false will make it scroll when you swipe down
                // because it tells RV to do its touch event which scrolls
                //  return draggingViewHolder != null
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                // (draggingViewHolder?.itemView as? DragView)?.onTouchEvent(e)
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

    }

    // API DRAFT

    fun startDrag(fromViewHolder: WaqtiViewHolder<View>,
                  dragListener: DragView.DragListener,
                  recyclerView: RecyclerView = this) {

        // the parent of the DragView should be the passed in RecyclerView which may not
        // necessarily be this RecyclerView which will allow us to have Board DragView

        // we need to inflate the DragView with the contents of the viewHolder.itemView
        // one way to do this is to cache the R.layout.id of the itemView in WaqtiViewHolder
        // and then inflate and bind using that on here, it's not impossible but might not be
        // very scalable? All we'd need to change is the ViewHolder become a WaqtiViewHolder and
        // make WaqtiViewHolder contain the R.layout id of the inflated view

        recyclerView.addView(
                DragView(recyclerView.context).also {
                    it.dragListener = dragListener
                    it.startDragFromView(fromViewHolder.itemView)
                }
        )
    }

    /*
    *
    * // Drag stuff
        boardListView.setChildDrawingOrderCallback { childCount, i ->
            if (draggingPosition == -1) return@setChildDrawingOrderCallback i
            if (i == childCount - 1) return@setChildDrawingOrderCallback draggingPosition
            if (i < draggingPosition) i else i + 1
        }
        gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?) = true
            override fun onLongPress(e: MotionEvent) {
                boardListView.findChildViewUnder(e.x, e.y)?.also { view ->
                    draggingViewHolder = (boardListView.getChildViewHolder(view) as BoardListViewHolder)
                    draggingPosition = boardListView.indexOfChild(view)
                    (view as DragView).startDrag()
                }
            }
        }
        gestureDetector = GestureDetectorCompat(boardListView.context, gestureListener)
        boardListView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                // return false will make it scroll when you swipe down
                // because it tells RV to do its touch event which scrolls
                return draggingViewHolder != null
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                (draggingViewHolder?.itemView as? DragView)?.onTouchEvent(e)
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    *
    * */

    /*
    * // Drag Listener
    *
    * dragListener = DragView.SimpleDragListener(
                        onStartDrag = {
                            draggingViewHolder = holder
                            draggingPosition = boardListView.indexOfChild(this)
                            this.alpha = 0.7F
                            boardListView.requestLayout()
                        },
                        onEnteredView = { dragView, newView, oldView, touchPoint ->
                            return@SimpleDragListener if (newView is DragView) {

                                val fromPos = boardListView
                                        .findContainingViewHolder(dragView)?.adapterPosition
                                val toPos = boardListView
                                        .findContainingViewHolder(newView)?.adapterPosition

                                if (fromPos != null && toPos != null) {
                                    boardList.move(fromPos, toPos).update()
                                    notifyItemMoved(fromPos, toPos)
                                    boardListView.requestLayout()
                                }
                                true
                            } else false
                        },
                        onEndDrag = {
                            this.alpha = 1F
                            draggingViewHolder = null
                            draggingPosition = -1
                        }
                )
    *
    * */
}