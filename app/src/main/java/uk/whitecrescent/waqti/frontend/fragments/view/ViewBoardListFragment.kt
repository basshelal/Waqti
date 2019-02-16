package uk.whitecrescent.waqti.frontend.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_board_list_view.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.frontend.BOARD_LIST_NAME_PREFERENCES_KEY
import uk.whitecrescent.waqti.frontend.BOARD_LIST_VIEW_MODE_KEY
import uk.whitecrescent.waqti.frontend.CREATE_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.FABOnScrollListener
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.Orientation
import uk.whitecrescent.waqti.frontend.addAfterTextChangedListener
import uk.whitecrescent.waqti.frontend.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.frontend.fragments.create.CreateBoardFragment
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.mainActivity

class ViewBoardListFragment : WaqtiViewFragment<BoardList>() {

    lateinit var viewMode: ViewMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (Caches.boardLists.isEmpty()) Caches.boardLists.put(BoardList("Default"))

        mainActivityViewModel.boardPosition = false to 0
        viewMode = ViewMode.valueOf(mainActivity.waqtiSharedPreferences
                .getString(BOARD_LIST_VIEW_MODE_KEY, ViewMode.LIST_VERTICAL.name)!!)

        val boardList = Caches.boardLists.first()

        boardsList_recyclerView.adapter = BoardListAdapter(boardList.id, viewMode)

        require(Caches.boardLists.size <= 1)
        setUpViews(boardList)
    }

    override fun setUpViews(element: BoardList) {

        mainActivity.resetStatusBarColor()

        boardList_appBar.apply {
            editTextView.apply {
                fun update() {
                    if (text != null && text!!.isNotBlank() && text!!.isNotEmpty()) {
                        if (text.toString() != mainActivity.waqtiSharedPreferences
                                        .getString(BOARD_LIST_NAME_PREFERENCES_KEY, getString(R.string.allBoards))) {
                            mainActivity.waqtiSharedPreferences
                                    .edit().putString(BOARD_LIST_NAME_PREFERENCES_KEY,
                                            text.toString()).apply()
                        }
                    }
                }
                text = SpannableStringBuilder(
                        mainActivity.waqtiSharedPreferences
                                .getString(BOARD_LIST_NAME_PREFERENCES_KEY, getString(R.string.allBoards)))
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

                        }
                        ViewMode.GRID_VERTICAL -> {
                            setImageResource(R.drawable.list_icon)
                            boardsList_recyclerView.changeViewMode(ViewMode.GRID_VERTICAL)
                        }
                    }
                    mainActivity.waqtiSharedPreferences
                            .edit().putString(BOARD_LIST_VIEW_MODE_KEY, viewMode.name).apply()
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
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardListPosition = false to boardsList_recyclerView.boardListAdapter.itemCount - 1

                it.clearFocusAndHideSoftKeyboard()

                replace(R.id.fragmentContainer, CreateBoardFragment(), CREATE_BOARD_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
            }.commit()
        }

        boardsList_recyclerView.apply {
            if (this.boardListAdapter.itemCount > 0) {
                postDelayed(
                        {
                            mainActivityViewModel.boardListPosition.apply {
                                if (first) smoothScrollToPosition(second)
                            }
                        },
                        100L
                )
            }
            addOnScrollListener(FABOnScrollListener(
                    this@ViewBoardListFragment.addBoard_FloatingButton, Orientation.VERTICAL))
        }
    }

    override fun finish() {

    }
}

enum class ViewMode {
    LIST_VERTICAL, GRID_VERTICAL;

    fun switch(): ViewMode {
        return if (ordinal + 1 >= ViewMode.values().size) ViewMode.values()[0]
        else ViewMode.values()[ordinal + 1]
    }
}
