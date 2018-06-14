package uk.whitecrescent.waqti.model.collections

interface WaqtiCollection<E> : Collection<E> {

    operator fun plus(element: E): WaqtiCollection<E>

    operator fun minus(element: E): WaqtiCollection<E>

    operator fun get(element: E): E

    fun add(element: E): WaqtiCollection<E>

    fun addAll(vararg elements: E): WaqtiCollection<E>

    fun addAll(collection: Collection<E>): WaqtiCollection<E>

    fun update(old: E, new: E): WaqtiCollection<E>

    fun updateAllTo(collection: Collection<E>, new: E): WaqtiCollection<E>

    fun updateIf(predicate: (E) -> Boolean, new: E): WaqtiCollection<E>

    fun removeFirst(element: E): WaqtiCollection<E>

    fun removeAll(vararg elements: E): WaqtiCollection<E>

    fun removeAll(collection: Collection<E>): WaqtiCollection<E>

    fun removeIf(predicate: (E) -> Boolean): WaqtiCollection<E>

    fun addIf(collection: Collection<E>, predicate: (E) -> Boolean): WaqtiCollection<E>

    fun countOf(element: E): Int

    fun getAll(vararg elements: E): List<E>

    fun getAll(collection: Collection<E>): List<E>

    fun containsAny(predicate: (E) -> Boolean): Boolean

    fun clear(): WaqtiCollection<E>

    fun sort(comparator: Comparator<E>): WaqtiCollection<E>

    fun toList(): List<E>

    fun containsAll(vararg elements: E): Boolean
}