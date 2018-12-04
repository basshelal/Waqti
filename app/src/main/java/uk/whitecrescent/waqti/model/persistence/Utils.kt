@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.model.persistence

import io.objectbox.Box
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.task.ID
import java.util.concurrent.TimeUnit

inline val <T> Box<T>.size: Int
    get() = this.count().toInt()

inline fun <T> Box<T>.isEmpty() = this.count() == 0L

inline fun <T> Box<T>.forEach(action: (T) -> Unit) =
        this.all.forEach(action)

inline val <T : Cacheable> Box<T>.ids: List<ID>
    get() = this.all.map { it.id }

const val CACHE_CHECKING_PERIOD = 10L
val CACHE_CHECKING_UNIT = TimeUnit.SECONDS