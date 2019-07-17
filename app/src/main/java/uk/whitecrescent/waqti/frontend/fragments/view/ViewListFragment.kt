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
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.fragment_view_list.*
import kotlinx.android.synthetic.main.list_options.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.alsoIfNotNull
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.fadeIn
import uk.whitecrescent.waqti.fadeOut
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.PREVIOUS_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.shortSnackBar
import uk.whitecrescent.waqti.verticalFABOnScrollListener

class ViewListFragment : WaqtiViewFragment() {

    private var listID: ID = 0L
    private var boardID: ID = 0L
    private lateinit var viewModel: ViewListFragmentViewModel
    private lateinit var taskList: TaskList
    private lateinit var board: Board

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listID = mainActivityVM.listID
        boardID = mainActivityVM.boardID

        taskList = Caches.taskLists[listID]
        board = Caches.boards[boardID]

        viewModel = getViewModel()

        setUpViews()

    }

    override fun setUpViews() {
        setUpAppBar()

        taskList_recyclerView.adapter = mainActivityVM.boardAdapter?.getListAdapter(listID)

        doInBackground {

            taskList_recyclerView {
                background = board.backgroundColor.toColorDrawable
                addOnScrollListener(this@ViewListFragment.addTask_floatingButton.verticalFABOnScrollListener)
            }

            addTask_floatingButton.setOnClickListener {
                @FragmentNavigation(from = VIEW_LIST_FRAGMENT, to = CREATE_TASK_FRAGMENT)
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivityViewModel.boardID = boardID
                    it.mainActivityViewModel.listID = listID

                    it.clearFocusAndHideKeyboard()

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
                                            listAdapter?.notifyItemRemoved(
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

    override fun setUpAppBar() {
        mainActivity.setColorScheme(board.barColor.colorScheme)
        mainActivity.appBar {
            elevation = DEFAULT_ELEVATION
            leftImageBack()
            editTextView {
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.listNameHint)
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != taskList.name)
                            this@ViewListFragment.taskList.name = it.toString()
                    }
                }
                text = SpannableStringBuilder(taskList.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }
            rightImageOptions()
        }
        mainActivity.setColorScheme(Caches.boards[boardID].barColor.colorScheme)
    }

    override fun onResume() {
        super.onResume()

        LayoutInflater.from(context).inflate(R.layout.list_options,
                mainActivity.drawerLayout, true)

        mainActivity.appBar {
            rightImageView.setOnClickListener {
                mainActivity.drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        mainActivity.drawerLayout.listOptions_navigationView {
            listHeaderColor_listOption {
                setOnClickListener {
                    it.shortSnackBar("Not yet implemented")
                }
            }
            cardColor_listOption {
                setOnClickListener {
                    it.shortSnackBar("Not yet implemented")
                }
            }
            clearList_listOption {
                setOnClickListener {
                    ConfirmDialog().apply {
                        title = this@ViewListFragment.mainActivity.getString(R.string.clearListQuestion)
                        message = this@ViewListFragment.mainActivity.getString(R.string.clearListDetails)
                        onConfirm = {
                            val listName = taskList.name
                            dismiss()
                            taskList.clear().update()
                            mainActivity.appBar.shortSnackBar(getString(R.string.clearedList)
                                    + " $listName")
                            this@ViewListFragment.taskList_recyclerView.listAdapter?.notifyDataSetChanged()
                        }
                    }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                    mainActivity.drawerLayout.closeDrawer(this@listOptions_navigationView)
                }
            }
            deleteList_listOption {
                setOnClickListener {
                    ConfirmDialog().apply {
                        title = this@ViewListFragment.mainActivity.getString(R.string.deleteListQuestion)
                        message = this@ViewListFragment.mainActivity.getString(R.string.deleteListDetails)
                        onConfirm = {
                            val listName = taskList.name
                            dismiss()
                            Caches.deleteTaskList(listID, boardID)
                            mainActivity.appBar.shortSnackBar(getString(R.string.deletedList)
                                    + " $listName")
                            finish()
                        }
                    }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                    mainActivity.drawerLayout.closeDrawer(this@listOptions_navigationView)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        mainActivity.drawerLayout {
            listOptions_navigationView.alsoIfNotNull {
                closeDrawer(it)
                removeView(it)
            }
        }
    }

    override fun finish() {
        @FragmentNavigation(from = VIEW_LIST_FRAGMENT, to = PREVIOUS_FRAGMENT)
        mainActivity.supportFragmentManager.popBackStack()
    }
}

class ViewListFragmentViewModel : WaqtiViewFragmentViewModel()