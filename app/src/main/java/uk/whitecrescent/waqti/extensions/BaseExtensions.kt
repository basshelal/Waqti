@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package uk.whitecrescent.waqti.extensions

import java.util.Objects
import kotlin.math.roundToInt

inline val Number.I: Int get() = this.toInt()
inline val Number.D: Double get() = this.toDouble()
inline val Number.F: Float get() = this.toFloat()
inline val Number.L: Long get() = this.toLong()

inline fun Pair<Int, Int>.getValue(percent: Int): Int =
        ((percent.D / 100.0) * (second - first).D).roundToInt() + first

inline fun Pair<Int, Int>.getPercent(value: Int): Int =
        (((value - first).D / (second - first).D) * 100.0).roundToInt()

fun <T> MutableList<T>.matchOrder(other: Collection<T>): MutableList<T> {
    other.forEachIndexed { index, value -> this[index] = value }
    return this
}

inline fun hash(vararg elements: Any?) = Objects.hash(*elements)
