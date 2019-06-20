package uk.whitecrescent.waqti.frontend.fragments.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.SpannableStringBuilder
import android.view.DragEvent
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_view_list.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.fadeIn
import uk.whitecrescent.waqti.fadeOut
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.TaskListView
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.shortSnackBar
import uk.whitecrescent.waqti.verticalFABOnScrollListener

class ViewListFragment : WaqtiViewFragment<TaskList>() {

    private var listID: ID = 0L
    private var boardID: ID = 0L
    lateinit var taskListView: TaskListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_list, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listID = mainActivityVM.listID
        boardID = mainActivityVM.boardID

        mainActivityVM.boardAdapter?.clickedTaskListView?.let {
            (it.parent as ViewGroup).removeView(it)
            viewListFragment_constraintLayout.addView(it)
            taskListView = it
        }

        // TODO: 20-Jun-19 remove later, this is for swiping inside the TaskListView
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                taskListView.shortSnackBar("Flinged X: $velocityX, Y, $velocityY")
                return true
            }
        })
        taskListView.setOnTouchListener { v, event ->
            return@setOnTouchListener if (gestureDetector.onTouchEvent(event)) {
                true
            } else taskListView.onTouchEvent(event)
        }

        setUpViews(Caches.taskLists[listID])

    }

    override fun setUpViews(element: TaskList) {
        mainActivity.resetNavBarStatusBarColor()
        mainActivity.appBar {
            color = Caches.boards[boardID].barColor
            elevation = DEFAULT_ELEVATION
            leftImageDefault()
            editTextView {
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
                                taskListView.listAdapter.notifyDataSetChanged()
                            }
                        }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                        true
                    }
                    else -> false
                }
            }
        }

        taskListView.apply {
            clearOnScrollListeners()
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

        delete_imageView.apply {
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
                                    Caches.deleteTask(draggingState.taskID, draggingState.taskListID)
                                    taskListView.apply {
                                        listAdapter.notifyItemRemoved(
                                                findViewHolderForItemId(draggingState.taskID).adapterPosition
                                        )
                                    }
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

    override fun finish() {
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
