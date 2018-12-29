@file:Suppress("NOTHING_TO_INLINE", "unused")

package uk.whitecrescent.waqti

import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Task

// Time

val testTimePast: Time
    get() = yesterday at 11

val testTimeFuture: Time
    get() = tomorrow at 11

// Task

val testTask: Task
    get() = Task("TestTask")

// TaskList

val testTaskListEmpty: TaskList
    get() = TaskList("TestTaskList")

val testTaskListFull: TaskList
    get() = TaskList("TestTaskList", getTasks(10))

// Board

val testBoardEmpty: Board
    get() = Board("TestBoard")

val testBoardFullOfEmptyLists: Board
    get() = Board("TestBoard", getEmptyTaskLists(10))

val testBoardFullOfFullLists: Board
    get() = Board("TestBoard", getFilledTaskLists(10, 10))

// Get Functions

inline fun getBoardsFullOfFullLists(amountOfBoards: Int, amountOfLists: Int, amountOfTasks: Int) =
        Array(amountOfBoards) {
            Board("TestBoard # ${it + 1}", getFilledTaskLists(amountOfLists, amountOfTasks))
        }

inline fun getBoardFullOfFullLists(amountOfLists: Int, amountOfTasks: Int) =
        Board("TestBoard", getFilledTaskLists(amountOfLists, amountOfTasks))

inline fun getBoardFullOfEmptyLists(amountOfLists: Int) =
        Board("TestBoard", getEmptyTaskLists(amountOfLists))

inline fun getBoardsFullOfEmptyLists(amountOfBoards: Int, amountOfLists: Int) =
        Array(amountOfBoards) {
            Board("TestBoard # ${it + 1}", getEmptyTaskLists(amountOfLists))
        }.toList()

inline fun getEmptyBoards(amount: Int) = Array(amount) { Board("TestBoard") }

inline fun getEmptyTaskLists(amount: Int) =
        Array(amount) { TaskList("TestTask # ${it + 1}") }.toList()

inline fun getFilledTaskList(amountOfTasks: Int) =
        TaskList("TestTaskList", getTasks(amountOfTasks))

inline fun getFilledTaskLists(amountOfLists: Int, amountOfTasks: Int) =
        Array(amountOfLists) { getFilledTaskList(amountOfTasks) }.toList()

inline fun getTasks(amount: Int) = Array(amount) { Task("TestTask # ${it + 1}") }.toList()

inline fun getLabels(amount: Int) = Array(amount) { Label("Label # ${it + 1}") }.toList()

// Other

fun after(duration: Duration, func: () -> Any) {
    Thread.sleep(duration.toMillis())
    func.invoke()
}

inline fun sleep(duration: Duration) {
    Thread.sleep(duration.toMillis())
}

inline fun <T> message(expected: T, actual: T) {
    println("Expected:\t${expected.toString()} \nActual:\t\t${actual.toString()}")
}

// Global variable to check if the DB has been built, don't remove!
var DB_BUILT = false