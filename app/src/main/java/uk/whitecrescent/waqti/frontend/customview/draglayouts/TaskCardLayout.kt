package uk.whitecrescent.waqti.frontend.customview.draglayouts

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.task_card.view.*
import org.jetbrains.anko.margin
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.find
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.customview.drag.ObservableDragBehavior
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskViewHolder
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.setIndeterminateColor

class TaskCardLayout
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    var taskID: ID = 0L
    var taskListID: ID = 0L
    inline val cardView: CardView get() = task_cardView
    inline val progressBar: ProgressBar get() = taskCard_progressBar
    inline val textView: TextView get() = task_textView

    val dragBehavior = TaskCardLayoutDragBehavior(this)

    init {
        View.inflate(context, R.layout.task_card, this)
    }

    fun matchTaskViewHolder(viewHolder: TaskViewHolder) {
        updateLayoutParams {
            width = viewHolder.cardView.width
            height = viewHolder.cardView.height
        }
        find<ProgressBar>(R.id.taskCard_progressBar) {
            isGone = true
        }
        find<CardView>(R.id.task_cardView) {
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                margin = 0
            }
            setCardBackgroundColor(viewHolder.cardView.cardBackgroundColor)
        }
        find<TextView>(R.id.task_textView) {
            isVisible = true
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewHolder.textView.textSize)
            text = viewHolder.textView.text
        }
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        progressBar { setIndeterminateColor(colorScheme.text) }
        cardView { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
        textView { textColor = colorScheme.text.toAndroidColor }
    }

}

class TaskCardLayoutDragBehavior(val taskCardLayout: TaskCardLayout) : ObservableDragBehavior(taskCardLayout)