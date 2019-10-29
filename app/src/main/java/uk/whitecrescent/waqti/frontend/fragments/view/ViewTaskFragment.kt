@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.board_options.view.*
import kotlinx.android.synthetic.main.fragment_view_task.*
import kotlinx.android.synthetic.main.task_options.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.DEFAULT_DEADLINE_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION_PROPERTY
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME_PROPERTY
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.extensions.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.extensions.commitTransaction
import uk.whitecrescent.waqti.extensions.getViewModel
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.mainActivity
import uk.whitecrescent.waqti.extensions.shortSnackBar
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.ColorScheme
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.DateTimePickerDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.EditTextDialog
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel
import uk.whitecrescent.waqti.rfcFormatted

class ViewTaskFragment : WaqtiViewFragment() {

    private var taskID: ID = 0L
    private var listID: ID = 0L
    private var boardID: ID = 0L
    private lateinit var viewModel: ViewTaskFragmentViewModel
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

        viewModel = getViewModel()

        task = Caches.tasks[taskID]
        taskList = Caches.taskLists[listID]
        board = Caches.boards[boardID]

        setUpViews()

    }

    override fun setUpViews() {

        setUpAppBar()

        setUpTimeViews(task)

        setUpDeadlineViews(task)

        setUpDescriptionViews(task)

    }


    override fun setUpAppBar() {
        this.setColorScheme(board.cardColor.colorScheme)
        mainActivity.appBar {
            elevation = 0F
            leftImageBack()
            editTextView {
                isEditable = true
                hint = getString(R.string.taskNameHint)
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != task.name)
                            task.name = it.toString()
                    }
                }
                textChangedListener = { update() }
                text = SpannableStringBuilder(task.name)
                setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }
            rightImageView.isVisible = true
            rightImage = R.drawable.overflow_icon
        }
        mainActivity.setColorScheme(board.cardColor.colorScheme)
    }

    override fun onStart() {
        super.onStart()

        createOptionsMenu()
    }

    override fun onStop() {
        super.onStop()

        destroyOptionsMenu()
    }

    override fun finish() {
        @FragmentNavigation(from = VIEW_TASK_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }

    private inline fun setColorScheme(colorScheme: ColorScheme) {
        mainActivity.setColorScheme(colorScheme)
        mainActivity.drawerLayout.boardOptions_navigationView {
            setBackgroundColor(colorScheme.main.toAndroidColor)
        }
        viewTaskFragment_constraintLayout.setBackgroundColor(colorScheme.main.toAndroidColor)
        nestedScrollView.setBackgroundColor(colorScheme.main.toAndroidColor)
        linearLayout.setBackgroundColor(colorScheme.main.toAndroidColor)
    }

    private inline fun createOptionsMenu() {

        LayoutInflater.from(context).inflate(R.layout.task_options,
                mainActivity.drawerLayout, true)

        mainActivity.appBar {
            rightImageView.setOnClickListener {
                mainActivity.drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        mainActivity.drawerLayout.taskOptions_navigationView {
            setBackgroundColor(board.cardColor.toAndroidColor)
            deleteTask_taskOption {
                setOnClickListener {
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
                    mainActivity.drawerLayout.closeDrawer(this@taskOptions_navigationView)
                }
            }
        }
    }

    private inline fun destroyOptionsMenu() {
        mainActivity.drawerLayout {
            taskOptions_navigationView?.also {
                closeDrawer(it)
                removeView(it)
            }
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

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = VIEW_BOARD_FRAGMENT + VIEW_LIST_FRAGMENT,
                        to = VIEW_TASK_FRAGMENT)
                replace(R.id.fragmentContainer, ViewTaskFragment(), VIEW_TASK_FRAGMENT)
                addToBackStack(null)
            }
        }
    }

}

class ViewTaskFragmentViewModel : WaqtiViewFragmentViewModel()