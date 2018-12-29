package uk.whitecrescent.waqti.android.fragments

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
import uk.whitecrescent.waqti.android.customview.BoardAdapter
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiFragment
import uk.whitecrescent.waqti.android.mainActivity
import uk.whitecrescent.waqti.android.snackBar
import uk.whitecrescent.waqti.model.task.ID

class BoardFragment : WaqtiFragment() {

    companion object {
        fun newInstance() = BoardFragment()
    }

    private var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        boardID = viewModel.boardID

        boardView.adapter = BoardAdapter(boardID)

        mainActivity.supportActionBar?.title =
                "Waqti - ${boardView.boardAdapter.board.name} ${boardView.boardAdapter.boardID} " +
                "DEV BUILD"

        addList_floatingButton.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = boardID
                it.mainActivity.viewModel.boardPosition = boardView.boardAdapter.itemCount - 1

                replace(R.id.fragmentContainer, CreateListFragment.newInstance(), CREATE_LIST_FRAGMENT)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                addToBackStack("")
            }.commit()
        }

        if (boardView.boardAdapter.itemCount > 0) {
            boardView.postDelayed(
                    { boardView.smoothScrollToPosition(viewModel.boardPosition) },
                    100L
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO: 27-Dec-18 This menu could be a Context or Popup menu in the HomeFragment for each View
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
}
