@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.github.basshelal.threetenktx.threetenabp.rfcFormatted
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.android.synthetic.main.property_card.view.*
import org.jetbrains.anko.backgroundColor
import org.threeten.bp.LocalDateTime
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.extensions.commitTransaction
import uk.whitecrescent.waqti.extensions.getViewModel
import uk.whitecrescent.waqti.extensions.invoke
import uk.whitecrescent.waqti.extensions.isNotDefault
import uk.whitecrescent.waqti.extensions.mainActivity
import uk.whitecrescent.waqti.extensions.requestFocusAndShowKeyboard
import uk.whitecrescent.waqti.extensions.smoothScrollToEnd
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.DateTimePickerDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.EditTextDialog
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragmentViewModel

class CreateTaskFragment : WaqtiCreateFragment<Task>() {

    override lateinit var viewModel: CreateTaskFragmentViewModel
    private var boardID: ID = 0L
    private var listID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel()

        boardID = mainActivityVM.boardID
        listID = mainActivityVM.listID

        setUpViews()
    }

    override fun setUpViews() {

        setUpAppBar()

        setUpButton()

        setUpTimeViews()

        setUpDeadlineViews()

        setUpDescriptionViews()

        hideProperties()

    }

    override fun setUpAppBar() {
        mainActivity.appBar {
            backgroundColor = WaqtiColor.TRANSPARENT.toAndroidColor
            elevation = 0F
            leftImageView.isVisible = false
            editTextView {
                resetTextColor()
                isEditable = true
                hint = getString(R.string.taskNameHint)
                textChangedListener = {
                    if (it != null) {
                        addTask_button.isVisible = !(it.isEmpty() || it.isBlank())
                    }
                }
                text = SpannableStringBuilder("")
                requestFocusAndShowKeyboard()
            }
            rightImageView.isVisible = false
        }
    }

    private inline fun setUpButton() {
        addTask_button.apply {
            isVisible = false
            setOnClickListener {
                Caches.boards[boardID][listID].add(createElement()).update()
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpTimeViews() {
        taskTime_propertyCard.apply {
            onClick {
                DateTimePickerDialog().also {
                    it.initialTime = viewModel.taskTime
                    it.onConfirm = { time ->
                        viewModel.taskTime = time
                        this@apply.title_textView.text = getString(R.string.timeColon) + time.rfcFormatted
                        it.dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
            popupMenuOnItemClicked {
                viewModel.taskTime = DEFAULT_TIME
                this@apply.title_textView.text = getString(R.string.selectTimeProperty)
                true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpDeadlineViews() {
        taskDeadline_propertyCard.apply {
            onClick {
                DateTimePickerDialog().also {
                    it.initialTime = viewModel.taskDeadline
                    it.onConfirm = { time ->
                        viewModel.taskDeadline = time
                        this@apply.title_textView.text = getString(R.string.deadlineColon) + time.rfcFormatted
                        it.dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
            popupMenuOnItemClicked {
                viewModel.taskDeadline = DEFAULT_TIME
                this@apply.title_textView.text = getString(R.string.selectDeadlineProperty)
                true
            }
        }
    }

    private inline fun setUpDescriptionViews() {
        taskDescription_propertyCard.apply {
            onClick {
                EditTextDialog().also {
                    it.hint = this@CreateTaskFragment.getString(R.string.enterDescription)
                    it.initialText = viewModel.taskDescription
                    it.onConfirm = { s ->
                        viewModel.taskDescription = s
                        this@apply.title_textView.text = s
                        it.dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
            popupMenuOnItemClicked {
                viewModel.taskDescription = DEFAULT_DESCRIPTION
                this@apply.title_textView.text = getString(R.string.selectDescriptionProperty)
                true
            }
        }
    }

    override fun createElement(): Task {
        return viewModel.createElement(Task(mainActivity.appBar.editTextView.text.toString()))
    }

    private inline fun hideProperties() {
        taskTime_propertyCard.isVisible = false
        taskDeadline_propertyCard.isVisible = false
        taskDescription_propertyCard.isVisible = false
    }

    override fun finish() {
        mainActivityVM.boardAdapter?.getListAdapter(listID)?.onInflate = {
            // TODO: 29-Jun-19 This doesn't always work on the ViewListFragment list for some reason
            smoothScrollToEnd()
        }
        @FragmentNavigation(from = CREATE_TASK_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }

    private inline fun Task.setTime() {
        viewModel.taskTime.also {
            if (it.isNotDefault) {
                //if (taskTime_propertyCard.constraint_checkBox.isChecked) setTimeConstraintValue (it) else
                setTimePropertyValue(it)
            }
        }
    }

    private inline fun Task.setDeadline() {
        viewModel.taskDeadline.also {
            if (it.isNotDefault) {
                //if (taskDeadline_propertyCard.constraint_checkBox.isChecked) setDeadlineConstraintValue(it) else
                setDeadlinePropertyValue(it)
            }
        }
    }

    private inline fun Task.setDescription() {
        viewModel.taskDescription.also {
            if (it.isNotDefault) setDescriptionValue(it)
        }
    }

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = VIEW_BOARD_FRAGMENT + VIEW_LIST_FRAGMENT,
                        to = CREATE_TASK_FRAGMENT)
                replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                addToBackStack(null)
            }
        }
    }

}

class CreateTaskFragmentViewModel : WaqtiCreateFragmentViewModel<Task>() {

    var taskTime: LocalDateTime = DEFAULT_TIME
    var taskDeadline: LocalDateTime = DEFAULT_TIME
    var taskDescription: String = DEFAULT_DESCRIPTION

    override fun createElement(fromFragment: Task): Task {
        return fromFragment.apply {
            setTime()
            setDeadline()
            setDescription()
        }
    }

    private inline fun Task.setTime() {
        taskTime.also {
            if (it.isNotDefault) {
                //if (taskTime_propertyCard.constraint_checkBox.isChecked) setTimeConstraintValue (it) else
                setTimePropertyValue(it)
            }
        }
    }

    private inline fun Task.setDeadline() {
        taskDeadline.also {
            if (it.isNotDefault) {
                //if (taskDeadline_propertyCard.constraint_checkBox.isChecked) setDeadlineConstraintValue(it) else
                setDeadlinePropertyValue(it)
            }
        }
    }

    private inline fun Task.setDescription() {
        taskDescription.also {
            if (it.isNotDefault) setDescriptionValue(it)
        }
    }

}


