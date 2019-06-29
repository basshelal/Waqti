package uk.whitecrescent.waqti.frontend.fragments.view

import android.annotation.SuppressLint
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
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.fadeIn
import uk.whitecrescent.waqti.fadeOut
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.shortSnackBar
import uk.whitecrescent.waqti.verticalFABOnScrollListener

class ViewListFragment : WaqtiViewFragment<TaskList>() {

    private var listID: ID = 0L
    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_list, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listID = mainActivityVM.listID
        boardID = mainActivityVM.boardID

        setUpViews(Caches.taskLists[listID])

    }

    override fun setUpViews(element: TaskList) {
        setUpAppBar(element)

        taskList_recyclerView.adapter = mainActivityVM.boardAdapter?.getListAdapter(listID)

        doInBackground {

            taskList_recyclerView {
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

            delete_imageView {
                bringToFront()
                alpha = 0F
                setOnDragListener { _, event ->
                    if (event.localState is DragEventLocalState) {
                        val draggingState = event.localState as DragEventLocalState
                        when (event.action) {
                            DragEvent.ACTION_DRAG_STARTED -> {
                                alpha = 1F
                                fadeIn(200)
                            }
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                (mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrateCompat(50)
                            }
                            DragEvent.ACTION_DROP -> {
                                ConfirmDialog().apply {
                                    title = this@ViewListFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                                    onConfirm = {
                                        val taskName = Caches.tasks[draggingState.taskID].name
                                        Caches.deleteTask(draggingState.taskID, draggingState.taskListID)
                                        this@ViewListFragment.taskList_recyclerView.apply {
                                            listAdapter.notifyItemRemoved(
                                                    findViewHolderForItemId(draggingState.taskID).adapterPosition
                                            )
                                        }
                                        mainActivity.appBar.shortSnackBar(getString(R.string.deletedTask)
                                                + " $taskName")
                                        this.dismiss()
                                    }
                                }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                            }
                            DragEvent.ACTION_DRAG_ENDED -> {
                                fadeOut(200)
                            }
                        }
                    }
                    true
                }
            }
        }
    }

    private fun setUpAppBar(element: TaskList) {
        mainActivity.resetNavBarStatusBarColor()
        mainActivity.appBar {
            color = Caches.boards[boardID].barColor
            elevation = DEFAULT_ELEVATION
            leftImageBack()
            editTextView {
                textColor = WaqtiColor.WAQTI_WHITE.toAndroidColor
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.listNameHint)
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
            rightImageDefault(R.menu.menu_list) {
                when (it.itemId) {
                    R.id.deleteList_menuItem -> {
                        ConfirmDialog().apply {
                            title = this@ViewListFragment.mainActivity.getString(R.string.deleteListQuestion)
                            message = this@ViewListFragment.mainActivity.getString(R.string.deleteListDetails)
                            onConfirm = {
                                val listName = Caches.taskLists[listID].name
                                dismiss()
                                Caches.deleteTaskList(listID, boardID)
                                mainActivity.appBar.shortSnackBar(getString(R.string.deletedList)
                                        + " $listName")
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
                                val listName = Caches.taskLists[listID].name
                                dismiss()
                                Caches.taskLists[listID].clear().update()
                                mainActivity.appBar.shortSnackBar(getString(R.string.clearedList)
                                        + " $listName")
                                this@ViewListFragment.taskList_recyclerView.listAdapter.notifyDataSetChanged()
                            }
                        }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun finish() {
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
