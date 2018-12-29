package uk.whitecrescent.waqti.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.board_card.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.BOARD_FRAGMENT
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.fragments.parents.WaqtiFragment
import uk.whitecrescent.waqti.android.mainActivity
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

        viewModel.boardPosition = 0

        boardsGrid_recyclerView.layoutManager = GridLayoutManager(context, 3)
        boardsGrid_recyclerView.adapter = BoardsGridAdapter()

        addBoard_Button.setOnClickListener {
            Caches.boards.put(Board("New Board"))
            boardsGrid_recyclerView.adapter?.notifyDataSetChanged()
            boardsGrid_recyclerView.scrollToEnd()
        }


    }

}

class BoardsGridAdapter : RecyclerView.Adapter<BoardGridViewHolder>() {

    // TODO: 25-Dec-18 Below is bad! Will be slow!!
    // TODO: 25-Dec-18 The thing holding all the boards needs to also be an AbstractWaqtiList
    val itemList: List<Board>
        get() = Caches.boards.all

    init {
        this.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return itemList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardGridViewHolder {
        return BoardGridViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.board_card, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BoardGridViewHolder, position: Int) {
        holder.itemView.board_button.text = itemList[position].name + " id: ${itemList[position].id}"
        holder.itemView.board_button.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = itemList[position].id

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, BoardFragment.newInstance(), BOARD_FRAGMENT)
                addToBackStack("BoardFragment")
            }.commit()
        }
    }

}

class BoardGridViewHolder(view: View) : RecyclerView.ViewHolder(view)
