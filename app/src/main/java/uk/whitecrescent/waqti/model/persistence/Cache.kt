package uk.whitecrescent.waqti.model.persistence

import android.annotation.SuppressLint
import io.objectbox.Box
import io.reactivex.Observable
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.forEach
import uk.whitecrescent.waqti.model.size
import uk.whitecrescent.waqti.model.task.ID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

// TODO: 28-Jul-18 Test and doc

// TODO: 08-Nov-18 Is this thread safe?
// no guarantee for order!
open class Cache<E : Cacheable>(private val db: Box<E>) : Collection<E> {

    private val map = ConcurrentHashMap<ID, E>()
    private var isChecking = false

    override val size: Int
        get() = map.size

    fun put(element: E) {
        val id = db.put(element)
        assert(element.id == id)
        map[id] = element
    }

    fun put(elements: Collection<E>) {
        db.put(elements)
        elements.forEach { assert(it.id != 0L) }
        elements.forEach { map[it.id] = it }
    }

    operator fun get(element: E) =
            this.safeGet(element.id)

    operator fun get(id: ID) =
            this.safeGet(id)

    operator fun get(elements: Collection<E>) =
            elements.map { this[it] }

    fun remove(id: ID) {
        db.remove(id)
        map.remove(id)
    }

    fun remove(element: E) {
        this.remove(element.id)
    }

    fun remove(elements: Collection<E>) {
        db.remove(elements)
        elements.forEach { map.remove(it.id) }
    }

    operator fun plus(element: E): Cache<E> {
        this.put(element)
        return this
    }

    operator fun plus(elements: Collection<E>): Cache<E> {
        this.put(elements)
        return this
    }

    operator fun minus(element: E): Cache<E> {
        this.remove(element)
        return this
    }

    operator fun minus(elements: Collection<E>): Cache<E> {
        this.remove(elements)
        return this
    }

    fun getByIDs(ids: Collection<ID>) =
            ids.map { this[it] }

    fun idOf(element: E): ID {
        return this[element].id
        // above will throw exception since it calls safeGet()
    }

    fun idsOf(elements: Collection<E>) =
            elements.map { idOf(it) }

    fun removeIDs(ids: Collection<ID>) =
            this.remove(ids.map { this.get(it) })

    fun removeIf(predicate: () -> Boolean) =
            map.forEach { if (predicate.invoke()) remove(it.value) }

    fun clear() = map.clear()

    fun query() = map.values.toList()

    fun toImmutableMap() = map.toMap()

    override fun isEmpty() = map.isEmpty()

    override operator fun iterator() = map.values.iterator()

    override operator fun contains(element: E) = element in map

    override fun containsAll(elements: Collection<E>) = elements.all { this.contains(it) }

    override fun hashCode() = map.hashCode()

    override fun equals(other: Any?) =
            other is Cache<*> &&
                    other.hashCode() == this.hashCode() &&
                    other.toImmutableMap() == this.toImmutableMap()

    override fun toString(): String {
        return map.toString()
    }

    @Throws(CacheElementNotFoundException::class)
    protected fun safeGet(id: ID): E {
        val mapFound = map[id]
        //val dbFound = db.get(id)

        if (mapFound == null) throw  CacheElementNotFoundException(id)
        return mapFound

        // below queries the DB, we want to reduce this as much as possible
        // we do this by making sure every update made to the db will also be done to the cache
        // we may also let the cache update itself every once in a while to keep the values
        // correct in the cache

//        return when {
//            mapFound == null -> {
//                if (dbFound == null) throw CacheElementNotFoundException(id)
//                else {
//                    map[dbFound.id] = dbFound
//                    return dbFound
//                }
//            }
//            mapFound != dbFound -> {
//                map[dbFound.id] = dbFound
//                dbFound
//            }
//            else -> mapFound
//        }
    }

    private val isInconsistent: Boolean
        get() {
            if (map.size != db.size) {
                if (map.values.sortedBy { it.id } != db.all.sortedBy { it.id }) {
                    return true
                }
            }
            return false
        }

    // not slow for 10_000!
    // only executes something if the map and db are different for whatever reason
    fun update() {
        if (isInconsistent) {
            map.clear()
            db.forEach { map[it.id] = it }
        }
    }

    // if designed right, we will never need this implementation!
    @SuppressLint("CheckResult")
    fun startAsyncCheck(checkSeconds: Long = 30L) {
        isChecking = true
        Observable.interval(checkSeconds, TimeUnit.SECONDS)
                .takeWhile { isChecking }
                .subscribe {
                    update()
                }
    }

    fun stopAsyncCheck() {
        isChecking = false
    }

}