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
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.FragmentNavigation
import uk.whitecrescent.waqti.frontend.SimpleItemTouchHelperCallback
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_FRAGMENT
import uk.whitecrescent.waqti.frontend.VIEW_BOARD_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.view.ViewBoardFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewMode
import uk.whitecrescent.waqti.hideKeyboard
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.mainActivityViewModel

class BoardListView
@JvmOverloads constructor(context: Context,
                          attributeSet: AttributeSet? = null,
                          defStyle: Int = 0) : RecyclerView(context, attributeSet, defStyle) {

    val boardListAdapter: BoardListAdapter
        get() = this.adapter as BoardListAdapter

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
    lateinit var boardListView: BoardListView

    init {
        this.setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        require(recyclerView is BoardListView) {
            "Recycler View attached to a BoardListAdapter must be a BoardListView," +
                    " passed in ${recyclerView::class}"
        }
        boardListView = recyclerView
        boardListView.changeViewMode(viewMode)

        ItemTouchHelper(object : SimpleItemTouchHelperCallback() {

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder is BoardListViewHolder) {
                    viewHolder.itemView.alpha = 1F
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (viewHolder != null && viewHolder is BoardListViewHolder) {
                    viewHolder.itemView.alpha = 0.7F
                }
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int,
                                 target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

                boardList.move(fromPos, toPos).update()
                notifyItemMoved(fromPos, toPos)

                boardListView.mainActivityViewModel.boardListPosition.changeTo(true to toPos)
            }

        }).attachToRecyclerView(boardListView)
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
            backgroundColor = boardList[position].barColor.toAndroidColor
            textColor = boardList[position].barColor.colorScheme.text.toAndroidColor
        }

        holder.itemView.boardCard_cardView.apply {
            setOnClickListener {
                @FragmentNavigation(from = VIEW_BOARD_LIST_FRAGMENT, to = VIEW_BOARD_FRAGMENT)
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.hideKeyboard()

                    it.mainActivityViewModel.apply {
                        boardID = boardList[holder.adapterPosition].id
                        boardListPosition.changeTo(false to position)
                    }

                    replace(R.id.fragmentContainer, ViewBoardFragment(), VIEW_BOARD_FRAGMENT)
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