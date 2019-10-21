package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.task_list.view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.customview.drag.DragBehavior
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

    val dragBehavior = this.addTaskListLayoutDragBehavior()

    init {
        View.inflate(context, R.layout.task_list, this)
    }

    fun matchBoardViewHolder(viewHolder: BoardViewHolder) {
        TODO()
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

    fun addTaskListLayoutDragBehavior() = TaskListLayoutDragBehavior(this)
}

class TaskListLayoutDragBehavior(val taskListLayout: TaskListLayout) : DragBehavior(taskListLayout)