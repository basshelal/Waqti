@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package uk.whitecrescent.waqti.extensions

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

//region RxJava Extensions

inline fun <T> Subscriber(
        crossinline onError: (Throwable?) -> Unit = {},
        crossinline onComplete: () -> Unit = {},
        crossinline onSubscribe: (Subscription?) -> Unit = {},
        crossinline onNext: (T) -> Unit = {}
) = object : Subscriber<T> {
    override fun onNext(t: T) = onNext(t)
    override fun onError(t: Throwable?) = onError(t)
    override fun onComplete() = onComplete()
    override fun onSubscribe(s: Subscription?) = onSubscribe(s)
}

inline fun <T> Observer(
        crossinline onError: (Throwable) -> Unit = {},
        crossinline onComplete: () -> Unit = {},
        crossinline onSubscribe: (Disposable) -> Unit = {},
        crossinline onNext: (T) -> Unit = {}
) = object : Observer<T> {
    override fun onNext(t: T) = onNext(t)
    override fun onError(t: Throwable) = onError(t)
    override fun onComplete() = onComplete()
    override fun onSubscribe(d: Disposable) = onSubscribe(d)
}

inline fun <T> Observable<T>.onAndroid() = this.observeOn(AndroidSchedulers.mainThread())

/**
 * Provides an Observable that can be used to execute code on the computation thread and notify
 * the main thread, this means this Observable can be used to safely modify any UI elements on a
 * background thread
 */
inline fun <T> T.androidObservable(): Observable<T> {
    return Observable.just(this)
            .onAndroid()
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
            .onAndroid()
            .subscribeOn(Schedulers.computation())
            .subscribe({
                if (!predicate(this)) invoked = false
                if (predicate(this) && !invoked) {
                    onNext(this)
                    invoked = true
                }
            }, {
                this?.logE("Error on doInBackgroundAsync, provided ${T::class.java}")
                this?.logE(it.message)
                it.printStackTrace()
                throw it
            })
}

/**
 * Executes the code provided by [onNext] once as soon as the provided [predicate] is true.
 * All this is done on a background thread and notified on the main thread just like
 * [androidObservable].
 */
inline fun <reified T> T.doInBackgroundOnceWhen(crossinline predicate: (T) -> Boolean,
                                                period: Number = 100,
                                                timeUnit: java.util.concurrent.TimeUnit =
                                                        java.util.concurrent.TimeUnit.MILLISECONDS,
                                                crossinline onNext: T.() -> Unit): Disposable {
    var done = false
    return Observable.interval(period.toLong(), timeUnit, Schedulers.computation())
            .onAndroid()
            .subscribeOn(Schedulers.computation())
            .takeWhile { !done }
            .subscribe({
                if (predicate(this)) {
                    onNext(this)
                    done = true
                }
            }, {
                this?.logE("Error on doInBackgroundAsync, provided ${T::class.java}")
                this?.logE(it.message)
                it.printStackTrace()
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
                this?.logE("Error on doInBackgroundAsync, provided ${T::class.java}")
                this?.logE(it.message)
                it.printStackTrace()
                throw it
            })
}

inline fun <reified T> T.doInBackground(crossinline onNext: T.() -> Unit): Disposable {
    return androidObservable()
            .subscribe({
                it.apply(onNext)
            }, {
                this?.logE("Error on doInBackground, provided ${T::class.java}")
                this?.logE(it.message)
                it.printStackTrace()
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
                this?.logE("Error on doInBackgroundDelayed, provided ${T::class.java} with delay $delayMillis")
                this?.logE(it.message)
                it.printStackTrace()
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