package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.clearFocusAndHideSoftKeyboard
import uk.whitecrescent.waqti.commitTransaction
import uk.whitecrescent.waqti.frontend.CREATE_TASK_FRAGMENT
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.VIEW_LIST_FRAGMENT
import uk.whitecrescent.waqti.frontend.fragments.create.CreateTaskFragment
import uk.whitecrescent.waqti.frontend.fragments.view.ViewListFragment
import uk.whitecrescent.waqti.mainActivity
import uk.whitecrescent.waqti.verticalFABOnScrollListener

interface BoardViewCallback {

    fun taskListOnScroll(boardViewHolder: BoardViewHolder): OnScrollListener

    fun taskListHeaderOnClick(boardViewHolder: BoardViewHolder): OnClickListener

    fun taskListHeaderOnLongClick(boardViewHolder: BoardViewHolder): OnLongClickListener

    fun taskListAddButtonOnClick(boardViewHolder: BoardViewHolder): OnClickListener
}

val myBoardViewCallback = object : BoardViewCallback {

    override fun taskListOnScroll(boardViewHolder: BoardViewHolder): OnScrollListener {
        return boardViewHolder.addButton.verticalFABOnScrollListener
    }

    override fun taskListHeaderOnClick(boardViewHolder: BoardViewHolder): OnClickListener {
        return OnClickListener {
            @GoToFragment
            it.mainActivity.supportFragmentManager.commitTransaction {

                it.mainActivity.viewModel.listID = boardViewHolder.adapter.board[boardViewHolder.adapterPosition].id

                it.clearFocusAndHideSoftKeyboard()

                addToBackStack(null)
                replace(R.id.fragmentContainer, ViewListFragment.instance, VIEW_LIST_FRAGMENT)
            }
        }
    }

    override fun taskListHeaderOnLongClick(boardViewHolder: BoardViewHolder): OnLongClickListener {
        return OnLongClickListener {
            boardViewHolder.adapter.itemTouchHelper.startDrag(boardViewHolder)
            true
        }
    }

    override fun taskListAddButtonOnClick(boardViewHolder: BoardViewHolder): OnClickListener {
        return OnClickListener {
            @GoToFragment
            it.mainActivity.supportFragmentManager.commitTransaction {

                it.mainActivity.viewModel.boardID = boardViewHolder.adapter.boardID
                it.mainActivity.viewModel.listID = boardViewHolder.taskListView.listAdapter.taskListID

                it.clearFocusAndHideSoftKeyboard()

                replace(R.id.fragmentContainer, CreateTaskFragment.instance, CREATE_TASK_FRAGMENT)
                addToBackStack(null)
            }
        }
    }

}