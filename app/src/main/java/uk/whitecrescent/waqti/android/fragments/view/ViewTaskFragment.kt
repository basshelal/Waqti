package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_view_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteTask_menuItem -> {
                Caches.deleteTask(taskID, listID)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setUpViews(element: Task) {
        mainActivity.supportActionBar?.title = "Task"

        taskName_editTextView.text = SpannableStringBuilder(element.name)
        taskName_editTextView.addAfterTextChangedListener {
            if (it != null) {
                confirmEditTask_button.isEnabled =
                        !(it.isEmpty() || it.isBlank() || it.toString() == element.name)
            }
        }
        taskName_editTextView.setOnEditorActionListener { textView, actionId, event ->
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


        confirmEditTask_button.isEnabled = false
        confirmEditTask_button.setOnClickListener {
            Caches.tasks[viewModel.taskID].changeName(taskName_editTextView.text.toString())
            finish()
        }
    }

    override fun finish() {
        taskName_editTextView.hideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
