package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_list.*
import uk.whitecrescent.waqti.FutureIdea
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class ViewListFragment : WaqtiViewFragment<TaskList>() {

    companion object {
        fun newInstance() = ViewListFragment()
    }

    private var listID: ID = 0L
    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listID = viewModel.listID
        boardID = viewModel.boardID

        setUpViews(Caches.taskLists[listID])
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteList_menuItem -> {
                @FutureIdea
                // TODO: 31-Dec-18 Undo delete would be cool
                // so a snackbar that says deleted List with a button to undo
                Caches.deleteTaskList(listID, boardID)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setUpViews(element: TaskList) {
        mainActivity.supportActionBar?.title = "List"

        listName_editTextView.text = SpannableStringBuilder(element.name)
        listName_editTextView.addAfterTextChangedListener {
            if (it != null) {
                confirmEditList_button.isEnabled =
                        !(it.isEmpty() || it.isBlank() || it.toString() == element.name)
            }
        }

        confirmEditList_button.isEnabled = false
        confirmEditList_button.setOnClickListener {
            Caches.taskLists[listID].name = listName_editTextView.text.toString()
            finish()
        }
    }

    override fun finish() {
        listName_editTextView.hideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
