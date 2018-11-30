@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.model

import android.util.Log
import io.objectbox.Box
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.cache2k.Cache
import uk.whitecrescent.waqti.model.collections.Tuple
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.task.GRACE_PERIOD
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Task
import java.util.Objects

inline fun sleep(seconds: Int) = Thread.sleep((seconds) * 1000L)

inline fun <T> logD(t: T) {
    Log.d("DEFAULT", t.toString())
}

inline fun <T> logI(t: T) {
    Log.i("DEFAULT", t.toString())
}

inline fun <T> logE(t: T) {
    Log.e("DEFAULT", t.toString())
}

inline fun setGracePeriod(duration: Duration) {
    GRACE_PERIOD = duration
}

inline fun hash(vararg elements: Any) =
        Objects.hash(*elements)

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

fun async(func: () -> Any) {
    Observable.fromCallable(func)
            .subscribeOn(Schedulers.newThread())
            .subscribe()
}

// Persistence Extensions

val <T> Box<T>.size: Int
    get() = this.count().toInt()

fun <T> Box<T>.isEmpty() = this.count() == 0L

fun <T> Box<T>.forEach(action: (T) -> Unit) =
        this.all.forEach(action)

val <K, V> Cache<K, V>.size: Int
    get() = this.keys().count()

val <K, V> Cache<K, V>.values: List<V>
    get() = this.entries().map { it.value }

operator fun <K, V> Cache<K, V>.set(key: K, value: V) =
        this.put(key, value)

fun <V : Cacheable> Cache<ID, V>.putAll(collection: Collection<V>) {
    this.putAll(collection.map { it.id to it }.toMap())
}

fun <V : Cacheable> Cache<ID, V>.removeAll(collection: Collection<V>) {
    this.removeAll(collection.map { it.id })
}

fun <K, V> Cache<K, V>.isEmpty() = this.size == 0

operator fun <V : Cacheable> Cache<ID, V>.contains(element: V) =
        this.containsKey(element.id)

val <V : Cacheable> Cache<ID, V>.map: Map<ID, V>
    get() = this.entries().map { it.key to it.value }.toMap()

inline infix fun <A, B> List<A>.to(other: List<B>): Map<A, B> {
    return this.mapIndexed { index, a -> a to other[index] }.toMap()
}