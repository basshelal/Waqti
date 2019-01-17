package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_view_list.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialConfirmDialog
import uk.whitecrescent.waqti.android.customview.recyclerviews.TaskListAdapter
import uk.whitecrescent.waqti.android.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.hideSoftKeyboard
import uk.whitecrescent.waqti.android.mainActivity
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

    override fun setUpViews(element: TaskList) {
        mainActivity.supportActionBar?.title = "List"

        listName_editTextView.text = SpannableStringBuilder(element.name)
        listName_editTextView.addAfterTextChangedListener {
            if (it != null) {
                confirmEditList_button.isEnabled =
                        !(it.isEmpty() || it.isBlank() || it.toString() == element.name)
            }
        }
        listName_editTextView.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (textView.text != null &&
                        textView.text.isNotBlank() &&
                        textView.text.isNotEmpty()) {
                    if (textView.text != element.name) {
                        Caches.taskLists[listID].name = listName_editTextView.text.toString()
                    }
                }
                textView.clearFocus()
                textView.hideSoftKeyboard()
                true
            } else false
        }

        deleteList_imageButton.setOnClickListener {
            MaterialConfirmDialog().apply {
                title = this@ViewListFragment.mainActivity.getString(R.string.deleteListQuestion)
                message = this@ViewListFragment.mainActivity.getString(R.string.deleteListDetails)
                onConfirm = {
                    this.dismiss()
                    Caches.deleteTaskList(listID, boardID)
                    finish()
                }
            }.show(mainActivity.supportFragmentManager, "MaterialConfirmDialog")
        }

        taskList_recyclerView.adapter = TaskListAdapter(listID)

        addTask_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = boardID
                it.mainActivity.viewModel.listID = listID

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, CreateTaskFragment.newInstance(), CREATE_TASK_FRAGMENT)
                addToBackStack("")
            }.commit()
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
