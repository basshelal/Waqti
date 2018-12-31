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

        boardID = viewModel.boardID
        listID = viewModel.listID

        setUpViews()
    }

    private fun setUpViews() {
        if (!BuildConfig.DEBUG) dev_addTask_button.visibility = View.GONE

        mainActivity.supportActionBar?.title = "New Task"

        taskName_editText.openKeyboard()

        taskName_editText.addAfterTextChangedListener {
            if (it != null) {
                addTask_button.isEnabled = !(it.isEmpty() || it.isBlank())
            }
        }

        addTask_button.isEnabled = false
        addTask_button.setOnClickListener {
            Caches.boards[boardID][listID].add(taskFromEditText()).update()
            finalize()
        }

        dev_addTask_button.setOnClickListener {
            Caches.boards[boardID][listID]
                    .add(Task("Dev Task")).update()
            finalize()
        }
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
