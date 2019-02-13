@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.objectbox.Box
import uk.whitecrescent.waqti.android.MainActivity
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.BoardList
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.collections.Tuple
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.DEBUG
import uk.whitecrescent.waqti.model.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.model.task.DEFAULT_TIME
import uk.whitecrescent.waqti.model.task.Description
import uk.whitecrescent.waqti.model.task.GRACE_PERIOD
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit
import java.util.Objects

//region Debug Utils

inline fun logE(any: Any?) {
    Log.e("DEFAULT", any.toString())
}

inline fun logE(tag: String, any: Any?) {
    Log.e(tag, any.toString())
}

inline fun View.red() = setBackgroundColor(Color.RED)

inline fun View.shortSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_SHORT).show()

inline fun View.longSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_LONG).show()

inline fun View.infSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_INDEFINITE).show()

//endregion Debug Utils

//region Android Utils

/**
 * Just a utility to show us where there are Fragment changes happening
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
annotation class GoToFragment

inline fun Activity.checkWritePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }
}

inline val Activity.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

inline val View.mainActivity: MainActivity
    get() = this.context as MainActivity

inline fun View.hideSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)
}

inline fun View.showSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, 0)
}

inline fun View.requestFocusAndShowSoftKeyboard() {
    requestFocus()
    showSoftKeyboard()
}

inline fun View.clearFocusAndHideSoftKeyboard() {
    clearFocus()
    hideSoftKeyboard()
}

inline fun addTasks(amount: Int) {
    Database.tasks.put(
            Array(amount) { Task("Auto Generated Task #$it") }.toList())
}

inline fun RecyclerView.scrollToEnd() {
    if (this.adapter != null) {
        this.smoothScrollToPosition(adapter!!.itemCount - 1)
    }
}

inline fun RecyclerView.scrollToStart() {
    if (this.adapter != null) {
        this.smoothScrollToPosition(0)
    }
}

inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

//endregion Android Utils

//region Collections Utils

fun <T> MutableList<T>.matchOrder(other: Collection<T>) {
    val pairs = other.mapIndexed { index, it -> index to it }.toMap()
    pairs.forEach { this[it.key] = it.value }
}

//endregion Collections Utils

//region Persistence Utils

inline val <T> Box<T>.size: Int
    get() = this.count().toInt()

// This is slow because Reflection
inline operator fun <reified T : Cacheable> Caches.get(id: ID): T {
    return when (T::class) {
        Task::class -> Caches.tasks[id] as T
        Template::class -> Caches.templates[id] as T
        Label::class -> Caches.labels[id] as T
        Priority::class -> Caches.priorities[id] as T
        TimeUnit::class -> Caches.timeUnits[id] as T

        TaskList::class -> Caches.taskLists[id] as T
        Board::class -> Caches.boards[id] as T
        BoardList::class -> Caches.boardLists[id] as T
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <reified T : Cacheable> Caches.put(element: T) {
    when (element) {
        is Task -> Caches.tasks.put(element)
        is Template -> Caches.templates.put(element)
        is Label -> Caches.labels.put(element)
        is Priority -> Caches.priorities.put(element)
        is TimeUnit -> Caches.timeUnits.put(element)

        is TaskList -> Caches.taskLists.put(element)
        is Board -> Caches.boards.put(element)
        is BoardList -> Caches.boardLists.put(element)
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <reified T : Cacheable> Box<T>.archive(element: T) {
    when (element) {
        is Task -> Caches.tasks.put(element)
        is Template -> Caches.templates.put(element)
        is Label -> Caches.labels.put(element)
        is Priority -> Caches.priorities.put(element)
        is TimeUnit -> Caches.timeUnits.put(element)

        is TaskList -> Caches.taskLists.put(element)
        is Board -> Caches.boards.put(element)
        is BoardList -> Caches.boardLists.put(element)
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <reified T : Cacheable> Box<T>.restore(element: T) {
    when (element) {
        is Task -> Caches.tasks.put(element)
        is Template -> Caches.templates.put(element)
        is Label -> Caches.labels.put(element)
        is Priority -> Caches.priorities.put(element)
        is TimeUnit -> Caches.timeUnits.put(element)

        is TaskList -> Caches.taskLists.put(element)
        is Board -> Caches.boards.put(element)
        is BoardList -> Caches.boardLists.put(element)
        else -> throw IllegalStateException("Couldn't find Cache of type ${T::class} in Caches")
    }
}

inline fun <T> Box<T>.forEach(action: (T) -> Unit) =
        this.all.forEach(action)

inline val <T : Cacheable> Box<T>.ids: List<ID>
    get() = this.all.map { it.id }

const val CACHE_CHECKING_PERIOD = 10L
val CACHE_CHECKING_UNIT = java.util.concurrent.TimeUnit.SECONDS

//endregion Persistence Utils

//region Model Utils

inline fun debug(string: String) {
    if (DEBUG) println(string)
}

inline fun setGracePeriod(duration: Duration) {
    GRACE_PERIOD = duration
}

inline fun hash(vararg elements: Any?) = Objects.hash(*elements)

// Extensions

inline val <E> Collection<E>.toArrayList: ArrayList<E>
    get() {
        return ArrayList(this)
    }

inline val Collection<Cacheable>.ids: List<ID>
    get() = this.map { it.id }

inline val Collection<ID>.tasks: List<Task>
    get() = Caches.tasks.getByIDs(this)

inline val Collection<Tuple>.tasks: Array<Task>
    get() {
        val result = ArrayList<Task>(this.size)
        for (tuple in this) {
            result.addAll(tuple.toList())
        }
        return result.toTypedArray()
    }

inline val <T> Property<T>.isNotConstrained: Boolean
    get() = !this.isConstrained

inline val <T> Property<T>.isUnMet: Boolean
    get() = !this.isMet

inline val <T> Property<T>.isHidden: Boolean
    get() = !this.isVisible

inline val Time.isDefault: Boolean
    get() = this == DEFAULT_TIME

inline val Time.isNotDefault: Boolean
    get() = this != DEFAULT_TIME

inline val Description.isDefault: Boolean
    get() = this == DEFAULT_DESCRIPTION

inline val Description.isNotDefault: Boolean
    get() = this != DEFAULT_DESCRIPTION

//endregion Model Utils

open class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
    }

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        /*This will never be called as we do not support swiping*/
    }

    override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView, viewSize: Int,
                                              viewSizeOutOfBounds: Int, totalSize: Int,
                                              msSinceStartScroll: Long): Int {
        return super.interpolateOutOfBoundsScroll(
                recyclerView, viewSize, viewSizeOutOfBounds, totalSize, 1500)
    }

}

enum class Orientation {
    HORIZONTAL, VERTICAL
}

class FABOnScrollListener(
        val fab: FloatingActionButton,
        val orientation: Orientation) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        when (orientation) {
            Orientation.HORIZONTAL -> {
                if (dx > 0 && fab.isVisible) fab.hide()
                else if (dx < 0 && !fab.isVisible) fab.show()
            }
            Orientation.VERTICAL -> {
                if (dy > 0 && fab.isVisible) fab.hide()
                else if (dy < 0 && !fab.isVisible) fab.show()
            }
        }

    }
}