package uk.whitecrescent.waqti.backend.persistence

import android.annotation.SuppressLint
import io.objectbox.Box
import io.reactivex.Observable
import uk.whitecrescent.waqti.CACHE_CHECKING_PERIOD
import uk.whitecrescent.waqti.CACHE_CHECKING_UNIT
import uk.whitecrescent.waqti.backend.Cacheable
import uk.whitecrescent.waqti.backend.Committable
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.debug
import uk.whitecrescent.waqti.doInBackgroundAsync
import uk.whitecrescent.waqti.ids
import uk.whitecrescent.waqti.size
import java.util.concurrent.TimeUnit

open class Cache<E : Cacheable>(
        private val db: Box<E>,
        var sizeLimit: Int = 1000) : Collection<E> {

    private val map = LinkedHashMap<ID, E>(sizeLimit, 0.75F, true)
    private var isChecking = false

    override val size: Int
        get() = map.size

    @QueriesDataBase
    private val isInconsistent: Boolean
        get() = map.asSequence().any { it.key !in db.ids }

    val type: String = db.entityInfo.dbName

    // TODO: 18-Jun-19 This looks like a problem
    fun initialize() {
        doInBackgroundAsync {
            debug("Started initialization for Cache of $type")
            Sequence { db.all.iterator() }
                    .asSequence().take(sizeLimit).forEach {
                        it.initialize()
                        safeAdd(it)
                    }
            debug("Completed initialization for Cache of $type")
            debug("Cache of $type is of size ${size}")
            debug("DB of $type is of size ${db.size}")
        }
    }

    //region Core Modification

    @Throws(ElementNotFoundException::class)
    private fun safeGet(id: ID): E {
        val mapFound = map[id]

        // below queries the DB, we want to reduce this as much as possible
        // we do this by making sure every update made to the db will also be done to the cache
        // we may also let the cache update itself every once in a while to keep the values
        // correct in the cache

        return when {
            @QueriesDataBase
            mapFound == null -> {
                val dbFound = db[id]
                if (dbFound == null) throw ElementNotFoundException(id, cache = type)
                else {
                    safeAdd(dbFound)
                    dbFound
                }
            }
            else -> mapFound
        }
    }

    private fun safeAdd(element: E) {
        map[element.id] = element
    }

    fun put(element: E) {
        val id = db.put(element)
        check(element.id == id)
        safeAdd(element)
    }

    fun put(elements: Collection<E>) {
        db.put(elements)
        elements.forEach {
            check(it.id != 0L)
            safeAdd(it)
        }
    }

    fun remove(id: ID) {
        db.remove(id)
        map.remove(id)
    }

    //endregion Core Modification

    //region Operators

    operator fun get(element: E) =
            this.safeGet(element.id)

    operator fun get(id: ID) =
            this.safeGet(id)

    operator fun get(elements: Collection<E>) =
            elements.map { this[it] }

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

    //endregion Operators

    //region Delegated

    fun remove(element: E) = this.remove(element.id)

    fun remove(elements: Collection<E>) {
        db.remove(elements)
        elements.forEach { map.remove(it.id) }
    }

    fun getByIDs(ids: Collection<ID>) = ids.map { this[it] }

    //could throw exception since it calls safeGet()
    fun idOf(element: E) = this[element].id

    fun idsOf(elements: Collection<E>) =
            elements.map { idOf(it) }

    fun removeIDs(ids: Collection<ID>) =
            this.remove(ids.map { this.get(it) })

    fun removeIf(predicate: () -> Boolean) =
            this.forEach { if (predicate.invoke()) remove(it) }

    fun idList() = map.keys.toList()

    fun valueList() = map.values.toList()

    override fun isEmpty() = map.isEmpty()

    override operator fun iterator() = valueList().iterator()

    override operator fun contains(element: E) = element.id in map

    override fun containsAll(elements: Collection<E>) = elements.all { this.contains(it) }

    //endregion Delegated

    //region Dangerous Bulk Removes

    fun clearMap() = doInBackgroundAsync { map.clear() }

    fun clearDB() = Committable {
        db.removeAll()
    }

    fun clearAll() = Committable {
        clearDB().commit()
        clearMap()
    }

    fun close() {
        stopAsyncCheck()
        clearMap()
    }

    //endregion Dangerous Bulk Removes

    //region Concurrent

    @QueriesDataBase
    fun clean() {
        if (isInconsistent) {
            // removes those in map not in DB but doesn't add anything new to fill up
            map.keys.toList()
                    .filter { it !in db.ids }
                    .forEach { map.remove(it) }
            check(!isInconsistent)
        }
    }

    fun trim() {
        if (size > sizeLimit) {
            map.keys.toList()
                    .filterIndexed { index, _ -> index < (size - sizeLimit) }
                    .forEach { map.remove(it) }
            check(map.size == sizeLimit)
        }
    }

    @SuppressLint("CheckResult")
    fun startAsyncCheck(checkPeriod: Long = CACHE_CHECKING_PERIOD,
                        checkUnit: TimeUnit = CACHE_CHECKING_UNIT): Cache<E> {
        isChecking = true
        Observable.interval(checkPeriod, checkUnit)
                .takeWhile { isChecking }
                .subscribe(
                        {
                            clean()
                            trim()
                        },
                        {
                            throw it
                        }

                )
        return this
    }

    fun stopAsyncCheck(): Cache<E> {
        isChecking = false
        return this
    }

    //endregion Concurrent

    //region Overriden from kotlin.Any

    override fun hashCode() = map.hashCode()

    override fun equals(other: Any?) =
            other is Cache<*> &&
                    other.hashCode() == this.hashCode() &&
                    other.idList() == this.idList()

    override fun toString(): String {
        return map.toString()
    }

    //endregion Overriden from kotlin.Any

}