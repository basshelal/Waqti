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
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.persistence.ElementNotFoundException
import uk.whitecrescent.waqti.model.task.ID

class BoardListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    val boardListAdapter: BoardListAdapter
        get() = this.adapter as BoardListAdapter

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
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN
                        or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
            }

            override fun isLongPressDragEnabled() = true

            override fun isItemViewSwipeEnabled() = false

            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                /*This will never be called as we do not support swiping*/
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder is BoardListViewHolder) {
                    viewHolder.itemView.alpha = 1F
                }
            }

            override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (viewHolder != null && viewHolder is BoardListViewHolder) {
                    viewHolder.itemView.alpha = 0.7F
                }
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                                 target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                boardListAdapter.apply {
                    boardList.move(fromPos, toPos).update()
                    notifyItemMoved(fromPos, toPos)
                }
                mainActivity.viewModel.boardListPosition = true to toPos
            }

            override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int,
                                                      viewSizeOutOfBounds: Int, totalSize: Int,
                                                      msSinceStartScroll: Long): Int {
                return super.interpolateOutOfBoundsScroll(
                        recyclerView, viewSize, viewSizeOutOfBounds, totalSize, 1500)
                // TODO: 13-Dec-18 Override this to make better when we drag outside the bounds
            }

        }).attachToRecyclerView(this)

        object : LinearSnapHelper() {
            override fun findTargetSnapPosition(layoutManager: LayoutManager?, velocityX: Int, velocityY: Int): Int {
                val currentPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                mainActivity.viewModel.boardListPosition = true to currentPos
                return currentPos
            }
        }.attachToRecyclerView(this)
    }

}

class BoardListAdapter(val boardListID: ID) : RecyclerView.Adapter<BoardListViewHolder>() {

    val boardList = Database.boardLists[boardListID] ?: throw ElementNotFoundException(boardListID)

    init {
        this.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return boardList[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        return BoardListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.board_card, parent, false))
    }

    override fun getItemCount(): Int {
        return boardList.size
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        holder.itemView.boardName_textView.text = boardList[position].name
        holder.itemView.boardCard_cardView.setOnClickListener {
            @GoToFragment()
            it.mainActivity.supportFragmentManager.beginTransaction().apply {

                it.mainActivity.viewModel.boardID = boardList[position].id

                it.mainActivity.viewModel.boardListPosition = false to position

                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.fragmentContainer, ViewBoardFragment.newInstance(), BOARD_FRAGMENT)
                addToBackStack("ViewBoardFragment")
            }.commit()
        }
    }

}

class BoardListViewHolder(view: View) : RecyclerView.ViewHolder(view)