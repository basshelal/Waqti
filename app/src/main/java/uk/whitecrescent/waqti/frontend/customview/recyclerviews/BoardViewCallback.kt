package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import uk.whitecrescent.waqti.backend.task.ID

open class BoardViewCallback {

    // Board

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