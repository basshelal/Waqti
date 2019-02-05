package uk.whitecrescent.waqti.android.fragments.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_task.*
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialDateTimePickerDialog
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialEditTextDialog
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.openKeyboard
import uk.whitecrescent.waqti.rfcFormatted

@Suppress("NOTHING_TO_INLINE")
class CreateTaskFragment : WaqtiCreateFragment<Task>() {

    companion object {
        fun newInstance() = CreateTaskFragment()
    }

    private var boardID: ID = 0L
    private var listID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = viewModel.boardID
        listID = viewModel.listID

        setUpViews()
    }

    @FutureIdea
    // TODO: 16-Jan-19 Draggable Property Cards
    override fun setUpViews() {

        taskName_editText.apply {
            openKeyboard()
            addAfterTextChangedListener {
                if (it != null) {
                    addTask_button.isEnabled = !(it.isEmpty() || it.isBlank())
                }
            }
        }

        addTask_button.apply {
            isEnabled = false
            setOnClickListener {
                Caches.boards[boardID][listID].add(createElement()).update()
                finish()
            }
        }

        setUpTimeViews()

        setUpDeadlineViews()

        setUpDescriptionViews()

    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpTimeViews() {

        taskTime_cardView.apply {
            setOnClickListener {
                MaterialDateTimePickerDialog().apply {
                    initialTime = this@CreateTaskFragment.viewModel.createdTaskTime
                    onConfirm = {
                        viewModel.createdTaskTime = it
                        this@CreateTaskFragment.selectTime_textView.text = getString(R.string.timeColon) + it.rfcFormatted
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
        }

        taskTimeClear_imageButton.apply {
            setOnClickListener {
                viewModel.createdTaskTime = DEFAULT_TIME
                selectTime_textView.text = getString(R.string.selectTimeProperty)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private inline fun setUpDeadlineViews() {
        taskDeadline_cardView.apply {
            setOnClickListener {
                MaterialDateTimePickerDialog().apply {
                    initialTime = this@CreateTaskFragment.viewModel.createdTaskDeadline
                    onConfirm = {
                        viewModel.createdTaskDeadline = it
                        this@CreateTaskFragment.selectDeadline_textView.text = getString(R.string.deadlineColon) + it.rfcFormatted
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
        }

        taskDeadlineClear_imageButton.apply {
            setOnClickListener {
                viewModel.createdTaskDeadline = DEFAULT_TIME
                selectDeadline_textView.text = getString(R.string.selectDeadlineProperty)
            }
        }
    }

    private inline fun setUpDescriptionViews() {
        taskDescription_cardView.apply {
            setOnClickListener {
                MaterialEditTextDialog().apply {
                    title = this@CreateTaskFragment.getString(R.string.enterDescription)
                    hint = this@CreateTaskFragment.getString(R.string.enterDescription)
                    initialText = this@CreateTaskFragment.viewModel.createdTaskDescription
                    onConfirm = {
                        viewModel.createdTaskDescription = it
                        this@CreateTaskFragment.selectDescription_textView.text = it
                        dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
        }

        taskDescriptionClear_imageButton.apply {
            setOnClickListener {
                viewModel.createdTaskDescription = DEFAULT_DESCRIPTION
                selectDescription_textView.text = getString(R.string.selectDescriptionProperty)
            }
        }
    }

    override fun createElement(): Task {
        return Task(taskName_editText.text.toString()).apply {
            setTime()
            setDeadline()
            setDescription()
        }
    }

    private inline fun Task.setTime() {
        if (viewModel.createdTaskTime != DEFAULT_TIME) {
            if (taskTimeConstraint_checkBox.isChecked) setTimeConstraintValue(viewModel.createdTaskTime)
            else setTimePropertyValue(viewModel.createdTaskTime)
        }
    }

    private inline fun Task.setDeadline() {
        if (viewModel.createdTaskDeadline != DEFAULT_TIME) {
            if (taskDeadlineConstraint_checkBox.isChecked) setDeadlineConstraintValue(viewModel.createdTaskDeadline)
            else setDeadlinePropertyValue(viewModel.createdTaskDeadline)
        }
    }

    private inline fun Task.setDescription() {
        if (viewModel.createdTaskDescription != DEFAULT_DESCRIPTION) {
            setDescriptionValue(viewModel.createdTaskDescription)
        }
    }

    override fun finish() {
        taskName_editText.hideSoftKeyboard()
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }

}
