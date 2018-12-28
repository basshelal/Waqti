package uk.whitecrescent.waqti.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.fragments.base.WaqtiFragment
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task

class ViewTaskFragment : WaqtiFragment() {

    companion object {
        fun newInstance() = ViewTaskFragment()
    }

    private var taskID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments != null) {
            taskID = arguments!!["taskID"] as ID
        }

        setUpViews(Caches.tasks[taskID])

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_card, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteTask_menuItem -> {
                taskName_textView.snackBar("Clicked delete!")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViews(task: Task) {
        mainActivity.supportActionBar?.title = task.name
        taskName_textView.text = task.name
    }

}
