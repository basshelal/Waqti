package uk.whitecrescent.waqti.frontend.customview.draglayouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.customview.drag.DragBehavior
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardListViewHolder

class BoardCardLayout
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    val dragBehavior = BoardCardLayoutDragBehavior(this)

    init {
        View.inflate(context, R.layout.board_card, this)
    }

    fun matchBoardListViewHolder(viewHolder: BoardListViewHolder) {
        TODO()
    }

}

class BoardCardLayoutDragBehavior(val boardCardLayout: BoardCardLayout) : DragBehavior(boardCardLayout)