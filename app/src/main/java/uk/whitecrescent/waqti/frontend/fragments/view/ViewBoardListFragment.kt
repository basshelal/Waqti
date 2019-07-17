package uk.whitecrescent.waqti.frontend.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import kotlinx.android.synthetic.main.blank_activity.*
import kotlinx.android.synthetic.main.fragment_board_list_view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.clearFocusAndHideKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.convertDpToPx
import uk.whitecrescent.waqti.doInBackground
import uk.whitecrescent.waqti.frontend.CREATE_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.frontend.fragments.create.CreateBoardFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragmentViewModel
import uk.whitecrescent.waqti.getViewModel
import uk.whitecrescent.waqti.invoke
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel
import uk.whitecrescent.waqti.setImageTint
import uk.whitecrescent.waqti.verticalFABOnScrollListener

class ViewBoardListFragment : WaqtiViewFragment() {

    lateinit var viewMode: ViewMode
    private lateinit var viewModel: ViewBoardListFragmentViewModel
    private lateinit var boardList: BoardList

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivityVM.boardPosition.changeTo(false to 0)

        viewMode = mainActivity.waqtiPreferences.boardListViewMode

        boardList = Caches.boardList

        viewModel = getViewModel()

        setUpViews()
    }

    override fun setUpViews() {
        doInBackground {
            boardsList_recyclerView {
                adapter = BoardListAdapter(boardList.id, viewMode)
                setUpAppBar()
                if (boardListAdapter.boardList.isEmpty()) {
                    emptyState_scrollView.isVisible = true
                    addBoard_FloatingButton.customSize = convertDpToPx(85, mainActivity)
                }
                if (this.boardListAdapter.itemCount > 0) {
                    postDelayed(100L) {
                        mainActivityViewModel.boardListPosition.apply {
                            if (positionChanged) smoothScrollToPosition(position)
                        }
                    }
                }
                addOnScrollListener(this@ViewBoardListFragment.addBoard_FloatingButton.verticalFABOnScrollListener)
            }

            addBoard_FloatingButton {
                setImageTint(WaqtiColor.WHITE)
                setOnClickListener {
                    @FragmentNavigation(from = VIEW_BOARD_LIST_FRAGMENT, to = CREATE_BOARD_FRAGMENT)
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivityViewModel.boardListPosition
                                .changeTo(false to boardsList_recyclerView.boardListAdapter.itemCount - 1)

                        it.clearFocusAndHideKeyboard()

                        replace(R.id.fragmentContainer, CreateBoardFragment(), CREATE_BOARD_FRAGMENT)
                        addToBackStack(null)
                    }
                }
            }
        }
    }

    override fun setUpAppBar() {
        mainActivity.resetColorScheme()
        mainActivity.appBar {
            elevation = DEFAULT_ELEVATION
            leftImageMenu()
            editTextView {
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.allBoards)
                fun update() {
                    if (text != null && text!!.isNotBlank() && text!!.isNotEmpty()) {
                        if (text.toString() != mainActivity.waqtiPreferences.boardListName) {
                            mainActivity.waqtiPreferences.boardListName = text.toString()
                        }
                    }
                }
                addAfterTextChangedListener { update() }
                text = SpannableStringBuilder(mainActivity.waqtiPreferences.boardListName)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        clearFocusAndHideKeyboard()
                        true
                    } else false
                }
            }
            rightImageView {
                rightImageView.isVisible = true
                fun update() {
                    when (viewMode) {
                        ViewMode.LIST_VERTICAL -> {
                            rightImage = mainActivity.getDrawable(R.drawable.grid_icon)!!
                            rightImage.setTint(WaqtiColor.WAQTI_WHITE.toAndroidColor)
                            this@ViewBoardListFragment.boardsList_recyclerView
                                    .changeViewMode(ViewMode.LIST_VERTICAL)

                            mainActivity.navigationView.menu
                                    .findItem(R.id.allBoards_navDrawerItem)
                                    .setIcon(R.drawable.boardlist_icon)
                        }
                        ViewMode.GRID_VERTICAL -> {
                            rightImage = mainActivity.getDrawable((R.drawable.boardlist_icon))!!
                            rightImage.setTint(WaqtiColor.WAQTI_WHITE.toAndroidColor)
                            this@ViewBoardListFragment.boardsList_recyclerView
                                    .changeViewMode(ViewMode.GRID_VERTICAL)
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

class ViewBoardListFragmentViewModel : WaqtiViewFragmentViewModel()