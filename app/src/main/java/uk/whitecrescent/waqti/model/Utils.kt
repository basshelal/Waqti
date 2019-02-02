@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.model

import uk.whitecrescent.waqti.Duration
import uk.whitecrescent.waqti.model.collections.Tuple
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.DEBUG
import uk.whitecrescent.waqti.model.task.GRACE_PERIOD
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task
import java.util.Objects

inline fun debug(string: String) {
    if (DEBUG) println(string)
}

inline fun setGracePeriod(duration: Duration) {
    GRACE_PERIOD = duration
}

inline fun hash(vararg elements: Any?) = Objects.hash(*elements)

// Extensions

inline val <E> Collection<E>.toArrayList: ArrayList<E>
    get() {
        return ArrayList(this)
    }

inline val Collection<Cacheable>.ids: List<ID>
    get() = this.map { it.id }

inline val Collection<ID>.tasks: List<Task>
    get() = Caches.tasks.getByIDs(this)

inline val Collection<Tuple>.tasks: Array<Task>
    get() {
        val result = ArrayList<Task>(this.size)
        for (tuple in this) {
            result.addAll(tuple.toList())
        }
        return result.toTypedArray()
    }