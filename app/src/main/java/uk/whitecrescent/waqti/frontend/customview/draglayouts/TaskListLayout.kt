package uk.whitecrescent.waqti.frontend.customview.draglayouts

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.task_list.view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.customview.drag.ObservableDragBehavior
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardViewHolder
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskListView
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.setColorScheme
import uk.whitecrescent.waqti.setEdgeEffectColor

class TaskListLayout
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    inline val header: CardView get() = taskListHeader
    inline val headerTextView: TextView get() = taskListHeader_textView
    inline val taskListView: TaskListView get() = taskList_recyclerView
    inline val addButton: FloatingActionButton get() = taskListFooter_fab
    inline val rootView: ConstraintLayout get() = taskList_rootView

    val dragBehavior = TaskListLayoutDragBehavior(this)

    init {
        View.inflate(context, R.layout.task_list, this)
    }

    fun matchBoardViewHolder(viewHolder: BoardViewHolder) {
        // TODO: 28-Oct-19 The margin offset that happens is because of
        //  DragBehavior.startDragFromView it needs to take into account the relationship between
        //  the parents

        this.updateLayoutParams {
            width = WRAP_CONTENT
            height = MATCH_PARENT
        }
        rootView.updateLayoutParams<MarginLayoutParams> {
            val viewHolderLayoutParams = viewHolder.rootView.layoutParams as RecyclerView.LayoutParams
            width = viewHolderLayoutParams.width
            height = viewHolderLayoutParams.height
        }
        headerTextView {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewHolder.headerTextView.textSize)
            text = viewHolder.headerTextView.text
        }
        setHeaderColorScheme(viewHolder.headerColorScheme)
        setListColorScheme(viewHolder.listColorScheme)

        taskListView.swapAdapter(viewHolder.taskListView.adapter, false)

        // TODO: 28-Oct-19 Items won't show because they have alpha = 0F for some reason
        taskListView.allViewHolders.forEach {
            it.itemView.alpha = 1F
        }
    }

    fun setHeaderColorScheme(colorScheme: ColorScheme) {
        taskListView {
            scrollBarColor = colorScheme.text
            setEdgeEffectColor(colorScheme.dark)
        }
        header { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
        headerTextView { textColor = colorScheme.text.toAndroidColor }
        addButton { setColorScheme(colorScheme) }
    }

    fun setListColorScheme(colorScheme: ColorScheme) {
        taskListView { setColorScheme(colorScheme) }
    }

}

class TaskListLayoutDragBehavior(val taskListLayout: TaskListLayout) : ObservableDragBehavior(taskListLayout)