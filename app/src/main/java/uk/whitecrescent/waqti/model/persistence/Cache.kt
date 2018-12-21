package uk.whitecrescent.waqti.model.persistence

import android.annotation.SuppressLint
import io.objectbox.Box
import io.reactivex.Observable
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.Committable
import uk.whitecrescent.waqti.model.task.ID
import java.util.concurrent.TimeUnit

// TODO: 28-Jul-18 Test and doc

// TODO: 04-Dec-18 Make this thread safe!

// TODO: 20-Dec-18 Revise the practicality of this, why not use a list of IDs instead???
open class Cache<E : Cacheable>(
        private val db: Box<E>,
        var sizeLimit: Int = 1000) : Collection<E> {

    private val map = LinkedHashMap<ID, E>(100, 0.75F, true)
    private var isChecking = false

    val all: List<E>
        get() = db.all

    override val size: Int
        get() = map.size

    @QueriesDataBase
    private val isInconsistent: Boolean
        get() = !map.all { it.key in db.ids }

    @SuppressLint("CheckResult")
    fun initialize() {

        // TODO: 12-Dec-18 Cannot be async because of deadlock!
        // some Caches require that other Caches be initialized first

        println("Started initialization for Cache of ${db.entityInfo.dbName}")

        db.all.take(sizeLimit).forEach {
            it.initialize()
            this.safeAdd(it)
        }

        println("Completed initialization for Cache of ${db.entityInfo.dbName}")
        println("Cache of ${db.entityInfo.dbName} is of size ${this.size}")
        println("DB of ${db.entityInfo.dbName} is of size ${db.size}")

        /*Observable
                .fromIterable(db.all)
                .take(sizeLimit.toLong())
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            logE("$now")
                            (it as? Task)?.backgroundObserver()
                            this.safeAdd(it)
                        },
                        {

                        },
                        {
                            println("Completed initialization for Cache of ${db.entityInfo.dbName}")
                            println("Cache of ${db.entityInfo.dbName} is of size ${this.size}")
                            println("DB of ${db.entityInfo.dbName} is of size ${db.size}")
                        },
                        {
                            println("Started initialization for Cache of ${db.entityInfo.dbName}")
                        }
                )*/
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
                if (dbFound == null) throw ElementNotFoundException(id)
                else {
                    safeAdd(dbFound)
                    dbFound
                }
            }
            @QueriesDataBase
            map.size != db.size -> {
                clean()
                safeGet(id)
            }
            else -> mapFound
        }
    }

    private fun safeAdd(element: E) {
        map[element.id] = element
    }

    fun put(element: E) {
        val id = db.put(element)
        assert(element.id == id)
        safeAdd(element)
    }

    fun put(elements: Collection<E>) {
        db.put(elements)
        elements.forEach {
            assert(it.id != 0L)
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

    fun clearMap() = this.map.clear()

    fun clearDB() = object : Committable {
        override fun commit() {
            db.removeAll()
        }
    }

    fun clearAll(): Committable {
        return object : Committable {
            override fun commit() {
                clearDB().commit()
                clearMap()
            }
        }
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
            assert(!isInconsistent)
        }
    }

    fun trim() {
        if (size > sizeLimit) {
            map.keys.toList()
                    //.reversed()
                    .filterIndexed { index, _ -> index < (size - sizeLimit) }
                    .forEach { map.remove(it) }
            assert(map.size == sizeLimit)
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
                            // TODO: 04-Dec-18 do this
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