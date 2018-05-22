package uk.whitecrescent.waqti.collections

interface WaqtiList<E> : WaqtiCollection<E> {

    //New functions

    operator fun set(index: Int, element: E): WaqtiList<E>

    operator fun set(oldElement: E, newElement: E): WaqtiList<E>

    fun addAt(index: Int, element: E): WaqtiList<E>

    fun updateAt(oldIndex: Int, newElement: E): WaqtiList<E>

    fun addAllAt(index: Int, vararg elements: E): WaqtiList<E>

    fun addAllAt(index: Int, collection: Collection<E>): WaqtiList<E>

    fun removeAt(index: Int): WaqtiList<E>

    fun growTo(size: Int): WaqtiList<E>

    fun move(fromIndex: Int, toIndex: Int): WaqtiList<E>

    fun move(from: E, to: E): WaqtiList<E>

    fun swap(thisIndex: Int, thatIndex: Int): WaqtiList<E>

    fun swap(`this`: E, that: E): WaqtiList<E>

    fun moveAllTo(toIndex: Int, vararg elements: E): WaqtiList<E>

    fun moveAllTo(collection: Collection<E>, toIndex: Int): WaqtiList<E>

    fun removeRange(fromIndex: Int, toIndex: Int): WaqtiList<E>

    fun allIndexesOf(element: E): List<Int>

    //region From kotlin.collections.List

    operator fun get(index: Int): E

    fun indexOf(element: E): Int

    fun lastIndexOf(element: E): Int

    fun listIterator(): ListIterator<E>

    fun listIterator(index: Int): ListIterator<E>

    fun subList(fromIndex: Int, toIndex: Int): List<E>

    //endregion From kotlin.collections.List
}