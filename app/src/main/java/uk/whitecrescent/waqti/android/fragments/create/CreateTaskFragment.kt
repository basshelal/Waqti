package uk.whitecrescent.waqti.android.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_task.*
import uk.whitecrescent.waqti.BuildConfig
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialDateTimePickerDialog
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.openKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

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

    override fun setUpViews() {
        if (!BuildConfig.DEBUG) dev_addTask_button.visibility = View.GONE

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

        dev_addTask_button.apply {
            setOnClickListener {
                Caches.boards[boardID][listID]
                        .add(Task("Dev Task")).update()
                finish()
            }
        }

        taskTime_button.apply {
            setOnClickListener {
                MaterialDateTimePickerDialog().apply {
                    onConfirm = {
                        viewModel.createdTaskTime = it
                        this.dismiss()
                    }
                }.show(mainActivity.supportFragmentManager, "")
            }
        }

    }

    override fun createElement(): Task {
        return Task(taskName_editText.text.toString()).apply {
            if (viewModel.createdTaskTime != DEFAULT_TIME) setTimePropertyValue(viewModel.createdTaskTime)
        }
    }

    override fun finish() {
        taskName_editText.hideSoftKeyboard()
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }

}
