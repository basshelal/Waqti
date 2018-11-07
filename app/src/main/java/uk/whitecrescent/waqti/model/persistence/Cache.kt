package uk.whitecrescent.waqti.model.persistence

import io.objectbox.Box
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.task.ID
import java.util.concurrent.ConcurrentHashMap

// TODO: 28-Jul-18 Test and doc

// TODO: 07-Nov-18 Fix the interactions between this and DB and runtime entities!!!

// no guarantee for order!
open class Cache<E : Cacheable>(private val db: Box<E>) : Collection<E> {

    private val map = ConcurrentHashMap<ID, E>()

    override val size: Int
        get() = map.size

    fun newID(): ID {
        return db.all.maxBy { it.id }?.id?.plus(1) ?: 1
    }

    // Creates if doesn't exist, updates if does
    open fun put(element: E) {
        map[element.id] = element
        //db.put(element)
    }

    fun put(elements: Collection<E>) {
        elements.forEach { map[it.id] = it }
        //db.put(elements)
    }

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

    fun getByIDs(ids: Collection<ID>) =
            ids.map { this[it] }

    fun idOf(element: E): ID {
        if (element !in this) throw CacheElementNotFoundException(element.id, element)
        else return this[element].id
    }

    fun idsOf(elements: Collection<E>) =
            elements.map { idOf(it) }

    open fun remove(id: ID) {
        map.remove(id)
        db.remove(id)
    }

    fun remove(element: E) {
        this.remove(element.id)
    }

    fun remove(elements: Collection<E>) {
        elements.forEach { map.remove(it.id) }
        db.remove(elements)
    }

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

    protected open fun safeGet(id: ID): E {
        val mapFound = map[id]
        val dbFound = db[id]

        if (mapFound == null) throw  CacheElementNotFoundException(id)
        return mapFound

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

    // not slow for 10_000!
    private fun updateMap(amount: Int = size, throwIfGreater: Boolean = false) {
        if (size != 0) {
            if (amount < 1 || (amount > size && throwIfGreater))
                throw IllegalArgumentException("Amount cannot be greater than $size or less than 1")

            var end = amount - 1

            if (amount > size && !throwIfGreater) {
                end = size - 1
            }
            db.all.subList(0, end).forEach { map[it.id] = it }
        }
    }

}