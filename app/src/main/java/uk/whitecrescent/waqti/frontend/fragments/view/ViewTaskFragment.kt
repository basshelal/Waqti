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
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.DateTimePickerDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.EditTextDialog
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.rfcFormatted
import uk.whitecrescent.waqti.shortSnackBar

class ViewTaskFragment : WaqtiViewFragment() {

    private var taskID: ID = 0L
    private var listID: ID = 0L
    private var boardID: ID = 0L

    private lateinit var task: Task
    private lateinit var taskList: TaskList
    private lateinit var board: Board

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskID = mainActivityVM.taskID
        listID = mainActivityVM.listID
        boardID = mainActivityVM.boardID

        task = Caches.tasks[taskID]
        taskList = Caches.taskLists[listID]
        board = Caches.boards[boardID]

        setUpViews()

    }

    override fun setUpViews() {

        setUpAppBar()

        // TODO: 07-Jun-19 Make background color of the linearLayout be same as Task color
        board.cardColor.toAndroidColor.also {
            viewTaskFragment_constraintLayout.setBackgroundColor(it)
            nestedScrollView.setBackgroundColor(it)
            linearLayout.setBackgroundColor(it)
        }

        setUpTimeViews(task)

        setUpDeadlineViews(task)

        setUpDescriptionViews(task)

    }


    override fun setUpAppBar() {
        mainActivity.appBar {
            backgroundColor = board.cardColor.toAndroidColor
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
                            task.changeName(text.toString())
                    }
                }
                text = SpannableStringBuilder(task.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideKeyboard()
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
                                val taskName = task.name
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
        taskTime_propertyCard {
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
        taskDeadline_propertyCard {
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
        taskDescription_propertyCard {
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
        @FragmentNavigation(from = VIEW_TASK_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }
}
