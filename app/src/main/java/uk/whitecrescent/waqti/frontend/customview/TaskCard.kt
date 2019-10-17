package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.task_card.view.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.customview.drag.addDragBehavior
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskViewHolder
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.setIndeterminateColor

class TaskCard
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

    val dragBehavior = this.addDragBehavior()

    init {
        View.inflate(context, R.layout.task_card, this)
    }

    fun matchTaskViewHolder(viewHolder: TaskViewHolder) {
        TODO()
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        progressBar { setIndeterminateColor(colorScheme.text) }
        cardView { setCardBackgroundColor(colorScheme.main.toAndroidColor) }
        textView { textColor = colorScheme.text.toAndroidColor }
    }
}