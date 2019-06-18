package uk.whitecrescent.waqti.frontend.customview.recyclerviews

/*interface BoardViewCallback {

    fun taskListOnScroll(boardViewHolder: BoardViewHolder): OnScrollListener

    fun taskListHeaderOnClick(boardViewHolder: BoardViewHolder): OnClickListener

    fun taskListHeaderOnLongClick(boardViewHolder: BoardViewHolder): OnLongClickListener

    fun taskListAddButtonOnClick(boardViewHolder: BoardViewHolder): OnClickListener
}*/

/*
val myBoardViewCallback = object : BoardViewCallback {

    override fun taskListOnScroll(boardViewHolder: BoardViewHolder): OnScrollListener {
        return boardViewHolder.addButton.verticalFABOnScrollListener
    }

    override fun taskListHeaderOnClick(boardViewHolder: BoardViewHolder): OnClickListener {
        return OnClickListener {
            @GoToFragment
            it.mainActivity.supportFragmentManager.commitTransaction {

                it.mainActivityViewModel.listID = boardViewHolder.adapter.board[boardViewHolder.adapterPosition].id

                it.clearFocusAndHideSoftKeyboard()

                addToBackStack(null)
                replace(R.id.fragmentContainer, ViewListFragment(), VIEW_LIST_FRAGMENT)
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

                it.mainActivityViewModel.boardID = boardViewHolder.adapter.boardID
                it.mainActivityViewModel.listID = boardViewHolder.taskListView.listAdapter.taskListID

                it.clearFocusAndHideSoftKeyboard()

                replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                addToBackStack(null)
            }
        }
    }

}*/
