package uk.whitecrescent.waqti.android.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.board_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.android.fragments.BoardFragment
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches

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
            (it.context as MainActivity).supportFragmentManager.beginTransaction().apply {
                val fragment = BoardFragment.newInstance()
                val bundle = Bundle()
                bundle.putLong("boardID", itemList[position].id)
                fragment.arguments = bundle
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.blank_constraintLayout, fragment, "BoardFragment")
                addToBackStack("BoardFragment")
            }.commit()
        }
    }

}

class BoardGridViewHolder(view: View) : RecyclerView.ViewHolder(view)