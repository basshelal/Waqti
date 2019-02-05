package uk.whitecrescent.waqti.android.fragments.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_view_task.*
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialConfirmDialog
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_DESCRIPTION_PROPERTY
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.rfcFormatted
import uk.whitecrescent.waqti.shortSnackBar

class ViewTaskFragment : WaqtiViewFragment<Task>() {

    companion object {
        fun newInstance() = ViewTaskFragment()
    }

    private var taskID: ID = 0L
    private var listID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskID = viewModel.taskID
        listID = viewModel.listID

        setUpViews(Caches.tasks[taskID])

    }

    @SuppressLint("SetTextI18n")
    override fun setUpViews(element: Task) {

        taskName_editTextView.apply {
            text = SpannableStringBuilder(element.name)
            addAfterTextChangedListener {
                if (it != null) {
                    confirmEditTask_button.isEnabled =
                            !(it.isEmpty() || it.isBlank() || it.toString() == element.name)
                }
            }
            setOnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (textView.text != null &&
                            textView.text.isNotBlank() &&
                            textView.text.isNotEmpty()) {
                        if (textView.text != element.name) {
                            Caches.tasks[taskID].changeName(taskName_editTextView.text.toString())
                        }
                    }
                    textView.clearFocus()
                    textView.hideSoftKeyboard()
                    true
                } else false
            }
        }

        deleteTask_imageButton.apply {
            setOnClickListener {
                MaterialConfirmDialog().apply {
                    title = this@ViewTaskFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                    onConfirm = {
                        this.dismiss()
                        Caches.deleteTask(taskID, listID)
                        finish()
                    }
                }.show(mainActivity.supportFragmentManager, "MaterialConfirmDialog")

                @FutureIdea
                // TODO: 31-Dec-18 Undo delete would be cool
                // so a snackbar that says deleted task with a button to undo
                Snackbar.make(it, "Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", { it.shortSnackBar("Not yet implemented") })
                        .show()
            }
        }

        confirmEditTask_button.apply {
            isEnabled = false
            setOnClickListener {
                Caches.tasks[viewModel.taskID].changeName(taskName_editTextView.text.toString())
                finish()
            }
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
        taskName_editTextView.hideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
