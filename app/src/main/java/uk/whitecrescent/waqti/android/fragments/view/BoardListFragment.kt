package uk.whitecrescent.waqti.android.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_board_list.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.recyclerviews.BoardListAdapter
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiViewFragment
import uk.whitecrescent.waqti.android.scrollToEnd
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches

class BoardListFragment : WaqtiViewFragment() {

    companion object {
        fun newInstance() = BoardListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity.supportActionBar?.title = "Boards"

        viewModel.boardPosition = false to 0

        boardsList_recyclerView.adapter = BoardListAdapter()

        addBoard_Button.setOnClickListener {
            Caches.boards.put(Board("New Board"))
            boardsList_recyclerView.adapter?.notifyDataSetChanged()
            boardsList_recyclerView.scrollToEnd()
        }


    }

}
