package uk.whitecrescent.waqti.frontend.fragments.view

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.SpannableStringBuilder
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_view_list.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.addAfterTextChangedListener
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.verticalFABOnScrollListener

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
        taskList_recyclerView.adapter = mainActivityViewModel.boardAdapter
                ?.getOrCreateListAdapter(listID)
        doInBackground {
            mainActivity.resetNavBarStatusBarColor()
            taskList_appBar.apply {
                setBackgroundColor(Caches.boards[boardID].barColor)
                editTextView.apply {
                    mainActivity.hideableEditTextView = this
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
                popupMenuOnItemClicked {
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
                background = Caches.boards[boardID].backgroundColor.toColorDrawable
                addOnScrollListener(this@ViewListFragment.addTask_floatingButton.verticalFABOnScrollListener)
            }

            addTask_floatingButton.setOnClickListener {
                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivityViewModel.boardID = boardID
                    it.mainActivityViewModel.listID = listID

                    it.clearFocusAndHideSoftKeyboard()

                    replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                    addToBackStack(null)
                }
            }

            delete_floatingButton.apply {
                alpha = 0F
                setOnDragListener { _, event ->
                    if (event.localState is DragEventLocalState) {
                        val draggingState = event.localState as DragEventLocalState
                        when (event.action) {
                            DragEvent.ACTION_DRAG_STARTED -> {
                                delete_floatingButton.alpha = 1F
                            }
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                (mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrateCompat(50)
                            }
                            DragEvent.ACTION_DROP -> {
                                ConfirmDialog().apply {
                                    title = this@ViewListFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                                    onConfirm = {
                                        Caches.deleteTask(draggingState.taskID, draggingState.taskListID)
                                        this@ViewListFragment.taskList_recyclerView.apply {
                                            listAdapter.notifyItemRemoved(
                                                    findViewHolderForItemId(draggingState.taskID).adapterPosition
                                            )
                                        }
                                        this.dismiss()
                                    }
                                }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                            }
                            DragEvent.ACTION_DRAG_ENDED -> {
                                delete_floatingButton.alpha = 0F
                            }
                        }
                    }
                    true
                }
            }
        }
    }

    override fun finish() {
        taskList_appBar.clearFocusAndHideSoftKeyboard()
        mainActivity.supportFragmentManager.popBackStack()
    }
}
