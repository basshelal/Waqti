package uk.whitecrescent.waqti.android.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.board_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.android.BOARD_FRAGMENT
import uk.whitecrescent.waqti.android.GoToFragment
import uk.whitecrescent.waqti.android.fragments.view.ViewBoardFragment
import uk.whitecrescent.waqti.android.mainActivity
import uk.whitecrescent.waqti.ForLater
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.ID

class BoardListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    init {
        layoutManager = LinearLayoutManager(this.context, VERTICAL, false)
    }

    override fun setAdapter(_adapter: Adapter<*>?) {
        super.setAdapter(_adapter)
        require(this.adapter != null &&
                this.adapter is BoardListAdapter
        ) { "Adapter must be non null and a BoardListAdapter, passed in ${_adapter}" }

        attachHelpers()
    }

    private fun attachHelpers() {
        @ForLater
        // TODO: 29-Dec-18 Make this proper when we have our Collection done
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN
                        or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {

            }

        }).attachToRecyclerView(this)

        LinearSnapHelper().attachToRecyclerView(this)
    }

}

class BoardListAdapter : RecyclerView.Adapter<BoardListViewHolder>() {

    @ForLater
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        return BoardListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.board_card, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        holder.itemView.boardName_textView.text = itemList[position].name + " id: ${itemList[position].id}"
        holder.itemView.boardCard_cardView.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = itemList[position].id

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, ViewBoardFragment.newInstance(), BOARD_FRAGMENT)
                addToBackStack("ViewBoardFragment")
            }.commit()
        }
    }

    fun moveBoards(fromPosition: Int, toPosition: Int) {
        @ForLater {}
        // In the Caches move the order of the Boards
    }

    fun deleteBoard(boardID: ID) {
        @ForLater {}
        // Delete everything in that board
        // Delete the board itself
    }

}

class BoardListViewHolder(view: View) : RecyclerView.ViewHolder(view)