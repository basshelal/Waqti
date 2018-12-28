package uk.whitecrescent.waqti.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.customview.BoardsGridAdapter
import uk.whitecrescent.waqti.android.fragments.base.WaqtiFragment
import uk.whitecrescent.waqti.android.scrollToEnd
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches

class HomeFragment : WaqtiFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity.supportActionBar?.title = "Waqti - All Boards"

        boardsGrid_recyclerView.layoutManager = GridLayoutManager(context, 3)
        boardsGrid_recyclerView.adapter = BoardsGridAdapter()

        addBoard_Button.setOnClickListener {
            Caches.boards.put(Board("New Board"))
            boardsGrid_recyclerView.adapter?.notifyDataSetChanged()
            boardsGrid_recyclerView.scrollToEnd()
        }


    }

}
