package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_view_list.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.dialogs.MaterialConfirmDialog
import uk.whitecrescent.waqti.android.customview.recyclerviews.TaskListAdapter
import uk.whitecrescent.waqti.android.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
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

        taskList_appBar.apply {
            editTextView.apply {
                fun update() {
                    Caches.taskLists[listID].name = text.toString()
                }
                text = SpannableStringBuilder(element.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { textView, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (textView.text != null &&
                                textView.text.isNotBlank() &&
                                textView.text.isNotEmpty()) {
                            if (textView.text != element.name) {
                                update()
                            }
                        }
                        textView.clearFocusAndHideSoftKeyboard()
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && this@ViewListFragment.addTask_floatingButton.visibility
                            == View.VISIBLE) {
                        this@ViewListFragment.addTask_floatingButton.hide()
                    } else if (dy < 0 && this@ViewListFragment.addTask_floatingButton.visibility != View.VISIBLE) {
                        this@ViewListFragment.addTask_floatingButton.show()
                    }
                }
            })
        }

        addTask_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = boardID
                it.mainActivity.viewModel.listID = listID

                it.clearFocusAndHideSoftKeyboard()

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, CreateTaskFragment.newInstance(), CREATE_TASK_FRAGMENT)
                addToBackStack("")
            }.commit()
        }
    }

    override fun finish() {
        taskList_appBar.clearFocusAndHideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
