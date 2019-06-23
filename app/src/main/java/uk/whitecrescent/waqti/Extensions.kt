@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.objectbox.Box
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.whitecrescent.waqti.backend.Cacheable
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.collections.Tuple
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.backend.task.DEBUG
import uk.whitecrescent.waqti.backend.task.DEFAULT_DESCRIPTION
import uk.whitecrescent.waqti.backend.task.DEFAULT_TIME
import uk.whitecrescent.waqti.backend.task.Description
import uk.whitecrescent.waqti.backend.task.GRACE_PERIOD
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Label
import uk.whitecrescent.waqti.backend.task.Priority
import uk.whitecrescent.waqti.backend.task.Property
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.backend.task.Template
import uk.whitecrescent.waqti.backend.task.TimeUnit
import uk.whitecrescent.waqti.frontend.FABOnScrollListener
import uk.whitecrescent.waqti.frontend.MainActivity
import uk.whitecrescent.waqti.frontend.MainActivityViewModel
import java.util.Objects
import kotlin.math.roundToInt

// Everything here is mostly Extensions and Top Level Functions

//region Debug Utils

inline fun logE(message: Any?, tag: String = "DEFAULT") {
    Log.e(tag, message.toString())
}

inline fun logD(message: Any?, tag: String = "DEFAULT") {
    Log.d(tag, message.toString())
}

inline fun logI(message: Any?, tag: String = "DEFAULT") {
    Log.i(tag, message.toString())
}

inline fun View.shortSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_SHORT).show()

inline fun View.longSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_LONG).show()

inline fun View.infSnackBar(string: String) = Snackbar.make(this, string, Snackbar.LENGTH_INDEFINITE).show()

//endregion Debug Utils

//region Android Utils

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

inline val View.mainActivityViewModel: MainActivityViewModel
    get() = mainActivity.viewModel

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

inline val FloatingActionButton.verticalFABOnScrollListener: FABOnScrollListener
    get() = FABOnScrollListener(this, FABOnScrollListener.Orientation.VERTICAL)

inline val FloatingActionButton.horizontalFABOnScrollListener: FABOnScrollListener
    get() = FABOnScrollListener(this, FABOnScrollListener.Orientation.HORIZONTAL)

inline val RecyclerView.Adapter<*>.lastPosition: Int
    get() = this.itemCount - 1

inline fun RecyclerView.Adapter<*>.notifySwapped(fromPosition: Int, toPosition: Int) {
    notifyItemRemoved(fromPosition)
    notifyItemInserted(fromPosition)
    notifyItemRemoved(toPosition)
    notifyItemInserted(toPosition)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T =
        ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.getViewModel(): T =
        ViewModelProviders.of(this).get(T::class.java)

inline fun FragmentManager.commitTransaction(block: FragmentTransaction.() -> Unit) {
    this.beginTransaction().apply(block).commit()
}

inline operator fun <reified V : View> V.invoke(block: V.() -> Unit) {
    this.apply(block)
}

inline fun ComponentActivity.addOnBackPressedCallback(crossinline onBackPressed: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = onBackPressed()
    })
}

inline val View.locationOnScreen: Point
    get() {
        val point = IntArray(2).also {
            this.getLocationOnScreen(it)
        }
        return Point(point[0], point[1])
    }

inline fun View.fadeIn(durationMillis: Long = 250) {
    this.startAnimation(AlphaAnimation(0F, 1F).apply {
        duration = durationMillis
        fillAfter = true
    })
}

inline fun View.fadeOut(durationMillis: Long = 250) {
    this.startAnimation(AlphaAnimation(1F, 0F).apply {
        duration = durationMillis
        fillAfter = true
    })
}

