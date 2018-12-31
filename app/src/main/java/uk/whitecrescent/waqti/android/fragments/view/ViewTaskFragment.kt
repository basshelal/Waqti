package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

class ViewTaskFragment : WaqtiViewFragment() {

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteTask_menuItem -> {
                Caches.deleteTask(taskID, listID)
                finalize()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViews(task: Task) {
        mainActivity.supportActionBar?.title = "Task"

        taskName_editTextView.text = SpannableStringBuilder(task.name)
        taskName_editTextView.addAfterTextChangedListener {
            if (it != null) {
                confirmEditTask_button.isEnabled =
                        !(it.isEmpty() || it.isBlank() || it.toString() == task.name)
            }
        }

        confirmEditTask_button.isEnabled = false
        confirmEditTask_button.setOnClickListener {
            Caches.tasks[viewModel.taskID].changeName(taskName_editTextView.text.toString())
            finalize()
        }
    }

    private fun finalize() {
        taskName_editTextView.hideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
