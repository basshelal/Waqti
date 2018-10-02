package uk.whitecrescent.waqti.model.persistence

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.task.ID
import java.util.Random
import java.util.concurrent.ConcurrentHashMap

// TODO: 28-Jul-18 Test and doc

// no guarantee for order!
open class Cache<E : Cacheable> : Collection<E> {

    protected val map = ConcurrentHashMap<ID, E>()

    override val size: Int
        get() = map.size

    fun newID(): ID {
        var id = Math.abs(Random().nextLong())
        while (map.containsKey(id)) {
            id = Math.abs(Random().nextLong())
        }
        return id
    }

    // Creates if doesn't exist, updates if does
    open fun put(element: E) {
        map[element.id] = element
    }

    fun put(elements: Collection<E>) =
            elements.forEach { this.put(it) }

    operator fun set(id: ID, element: E) {
        map[id] = element
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
        if (map.containsKey(id)) map.remove(id)
    }

    fun remove(element: E) =
            this.remove(element.id)

    fun remove(elements: Collection<E>) =
            elements.forEach { this.remove(it) }

    fun removeIDs(ids: Collection<ID>) =
            ids.forEach { this.remove(it) }

    fun removeIf(predicate: () -> Boolean) =
            map.forEach { if (predicate.invoke()) remove(it.value) }

    fun clear() = map.clear()

    fun query() = map.values.toList()

    fun toImmutableMap() = map.toMap()

    fun toSortedMap() = map.toSortedMap() //useful??

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
        val found = map[id]
        if (found == null) throw CacheElementNotFoundException(id)
        else return found
    }

}