package uk.whitecrescent.waqti.android.fragments.create

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.openKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

class CreateTaskFragment : WaqtiCreateFragment() {

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

        mainActivity.supportActionBar?.title = "New Task"

        boardID = viewModel.boardID
        listID = viewModel.listID

        focusTaskNameTextView()

        setUpSendButtonOnClick()
    }

    private fun focusTaskNameTextView() {
        taskName_editText.openKeyboard()
    }

    private fun setUpSendButtonOnClick() {
        addTask_button.setOnClickListener {
            val text = taskName_editText.text
            if (text == null || text.isEmpty() || text.isBlank()) {
                textIsIncorrect()
            } else {
                textIsCorrect()
            }
        }

        dev_addTask_button.setOnClickListener {
            Caches.boards[boardID][listID]
                    .add(Task("Dev Task")).update()
            finalize()
        }
    }

    private fun textIsIncorrect() {
        taskName_editText.setHintTextColor(Color.RED)
        taskName_editText.hint = "Task Name cannot be empty!"
    }

    private fun textIsCorrect() {
        Caches.boards[boardID][listID]
                .add(taskFromEditText()).update()
        finalize()
    }

    private fun taskFromEditText(): Task {
        return Task(taskName_editText.text.toString())
    }

    private fun finalize() {
        taskName_editText.hideSoftKeyboard()
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }

}
