@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_view_task.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.DateTimePickerDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.EditTextDialog
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
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

        taskID = mainActivityVM.taskID
        listID = mainActivityVM.listID
        boardID = mainActivityVM.boardID

        setUpViews(Caches.tasks[taskID])

    }

    @SuppressLint("SetTextI18n")
    override fun setUpViews(element: Task) {

        setUpAppBar(element)

        // TODO: 07-Jun-19 Make background color of the linearLayout be same as Task color
        Caches.boards[boardID].cardColor.toAndroidColor.also {
            viewTaskFragment_constraintLayout.setBackgroundColor(it)
            nestedScrollView.setBackgroundColor(it)
            linearLayout.setBackgroundColor(it)
        }

        setUpTimeViews(element)

        setUpDeadlineViews(element)

        setUpDescriptionViews(element)

    }


    private fun setUpAppBar(task: Task) {
        mainActivity.appBar {
            color = Caches.boards[boardID].cardColor
            elevation = 0F
            leftImageBack()
            leftImage.setTint(WaqtiColor.BLACK.toAndroidColor)
            editTextView {
                textColor = WaqtiColor.BLACK.toAndroidColor
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.taskNameHint)
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != task.name)
                            Caches.tasks[taskID].changeName(text.toString())
                    }
                }
                text = SpannableStringBuilder(task.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideSoftKeyboard()
                        true
                    } else false
                }
            }
            rightImageDefault(R.menu.menu_task) {
                when (it.itemId) {
                    R.id.deleteTask_menuItem -> {
                        ConfirmDialog().apply {
                            title = this@ViewTaskFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                            onConfirm = {
                                val taskName = Caches.tasks[taskID].name
                                this.dismiss()
                                Caches.deleteTask(taskID, listID)
                                mainActivity.appBar.shortSnackBar(getString(R.string.deletedTask)
                                        + " $taskName")
                                finish()
                            }
                        }.show(mainActivity.supportFragmentManager, "ConfirmDialog")


                        true
                    }
                    else -> false
                }
            }
            rightImage.setTint(WaqtiColor.BLACK.toAndroidColor)
        }
    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpTimeViews(task: Task) {
        taskTime_propertyCard.apply {
            task.time.let {
                if (it != DEFAULT_TIME_PROPERTY) {
                    if (it.isConstrained) text = getString(R.string.timeColon) + getString(R.string.constraint) + it.value.rfcFormatted
                    else text = getString(R.string.timeColon) + getString(R.string.property) + it.value.rfcFormatted
                } else isVisible = false
            }

            onClick {
                DateTimePickerDialog().apply {
                    initialTime = task.time.value
                    onConfirm = {
                        task.time.value = it
                        text = getString(R.string.timeColon) + it.rfcFormatted
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpDeadlineViews(task: Task) {
        taskDeadline_propertyCard.apply {
            task.deadline.let {
                if (it != DEFAULT_DEADLINE_PROPERTY) {
                    if (it.isConstrained) text = getString(R.string.deadlineColon) + getString(R.string.constraint) + it.value.rfcFormatted
                    else text = getString(R.string.deadlineColon) + getString(R.string.property) + it.value.rfcFormatted
                } else isVisible = false
            }
            onClick {
                DateTimePickerDialog().apply {
                    initialTime = task.deadline.value
                    onConfirm = {
                        task.deadline.value = it
                        text = getString(R.string.deadlineColon) + it.rfcFormatted
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpDescriptionViews(task: Task) {
        taskDescription_propertyCard.apply {
            task.description.let {
                if (it != DEFAULT_DESCRIPTION_PROPERTY) {
                    text = getString(R.string.descriptionColon) + it.value
                } else isVisible = false
            }

            onClick {
                EditTextDialog().apply {
                    hint = this@ViewTaskFragment.getString(R.string.enterDescription)
                    initialText = task.description.value
                    onConfirm = {
                        task.description.value = it
                        text = it
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
        }
    }

    override fun finish() {
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
