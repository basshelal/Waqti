package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_board_list_view.*
import uk.whitecrescent.waqti.GoToFragment
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.BOARD_LIST_NAME_PREFERENCES_KEY
import uk.whitecrescent.waqti.android.CREATE_BOARD_FRAGMENT
import uk.whitecrescent.waqti.android.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.android.fragments.create.CreateBoardFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.model.collections.BoardList
import uk.whitecrescent.waqti.model.persistence.Caches

class ViewBoardListFragment : WaqtiViewFragment<BoardList>() {

    companion object {
        fun newInstance() = ViewBoardListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (Caches.boardLists.isEmpty()) Caches.boardLists.put(BoardList("Default"))

        viewModel.boardPosition = false to 0

        val boardList = Caches.boardLists.first()

        boardsList_recyclerView.adapter = BoardListAdapter(boardList.id)

        require(Caches.boardLists.size <= 1)
        setUpViews(boardList)
    }

    override fun setUpViews(element: BoardList) {
        mainActivity.supportActionBar?.title = "Boards"

        boardListName_editTextView.text = SpannableStringBuilder(
                mainActivity.waqtiSharedPreferences
                        .getString(BOARD_LIST_NAME_PREFERENCES_KEY, getString(R.string.allBoards)))

        boardListName_editTextView.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (textView.text != null &&
                        textView.text.isNotBlank() &&
                        textView.text.isNotEmpty()) {
                    if (textView.text != mainActivity.waqtiSharedPreferences
                                    .getString(BOARD_LIST_NAME_PREFERENCES_KEY, getString(R.string.allBoards))) {
                        mainActivity.waqtiSharedPreferences
                                .edit().putString(BOARD_LIST_NAME_PREFERENCES_KEY, textView.text
                                        .toString()).apply()
                    }
                }
                textView.clearFocus()
                textView.hideSoftKeyboard()
                true
            } else false
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

        if (boardsList_recyclerView.boardListAdapter.itemCount > 0) {
            boardsList_recyclerView.postDelayed(
                    {
                        viewModel.boardListPosition.apply {
                            if (first) boardsList_recyclerView.smoothScrollToPosition(second)
                        }
                    },
                    100L
            )
        }
    }

    override fun finish() {

    }
}
