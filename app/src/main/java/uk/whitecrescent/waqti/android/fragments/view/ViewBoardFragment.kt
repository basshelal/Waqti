package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_board_view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.CREATE_LIST_FRAGMENT
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.customview.recyclerviews.BoardAdapter
import uk.whitecrescent.waqti.android.fragments.create.CreateListFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.mainActivity
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class ViewBoardFragment : WaqtiViewFragment() {

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

        boardID = viewModel.boardID

        setUpViews(Caches.boards[boardID])
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.renameBoard_menuItem -> {
                boardView.snackBar("Rename board clicked")
                true
            }
            R.id.deleteBoard_menuItem -> {
                boardView.snackBar("Delete board clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViews(board: Board) {
        mainActivity.supportActionBar?.title =
                "Board - ${board.name} ${board.id} "

        boardView.adapter = BoardAdapter(board.id)

        addList_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = board.id
                it.mainActivity.viewModel.boardPosition = false to boardView.boardAdapter.itemCount - 1

                replace(R.id.fragmentContainer, CreateListFragment.newInstance(), CREATE_LIST_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
            }.commit()
        }

        if (boardView.boardAdapter.itemCount > 0) {
            boardView.postDelayed(
                    {
                        viewModel.boardPosition.apply {
                            if (first) boardView.smoothScrollToPosition(second)
                        }
                    },
                    100L
            )
        }
    }
}