inline fun View.removeOnClickListener() = this.setOnClickListener(null)

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun convertDpToPx(dp: Number, context: Context): Int {
    return (dp.toFloat() * (context.resources.displayMetrics.densityDpi.toFloat() /
            DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun convertPxToDp(px: Number, context: Context): Int {
    return (px.toFloat() / (context.resources.displayMetrics.densityDpi.toFloat() /
            DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

inline fun Pair<Int, Int>.getValue(percent: Int): Int {
    return ((percent.toDouble() / 100.0) * (second - first).toDouble()).roundToInt() + first
}

inline fun Pair<Int, Int>.getPercent(value: Int): Int {
    return (((value - first).toDouble() / (second - first).toDouble()) * 100.0).roundToInt()
}

inline fun <T> T?.ifNotNullApply(func: T.() -> Unit): T? {
    if (this != null) this.apply(func)
    return this
}

inline fun <T> T?.ifNotNullAlso(func: (T) -> Unit): T? {
    if (this != null) this.also(func)
    return this
}

//endregion Android Utils

//region RxJava Extensions

/**
 * Provides an Observable that can be used to execute code on the computation thread and notify
 * the main thread, this means this Observable can be used to safely modify any UI elements on a
 * background thread
 */
inline fun <T> T.androidObservable(): Observable<T> {
    return Observable.just(this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
}

/**
 * Executes the code provided by [onNext] continuously when the provided [predicate] is true,
 * [onNext] will only be invoked once but if the [predicate] becomes false and then true again
 * [onNext] will execute again. All this is done on a background thread and notified on the main
 * thread just like [androidObservable].
 */
inline fun <reified T> T.doInBackgroundWhen(crossinline predicate: (T) -> Boolean,
                                            crossinline onNext: T.() -> Unit,
                                            period: Number = 100,
                                            timeUnit: java.util.concurrent.TimeUnit =
                                                    java.util.concurrent.TimeUnit.MILLISECONDS): Disposable {
    var invoked = false
    return Observable.interval(period.toLong(), timeUnit, Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe({
                if (!predicate(this)) invoked = false
                if (predicate(this) && !invoked) {
                    onNext(this)
                    invoked = true
                }
            }, {
                logE("Error on doInBackgroundAsync, provided ${T::class.java}")
                throw it
            })
}

/**
 * Executes the code provided by [onNext] once as soon as the provided [predicate] is true.
 * All this is done on a background thread and notified on the main thread just like
 * [androidObservable].
 */
inline fun <reified T> T.doInBackgroundOnceWhen(crossinline predicate: (T) -> Boolean,
                                                crossinline onNext: T.() -> Unit,
                                                period: Number = 100,
                                                timeUnit: java.util.concurrent.TimeUnit =
                                                        java.util.concurrent.TimeUnit.MILLISECONDS): Disposable {
    var done = false
    return Observable.interval(period.toLong(), timeUnit, Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .takeWhile { !done }
            .subscribe({
                if (predicate(this)) {
                    onNext(this)
                    done = true
                }
            }, {
                logE("Error on doInBackgroundAsync, provided ${T::class.java}")
                throw it
            })
}

inline fun <reified T> T.doInBackgroundAsync(crossinline onNext: T.() -> Unit): Disposable {
    return Observable.just(this)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                it.apply(onNext)
            }, {
                logE("Error on doInBackgroundAsync, provided ${T::class.java}")
                throw it
            })
}

inline fun <reified T> T.doInBackground(crossinline onNext: T.() -> Unit): Disposable {
    return androidObservable()
            .subscribe({
                it.apply(onNext)
            }, {
                logE("Error on doInBackground, provided ${T::class.java}")
                throw it
            })
}

inline fun <reified T> T.doInBackgroundDelayed(delayMillis: Long,
                                               crossinline onNext: T.() -> Unit): Disposable {
    return androidObservable()
            .delay(delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe({
                it.apply(onNext)
            }, {
                logE("Error on doInBackgroundDelayed, provided ${T::class.java} with delay $delayMillis")
                throw it
            })
}

inline fun <reified T> T.doInBackground(crossinline onNext: T.() -> Unit,
                                        noinline onError: (Throwable) -> Unit = {},
                                        noinline onComplete: () -> Unit = {},
                                        noinline onSubscribe: (Disposable) -> Unit = {}): Disposable {
    return androidObservable()
            .subscribe({
                it.apply(onNext)
            }, {
                onError(it)
            }, {
                onComplete()
            }, {
                onSubscribe(it)
            })
}


//endregion RxJava Extensions

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

inline fun debug(message: Any?, tag: String = "DEFAULT") {
    if (DEBUG) logD(message, tag)
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