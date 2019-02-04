@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package uk.whitecrescent.waqti

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.BoardList
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import uk.whitecrescent.waqti.model.task.UNMET

// Time

inline val testTimePast: Time
    get() = yesterday at 11

inline val testTimeFuture: Time
    get() = tomorrow at 11

// Duration

inline val testDuration: Duration
    get() = 7.days

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

// This is slow because Reflection
inline operator fun <reified T : Cacheable> Caches.get(id: ID): T {
    return when (T::class) {
        Task::class -> Caches.tasks[id] as T
        Template::class -> Caches.labels[id] as T
        Label::class -> Caches.labels[id] as T
        Priority::class -> Caches.priorities[id] as T
        TimeUnit::class -> Caches.timeUnits[id] as T

        TaskList::class -> Caches.taskLists[id] as T
        Board::class -> Caches.boards[id] as T
        BoardList::class -> Caches.boardLists[id] as T
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

// Global variable to check if the DB has been built, don't remove!
var DB_BUILT = false