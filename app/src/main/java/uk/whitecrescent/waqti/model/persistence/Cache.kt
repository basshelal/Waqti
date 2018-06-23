package uk.whitecrescent.waqti.model.persistence

import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.task.ID
import java.util.Random
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Cache<E : Cacheable> : Collection<E> {

    private val db = ConcurrentHashMap<ID, E>()

    override val size: Int
        get() = db.size

    fun newID(): ID {
        var id = Math.abs(Random().nextLong())
        while (db.containsKey(id)) {
            id = Math.abs(Random().nextLong())
        }
        return id
    }

    // Creates if doesn't exist, updates if does
    fun put(element: E) {
        db[element.id] = element
    }

    fun put(elements: Collection<E>) =
            elements.forEach { this.put(it) }

    operator fun set(id: ID, element: E) {
        db[id] = element
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
        if (db.containsKey(id)) db.remove(id)
    }

    fun remove(element: E) =
            this.remove(element.id)

    fun remove(elements: Collection<E>) =
            elements.forEach { this.remove(it) }

    fun removeIDs(ids: Collection<ID>) =
            ids.forEach { this.remove(it) }

    fun removeIf(predicate: () -> Boolean) =
            db.forEach { if (predicate.invoke()) remove(it.value) }

    fun clear() = db.clear()

    fun query() = db.values.toList()

    fun toImmutableMap() = db.toMap()

    override fun isEmpty() = db.isEmpty()

    override operator fun iterator() = db.values.iterator()

    override operator fun contains(element: E) = element in db

    override fun containsAll(elements: Collection<E>) = elements.all { this.contains(it) }

    override fun hashCode() = db.hashCode()

    override fun equals(other: Any?) =
            other is Cache<*> &&
                    other.hashCode() == this.hashCode() &&
                    other.toImmutableMap() == this.toImmutableMap()

    override fun toString(): String {
        return db.toString()
    }

    protected fun safeGet(id: ID): E {
        val found = db[id]
        if (found == null) throw CacheElementNotFoundException(id)
        else return found
    }

}