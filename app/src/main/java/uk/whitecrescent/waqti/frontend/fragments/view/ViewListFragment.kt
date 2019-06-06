package uk.whitecrescent.waqti.frontend.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_view_list.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.FABOnScrollListener
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.Orientation
import uk.whitecrescent.waqti.frontend.addAfterTextChangedListener
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskListAdapter
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.mainActivity

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

        mainActivity.setNavigationBarColor(Caches.boards[boardID].barColor)
        mainActivity.setStatusBarColor(Caches.boards[boardID].barColor)

        taskList_appBar.apply {
            setBackgroundColor(Caches.boards[boardID].barColor)
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
                setOnEditorActionListener { _, actionId, _ ->
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
                        ConfirmDialog().apply {
                            title = this@ViewListFragment.mainActivity.getString(R.string.deleteListQuestion)
                            message = this@ViewListFragment.mainActivity.getString(R.string.deleteListDetails)
                            onConfirm = {
                                dismiss()
                                Caches.deleteTaskList(listID, boardID)
                                finish()
                            }
                        }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                        true
                    }
                    R.id.clearList_menuItem -> {
                        ConfirmDialog().apply {
                            title = this@ViewListFragment.mainActivity.getString(R.string.clearListQuestion)
                            message = this@ViewListFragment.mainActivity.getString(R.string.clearListDetails)
                            onConfirm = {
                                dismiss()
                                Caches.taskLists[listID].clear().update()
                                this@ViewListFragment.taskList_recyclerView.listAdapter.notifyDataSetChanged()
                            }
                        }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                        true
                    }
                    else -> false
                }
            }
        }

        taskList_recyclerView.apply {
            adapter = TaskListAdapter(listID)
            background = Caches.boards[boardID].backgroundColor.toColorDrawable
            addOnScrollListener(FABOnScrollListener(
                    this@ViewListFragment.addTask_floatingButton, Orientation.VERTICAL))
        }

        addTask_floatingButton.setOnClickListener {
            @GoToFragment
            it.mainActivity.supportFragmentManager.commitTransaction {

                it.mainActivity.viewModel.boardID = boardID
                it.mainActivity.viewModel.listID = listID

                it.clearFocusAndHideSoftKeyboard()

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                addToBackStack("")
            }
        }
    }

    override fun finish() {
        taskList_appBar.clearFocusAndHideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
