@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.model

import android.app.Activity
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import uk.whitecrescent.waqti.model.collections.Tuple
import uk.whitecrescent.waqti.task.GRACE_PERIOD
import uk.whitecrescent.waqti.task.ID
import uk.whitecrescent.waqti.task.Task

inline fun sleep(seconds: Int) = Thread.sleep((seconds) * 1000L)

inline fun <T> logD(t: T) {
    Log.d("DEFAULT", t.toString())
}

inline fun <T> logI(t: T) {
    Log.i("DEFAULT", t.toString())
}

inline fun <T> logE(t: T) {
    Log.e("DEFAULT", t.toString())
}

inline fun setGracePeriod(duration: Duration) {
    GRACE_PERIOD = duration
}

// Extensions

inline fun Collection<Task>.taskIDs(): List<ID> {
    val ids = ArrayList<ID>(this.size)
    this.forEach { ids.add(it.taskID) }
    return ids
}

inline val <E> List<E>.toArrayList: ArrayList<E>
    get() {
        return ArrayList(this)
    }

inline val Collection<Cacheable>.ids: List<ID>
    get() = this.map { it.id() }

inline val Collection<ID>.tasks: List<Task>
    get() = Cache.getTasks(this)

inline val Collection<Tuple>.tasks: Array<Task>
    get() {
        val result = ArrayList<Task>(this.size)
        for (tuple in this) {
            result.addAll(tuple.toList())
        }
        return result.toTypedArray()
    }

inline fun Collection<Task>.putAll() {
    this.forEach { Cache.putTask(it) }
}

inline fun Activity.shortSnackbar(view: View, string: CharSequence) =
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()