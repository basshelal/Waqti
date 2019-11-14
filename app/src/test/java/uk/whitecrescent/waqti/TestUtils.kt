@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package uk.whitecrescent.waqti

import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import uk.whitecrescent.threetenabp.at
import uk.whitecrescent.threetenabp.days
import uk.whitecrescent.threetenabp.minutes
import uk.whitecrescent.threetenabp.tomorrow
import uk.whitecrescent.threetenabp.yesterday
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.task.CONSTRAINED
import uk.whitecrescent.waqti.backend.task.HIDDEN
import uk.whitecrescent.waqti.backend.task.Label
import uk.whitecrescent.waqti.backend.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.backend.task.Priority
import uk.whitecrescent.waqti.backend.task.Property
import uk.whitecrescent.waqti.backend.task.SHOWING
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.backend.task.TimeUnit
import uk.whitecrescent.waqti.backend.task.UNMET

// Time

inline val testTimePast: LocalDateTime
    get() = yesterday at 11

inline val testTimeFuture: LocalDateTime
    get() = tomorrow at 11

// Duration

inline val testDuration: Duration
    get() = 7.days

inline val testPriority: Priority
    get() = Priority("Default", 0)

inline val testTimeUnit: TimeUnit
    get() = TimeUnit("DEFAULT", 10.minutes)

// Task

inline val testTask: Task
    get() = Task("TestTask")

// TaskList

inline val testTaskListEmpty: TaskList
    get() = TaskList("TestTaskList")

inline val testTaskListFull: TaskList
    get() = TaskList("TestTaskList", getTasks(10))

// Board

inline val testBoardEmpty: Board
    get() = Board("TestBoard")

inline val testBoardFullOfEmptyLists: Board
    get() = Board("TestBoard", getEmptyTaskLists(10))

inline val testBoardFullOfFullLists: Board
    get() = Board("TestBoard", getFilledTaskLists(10, 10))

// Get Functions

inline fun getFilledBoardList(amountOfBoards: Int, amountOfLists: Int, amountOfTasks: Int): BoardList {
    val boardList = BoardList("TestBoardList")
    boardList.addAll(getBoardsFullOfFullLists(amountOfBoards, amountOfLists, amountOfTasks)).update()
    return boardList
}

inline fun getBoardsFullOfFullLists(amountOfBoards: Int, amountOfLists: Int, amountOfTasks: Int) =
        Array(amountOfBoards) {
            Board("TestBoard # ${it + 1}", getFilledTaskLists(amountOfLists, amountOfTasks))
        }.asList()

inline fun getBoardFullOfFullLists(amountOfLists: Int, amountOfTasks: Int) =
        Board("TestBoard", getFilledTaskLists(amountOfLists, amountOfTasks))

inline fun getBoardFullOfEmptyLists(amountOfLists: Int) =
        Board("TestBoard", getEmptyTaskLists(amountOfLists))

inline fun getBoardsFullOfEmptyLists(amountOfBoards: Int, amountOfLists: Int) =
        Array(amountOfBoards) {
            Board("TestBoard # ${it + 1}", getEmptyTaskLists(amountOfLists))
        }.asList()

inline fun getEmptyBoards(amount: Int) = Array(amount) { Board("TestBoard") }

inline fun getEmptyTaskLists(amount: Int) =
        Array(amount) { TaskList("TestTask # ${it + 1}") }.asList()

inline fun getFilledTaskList(amountOfTasks: Int) =
        TaskList("TestTaskList", getTasks(amountOfTasks))

inline fun getFilledTaskLists(amountOfLists: Int, amountOfTasks: Int) =
        Array(amountOfLists) { getFilledTaskList(amountOfTasks) }.asList()

inline fun getTasks(amount: Int) = Array(amount) { Task("TestTask # ${it + 1}") }.asList()

inline fun getLabels(amount: Int) = Array(amount) { Label("Label # ${it + 1}") }.asList()

// Other

inline fun sleep(seconds: Int) = Thread.sleep((seconds) * 1000L)

inline fun sleep(duration: Duration) = Thread.sleep(duration.toMillis())

inline fun <T> message(expected: T, actual: T) {
    println("Expected:\t${expected.toString()} \nActual:\t\t${actual.toString()}")
}

inline fun <V> simpleProperty(value: V): Property<V> {
    return Property(value = value)
}

inline fun <V> constraintProperty(value: V): Property<V> {
    return Property(
            isVisible = SHOWING,
            value = value,
            isConstrained = CONSTRAINED,
            isMet = UNMET
    )
}

inline fun <V> hiddenProperty(value: V): Property<V> {
    return Property(
            isVisible = HIDDEN,
            value = value,
            isConstrained = NOT_CONSTRAINED,
            isMet = UNMET
    )
}

// Global variable to check if the DB has been built, don't remove!
var DB_BUILT = false