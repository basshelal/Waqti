package uk.whitecrescent.waqti.frontend.customview.recyclerviews

import android.view.ViewGroup
import uk.whitecrescent.waqti.NewAPI

// TODO: 15-Jun-19 This Adapter should be used to keep any UI code in the views and any back end
//  code in the adapter, we take care of UI stuff, this guy does any other stuff
@NewAPI
abstract class BoardViewAdapter {

    // Board

    abstract fun getBoardItemCount(): Int

    abstract fun onCreateBoardViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder

    abstract fun onBindBoardViewHolder(holder: BoardViewHolder, position: Int)

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

    abstract fun getTaskItemCount(): Int

    abstract fun onCreateTaskViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder

    abstract fun onBindTaskViewHolder(holder: TaskViewHolder, position: Int)

    open fun onDragTaskInSameList(oldTaskViewHolder: TaskViewHolder,
                                  newTaskViewHolder: TaskViewHolder) {
    }

    open fun onTaskScrollDown(draggedViewHolder: TaskViewHolder,
                              draggingOverViewHolder: TaskViewHolder,
                              taskListView: TaskListView) {
    }

    open fun onTaskScrollUp(draggedViewHolder: TaskViewHolder,
                            draggingOverViewHolder: TaskViewHolder,
                            taskListView: TaskListView) {
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