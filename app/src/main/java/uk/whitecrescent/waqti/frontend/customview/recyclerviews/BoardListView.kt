package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.board_card.view.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.SimpleItemTouchHelperCallback
import uk.whitecrescent.waqti.frontend.fragments.view.ViewBoardFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewMode
import uk.whitecrescent.waqti.hideSoftKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel

class BoardListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    val boardListAdapter: BoardListAdapter
        get() = this.adapter as BoardListAdapter

    override fun setAdapter(_adapter: Adapter<*>?) {
        require(_adapter != null &&
                _adapter is BoardListAdapter
        ) { "Adapter must be non null and a BoardListAdapter, passed in ${_adapter}" }
        super.setAdapter(_adapter)
        changeViewMode(_adapter.viewMode)

        ItemTouchHelper(object : SimpleItemTouchHelperCallback() {

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

            override fun onMoved(recyclerView: RecyclerView, viewHolder: ViewHolder, fromPos: Int,
                                 target: ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                boardListAdapter.apply {
                    boardList.move(fromPos, toPos).update()
                    notifyItemMoved(fromPos, toPos)
                }
                mainActivityViewModel.boardListPosition.changeTo(true to toPos)
            }

        }).attachToRecyclerView(this)
    }

    fun changeViewMode(viewMode: ViewMode) {
        boardListAdapter.viewMode = viewMode
        when (viewMode) {
            ViewMode.LIST_VERTICAL -> {
                layoutManager = LinearLayoutManager(this.context, VERTICAL, false)
            }
            ViewMode.GRID_VERTICAL -> {
                layoutManager = GridLayoutManager(this.context, 2, VERTICAL, false)
            }
        }
    }

}

class BoardListAdapter(val boardListID: ID, var viewMode: ViewMode = ViewMode.LIST_VERTICAL)
    : RecyclerView.Adapter<BoardListViewHolder>() {

    val boardList = Caches.boardLists[boardListID]

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
        holder.itemView.boardName_textView.apply {
            text = boardList[position].name
            when (viewMode) {
                ViewMode.GRID_VERTICAL -> {
                    setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                }
                ViewMode.LIST_VERTICAL -> {
                    setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline3)
                }
            }
            setTextColor(resources.getColor(R.color.black, null))
        }

        holder.itemView.boardCard_cardView.apply {
            setOnClickListener {
                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.hideSoftKeyboard()

                    it.mainActivityViewModel.apply {
                        boardID = boardList[holder.adapterPosition].id
                        boardListPosition.changeTo(false to position)
                    }

                    replace(R.id.fragmentContainer, ViewBoardFragment(), BOARD_FRAGMENT)
                    addToBackStack(null)
                }
            }
        }

        holder.itemView.boardImage_imageView.apply {
            setImageDrawable(boardList[position].backgroundColor.toColorDrawable)
        }
    }

}

class BoardListViewHolder(view: View) : RecyclerView.ViewHolder(view)