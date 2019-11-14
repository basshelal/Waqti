@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package uk.whitecrescent.waqti.extensions

import org.threeten.bp.LocalDateTime
import uk.whitecrescent.waqti.backend.Cacheable
import uk.whitecrescent.waqti.backend.collections.Tuple
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME
import uk.whitecrescent.waqti.backend.task.Description
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Property
import uk.whitecrescent.waqti.backend.task.Task

inline val <E> Collection<E>.toArrayList: ArrayList<E> get() = ArrayList(this)

inline val Collection<Cacheable>.ids: List<ID> get() = this.map { it.id }

inline val Collection<ID>.tasks: List<Task> get() = Caches.tasks.getByIDs(this)

inline val Collection<Tuple>.tasks: Array<Task> get() = this.flatMap { it.toList() }.toTypedArray()

inline val <T> Property<T>.isNotConstrained: Boolean get() = !this.isConstrained

inline val <T> Property<T>.isUnMet: Boolean get() = !this.isMet

inline val <T> Property<T>.isHidden: Boolean get() = !this.isVisible

inline val LocalDateTime.isDefault: Boolean get() = this == DEFAULT_TIME

inline val LocalDateTime.isNotDefault: Boolean get() = this != DEFAULT_TIME

inline val Description.isDefault: Boolean get() = this == DEFAULT_DESCRIPTION

inline val Description.isNotDefault: Boolean get() = this != DEFAULT_DESCRIPTION