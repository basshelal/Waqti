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
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_board_view.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.frontend.CREATE_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.FABOnScrollListener
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.Orientation
import uk.whitecrescent.waqti.frontend.addAfterTextChangedListener
import uk.whitecrescent.waqti.frontend.customview.dialogs.ColorPickerDialog
import uk.whitecrescent.waqti.frontend.customview.dialogs.ConfirmDialog
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.DragEventLocalState
import uk.whitecrescent.waqti.frontend.fragments.create.CreateListFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.vibrateCompat
import uk.whitecrescent.waqti.mainActivity

class ViewBoardFragment : WaqtiViewFragment<Board>() {

    companion object {
        fun newInstance() = ViewBoardFragment()
    }

    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = mainActivityViewModel.boardID

        setUpViews(Caches.boards[boardID])
    }

    override fun setUpViews(element: Board) {

        board_appBar.apply {
            setBackgroundColor(element.barColor)
            editTextView.apply {
                fun update() {
                    text.also {
                        if (it != null &&
                                it.isNotBlank() &&
                                it.isNotEmpty() &&
                                it.toString() != element.name)
                            Caches.boards[boardID].name = it.toString()
                    }
                }
                text = SpannableStringBuilder(element.name)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { textView, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        textView.clearFocusAndHideSoftKeyboard()
                        true
                    } else false
                }
            }
            popupMenu.apply {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.deleteBoard_menuItem -> {
                            ConfirmDialog().apply {
                                title = this@ViewBoardFragment.mainActivity.getString(R.string.deleteBoardQuestion)
                                message = this@ViewBoardFragment.mainActivity.getString(R.string.deleteBoardDetails)
                                onConfirm = {
                                    this.dismiss()
                                    Caches.deleteBoard(boardID)
                                    finish()
                                }
                            }.show(mainActivity.supportFragmentManager, "ConfirmDialog")
                            true
                        }
                        R.id.changeBoardColor_menuItem -> {
                            ColorPickerDialog().apply {
                                title = this@ViewBoardFragment.getString(R.string.pickBoardColor)
                                initialColor = Caches.boards[boardID].backgroundColor
                                onClick = {
                                    this@ViewBoardFragment.boardView.background = it.toColorDrawable
                                }
                                onConfirm = {
                                    Caches.boards[boardID].backgroundColor = it
                                    dismiss()
                                }
                                onCancel = View.OnClickListener {
                                    Caches.boards[boardID].backgroundColor = initialColor
                                    this@ViewBoardFragment.boardView.background = initialColor.toColorDrawable
                                    dismiss()
                                }
                            }.show(mainActivity.supportFragmentManager, "ColorPickerDialog")
                            true
                        }
                        R.id.changeCardColor_menuItem -> {
                            ColorPickerDialog().apply {
                                title = this@ViewBoardFragment.getString(R.string.pickCardColor)
                                initialColor = Caches.boards[boardID].cardColor
                                onClick = { color ->
                                    this@ViewBoardFragment.boardView.allCards.forEach {
                                        it.setCardBackgroundColor(color.toAndroidColor)
                                    }
                                }
                                onConfirm = {
                                    Caches.boards[boardID].cardColor = it
                                    dismiss()
                                }
                                onCancel = View.OnClickListener {
                                    Caches.boards[boardID].cardColor = initialColor
                                    this@ViewBoardFragment.boardView.allCards.forEach {
                                        it.setCardBackgroundColor(initialColor.toAndroidColor)
                                    }
                                    dismiss()
                                }
                            }.show(mainActivity.supportFragmentManager, "ColorPickerDialog")
                            true
                        }
                        R.id.changeAppBarColor_menuItem -> {
                            ColorPickerDialog().apply {
                                title = this@ViewBoardFragment.getString(R.string.pickAppBarColor)
                                initialColor = Caches.boards[boardID].barColor
                                onClick = { color ->
                                    this@ViewBoardFragment.board_appBar.setBackgroundColor(color)
                                }
                                onConfirm = {
                                    Caches.boards[boardID].barColor = it
                                    dismiss()
                                }
                                onCancel = View.OnClickListener {
                                    Caches.boards[boardID].barColor = initialColor
                                    this@ViewBoardFragment.board_appBar.setBackgroundColor(initialColor)
                                    dismiss()
                                }
                            }.show(mainActivity.supportFragmentManager, "ColorPickerDialog")
                            true
                        }
                        R.id.changeBoardPhoto -> {
                            true
                        }
                        else -> false
                    }
                }
            }
        }

        boardView.apply {
            adapter = BoardAdapter(element.id)
            background = element.backgroundColor.toColorDrawable
            if (boardAdapter.itemCount > 0) {
                postDelayed(
                        {
                            mainActivityViewModel.boardPosition.apply {
                                if (first) smoothScrollToPosition(second)
                            }
                        },
                        100L
                )
            }
            addOnScrollListener(FABOnScrollListener(
                    this@ViewBoardFragment.addList_floatingButton, Orientation.HORIZONTAL))
        }

        addList_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = element.id
                it.mainActivity.viewModel.boardPosition = false to boardView.boardAdapter.itemCount - 1

                it.clearFocusAndHideSoftKeyboard()

                replace(R.id.fragmentContainer, CreateListFragment(), CREATE_LIST_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
            }.commit()
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
                                title = this@ViewBoardFragment.mainActivity.getString(R.string.deleteTaskQuestion)
                                onConfirm = {
                                    this.dismiss()
                                    Caches.deleteTask(draggingState.taskID, draggingState.taskListID)
                                    this@ViewBoardFragment.boardView.boardAdapter.notifyDataSetChanged()
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

    override fun finish() {
        mainActivity.supportFragmentManager.popBackStack()
    }
}
