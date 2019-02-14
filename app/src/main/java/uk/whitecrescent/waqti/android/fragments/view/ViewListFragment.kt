package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_view_list.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.FABOnScrollListener
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.Orientation
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialConfirmDialog
import uk.whitecrescent.waqti.android.customview.recyclerviews.TaskListAdapter
import uk.whitecrescent.waqti.android.customview.toColor
import uk.whitecrescent.waqti.android.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class ViewListFragment : WaqtiViewFragment<TaskList>() {

    private var listID: ID = 0L
    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listID = mainActivityViewModel.listID
        boardID = mainActivityViewModel.boardID

        setUpViews(Caches.taskLists[listID])
    }

    override fun setUpViews(element: TaskList) {

        taskList_appBar.apply {
            editTextView.apply {
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != element.name)
                            Caches.taskLists[listID].name = it.toString()
                    }
                }
                text = SpannableStringBuilder(element.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideSoftKeyboard()
                        true
                    } else false
                }
            }
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.deleteList_menuItem -> {
                        MaterialConfirmDialog().apply {
                            title = this@ViewListFragment.mainActivity.getString(R.string.deleteListQuestion)
                            message = this@ViewListFragment.mainActivity.getString(R.string.deleteListDetails)
                            onConfirm = {
                                this.dismiss()
                                Caches.deleteTaskList(listID, boardID)
                                finish()
                            }
                        }.show(mainActivity.supportFragmentManager, "MaterialConfirmDialog")
                        true
                    }
                    else -> false
                }
            }
        }

        taskList_recyclerView.apply {
            adapter = TaskListAdapter(listID)
            background = Caches.boards[boardID].backgroundValue.toColor.toColorDrawable
            addOnScrollListener(FABOnScrollListener(
                    this@ViewListFragment.addTask_floatingButton, Orientation.VERTICAL))
        }

        addTask_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = boardID
                it.mainActivity.viewModel.listID = listID

                it.clearFocusAndHideSoftKeyboard()

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                addToBackStack("")
            }.commit()
        }
    }

    override fun finish() {
        taskList_appBar.clearFocusAndHideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
