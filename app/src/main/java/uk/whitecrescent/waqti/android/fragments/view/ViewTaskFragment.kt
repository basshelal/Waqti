package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_view_task.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialEditTextDialog
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
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
            R.id.renameTask_menuItem -> {
                viewModel.taskID = this.taskID
                MaterialEditTextDialog().show(
                        mainActivity.supportFragmentManager.beginTransaction().apply {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            addToBackStack("")
                        },
                        "EditTextDialog"
                )
                true
            }
            R.id.deleteTask_menuItem -> {
                Caches.taskLists[listID].remove(taskID).update()
                Caches.tasks.remove(taskID)
                mainActivity.supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViews(task: Task) {
        mainActivity.supportActionBar?.title = "Task"
        taskName_textView.text = task.name
    }

    fun updateText() {
        setUpViews(Caches.tasks[taskID])
    }

}
