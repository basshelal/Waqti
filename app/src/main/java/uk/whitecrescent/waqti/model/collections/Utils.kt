package uk.whitecrescent.waqti.model.collections

fun <T> MutableList<T>.matchOrder(other: Collection<T>) {
        val pairs = other.mapIndexed { index, it -> index to it }.toMap()
        pairs.forEach { this[it.key] = it.value }
}