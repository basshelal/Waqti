@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package uk.whitecrescent.waqti.extensions

import com.google.gson.Gson
import io.objectbox.Box
import uk.whitecrescent.waqti.backend.Cacheable
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Label
import uk.whitecrescent.waqti.backend.task.Priority
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.backend.task.Template
import uk.whitecrescent.waqti.backend.task.TimeUnit

inline val <T> Box<T>.size: Int
    get() = this.count().toInt()

// This is slow because Reflection
inline operator fun <reified T : Cacheable> Caches.get(id: ID): T {
    return when (T::class) {
        Task::class -> Caches.tasks[id] as T
        Template::class -> Caches.templates[id] as T
        Label::class -> Caches.labels[id] as T
        Priority::class -> Caches.priorities[id] as T
        TimeUnit::class -> Caches.timeUnits[id] as T
        TaskList::class -> Caches.taskLists[id] as T
        Board::class -> Caches.boards[id] as T
        BoardList::class -> Caches.boardLists[id] as T
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <reified T : Cacheable> Caches.put(element: T) {
    when (element) {
        is Task -> Caches.tasks.put(element)
        is Template -> Caches.templates.put(element)
        is Label -> Caches.labels.put(element)
        is Priority -> Caches.priorities.put(element)
        is TimeUnit -> Caches.timeUnits.put(element)
        is TaskList -> Caches.taskLists.put(element)
        is Board -> Caches.boards.put(element)
        is BoardList -> Caches.boardLists.put(element)
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <reified T : Cacheable> Box<T>.archive(element: T) {
    when (element) {
        is Task -> Caches.tasks.put(element)
        is Template -> Caches.templates.put(element)
        is Label -> Caches.labels.put(element)
        is Priority -> Caches.priorities.put(element)
        is TimeUnit -> Caches.timeUnits.put(element)
        is TaskList -> Caches.taskLists.put(element)
        is Board -> Caches.boards.put(element)
        is BoardList -> Caches.boardLists.put(element)
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <reified T : Cacheable> Box<T>.restore(element: T) {
    when (element) {
        is Task -> Caches.tasks.put(element)
        is Template -> Caches.templates.put(element)
        is Label -> Caches.labels.put(element)
        is Priority -> Caches.priorities.put(element)
        is TimeUnit -> Caches.timeUnits.put(element)
        is TaskList -> Caches.taskLists.put(element)
        is Board -> Caches.boards.put(element)
        is BoardList -> Caches.boardLists.put(element)
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline val <T : Cacheable> Box<T>.ids: List<ID>
    get() = this.all.map { it.id }

val GSON = Gson()

inline val <T>T.toJson: String get() = GSON.toJson(this)

inline infix fun <T> String?.fromJsonTo(clss: Class<T>): T = GSON.fromJson(this, clss)

const val CACHE_CHECKING_PERIOD = 10L
val CACHE_CHECKING_UNIT = java.util.concurrent.TimeUnit.SECONDS