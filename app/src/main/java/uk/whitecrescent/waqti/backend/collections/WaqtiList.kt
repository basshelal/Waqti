package uk.whitecrescent.waqti.backend.collections

import uk.whitecrescent.waqti.FinalSince
import uk.whitecrescent.waqti.WaqtiVersion

/**
 * A [WaqtiList] is a [List] with additional functionality and better function names and return
 * types than the Kotlin [List] and [MutableList] interfaces.
 *
 * Generally if an operation is expected not to return anything such as a [clear] operation then
 * [WaqtiList] will always return the list after the operation was performed. This allows for
 * operation chaining.
 *
 * A [WaqtiList] is essentially a mutable List.
 *
 * See [AbstractWaqtiList] for a complete implementation of a [WaqtiList] which adds integration
 * with the [uk.whitecrescent.waqti.backend.persistence] system
 *
 * @author Bassam Helal
 */
@FinalSince(WaqtiVersion.JULY_2019)
interface WaqtiList<E> : List<E> {

    operator fun plus(element: E): WaqtiList<E>

    operator fun minus(element: E): WaqtiList<E>

    operator fun set(index: Int, element: E): WaqtiList<E>

    operator fun set(oldElement: E, newElement: E): WaqtiList<E>

    operator fun get(element: E): E

    fun add(element: E): WaqtiList<E>

    fun addAt(index: Int, element: E): WaqtiList<E>

    fun addAll(collection: Collection<E>): WaqtiList<E>

    fun addIf(collection: Collection<E>, predicate: (E) -> Boolean): WaqtiList<E>

    fun update(old: E, new: E): WaqtiList<E>

    fun updateAt(oldIndex: Int, newElement: E): WaqtiList<E>

    fun addAllAt(index: Int, collection: Collection<E>): WaqtiList<E>

    fun updateAllTo(collection: Collection<E>, new: E): WaqtiList<E>

    fun updateIf(predicate: (E) -> Boolean, new: E): WaqtiList<E>

    fun remove(element: E): WaqtiList<E>

    fun removeAt(index: Int): WaqtiList<E>

    fun removeAll(collection: Collection<E>): WaqtiList<E>

    fun keepAll(elements: Collection<E>): WaqtiList<E>

    fun removeIf(predicate: (E) -> Boolean): WaqtiList<E>

    fun keepIf(predicate: (E) -> Boolean): WaqtiList<E>

    fun removeRange(fromIndex: Int, toIndex: Int): WaqtiList<E>

    fun move(fromIndex: Int, toIndex: Int): WaqtiList<E>

    fun move(from: E, to: E): WaqtiList<E>

    fun swap(thisIndex: Int, thatIndex: Int): WaqtiList<E>

    fun swap(`this`: E, that: E): WaqtiList<E>

    fun moveAllTo(collection: Collection<E>, toIndex: Int): WaqtiList<E>

    fun clear(): WaqtiList<E>

    fun growTo(size: Int): WaqtiList<E>

    fun countOf(element: E): Int

    fun getAll(collection: Collection<E>): List<E>

    fun containsAny(predicate: (E) -> Boolean): Boolean

    fun sort(comparator: Comparator<E>): WaqtiList<E>

    fun allIndexesOf(element: E): List<Int>

    fun toList(): List<E>

    override fun iterator(): MutableListIterator<E>

}