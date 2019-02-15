package uk.whitecrescent.waqti.frontend.fragments.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_view_task.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.frontend.addAfterTextChangedListener
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.rfcFormatted
import uk.whitecrescent.waqti.shortSnackBar

class ViewTaskFragment : WaqtiViewFragment<Task>() {

    private var taskID: ID = 0L
    private var listID: ID = 0L
    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskID = mainActivityViewModel.taskID
        listID = mainActivityViewModel.listID
        boardID = mainActivityViewModel.boardID

        setUpViews(Caches.tasks[taskID])

    }

    @SuppressLint("SetTextI18n")
    override fun setUpViews(element: Task) {

        task_appBar.apply {
            editTextView.apply {
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != element.name)
                            Caches.tasks[taskID].changeName(text.toString())
                    }
                }
                text = SpannableStringBuilder(element.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideSoftKeyboard()
                        true
                    } else false
                }
            }
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.deleteTask_menuItem -> {
                        ConfirmDialog().apply {
                            title = this@ViewTaskFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                            onConfirm = {
                                this.dismiss()
                                Caches.deleteTask(taskID, listID)
                                finish()
                            }
                        }.show(mainActivity.supportFragmentManager, "ConfirmDialog")

                        Snackbar.make(this, "Deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo", { shortSnackBar("Not yet implemented") })
                                .show()
                        true
                    }
                    else -> false
                }
            }
            (parent as ConstraintLayout).background = Caches.boards[boardID].cardColor.toColorDrawable
        }

        taskTime_textView.apply {
            element.time.let {
                if (it != DEFAULT_TIME_PROPERTY) {
                    if (it.isConstrained) text = getString(R.string.timeColon) + getString(R.string.constraint) + it.value.rfcFormatted
                    else text = getString(R.string.timeColon) + getString(R.string.property) + it.value.rfcFormatted
                } else this.visibility = View.GONE
            }
        }

        taskDeadline_textView.apply {
            element.deadline.let {
                if (it != DEFAULT_DEADLINE_PROPERTY) {
                    if (it.isConstrained) text = getString(R.string.deadlineColon) + getString(R.string.constraint) + it.value.rfcFormatted
                    else text = getString(R.string.deadlineColon) + getString(R.string.property) + it.value.rfcFormatted
                } else this.visibility = View.GONE
            }
        }

        taskDescription_textView.apply {
            element.description.let {
                if (it != DEFAULT_DESCRIPTION_PROPERTY) {
                    text = getString(R.string.descriptionColon) + it.value
                } else this.visibility = View.GONE
            }
        }
    }

    override fun finish() {
        task_appBar.hideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
