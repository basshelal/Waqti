package uk.whitecrescent.waqti.frontend.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.postDelayed
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.fragment_board_list_view.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.addAfterTextChangedListener
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.CREATE_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.frontend.fragments.create.CreateBoardFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.verticalFABOnScrollListener

class ViewBoardListFragment : WaqtiViewFragment<BoardList>() {

    lateinit var viewMode: ViewMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mainActivityViewModel.boardPosition = false to 0

        viewMode = mainActivity.waqtiPreferences.boardListViewMode

        setUpViews(Caches.boardList)
    }

    override fun setUpViews(element: BoardList) {
        boardsList_recyclerView.adapter = BoardListAdapter(element.id, viewMode)
        mainActivity.resetNavBarStatusBarColor()

        boardList_appBar.apply {
            editTextView.apply {
                mainActivity.hideableEditTextView = this
                fun update() {
                    if (text != null && text!!.isNotBlank() && text!!.isNotEmpty()) {
                        if (text.toString() != mainActivity.waqtiPreferences.boardListName) {
                            mainActivity.waqtiPreferences.boardListName = text.toString()
                        }
                    }
                }
                text = SpannableStringBuilder(mainActivity.waqtiPreferences.boardListName)
                addAfterTextChangedListener { update() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideSoftKeyboard()
                        true
                    } else false
                }
            }
            rightImageView.apply {
                fun update() {
                    when (viewMode) {
                        ViewMode.LIST_VERTICAL -> {
                            setImageResource(R.drawable.grid_icon)
                            boardsList_recyclerView.changeViewMode(ViewMode.LIST_VERTICAL)

                            mainActivity.navigationView.menu
                                    .findItem(R.id.allBoards_navDrawerItem)
                                    .setIcon(R.drawable.list_icon)
                        }
                        ViewMode.GRID_VERTICAL -> {
                            setImageResource(R.drawable.list_icon)
                            boardsList_recyclerView.changeViewMode(ViewMode.GRID_VERTICAL)
                            mainActivity.navigationView.menu
                                    .findItem(R.id.allBoards_navDrawerItem)
                                    .setIcon(R.drawable.grid_icon)
                        }
                    }
                    mainActivity.waqtiPreferences.boardListViewMode = viewMode
                }
                update()
                setOnClickListener {
                    viewMode = viewMode.switch()
                    update()
                }
            }
        }

        addBoard_FloatingButton.setOnClickListener {
            @GoToFragment
            it.mainActivity.supportFragmentManager.commitTransaction {

                it.mainActivityViewModel.boardListPosition =
                        false to boardsList_recyclerView.boardListAdapter.itemCount - 1

                it.clearFocusAndHideSoftKeyboard()

                replace(R.id.fragmentContainer, CreateBoardFragment(), CREATE_BOARD_FRAGMENT)
                addToBackStack(null)
            }
        }

        boardsList_recyclerView.apply {
            if (this.boardListAdapter.itemCount > 0) {
                postDelayed(100L) {
                    mainActivityViewModel.boardListPosition.apply {
                        if (first) smoothScrollToPosition(second)
                    }
                }
            }
            addOnScrollListener(this@ViewBoardListFragment.addBoard_FloatingButton.verticalFABOnScrollListener)
        }
    }

    override fun finish() {

    }
}

enum class ViewMode {
    LIST_VERTICAL, GRID_VERTICAL;

    fun switch(): ViewMode {
        return if (ordinal + 1 >= values().size) values()[0]
        else values()[ordinal + 1]
    }
}
