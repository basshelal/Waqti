@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.view

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.fragment_board_list_view.*
import org.jetbrains.anko.image
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.convertDpToPx
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.doInBackgroundDelayed
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.NO_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.AppBar
import uk.whitecrescent.waqti.frontend.customview.DragView
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.isValid
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.setImageTint
import uk.whitecrescent.waqti.shortSnackBar
import uk.whitecrescent.waqti.toEditable
import uk.whitecrescent.waqti.verticalFABOnScrollListener

class ViewBoardListFragment : WaqtiViewFragment() {

    private lateinit var viewModel: ViewBoardListFragmentViewModel
    private lateinit var boardList: BoardList

    private inline var viewMode: ViewMode
        set(value) {
            mainActivity.preferences.boardListViewMode = value
        }
        get() = mainActivity.preferences.boardListViewMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivityVM.boardPosition.changeTo(false to 0)

        boardList = Caches.boardList

        viewModel = getViewModel()

        setUpViews()

        dragView.setOnLongClickListener {
            dragView.startDrag()
            true
        }

        dragView.dragListener = object : DragView.SimpleDragListener() {
            override fun onStartDrag(dragView: DragView) {
                dragView.alpha = 0.7F
            }

            override fun onEnteredView(dragView: DragView, newView: View, oldView: View?, touchPoint: PointF): Boolean {
                if (oldView != null)
                    dragView.shortSnackBar("Leaving ${oldView::class.simpleName}, entering ${newView::class.simpleName}")
                return false
            }

            override fun onEndDrag(dragView: DragView) {
                dragView.alpha = 1F
            }
        }

        doInBackgroundDelayed(3000) {
            dragView.isVisible = true
        }
    }

    override fun setUpViews() {
        doInBackground {
            boardsList_recyclerView {
                adapter = BoardListAdapter(boardList.id).also {
                    it.viewMode = viewMode
                }
                setUpAppBar()
                if (boardListAdapter?.boardList?.isEmpty() == true) {
                    emptyState_scrollView.isVisible = true
                    addBoard_FloatingButton.customSize = convertDpToPx(85, mainActivity)
                }
                restoreState(mainActivityVM.boardListState)
                addOnScrollListener(this@ViewBoardListFragment.addBoard_FloatingButton.verticalFABOnScrollListener)
                mainActivityVM.onInflateBoardListView(this)
            }

            addBoard_FloatingButton {
                setImageTint(WaqtiColor.WHITE)
                setOnClickListener {
                    //CreateBoardFragment.show(mainActivity)
                    dragView.isInvisible = !dragView.isInvisible
                }
            }
        }
    }

    override fun setUpAppBar() {
        mainActivity.appBar {
            elevation = AppBar.DEFAULT_ELEVATION
            leftImageView.isVisible = true
            leftImage = R.drawable.menu_icon
            editTextView {
                isEditable = true
                hint = getString(R.string.allBoards)
                textChangedListener = {
                    if (it.isValid && it.toString() != boardList.name) boardList.name = it.toString()
                }
                text = boardList.name.toEditable()
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }
            rightImageView {
                isVisible = true
                fun update() {
                    when (viewMode) {
                        ViewMode.LIST_VERTICAL -> {
                            image = mainActivity.getDrawable(R.drawable.grid_icon)
                            mainActivity.navigationView.menu
                                    .findItem(R.id.allBoards_navDrawerItem)
                                    .setIcon(R.drawable.boardlist_icon)
                        }
                        ViewMode.GRID_VERTICAL -> {
                            image = mainActivity.getDrawable(R.drawable.boardlist_icon)
                            mainActivity.navigationView.menu
                                    .findItem(R.id.allBoards_navDrawerItem)
                                    .setIcon(R.drawable.grid_icon)
                        }
                    }
                    mainActivity.resetColorScheme()
                    this@ViewBoardListFragment.boardsList_recyclerView
                            .changeViewMode(viewMode)
                    mainActivity.preferences.boardListViewMode = viewMode
                }
                update()
                setOnClickListener {
                    viewMode = viewMode.switch()
                    update()
                }
            }
        }
        mainActivity.resetColorScheme()
    }

    override fun finish() {

    }

    override fun onStop() {
        super.onStop()

        mainActivityVM.boardListState = boardsList_recyclerView.saveState()
    }

    companion object {
        inline fun show(mainActivity: MainActivity) {
            mainActivity.supportFragmentManager.commitTransaction {
                @FragmentNavigation(from = NO_FRAGMENT, to = VIEW_BOARD_LIST_FRAGMENT)
                add(R.id.fragmentContainer, ViewBoardListFragment(), VIEW_BOARD_LIST_FRAGMENT)
            }
        }
    }
}

enum class ViewMode {
    LIST_VERTICAL, GRID_VERTICAL;

    fun switch(): ViewMode {
        return if (ordinal + 1 >= values().size) values()[0]
        else values()[ordinal + 1]
    }
}

class ViewBoardListFragmentViewModel : WaqtiViewFragmentViewModel()