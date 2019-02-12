package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_board_list_view.*
import kotlinx.android.synthetic.main.view_appbar.view.*
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.BOARD_LIST_NAME_PREFERENCES_KEY
import uk.whitecrescent.waqti.android.BOARD_LIST_VIEW_MODE_KEY
import uk.whitecrescent.waqti.android.CREATE_BOARD_FRAGMENT
import uk.whitecrescent.waqti.android.customview.addAfterTextChangedListener
import uk.whitecrescent.waqti.android.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.android.fragments.create.CreateBoardFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.model.collections.BoardList
import uk.whitecrescent.waqti.model.persistence.Caches

class ViewBoardListFragment : WaqtiViewFragment<BoardList>() {

    companion object {
        fun newInstance() = ViewBoardListFragment()
    }

    lateinit var viewMode: ViewMode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (Caches.boardLists.isEmpty()) Caches.boardLists.put(BoardList("Default"))

        viewModel.boardPosition = false to 0
        viewMode = ViewMode.valueOf(mainActivity.waqtiSharedPreferences
                .getString(BOARD_LIST_VIEW_MODE_KEY, ViewMode.LIST_VERTICAL.name)!!)

        val boardList = Caches.boardLists.first()

        boardsList_recyclerView.adapter = BoardListAdapter(boardList.id, viewMode)

        require(Caches.boardLists.size <= 1)
        setUpViews(boardList)
    }

    override fun setUpViews(element: BoardList) {

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
                setOnEditorActionListener { textView, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        update()
                        textView.clearFocusAndHideSoftKeyboard()
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
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardListPosition = false to boardsList_recyclerView.boardListAdapter.itemCount - 1

                replace(R.id.fragmentContainer, CreateBoardFragment.newInstance(), CREATE_BOARD_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
            }.commit()
        }

        boardsList_recyclerView.apply {
            if (this.boardListAdapter.itemCount > 0) {
                postDelayed(
                        {
                            viewModel.boardListPosition.apply {
                                if (first) smoothScrollToPosition(second)
                            }
                        },
                        100L
                )
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && this@ViewBoardListFragment.addBoard_FloatingButton.visibility
                            == View.VISIBLE) {
                        this@ViewBoardListFragment.addBoard_FloatingButton.hide()
                    } else if (dy < 0 && this@ViewBoardListFragment.addBoard_FloatingButton
                                    .visibility != View.VISIBLE) {
                        this@ViewBoardListFragment.addBoard_FloatingButton.show()
                    }
                }
            })
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
