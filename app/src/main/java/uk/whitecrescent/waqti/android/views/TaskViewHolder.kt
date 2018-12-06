package uk.whitecrescent.waqti.android.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.KDragItemAdapter

class TaskViewHolder(view: View)
    : KDragItemAdapter.ViewHolder(view, R.id.button, true) {

    var text: String = ""
        set(value) {
            field = value
            itemView.findViewById<TextView>(R.id.task_cardView).text = value
            itemView.findViewById<ImageView>(R.id.button).setOnLongClickListener {
                Snackbar.make(it, "Long Clicked! ${it.id}", Snackbar.LENGTH_SHORT).show()
                true
            }
        }
        get() = itemView.findViewById<TextView>(R.id.task_cardView).text.toString()

    init {

    }

}