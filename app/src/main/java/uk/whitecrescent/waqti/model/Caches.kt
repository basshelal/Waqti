package uk.whitecrescent.waqti.model

import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

@Suppress("unused")
/*
 * The intent of this class is to contain all the objects we need in memory, literally a cache, what it has to do is
 * the following.
 * Be able to read the main persistence (Database, will differ by platform) then load the necessary (and only the
 * necessary) objects from persistence to ConcurrentHashMaps inside this class which we can then use to quickly
 * access the objects we need, not needing to request to read the database all the time, the issue will come with
 * automatic updating which actually we can do here and is one of the main purposes of this class, we can have a few
 * observers check to see that the objects correspond to their equivalent in the persistent database, if not then
 * we update the persistent database with what's in the cache because it implies that there was some update called
 * or done, this saves us from having to mess with the persistent database directly very often or even better, stops
 * us from having an inconsistency between memory and database making us not have to call 2 updates (1 for memory
 * and 1 for persistence).
 *
 * The point is that this class will abstract away all of that, making it seem like all we have to do is just update
 * the memory.
 *
 * There probably will be potential issues with this, concurrency being one, consistency being the other, who's the
 * source of truth etc etc, but the benefits are quite great so this is worth it but it needs to be tested and done
 * with care
 *
 * This also stops us from having to update a million things when the database implementation changes, we would just
 * need to change this class
 *
 * In summary the Caches is then just a middle man between the code (CRUDs) and the persistence database mainly for
 * the reason to have database independence/ modularity and minimize database operations directly, instead delegating
 * them to be done here
 *
 * Long note but well worth it, it's a good idea.
 *
 * Bassam Helal Mon-14-May-18
 *
 * We can put the persistence database operations into a queue maybe (not entirely sure why though)
 * Bassam Helal Mon-21-May
 *
 * */
object Caches {

    val tasks = Cache<Task>()
    val templates = Cache<Template>()
    val labels = Cache<Label>()
    val priorities = Cache<Priority>()
    val timeUnits = Cache<TimeUnit>()
    // TODO: 14-May-18 Collections stuff maybe but later, probably using IDs a lot

}

@Suppress("unused")
/*
 * Going to be the base class of all little Caches, change the object to be Caches, it will contain
 * instances of many caches
 * Implements Collection so you get all the free goodies from Kotlin stdlib on Iterable and
 * Collection
 */
class Cache<E : Cacheable> : Collection<E> {

    private val db = ConcurrentHashMap<ID, E>()

    override val size: Int
        get() = db.size

    fun newID() = db.newID()

    // Creates if doesn't exist, updates if does
    fun put(element: E) {
        db[element.id()] = element
    }

    fun put(elements: Collection<E>) =
            elements.forEach { this.put(it) }

    operator fun get(element: E) =
            db.safeGet(element.id())

    operator fun get(id: ID) =
            db.safeGet(id)

    operator fun get(elements: Collection<E>) =
            elements.map { this[it] }

    fun getByIDs(ids: Collection<ID>) =
            ids.map { this[it] }

    operator fun plus(element: E) = this.put(element)

    operator fun plus(elements: Collection<E>) = this.put(elements)

    operator fun minus(element: E) = this.remove(element)

    operator fun minus(elements: Collection<E>) = this.remove(elements)

    operator fun not() {}

    fun idOf(element: E): ID {
        if (element !in this) throw CacheElementNotFoundException(element.id())
        else return this[element].id()
    }

    fun idsOf(elements: Collection<E>) =
            elements.map { idOf(it) }

    operator fun set(id: ID, element: E) {
        db[id] = element
    }

    fun remove(id: ID) {
        if (db.containsKey(id)) db.remove(id)
    }

    fun remove(element: E) =
            this.remove(element.id())

    fun remove(elements: Collection<E>) =
            elements.forEach { this.remove(it) }

    fun removeIDs(ids: Collection<ID>) =
            ids.forEach { this.remove(it) }

    fun removeIf(predicate: () -> Boolean) =
            db.forEach { if (predicate.invoke()) remove(it.value) }

    fun clear() = db.clear()

    override fun isEmpty() = db.isEmpty()

    override operator fun iterator() = db.values.iterator()

    override operator fun contains(element: E) = element in db

    override fun containsAll(elements: Collection<E>) = elements.all { this.contains(it) }

    fun query() = ArrayList(db.values).toList()

    fun toImmutableMap() = db.toMap()

}


//region Extensions

fun <V : Cacheable> ConcurrentHashMap<ID, V>.safeGet(id: ID): V {
    val found = this[id]
    if (found == null) throw CacheElementNotFoundException(id)
    else return found
}

fun <V : Cacheable> ConcurrentHashMap<ID, V>.newID(): ID {
    var id = Math.abs(Random().nextLong())
    while (this.containsKey(id)) {
        id = Math.abs(Random().nextLong())
    }
    return id
}

operator fun <V : Cacheable> ConcurrentHashMap<ID, V>.get(value: V): V {
    val found = this[value.id()]
    if (found == null) throw CacheElementNotFoundException(value.id())
    else return found
}

operator fun <V : Cacheable> ConcurrentHashMap<ID, V>.set(old: V, new: V) {
    this[old.id()] = new
}

operator fun <V : Cacheable> ConcurrentHashMap<ID, V>.plus(value: V) {
    this[value.id()] = value
}

operator fun <V : Cacheable> ConcurrentHashMap<ID, V>.plus(collection: Collection<V>) {
    collection.forEach { this.putIfAbsent(it.id(), it) }
}

operator fun <V : Cacheable> ConcurrentHashMap<ID, V>.minus(value: V) {
    this.remove(value.id())
}

operator fun <V : Cacheable> ConcurrentHashMap<ID, V>.minus(collection: Collection<V>) {
    collection.forEach { this.remove(it.id()) }
}

//endregion Extensions