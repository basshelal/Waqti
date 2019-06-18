package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.view.ViewGroup
import uk.whitecrescent.waqti.NewAPI
import uk.whitecrescent.waqti.backend.task.ID

// TODO: 15-Jun-19 This Adapter should be used to keep any UI code in the views and any back end
//  code in the adapter, we take care of UI stuff, this guy does any other stuff
@NewAPI
abstract class BoardViewAdapter {

    // Board

    abstract fun getBoardItemCount(): Int

    abstract fun getBoardItemID(position: Int): ID

    abstract fun onCreateBoardViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder

    abstract fun onBindBoardViewHolder(holder: BoardViewHolder, position: Int)

    open fun onInitializeBoardAdapter(boardAdapter: BoardAdapter) {}

    open fun onBoardAdapterAttachedToBoard(boardAdapter: BoardAdapter,
                                           boardView: BoardView) {
    }

    open fun onMovedList(viewHolder: BoardViewHolder,
                         fromPos: Int,
                         target: BoardViewHolder,
                         toPos: Int, x: Int, y: Int) {
    }

    open fun onStartDragList(viewHolder: BoardViewHolder?) {}

    open fun onEndDragList(viewHolder: BoardViewHolder) {}

    open fun onScrollListAcross() {}

    open fun onSnapScrollToList(newPosition: Int) {}

    // Task

    abstract fun getTaskItemCount(taskListID: ID): Int

    abstract fun getTaskListItemID(position: Int, taskListID: ID): ID

    abstract fun onCreateTaskViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder

    abstract fun onBindTaskViewHolder(holder: TaskViewHolder, position: Int, taskListID: ID)

    open fun onInitializeTaskListAdapter(taskListAdapter: TaskListAdapter) {}

    open fun onTaskListAdapterAttachedToTaskList(taskListAdapter: TaskListAdapter,
                                                 taskListView: TaskListView) {
    }

    open fun onDragTaskInSameList(oldTaskViewHolder: TaskViewHolder,
                                  newTaskViewHolder: TaskViewHolder,
                                  taskListAdapter: TaskListAdapter,
                                  taskListID: ID) {
    }

    open fun onTaskScrollDown(draggedViewHolder: TaskViewHolder,
                              draggingOverViewHolder: TaskViewHolder) {
    }

    open fun onTaskScrollUp(draggedViewHolder: TaskViewHolder,
                            draggingOverViewHolder: TaskViewHolder) {
    }

    open fun onDragTaskAcrossFilledList(draggedViewHolder: TaskViewHolder,
                                        draggingOverViewHolder: TaskViewHolder,
                                        oldTaskListAdapter: TaskListAdapter,
                                        draggingOverTaskListAdapter: TaskListAdapter) {
    }

    open fun onScrollTaskAcrossFilledList(draggedViewHolder: TaskViewHolder,
                                          draggingOverViewHolder: TaskViewHolder,
                                          oldTaskListAdapter: TaskListAdapter,
                                          draggingOverTaskListAdapter: TaskListAdapter) {
    }

    open fun onDragTaskAcrossEmptyList(draggedViewHolder: TaskViewHolder,
                                       oldTaskListAdapter: TaskListAdapter,
                                       draggingOverTaskListAdapter: TaskListAdapter) {
    }

    open fun onScrollTaskAcrossEmptyList(draggedViewHolder: TaskViewHolder,
                                         oldTaskListAdapter: TaskListAdapter,
                                         draggingOverTaskListAdapter: TaskListAdapter) {
    }

}


/*
class WaqtiBoardViewAdapter(val boardID: ID) : BoardViewAdapter() {

    val board = Caches.boards[boardID]

    override fun getTaskItemCount(taskListID: ID): Int {
        return board[taskListID].size
    }

    override fun getTaskListItemID(position: Int, taskListID: ID): ID {
        return board[taskListID][position].id
    }

    override fun onCreateTaskViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_card, parent, false)
        )
    }

    override fun onBindTaskViewHolder(holder: TaskViewHolder, position: Int,
                                      taskListID: ID) {
        val taskList = board[taskListID]

        holder.taskID = taskList[position].id
        holder.taskListID = taskListID

        holder.itemView.apply {
            doInBackground {
                task_textView.text = taskList[position].name
                task_textView.textSize =
                        mainActivity.waqtiPreferences.taskCardTextSize.toFloat()
                if (this is CardView)
                    setCardBackgroundColor(Caches.boards[mainActivity.viewModel.boardID].cardColor.toAndroidColor)
                setOnClickListener {
                    @GoToFragment
                    it.mainActivity.supportFragmentManager.commitTransaction {

                        it.mainActivity.viewModel.taskID = holder.taskID
                        it.mainActivity.viewModel.listID = taskListID

                        it.clearFocusAndHideSoftKeyboard()

                        addToBackStack(null)
                        replace(R.id.fragmentContainer, ViewTaskFragment(), VIEW_TASK_FRAGMENT)
                    }
                }
            }
        }

    }

    override fun onDragTaskInSameList(oldTaskViewHolder: TaskViewHolder,
                                      newTaskViewHolder: TaskViewHolder,
                                      taskListAdapter: TaskListAdapter,
                                      taskListID: ID) {

        val oldDragPos = oldTaskViewHolder.adapterPosition
        val newDragPos = newTaskViewHolder.adapterPosition

        board[taskListID].swap(oldDragPos, newDragPos).update()
        taskListAdapter.notifySwapped(oldDragPos, newDragPos)

    }

    override fun getBoardItemCount(): Int {
        return board.size
    }

    override fun getBoardItemID(position: Int): ID {
        return board[position].id
    }

    override fun onCreateBoardViewHolder(parent: ViewGroup,
                                         viewType: Int): BoardViewHolder {
        return BoardViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_list, parent, false)
        )
    }

    override fun onBindBoardViewHolder(holder: BoardViewHolder, position: Int) {

        holder.itemView.taskList_rootView.doInBackground {
            updateLayoutParams {
                val percent = holder.itemView.mainActivity
                        .waqtiPreferences.taskListWidth / 100.0

                width = (holder.itemView.mainActivity.dimensions.first.toFloat() * percent)
                        .roundToInt()
            }
        }

        holder.header.doInBackground {
            text = board[position].name
            setOnClickListener {
                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivity.viewModel.listID = board[holder.adapterPosition].id

                    it.clearFocusAndHideSoftKeyboard()

                    addToBackStack(null)
                    replace(R.id.fragmentContainer, ViewListFragment(), VIEW_LIST_FRAGMENT)
                }
            }
        }
        holder.addButton.doInBackground {
            setOnClickListener {

                @GoToFragment
                it.mainActivity.supportFragmentManager.commitTransaction {

                    it.mainActivity.viewModel.boardID = boardID
                    it.mainActivity.viewModel.listID = holder.taskListView.listAdapter.taskListID

                    it.clearFocusAndHideSoftKeyboard()

                    replace(R.id.fragmentContainer, CreateTaskFragment(), CREATE_TASK_FRAGMENT)
                    addToBackStack(null)
                }
            }
        }
        holder.taskListView.doInBackground {
            addOnScrollListener(holder.addButton.verticalFABOnScrollListener)
        }

    }

}*/
